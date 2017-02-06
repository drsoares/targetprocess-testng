package pt.drsoares.plugins.targetprocess.client.authentication.basic;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import pt.drsoares.plugins.targetprocess.client.TargetProcess;

public final class BasicAuthenticationBuilder {

    public static TargetProcess build(String url, String username, String password) {
        return Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .requestInterceptor(new BasicAuthRequestInterceptor(username, password))
                .target(TargetProcess.class, url);
    }
}
