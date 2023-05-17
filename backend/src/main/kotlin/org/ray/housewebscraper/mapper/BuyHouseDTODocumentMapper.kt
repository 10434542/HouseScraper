package org.ray.housewebscraper.mapper

import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.persistence.BuyHouseDocument

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface BuyHouseDTODocumentMapper {
    fun toDTO(document: BuyHouseDocument): BuyHouseDTO
    fun toDocument(buyHouseDTO: BuyHouseDTO): BuyHouseDocument
}