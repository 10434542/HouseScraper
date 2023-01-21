package org.ray.housewebscraper.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class ClientConfiguration {
    @Bean
    fun getRestTemplate(): RestTemplate = RestTemplate()
}