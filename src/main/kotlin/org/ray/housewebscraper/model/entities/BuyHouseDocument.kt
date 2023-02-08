package org.ray.housewebscraper.model.entities

import org.springframework.data.mongodb.core.mapping.Document


@Document("BuyHouses")
data class BuyHouseDocument(
    val street: String,
    val houseNumber: String,
    val zipCode: String,
    val city: String,
    val price: String,
    val surface: String,
    val numberOfRooms: String,
    val link: String,
)
