package com.big_hackathon.backend_v2.filter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@Component
public class JwtUtil {

    private KeyPairGenerator keyPairGenerator;
    private KeyPair keyPair;

    public JwtUtil() throws NoSuchAlgorithmException{

        // KeyPairGenerator to make RSA encrypted keys
        this.keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        // set up generator to create keys that are 2048 bits in length - 2048 is a good balance between security and generation speed (can make 4096 for extra security)
        this.keyPairGenerator.initialize(2048);
        this.keyPair = this.keyPairGenerator.generateKeyPair();
    }

    
    public String generateJWT(){
        // TOOD: implement
        return null;
    }

    public Jwt getSub(String accessToken){
        Map<String, Object> jwtParsed = getIssuerId(accessToken);
        String issUrl = (String) jwtParsed.get("iss");

        //fetches the public key for the JWT from the URL mentioned in the properties - this also validates the JWT under the hood.
        JwtDecoder decoder = JwtDecoders.fromIssuerLocation(issUrl);
        return decoder.decode(accessToken);
    }

    // we are manually parsing the JWT here for the sole purposs of retrieving the iss - the actual JWT validation is done later.
    @SneakyThrows
    private Map<String, Object>  getIssuerId(String jwt){

        String[] s = jwt.split("\\");

        // part 1 = Header, part 2 = claims, part 3 = signature (optional)
        if(s.length < 2){
            throw new IllegalArgumentException("Invalid JWT: JWT missing parts.");
        }
        String payload = new String(Base64.getUrlDecoder().decode(s[1]));
        ObjectMapper mapper = new ObjectMapper();

        // Reading the JSON payload into a map - the mapper object does exactly that.
        Map<String, Object> payloadMap = mapper.readValue(payload, Map.class);
        return payloadMap;
    }
}
