package org.lele.sdtahzl.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAIL   (500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND (404, "数据不存在");

    private final int code;
    private final String msg;
}