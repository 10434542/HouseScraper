package org.ray.housewebscraper.funda

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType
data class PaginationPagesDto(
    @field:XmlElement(name = "div class=\"pagination-pages\"") val paginations : String
)
