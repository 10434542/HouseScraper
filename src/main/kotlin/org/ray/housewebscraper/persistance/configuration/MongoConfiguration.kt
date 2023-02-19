package org.ray.housewebscraper.persistance.configuration

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.ray.housewebscraper.model.entities.BuyHouseDocument
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.Index
import reactor.util.Loggers

@Configuration
class MongoConfiguration(private val mongoTemplate: ReactiveMongoTemplate) {

    companion object {
        private val log = Loggers.getLogger(MongoConfiguration::class.java)
    }

    @PostConstruct
    private fun init() = runBlocking {
        withContext(Dispatchers.IO) {
            mongoTemplate.indexOps(BuyHouseDocument::class.java)
                .ensureIndex(Index("key", Sort.DEFAULT_DIRECTION).unique())
                .awaitSingle()
            mongoTemplate.indexOps(BuyHouseDocument::class.java).indexInfo.toIterable()
                .also { log.info("MongoDB connected, house key index created: $it") }
        }
    }
}