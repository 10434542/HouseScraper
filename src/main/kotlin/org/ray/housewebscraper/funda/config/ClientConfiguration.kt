package org.ray.housewebscraper.funda.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.xml.Jaxb2XmlDecoder
import org.springframework.stereotype.Component
import org.springframework.util.MimeTypeUtils
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Component
@Configuration
class ClientConfiguration(private val fundaConfigurationProperties: FundaConfigurationProperties) {

    @Bean
    fun getRestTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    @Qualifier("Funda")
    fun getWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(fundaConfigurationProperties.url)
            .exchangeStrategies(ExchangeStrategies.builder().codecs { codecs ->
                codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
                codecs.defaultCodecs().jaxb2Decoder(
                    Jaxb2XmlDecoder(
                        MimeTypeUtils.APPLICATION_XML,
                        MimeTypeUtils.TEXT_XML,
                        MediaType("application", "*+xml"),
                        MimeTypeUtils.TEXT_HTML
                    )
                )
            }.build())
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create().followRedirect(true)
                )
            ).build()
    }
}
