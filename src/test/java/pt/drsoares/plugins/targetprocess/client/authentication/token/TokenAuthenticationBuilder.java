package pt.drsoares.plugins.targetprocess.client.authentication.token;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import pt.drsoares.plugins.targetprocess.client.TargetProcess;

public final class TokenAuthenticationBuilder {

    private TokenAuthenticationBuilder() {

    }

    public static TargetProcess build(String url, String token) {
        return Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .requestInterceptor(new TokenAuthRequestInterceptor(token))
                .target(TargetProcess.class, url);
    }
}
