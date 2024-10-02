package com.example.blobservice.service

import com.example.blobservice.persistence.repository.BlobRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class BlobService(
    private val blobRepository: BlobRepository
) {
    private val logger = KotlinLogging.logger {}

    val shardList: List<String> = listOf("Attachment", "OutsideAttachment", "ContactData")
    val globalList: List<String> = listOf("SentMessage", "SentAttachment")

    /**
     * BlobStorageID can be found in:
     *  ProtonMailShard.Attachment, ProtonMailShard.OutsideAttachment, ProtonMailShard.ContactData
     *  ProtonMailGlobal.SentMessage, ProtonMailGlobal.SentAttachment
     *
     *  ProtonMailGlobal.BlobStorage tracks the amount of times that blob is referenced in any of the above DB's
     *
     *  We need to detect inconsistencies:
     *  - count mismatches (does the NumReference refer to the number of times we see the BlobStorageID in other DB's)
     *  - references to missing blobs (does the BlobReferenceID in other DB's refer to an ID in BlobStorage)
     *  - orphan blobs with nonzero reference count (does the Blob have a NumReference of Zero?) THIS IS CLEANED UP ASYNC so do we need to do anything?
     * */

    fun checkOrphanBlobs() {
        logger.info("checking for orphan blobs")
        val orphanBlobs = blobRepository.findOrhanBlobs();
        if (orphanBlobs.isNotEmpty()) {
            logger.info("Found ${orphanBlobs.size} orphan blobs")
        } else {
            logger.info("No orphan blobs found")
        }
    }

    fun checkMissingBlob(blobList: List<String>, isShard: Boolean): MutableList<String> {
        val missingBlobStorageIdShard: MutableList<String> = mutableListOf()

        blobList.map { blobTable ->
            val blobReference = if (isShard) {
                blobRepository.findByShardBlobStorageID(blobTable)
            } else {
                blobRepository.findByGlobalBlobStorageID(blobTable)
            }

            logger.info("Found ${blobReference.size} blob reference in $blobTable")
            blobReference.map {
                if (!blobRepository.blobExists(it)) {
                    missingBlobStorageIdShard.add(it.toString())
                    logger.info("BlobStorageID: $it not found.")
                } else {
                    logger.info("Found 0 missing blobs in $blobTable")
                }
            }
        }

        return missingBlobStorageIdShard
    }

    fun missingBlobReference() {
        logger.info("checking for missing blob reference")

        val missingShardList : MutableList<String> = checkMissingBlob(shardList, true)
        val missingGlobalList : MutableList<String> = checkMissingBlob(globalList, false)

        logger.info("Missing Shards: ${missingShardList.size}")
        logger.info("Missing Globals: ${missingGlobalList.size}")
    }

    fun countMismatch() {
        logger.info("checking for count mismatch in blobs")
        val shardResults = blobRepository.findShardBlobsWithIncorrectReferenceCount(shardList, blobRepository.getBlobReferences())
        val globalResults = blobRepository.findGlobalBlobsWithIncorrectReferenceCount(globalList)

        logger.info("Incorrect Blob Reference Counts in Shard Database:")
        shardResults.forEach { blob ->
            println("BlobStorageID: ${blob.blobStorageID}, Stored Count: ${blob.referenceCount}, Actual Count: ${blob.actualCount}")
        }

        logger.info("Incorrect Blob Reference Counts in Global Database:")
        globalResults.forEach { blob ->
            println("BlobStorageID: ${blob.blobStorageID}, Stored Count: ${blob.referenceCount}, Actual Count: ${blob.actualCount}")
        }
    }

    fun checkBlobConsistency() {
        logger.info("start blob check.")
        checkOrphanBlobs()
        missingBlobReference()
        countMismatch()
    }
}