package com.example.blobservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.example.blobservice"],
    entityManagerFactoryRef = "shardEntityManager",
    transactionManagerRef = "shardTransactionManager"
)
class ShardDataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.shard")
    fun shardDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    fun shardJdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(shardDataSource())
    }

    @Bean
    fun shardEntityManager(): LocalContainerEntityManagerFactoryBean =
        (LocalContainerEntityManagerFactoryBean()).apply {
            dataSource = shardDataSource()
            setPackagesToScan("com.example.blobservice")
            jpaVendorAdapter = HibernateJpaVendorAdapter()
        }

    @Bean
    fun shardTransactionManager() = JpaTransactionManager(shardEntityManager().`object`!!)
}