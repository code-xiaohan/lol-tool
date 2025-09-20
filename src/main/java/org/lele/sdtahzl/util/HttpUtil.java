package org.lele.sdtahzl.util;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
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
    public static String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.get().build();
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
}

