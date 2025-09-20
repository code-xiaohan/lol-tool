package org.lele.sdtahzl.domain;

import lombok.Data;

@Data
public class ClientDTO {
    private String port;
    private String authToken;
    private String appName;
    private String preUrl;
}
