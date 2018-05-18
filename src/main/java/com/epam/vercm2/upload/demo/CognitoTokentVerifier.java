package com.epam.vercm2.upload.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class CognitoTokentVerifier {

    private final String cognitoClientId;

    private final AwsCognitoRsaKeyProvider keyProvider;

    private final Algorithm algorithm;

    public CognitoTokentVerifier(String cognitoClientId, AwsCognitoRsaKeyProvider keyProvider) {
        this.cognitoClientId = cognitoClientId;
        this.keyProvider = keyProvider;
        this.algorithm = Algorithm.RSA256(keyProvider);
    }

    public DecodedJWT verify(String token) {
        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withClaim("client_id", cognitoClientId)
                .withClaim("token_use", "access")
                .build();
        return jwtVerifier.verify(token);
    }
}
