package pt.drsoares.plugins.targetprocess.client.authentication.token;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import pt.drsoares.plugins.targetprocess.client.TargetProcess;
import pt.drsoares.plugins.targetprocess.utils.Builder;

public final class TokenAuthentication implements Builder<TargetProcess> {

    private String url;
    private String token;

    public TokenAuthentication(String url, String token) {
        this.url = url;
        this.token = token;
    }

    @Override
    public TargetProcess build() {
        return Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .requestInterceptor(new TokenAuthRequestInterceptor(token))
                .target(TargetProcess.class, url);
    }
}
