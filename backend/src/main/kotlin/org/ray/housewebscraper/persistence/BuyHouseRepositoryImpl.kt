package org.ray.housewebscraper.persistence

import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger { }

@Repository
class BuyHouseRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate,
) : BuyHouseRepository {
    override suspend fun insert(buyHouseDocument: BuyHouseDocument): BuyHouseDocument {
        return mongoTemplate.insert(buyHouseDocument).awaitSingle()
    }

    override fun insertAll(buyHouseDocuments: Collection<BuyHouseDocument>): Flow<BuyHouseDocument> {
        return mongoTemplate.insertAll(buyHouseDocuments.toMutableList())
            .asFlow() // maybe subscribe and make it fun (not suspend fun)
    }

    /**
     * Insert if exists
     *
     * @param buyHouseDocuments
     * @return [Flow] of [BuyHouseDocument]
     */
    override fun insertAllIfNotExists(buyHouseDocuments: Collection<BuyHouseDocument>): Flow<BuyHouseDocument> {
        val criteriaBase = Criteria.where("zipCodeHouseNumber").`in`(buyHouseDocuments.map { it.zipCodeHouseNumber })
        val existingDocs = runBlocking {
            flow<BuyHouseDocument> {
                coroutineScope {
                    mongoTemplate.find(Query().addCriteria(criteriaBase), BuyHouseDocument::class.java)
                        .asFlow()
                        .filterNotNull()
                }
            }.toList()
        }
        val docsToSave = buyHouseDocuments.filter {
            !existingDocs.contains(it)
        }
        return mongoTemplate.insertAll(docsToSave).asFlow()
    }

    override suspend fun getBuyHouseById(id: ZipCodeHouseNumber): BuyHouseDocument {
        return mongoTemplate.findById(id, BuyHouseDocument::class.java).doOnSuccess { }.awaitSingle()
    }

    override fun getBuyHousesByCity(city: String): Flow<BuyHouseDocument> {
        return mongoTemplate.find(Query().addCriteria(Criteria.where("city").`is`(city)), BuyHouseDocument::class.java)
            .asFlow()
    }

    /**
     * Update house price by id
     *
     * @param postalCode
     * @param houseNumber
     * @param price
     * @return [UpdateResult]
     */
    override suspend fun updateHousePriceById(postalCode: String, houseNumber: String, price: String): UpdateResult {
        val query = Query.query(
            Criteria
                .where("zipCodeHouseNumber.zipCode").`is`(postalCode)
                .and("zipCodeHouseNumber.houseNumber").`is`(houseNumber)
        )
        val update = Update().set("price", price)
        return mongoTemplate.updateFirst(query, update, BuyHouseDocument::class.java).awaitSingle()
    }

    override suspend fun getBuyHousesInPriceRange(minimum: String, maximum: String): List<BuyHouseDocument> {
        val query = Query.query(
            Criteria.where("price").gte(minimum).lte(maximum)
        )
        return mongoTemplate.find(query, BuyHouseDocument::class.java).collectList().awaitSingle()
    }
}


