package pt.drsoares.plugins.targetprocess.client;

import feign.Feign;
import feign.RequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import pt.drsoares.plugins.targetprocess.utils.Builder;

public class TargetProcessBuilder implements Builder<TargetProcess> {

    private String url;
    private RequestInterceptor requestInterceptor;

    public TargetProcessBuilder(String url, RequestInterceptor requestInterceptor) {
        this.url = url;
        this.requestInterceptor = requestInterceptor;
    }

    @Override
    public TargetProcess build() {
        return Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .requestInterceptor(requestInterceptor)
                .target(TargetProcess.class, url);
    }
}
