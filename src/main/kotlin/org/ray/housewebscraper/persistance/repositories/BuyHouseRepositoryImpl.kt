package org.ray.housewebscraper.persistance.repositories

import kotlinx.coroutines.flow.Flow
import org.ray.housewebscraper.model.entities.BuyHouseDocument
import org.springframework.data.mongodb.core.ReactiveMongoTemplate

class BuyHouseRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate,
) : BuyHouseRepository {
    override suspend fun insert(buyHouseDocument: BuyHouseDocument): BuyHouseDocument {
        TODO("Not yet implemented")
    }

    override suspend fun getBuyHouseById(string: String): BuyHouseDocument {
        TODO("Not yet implemented")
    }

    override suspend fun getBuyHousesByCity(city: String): Flow<BuyHouseDocument> {
        TODO("Not yet implemented")
    }
}