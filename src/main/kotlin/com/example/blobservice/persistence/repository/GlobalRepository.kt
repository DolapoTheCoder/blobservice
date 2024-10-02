//package com.example.blobservice.persistence.repository
//
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.jdbc.core.JdbcTemplate
//import org.springframework.stereotype.Repository
//
//@Repository
//class GlobalRepository(
//    @Qualifier("globalJdbcTemplate") val jdbcTemplate: JdbcTemplate) {
//    fun getBlobReference(): Map<String, Int> {
//        val sql = "SELECT BlobStorageID, NumReferences FROM BlobStorage"
//        return jdbcTemplate.query(sql) { rs, _ ->
//            rs.getString("BlobStorageID") to rs.getInt("NumReferences")
//        }.toMap()
//    }
//}