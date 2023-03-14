package org.ray.housewebscraper.persistance.repositories

import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.flow.Flow
import org.ray.housewebscraper.model.entities.BuyHouseDocument

interface BuyHouseRepository {
    suspend fun insert(buyHouseDocument: BuyHouseDocument): BuyHouseDocument

    suspend fun getBuyHouseById(id: String): BuyHouseDocument

    suspend fun getBuyHousesByCity(city: String): Flow<BuyHouseDocument>

    suspend fun updateHousePriceById(id: String, price: String): UpdateResult
}