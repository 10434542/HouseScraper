package org.ray.housewebscraper.model

import org.ray.housewebscraper.persistance.repositories.BuyHouseRepository
import org.ray.housewebscraper.persistance.repositories.BuyHouseRepositoryImpl
import org.springframework.stereotype.Service

@Service
class BuyHouseService(private val repository: BuyHouseRepository) {
}