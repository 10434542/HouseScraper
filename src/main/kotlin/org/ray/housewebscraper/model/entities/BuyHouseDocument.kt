package org.ray.housewebscraper.model.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(
    value = "BuyHouses",
)
data class BuyHouseDocument(
    @Id
    val zipCodeHouseNumber: ZipCodeHouseNumber,
    val street: String,
    val city: String,
    val price: String,
    val surface: String,
    val numberOfRooms: String,
    val link: String,
)
