package org.ray.housewebscraper.utility.testslices

import org.ray.housewebscraper.persistance.configuration.MongoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.ReactiveMongoTemplate

@SpringBootTest
class NoMongoConfigurationPresent {

    @MockBean
    private lateinit var mongoConfiguration: MongoConfiguration

    @MockBean
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate
}