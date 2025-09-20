package org.lele.sdtahzl.service;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.constant.ServerConstant;
import org.lele.sdtahzl.domain.ClientDTO;
import org.lele.sdtahzl.util.HttpUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ClientService {
    private volatile ClientDTO clientDTO;
    private void initClient() throws Exception {
        String appNameUrl = ServerConstant.LOCAL_HOST + "/riotclient/app-name";
        String portUrl = ServerConstant.LOCAL_HOST + "/riotclient/app-port";
        String authTokenUrl = ServerConstant.LOCAL_HOST + "/riotclient/auth-token";
        try {
            String appName = HttpUtil.get(appNameUrl, null);
            String port = HttpUtil.get(portUrl, null);
            String authToken = HttpUtil.get(authTokenUrl, null);
            if (StringUtils.isEmpty(appName) || StringUtils.isEmpty(port) || StringUtils.isEmpty(authToken)) {
                throw new Exception();
            }
            String preUrl = ServerConstant.LOCAL_HOST + ":" + port;
            clientDTO = new ClientDTO();
            clientDTO.setAppName(appName);
            clientDTO.setPort(port);
            clientDTO.setAuthToken(authToken);
            clientDTO.setPreUrl(preUrl);
        }catch (Exception e) {
            log.error("获取服务失败{}", e.getMessage(), e);
            throw new Exception(e);
        }
    }
    //可能为空
    public ClientDTO getClient() throws Exception {
        if (clientDTO == null) {
            synchronized (this) {
                if (clientDTO == null) {
                    initClient();
                }
            }
        }
        return clientDTO;
    }




}
