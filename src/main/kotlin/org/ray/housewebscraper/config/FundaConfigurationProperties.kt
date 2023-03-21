package org.ray.housewebscraper.config

import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "funda")
data class FundaConfigurationProperties
@ConstructorBinding
constructor(
    @field:NotEmpty val url: String
)
