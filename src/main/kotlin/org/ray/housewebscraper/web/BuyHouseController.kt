package org.ray.housewebscraper.web

import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.service.HouseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("/api/houses")
class BuyHouseController(private val buyHouseService: HouseService) {


    @GetMapping("/buyhouses/{city}")
    @Operation(summary = "Get a collection of houses for sale by city")
    suspend fun getByCity(@PathVariable(name = "city") city: String): Flow<ResponseEntity<BuyHouseDTO>> {
        return buyHouseService.getHousesByCity(city).map { ResponseEntity.ok(it) }
    }

    @GetMapping("/buyhouses")
    @Operation(summary = "Get a house for sale by its postal code and house number")
    suspend fun getByAdress(
        @RequestParam(name = "zipCode", required = true) zipCode: String,
        @RequestParam(name = "houseNumber", required = true) houseNumber: String,
    ): ResponseEntity<BuyHouseDTO?> {
        val byZipCodeHouseNumber = buyHouseService.getByZipCodeHouseNumber(zipCode, houseNumber)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(byZipCodeHouseNumber)
    }
}