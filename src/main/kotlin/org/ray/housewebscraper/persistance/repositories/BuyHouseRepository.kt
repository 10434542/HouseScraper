package org.ray.housewebscraper.persistance.repositories

import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.flow.Flow
import org.ray.housewebscraper.model.entities.BuyHouseDocument
import org.ray.housewebscraper.model.entities.ZipCodeHouseNumber

interface BuyHouseRepository {
    suspend fun insert(buyHouseDocument: BuyHouseDocument): BuyHouseDocument

    suspend fun getBuyHouseById(id: ZipCodeHouseNumber): BuyHouseDocument

    suspend fun getBuyHousesByCity(city: String): Flow<BuyHouseDocument>

    suspend fun updateHousePriceById(postalCode: String, houseNumber: String, price: String): UpdateResult
}
