package com.example.blobservice.rest.controller

import com.example.blobservice.service.BlobService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BlobControlle(
    private val blobService: BlobService
) {
    @GetMapping("/check")
    fun check(): String {
        blobService.checkBlobConsistency()
        return "Blob concurrent check completed"
    }

    @GetMapping("/check/oprhan")
    fun checkOprhan(): String {
        blobService.checkOrphanBlobs()
        return "Blob Orphan check completed"
    }
}