package pt.drsoares.plugins.targetprocess.client.authentication.token;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class TokenAuthRequestInterceptor implements RequestInterceptor {

    private final String token;

    public TokenAuthRequestInterceptor(String token) {
        this.token = token;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.query("access_token", token);
    }
}
