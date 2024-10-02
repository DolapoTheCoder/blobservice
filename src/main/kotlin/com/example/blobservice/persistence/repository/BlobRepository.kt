package com.example.blobservice.persistence.repository

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BlobRepository(
    @Qualifier("globalJdbcTemplate") val globalJdbcTemplate: JdbcTemplate,
    @Qualifier("shardJdbcTemplate") val shardJdbcTemplate: JdbcTemplate,
) {
    fun findOrhanBlobs(): List<Long> {
        val sql = """
            SELECT BlobStorageID 
            FROM BlobStorage 
            WHERE NumReferences = 0;
        """
        return globalJdbcTemplate.queryForList(sql, Long::class.java)
    }

    fun findByShardBlobStorageID(table: String): List<Long> {
        val sql  = """
            SELECT BlobStorageID FROM ${table}
        """
        return shardJdbcTemplate.queryForList(sql, Long::class.java)
    }

    fun findByGlobalBlobStorageID(table: String): List<Long> {
        val sql  = """
            SELECT BlobStorageID FROM ${table}
        """
        return globalJdbcTemplate.queryForList(sql, Long::class.java)
    }

    fun blobExists(blobStorageId: Long): Boolean {
        val sql = """ SELECT EXISTS(SELECT 1 FROM BlobStorage WHERE BlobStorageID = ?)"""
        return globalJdbcTemplate.queryForObject(sql, Boolean::class.java, blobStorageId)
    }

    fun getBlobReferences(): Map<Int, Int> {
        val sql = "SELECT BlobStorageID, NumReferences FROM BlobStorage"
        return globalJdbcTemplate.query(sql) { rs, _ ->
            rs.getInt("BlobStorageID") to rs.getInt("NumReferences")
        }.toMap()
    }

    data class BlobReferenceCountMismatch(
        val blobStorageID: Int,
        val referenceCount: Int,
        val actualCount: Int
    )

    fun findShardBlobsWithIncorrectReferenceCount(tables: List<String>, referenceMap: Map<Int, Int>): List<BlobReferenceCountMismatch> {
        val blobReferenceCounts = mutableMapOf<Int, Int>()

        // Query each table and count BlobStorageID references
        tables.forEach { table ->
            val sql = "SELECT BlobStorageID, COUNT(*) AS count FROM $table GROUP BY BlobStorageID"

            shardJdbcTemplate.query(sql) { rs ->
                val blobStorageID = rs.getInt("BlobStorageID")
                val count = rs.getInt("count")

                // Aggregate the count for each BlobStorageID across tables
                blobReferenceCounts[blobStorageID] = blobReferenceCounts.getOrDefault(blobStorageID, 0) + count
            }
        }

        // Compare counts with the provided referenceMap
        val mismatches = mutableListOf<BlobReferenceCountMismatch>()
        referenceMap.forEach { (blobStorageID, expectedCount) ->
            val actualCount = blobReferenceCounts[blobStorageID] ?: 0
            if (actualCount != expectedCount) {
                mismatches.add(BlobReferenceCountMismatch(blobStorageID, expectedCount, actualCount))
            }
        }

        return mismatches
    }
    //        val leftJoins = tables.joinToString(" ") { table ->
//            "LEFT JOIN $table t_$table ON bs.BlobStorageID = t_$table.BlobStorageID"
//        }
//
//        val counts = tables.joinToString(" + ") { table ->
//            "COALESCE(COUNT(t_$table.BlobStorageID), 0)"
//        }
//
//        val sql = """
//            SELECT bs.BlobStorageID,
//                   bs.NumReferences,
//                   ($counts) AS actual_count
//            FROM BlobStorage bs
//            $leftJoins
//            GROUP BY bs.BlobStorageID, bs.NumReferences
//            HAVING bs.NumReferences != ($counts)
//        """
//
//        return shardJdbcTemplate.query(sql) { rs, _ ->
//            BlobReferenceCountMismatch(
//                blobStorageID = rs.getInt("BlobStorageID"),
//                referenceCount = rs.getInt("NumReferences"),
//                actualCount = rs.getInt("actual_count")
//            )
//        }
//    }
//
    fun findGlobalBlobsWithIncorrectReferenceCount(tables: List<String>): List<BlobReferenceCountMismatch> {
        val leftJoins = tables.joinToString(" ") { table ->
            "LEFT JOIN $table t_$table ON bs.BlobStorageID = t_$table.BlobStorageID"
        }

        val counts = tables.joinToString(" + ") { table ->
            "COALESCE(COUNT(t_$table.BlobStorageID), 0)"
        }

        val sql = """
            SELECT bs.BlobStorageID, 
                   bs.NumReferences, 
                   ($counts) AS actual_count
            FROM BlobStorage bs
            $leftJoins
            GROUP BY bs.BlobStorageID, bs.NumReferences
            HAVING bs.NumReferences != ($counts)
        """

        return globalJdbcTemplate.query(sql) { rs, _ ->
            BlobReferenceCountMismatch(
                blobStorageID = rs.getInt("BlobStorageID"),
                referenceCount = rs.getInt("NumReferences"),
                actualCount = rs.getInt("actual_count")
            )
        }
    }

}