package org.ray.housewebscraper.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf().disable()
            .cors().disable()
            .authorizeExchange()
//            .pathMatchers("/v3/api-docs/**","/swagger-resources/**",
//                        "/webjars/**",
//                        "/swagger-ui/**", "/swagger-ui/index.html").authenticated()
            .anyExchange().authenticated()
            .and().httpBasic(withDefaults()).formLogin(withDefaults())
        return http.build()
    }
}