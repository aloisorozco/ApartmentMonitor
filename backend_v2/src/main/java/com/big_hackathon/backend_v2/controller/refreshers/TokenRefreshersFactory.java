package com.big_hackathon.backend_v2.controller.refreshers;

import org.springframework.stereotype.Component;

@Component
public class TokenRefreshersFactory {
    
    private final NativeTokenRefreser nativeRefresher;
    private final GoogleTokenRefresher googleRefresher;

    public TokenRefreshersFactory(NativeTokenRefreser nativeRefresher, GoogleTokenRefresher googleRefresher){
        this.nativeRefresher = nativeRefresher;
        this.googleRefresher = googleRefresher;
    }

    public IJwtRefresher getTokenRefresher(String iss){
        switch(iss){
            case "https://apartmentmonitor.com":
                return nativeRefresher;
            case "https://accounts.google.com":
                return googleRefresher;
            default:
                return null;
        }
    }
}
