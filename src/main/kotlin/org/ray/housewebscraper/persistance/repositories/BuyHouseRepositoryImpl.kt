package org.ray.housewebscraper.persistance.repositories

import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.ray.housewebscraper.model.entities.BuyHouseDocument
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

    override suspend fun getBuyHouseById(id: String): BuyHouseDocument {
        val query = Query.query(Criteria.where("key").`is`(id))
        return mongoTemplate.findOne(query, BuyHouseDocument::class.java).awaitSingle()
    }

    override suspend fun getBuyHousesByCity(city: String): Flow<BuyHouseDocument> = coroutineScope {
        val buyHouseDocuments = withContext(Dispatchers.Default) {
            mongoTemplate.find(Query().addCriteria(Criteria.where("city").`is`(city)), BuyHouseDocument::class.java)
                .asFlow()
        }
        return@coroutineScope buyHouseDocuments
    }

    override suspend fun updateHousePriceById(id: String, price: String): UpdateResult {
        val query = Query.query(Criteria.where("key").`is`(id))
        val update = Update().set("price", price)
        return mongoTemplate.updateFirst(query, update, BuyHouseDocument::class.java).awaitSingle()
    }
}