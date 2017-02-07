package pt.drsoares.plugins.targetprocess.client.authentication.basic;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import pt.drsoares.plugins.targetprocess.client.TargetProcess;

public final class BasicAuthenticationBuilder {

    private String url;
    private String username;
    private String password;

    public BasicAuthenticationBuilder(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public TargetProcess build() {
        return Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .requestInterceptor(new BasicAuthRequestInterceptor(username, password))
                .target(TargetProcess.class, url);
    }
}
