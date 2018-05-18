package com.epam.vercm2.upload.demo.config;

import com.epam.vercm2.upload.demo.AwsCognitoRsaKeyProvider;
import com.epam.vercm2.upload.demo.CognitoTokentVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${aws.cognito.region}")
    private String awsCognitoRegion;

    @Value("${aws.cognito.userPoolId}")
    private String awsUserPoolId;

    @Value("${aws.cognito.clientId}")
    private String cognitoClientId;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/login**", "/webjars/**", "/error**", "/me**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and().logout().logoutSuccessUrl("/").permitAll();
//                .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

    @Bean
    public AwsCognitoRsaKeyProvider awsCognitoRsaKeyProvider() {
        return new AwsCognitoRsaKeyProvider(awsCognitoRegion, awsUserPoolId);
    }

    @Bean
    public CognitoTokentVerifier tokentVerifier() {
        return new CognitoTokentVerifier(cognitoClientId, awsCognitoRsaKeyProvider());
    }
}
