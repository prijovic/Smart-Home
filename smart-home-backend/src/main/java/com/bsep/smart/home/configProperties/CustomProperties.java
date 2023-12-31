package com.bsep.smart.home.configProperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "custom")
@ConfigurationPropertiesScan
@Data
public class CustomProperties {
    private String jwtSecret;
    private Long authTokenExpirationMilliseconds;
    private String messageSource;
    private String defaultLocale;
    private String senderEmail;
    private String clientUrl;
    private Long jwtForgotPasswordExpiration;
    private Long jwtActivateEmailTokenExpiration;
    private int pinLength;
}
