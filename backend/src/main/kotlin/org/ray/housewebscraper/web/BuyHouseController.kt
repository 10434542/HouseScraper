package org.ray.housewebscraper.web

import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.flow.Flow
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.service.HouseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/houses")
class BuyHouseController(private val buyHouseService: HouseService) {


    @GetMapping("/buyhouses/{city}", produces = ["application/stream+json"])
    @Operation(summary = "Get a collection of houses for sale by city")
    suspend fun getByCity(@PathVariable(name = "city") city: String): ResponseEntity<Flow<BuyHouseDTO>> {
        val housesByCity = buyHouseService.getHousesByCity(city)
        return ResponseEntity.ok(housesByCity)
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
}