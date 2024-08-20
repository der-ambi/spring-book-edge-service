package de.bashburg.springbook.edgeservice.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.Principal

@Configuration
class RateLimiterConfig {
    @Bean
    //FIXME this does not work properly, principal is always empty
    fun keyResolver(): KeyResolver {
        return KeyResolver { exchange ->
            exchange.getPrincipal<Principal?>().map { p -> p.name }.defaultIfEmpty("anonymous")
        }
    }
}