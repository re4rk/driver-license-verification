package com.ark.driverlicense.common.error;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String body = new BufferedReader(new InputStreamReader(response.getBody()))
            .lines().collect(Collectors.joining("\n"));
        throw new HttpClientErrorException(
            response.getStatusCode(),
            response.getStatusText(),
            response.getHeaders(),
            body.getBytes(),
            Charset.defaultCharset()
        );
    }
}
