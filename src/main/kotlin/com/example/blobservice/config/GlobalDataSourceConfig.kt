package com.example.blobservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
//import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.example.blobservice"],
    entityManagerFactoryRef = "globalEntityManager",
    transactionManagerRef = "globalTransactionManager"
    )
class GlobalDataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.global")
    fun globalDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    fun globalJdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(globalDataSource())
    }

    @Bean
    fun globalEntityManager(): LocalContainerEntityManagerFactoryBean =
        (LocalContainerEntityManagerFactoryBean()).apply {
            dataSource = globalDataSource()
            setPackagesToScan("com.example.blobservice")
            jpaVendorAdapter = HibernateJpaVendorAdapter()
        }

    @Bean
    fun globalTransactionManager() = JpaTransactionManager(globalEntityManager().`object`!!)
}