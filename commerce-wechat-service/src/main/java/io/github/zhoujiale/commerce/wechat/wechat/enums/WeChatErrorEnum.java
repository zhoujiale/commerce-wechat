package io.github.zhoujiale.commerce.wechat.wechat.enums;

import lombok.Getter;

@Getter
public enum WeChatErrorEnum {


    ;
    private final Integer errCode;

    private final String errMsg;

    WeChatErrorEnum(Integer errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
