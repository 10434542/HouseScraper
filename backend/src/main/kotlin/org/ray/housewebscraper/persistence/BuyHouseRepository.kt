package org.ray.housewebscraper.persistence

import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.flow.Flow
import org.ray.housewebscraper.model.ZipCodeHouseNumber

interface BuyHouseRepository {
    suspend fun insert(buyHouseDocument: BuyHouseDocument): BuyHouseDocument

    fun insertAll(buyHouseDocuments: Collection<BuyHouseDocument>): Flow<BuyHouseDocument>

    suspend fun getBuyHouseById(id: ZipCodeHouseNumber): BuyHouseDocument

    fun getBuyHousesByCity(city: String): Flow<BuyHouseDocument>

    suspend fun updateHousePriceById(postalCode: String, houseNumber: String, price: String): UpdateResult

    suspend fun getBuyHousesInPriceRange(minimum: String, maximum: String): List<BuyHouseDocument>
}
