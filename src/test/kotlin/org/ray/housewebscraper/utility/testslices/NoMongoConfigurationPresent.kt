package org.ray.housewebscraper.utility.testslices

import com.mongodb.reactivestreams.client.MongoClient
import com.ninjasquad.springmockk.MockkBean
import org.ray.housewebscraper.HouseWebScraperSpringApplication
import org.ray.housewebscraper.persistance.configuration.MongoConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.ReactiveMongoTemplate

@SpringBootTest(classes = [HouseWebScraperSpringApplication::class])
@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
class NoMongoConfigurationPresent {

    @MockBean
    private lateinit var mongoConfiguration: MongoConfiguration

    @MockBean
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @MockkBean
    private lateinit var mongoClient: MongoClient
}