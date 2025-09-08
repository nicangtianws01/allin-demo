package org.example.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${token.secretKey}")
    private String secretKey;

    public String getToken(String userId, String userRole) {
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("userRole", userRole)
                .withClaim("timeStamp", System.currentTimeMillis())
                .sign(Algorithm.HMAC256(secretKey));
    }

    public Map<String, String> parseToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
        Map<String, String> map = new HashMap<>();
        map.put("userId", decodedJWT.getClaim("userId").asString());
        map.put("userRole", decodedJWT.getClaim("userRole").asString());
        map.put("timeStamp", decodedJWT.getClaim("timeStamp").asLong().toString());
        return map;
    }
}
