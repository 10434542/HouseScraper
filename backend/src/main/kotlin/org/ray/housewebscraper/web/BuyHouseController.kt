package org.ray.housewebscraper.web

import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.subscribe
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.service.HouseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/houses")
class BuyHouseController(private val buyHouseService: HouseService) {


//    @GetMapping("/buyhouses/{city}", produces = ["application/stream+json"])
    @GetMapping("/buyhouses/{city}")
    @Operation(summary = "Get a collection of houses for sale by city")
    suspend fun getByCity(@PathVariable(name = "city") city: String): Flow<BuyHouseDTO> {
        return buyHouseService.getHousesByCity(city)
    }

    @GetMapping("/buyhouses")
    @Operation(summary = "Get a house for sale by its postal code and house number")
    suspend fun getByAdress(
        @RequestParam(name = "zipCode", required = true) zipCode: String,
        @RequestParam(name = "houseNumber", required = true) houseNumber: String,
    ): ResponseEntity<BuyHouseDTO> {
        val byZipCodeHouseNumber = buyHouseService.getByZipCodeHouseNumber(zipCode, houseNumber)
        return ResponseEntity.ok(byZipCodeHouseNumber)
    }

    @GetMapping("/test")
    @Operation(summary = "test for usage of kotlin flow")
    fun getFlow(): Flow<Int> {
        return flowOf(1, 2, 3, 4)
    }
}