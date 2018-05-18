package com.epam.vercm2.upload.demo;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.interfaces.RSAKeyProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class AwsCognitoRsaKeyProvider implements RSAKeyProvider {

    private final JwkProvider provider;

    private final URL awsCognitoKidStoreUrl;

    public AwsCognitoRsaKeyProvider(String awsCognitoRegion, String awsUserPoolId) {
        String url = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", awsCognitoRegion, awsUserPoolId);
        try {
            this.awsCognitoKidStoreUrl = new URL(url);
            this.provider = new JwkProviderBuilder(awsCognitoKidStoreUrl).build();
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Invalid URL provided, URL=%s", url));
        }
    }

    @Override
    public RSAPublicKey getPublicKeyById(String kid) {
        try {
            Jwk jwk = provider.get(kid);
            return (RSAPublicKey) jwk.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to get JWT kid=%s from awsCognitoKidStoreUrl=%s", kid, awsCognitoKidStoreUrl));
        }
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return null;
    }

    @Override
    public String getPrivateKeyId() {
        return null;
    }
}