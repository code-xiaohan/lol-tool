package org.lele.sdtahzl.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .sslSocketFactory(getSslSocketFactory(), getX509TrustManager())
            .hostnameVerifier(getHostnameVerifier())
            .retryOnConnectionFailure(Boolean.TRUE)
            .connectTimeout(30, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(10, 10, TimeUnit.SECONDS))
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
            .build();

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 发送GET请求
     *
     * @param url 请求地址
     * @param headers 请求头
     * @return 响应内容
     * @throws IOException
     */
    public static String get(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if(!CollectionUtils.isEmpty(params)) {
            params.forEach(urlBuilder::addQueryParameter);
        }

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(urlBuilder.build());

        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(requestBuilder::addHeader);
        }


        Request request = requestBuilder.get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body() != null ? response.body().string() : null;
        }
    }

    /**
     * 发送POST请求 (JSON)
     *
     * @param url 请求地址
     * @param json JSON字符串
     * @param headers 请求头
     * @return 响应内容
     * @throws IOException
     */
    public static String post(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder builder = new Request.Builder().url(url).post(body);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body() != null ? response.body().string() : null;
        }
    }

    /**
     * 发送POST请求 (Form 表单)
     *
     * @param url 请求地址
     * @param formParams 表单参数
     * @param headers 请求头
     * @return 响应内容
     * @throws IOException
     */
    public static String postForm(String url, Map<String, String> formParams, Map<String, String> headers) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (formParams != null) {
            formParams.forEach(formBuilder::add);
        }

        Request.Builder builder = new Request.Builder().url(url).post(formBuilder.build());

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body() != null ? response.body().string() : null;
        }
    }
    private static SSLSocketFactory getSslSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static TrustManager[] getTrustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
    }

    public static HostnameVerifier getHostnameVerifier() {
        return (s, sslSession) -> true;
    }

    public static X509TrustManager getX509TrustManager() {
        X509TrustManager trustManager = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            assert trustManagers.length == 1;
            assert trustManagers[0] instanceof X509TrustManager;
            trustManager = (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            log.error("X509TrustManager配置错误", e);
        }
        return trustManager;
    }
}

