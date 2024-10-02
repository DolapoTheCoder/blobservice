package com.example.blobservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication
class BlobserviceApplication

fun main(args: Array<String>) {
	runApplication<BlobserviceApplication>(*args)
}
