package org.ray.housewebscraper

import org.junit.jupiter.api.Test
import org.ray.housewebscraper.utility.testslices.NoMongoConfigurationPresent
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HouseWebScraperSpringApplicationTests: NoMongoConfigurationPresent() {

    @Test
    fun contextLoads() {
    }

}
