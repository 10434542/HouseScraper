package org.ray.housewebscraper.persistance.repositories

import kotlinx.coroutines.flow.Flow
import org.ray.housewebscraper.model.entities.BuyHouseDocument

interface BuyHouseRepository {
    suspend fun insert(buyHouseDocument: BuyHouseDocument): BuyHouseDocument

    suspend fun getBuyHouseById(string: String): BuyHouseDocument

    suspend fun getBuyHousesByCity(city: String): Flow<BuyHouseDocument>
}