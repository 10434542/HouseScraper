package org.ray.housewebscraper.persistance.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import reactor.util.Loggers


@Configuration
class MongoConfiguration(private val mongoTemplate: ReactiveMongoTemplate) {

    companion object {
        private val log = Loggers.getLogger(MongoConfiguration::class.java)
    }

//    @Autowired
//    fun setMapKeyDotReplacement(mongoConverter: MappingMongoConverter) {
//        mongoConverter.setMapKeyDotReplacement("-")
//    }

}

//    @PostConstruct
//    private fun init() = runBlocking {
//        withContext(Dispatchers.IO) {
//            mongoTemplate.indexOps(BuyHouseDocument::class.java)
//                .ensureIndex(Index("key", Sort.DEFAULT_DIRECTION).unique())
//                .awaitSingle()
//            mongoTemplate.indexOps(BuyHouseDocument::class.java).indexInfo.toIterable()
//                .also { log.info("MongoDB connected, house key index created: $it") }
//        }
//    }

