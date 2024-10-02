//package com.example.blobservice.persistence.repository
//
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.jdbc.core.JdbcTemplate
//import org.springframework.stereotype.Repository
//
//@Repository
//class ShardRepository(
//    @Qualifier("shardJdbcTemplate") val jdbcTemplate: JdbcTemplate) {
//    fun getBlobReferences(): Map<String, Int> {
//        val sql = "SELECT BlobStorageID, FileSize FROM Attachment"
//        return jdbcTemplate.query(sql) { rs, _ ->
//            rs.getString("BlobStorageID") to rs.getInt("FileSize")
//        }.toMap()
//    }
//}