package com.big_hackathon.backend_v2.filter;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.big_hackathon.backend_v2.controller.ApartmentController;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;

@Component
public class JwtUtil {

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    private KeyPairGenerator keyPairGenerator;
    private final int DEFAULT_JWT_TTL_SECONDS = 300;
    private final int REFRESH_TOKEN_TTL_DAYS = 3;

    // Public/Private Key pair
    private KeyPair keyPair;
    private final String ISS = "https://apartmentmonitor.com";

    public JwtUtil() throws NoSuchAlgorithmException{

        // KeyPairGenerator to make RSA encrypted keys
        this.keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        // set up generator to create keys that are 2048 bits in length - 2048 is a good balance between security and generation speed (can make 4096 for extra security)
        this.keyPairGenerator.initialize(2048);
        this.keyPair = this.keyPairGenerator.generateKeyPair();
    }

    public String generateJWT(String email, String name){
        Map<String, String> payload = Map.of(
            "sub", UUID.randomUUID().toString(),
            "email", email,
            "name", name
        );
        return generateJWT(payload, DEFAULT_JWT_TTL_SECONDS);
    }

    private String generateJWT(Map<String, String> payload, int ttl){
        Builder tokenBuilder = JWT.create()
            .withIssuer(ISS)
            .withClaim("jti", UUID.randomUUID().toString())
            .withExpiresAt(Date.from(Instant.now().plusSeconds(ttl))) // 5 minute TTL -> can reduce in case need a shortlived token
            .withIssuedAt(Date.from(Instant.now()));

        payload.entrySet().forEach(claim -> tokenBuilder.withClaim(claim.getKey(), claim.getValue()));

        return tokenBuilder.sign(Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate()));
    }
    
    public ResponseCookie generateRefreshTokenAsCookie(){

        // we set the refresh token as a cookie to prevent XSS attacks - it is risky for us to send the refresh token in plain text, and save in user session!
        // if someone gets hold of the refresh token and user email, they pretty much can request a new JWT, which mean nico gets fired from the company for slacking!
        ResponseCookieBuilder refreshCookie  = ResponseCookie.from("refreshToken", UUID.randomUUID().toString());
        return refreshTokenConfigs(refreshCookie);
    }

    public ResponseCookie generateRefreshTokenAsCookie(String tokenValue){

        ResponseCookieBuilder refreshCookie  = ResponseCookie.from("refreshToken", tokenValue);
        return refreshTokenConfigs(refreshCookie);
    }
    
    private ResponseCookie refreshTokenConfigs(ResponseCookieBuilder cookieBuilder){

        cookieBuilder.httpOnly(true) // assures that the cookie is accesed only by HTTP - cannot do smt like document.getCookie in JS!
            .secure(false) // TODO: when we have HTTPS setup, switch back to 'true' to ensure that cookie only send over HTTPS
            .sameSite("None") // TODO: also change to "Strict" later on, to prevent cookie being sent in CSRF situations.
            .path("/auth") // VERY IMPORTANT! this tells the browser "attach the refresh token when requesting to this endpoint"
            .maxAge(Duration.ofDays(REFRESH_TOKEN_TTL_DAYS));

        return cookieBuilder.build();
    }

    private Jwt decodeNativeToken(String encodedToken){
        Algorithm rsaAlgo = Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        try{

            // Veryfying the JWT based on it's signature, using our Private/Public key pair
            DecodedJWT jwtDecoded= JWT.require(rsaAlgo).build().verify(encodedToken);
            byte[] base64Header = Base64.getUrlDecoder().decode(jwtDecoded.getHeader());
            byte[] base64Payload = Base64.getUrlDecoder().decode(jwtDecoded.getPayload());

            // Parsing the custom DecodedJWT object to a Jwt object - trying to keep one type of JWT object in our code
            // The reason we add '{}' is to create an anonymous subclass of TypeReference -> this is done to circumvent Java's Type Erasure issue, where at runtime Java sees Map<String, Object> as just a Map object, 
            // while the Jackson library NEEDS to know the actual type into which to deserialize the data -> we then need the anonymous subclass to save the type info as metadata!
            Map<String, Object> headers = new ObjectMapper().readValue(new String(base64Header, StandardCharsets.UTF_8), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> claims = new ObjectMapper().readValue(new String(base64Payload, StandardCharsets.UTF_8), new TypeReference<Map<String, Object>>() {});
            
            Jwt token = new Jwt(jwtDecoded.getToken(), jwtDecoded.getIssuedAt().toInstant(), jwtDecoded.getExpiresAt().toInstant(), headers, claims);
            return token;
        }catch(JWTVerificationException e){
            logger.info("Passed JWT token is invalid: " +  e.getMessage());
        }catch(Exception e){
            logger.info("Issue parsin the JWT " +  e.getMessage());
        }
        
        return null;
    }

    public Jwt decodeToken(String accessToken){        
        String issUrl = getIssuerId(accessToken);

        // Redirect to our custom Auth chain -> idealy we should have our own Auth server endpoint (ask Daniel if you are brave enough)
        if(issUrl.equals(ISS)){
            return decodeNativeToken(accessToken);
        }

        //fetches the public key for the JWT based on the ISS from the URL mentioned in the properties - this also validates the JWT under the hood.
        JwtDecoder decoder = JwtDecoders.fromIssuerLocation(issUrl);
        return decoder.decode(accessToken);
    }

    // we are manually parsing the JWT here for the sole purposs of retrieving the iss - the actual JWT validation is done later.
    @SneakyThrows
    public String getIssuerId(String jwt){

        String[] s = jwt.split("\\.");

        // part 1 = Header, part 2 = claims, part 3 = signature (optional)
        if(s.length < 2){
            throw new IllegalArgumentException("Invalid JWT: JWT missing parts.");
        }
        String payload = new String(Base64.getUrlDecoder().decode(s[1]));

        // Reading the JSON payload into a map - the mapper object does exactly that.
        Map<String, Object> payloadMap = new ObjectMapper().readValue(payload, new TypeReference<Map<String, Object>>() {});
        return payloadMap.get("iss").toString();
    }

    public String extractJwtBearer(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        return extractJwtBearer(bearerToken);
    }

    public String extractJwtBearer(String authHeader){
        if(authHeader != null && authHeader.startsWith("Bearer")){
            return authHeader.substring(7);
        }
        return null;
    }
}
