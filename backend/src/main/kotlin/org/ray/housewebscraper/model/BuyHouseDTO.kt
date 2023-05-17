package org.ray.housewebscraper.model

data class BuyHouseDTO(
    val zipCodeHouseNumber: ZipCodeHouseNumber,
    val street: String,
    val city: String,
    val price: String,
    val surface: String,
    val numberOfRooms: String,
    val link: String,
)
