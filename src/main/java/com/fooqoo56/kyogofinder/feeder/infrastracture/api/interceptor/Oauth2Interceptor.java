package com.fooqoo56.kyogofinder.feeder.infrastracture.api.interceptor;

import com.fooqoo56.kyogofinder.feeder.infrastracture.api.dto.response.Oauth2Response;
import com.fooqoo56.kyogofinder.feeder.infrastracture.api.repositoryimpl.BearerTokenClient;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Oauth2Interceptor implements ClientHttpRequestInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final BearerTokenClient bearerTokenClient;

    /**
     * interceptメソッド.
     *
     * @param request   HttpRequestインスタンス
     * @param body      body要素
     * @param execution ClientHttpRequestExecutionインスタンス
     * @return ClientHttpResponseインスタンス
     * @throws IOException 入出力例外
     */
    @Override
    public ClientHttpResponse intercept(
            final HttpRequest request, final byte[] body,
            final ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().set(HttpHeaders.AUTHORIZATION, getBearerToken());

        return execution.execute(request, body);
    }

    /**
     * BearerのTokenを取得する.
     *
     * @return Bearerの取得
     */
    private String getBearerToken() {
        final Oauth2Response response = bearerTokenClient.getBearerToken();

        if (Objects.nonNull(response)) {
            return BEARER_PREFIX + response.getAccessToken();
        }

        throw new RuntimeException();
    }
}
