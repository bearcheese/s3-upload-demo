package com.epam.vercm2.upload.demo.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${aws.region_name}")
    private String awsRegion;

    @Value("${aws.cognito.region}")
    private String awsCognitoRegion;

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new EnvironmentVariableCredentialsProvider();
    }

    @Bean
    public AmazonS3 s3Client() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(awsCredentialsProvider())
                .build();
    }

    @Bean
    public AWSCognitoIdentityProvider cognitoIdentityProviderClient() {
        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withRegion(awsCognitoRegion)
                .withCredentials(awsCredentialsProvider())
                .build();
    }

}
