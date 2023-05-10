package org.ray.housewebscraper.persistence

import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class BuyHouseRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate,
) : BuyHouseRepository {
    override suspend fun insert(buyHouseDocument: BuyHouseDocument): BuyHouseDocument {
        return mongoTemplate.insert(buyHouseDocument).awaitSingle()
    }

    override suspend fun getBuyHouseById(id: ZipCodeHouseNumber): BuyHouseDocument {
        return mongoTemplate.findById(id, BuyHouseDocument::class.java).awaitSingle()
    }

    override suspend fun getBuyHousesByCity(city: String): Flow<BuyHouseDocument> = coroutineScope {
        val buyHouseDocuments = withContext(Dispatchers.Default) {
            mongoTemplate.find(Query().addCriteria(Criteria.where("city").`is`(city)), BuyHouseDocument::class.java)
                .asFlow()
        }
        return@coroutineScope buyHouseDocuments
    }

    override suspend fun updateHousePriceById(postalCode: String, houseNumber: String, price: String): UpdateResult {
        val query = Query.query(
            Criteria
                .where("zipCodeHouseNumber.zipCode").`is`(postalCode)
                .and("zipCodeHouseNumber.houseNumber").`is`(houseNumber))
        val update = Update().set("price", price)
        return mongoTemplate.updateFirst(query, update, BuyHouseDocument::class.java).awaitSingle()
    }

    override suspend fun getBuyHousesInPriceRange(minimum: String, maximum: String): List<BuyHouseDocument> {
        val query =  Query.query(
            Criteria.where("price").gte(minimum).lte(maximum)
        )
        return mongoTemplate.find(query, BuyHouseDocument::class.java).collectList().awaitSingle()
    }
}
