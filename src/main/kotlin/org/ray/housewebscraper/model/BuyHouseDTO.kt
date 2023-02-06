package org.ray.housewebscraper.model

data class BuyHouseDTO(
    val street: String,
    val houseNumber: String,
    val zipCode: String,
    val city: String,
    val price: String,
    val surface: String,
    val numberOfRooms: String,
    val link: String,
)
