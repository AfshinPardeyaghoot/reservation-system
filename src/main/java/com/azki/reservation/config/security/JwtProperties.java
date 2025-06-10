package com.azki.reservation.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    private String secret;
    private long accessTokenValiditySeconds;
}
