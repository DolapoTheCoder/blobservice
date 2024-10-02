//package com.example.blobservice.config
//
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.boot.context.properties.ConfigurationProperties
//import org.springframework.boot.jdbc.DataSourceBuilder
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.jdbc.core.JdbcTemplate
//import org.springframework.jdbc.datasource.DriverManagerDataSource
//import javax.sql.DataSource
//
//@Configuration
//class DataSourceConfig {
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.global")
//    fun globalDataSource(): DataSource {
//        return DriverManagerDataSource()
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.shard")
//    fun shardDataSource(): DataSource {
//        return DataSourceBuilder.create().build()
//    }
//
//    @Bean
//    fun globalJdbcTemplate(@Qualifier("globalDataSource") dataSource: DataSource): JdbcTemplate {
//        return JdbcTemplate(dataSource)
//    }
//
//    @Bean
//    fun shardJdbcTemplate(@Qualifier("shardDataSource") dataSource: DataSource): JdbcTemplate {
//        return JdbcTemplate(dataSource)
//    }
//}
