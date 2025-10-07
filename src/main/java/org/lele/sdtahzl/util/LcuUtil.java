package org.lele.sdtahzl.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import org.lele.sdtahzl.constant.ServerConstant;
import org.lele.sdtahzl.domain.ClientDTO;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LcuUtil {
    private static final Map<String, String> headers = new HashMap<>();
    private static String port = "";
    public static String doGet(String url, Map<String, String> params) throws Exception {
        url = ServerConstant.LOCAL_HOST + ":" + port + url;
        return HttpUtil.get(url, headers, params);
    }
    public static byte[] doGetByte(String url, Map<String, String> params) throws Exception {
        url = ServerConstant.LOCAL_HOST + ":" + port + url;
        return HttpUtil.getByte(url, headers, params);
    }
    public static String doPost(String url, String body) throws Exception {
        url = ServerConstant.LOCAL_HOST + ":" + port + url;
        return HttpUtil.post(url, body, headers);
    }

    public static boolean init() {
        ClientDTO client = null;
        try {
            client = ClientUtil.getClient();
        } catch (Exception e) {
            log.error("客户端初始化失败{}", e.getMessage());
            return false;
        }
        if (client == null) {
            return false;
        }
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Authorization", Credentials.basic("riot", client.getAuthToken()));
        port = client.getPort();
        return true;
    }
}
