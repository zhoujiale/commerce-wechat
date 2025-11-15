package io.github.zhoujiale.commerce.wechat.wechat.constants;

import lombok.Getter;

public interface WeChatConstant {

    String ERROR_CODE = "errcode";

    String ERROR_MSG = "errmsg";

    /**
     * access_token
     **/
    String ACCESS_TOKEN = "access_token";

    String SIGNATURE = "Wechatpay-Signature";

    String NONCE = "Wechatpay-Nonce";

    String TIMESTAMP = "Wechatpay-Timestamp";

    String SERIAL = "Wechatpay-Serial";

    String SIGNATURE_TYPE = "Wechatpay-Signature-Type";

    /**
     * 微信小程序登录
     **/
    String CODE_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";
    /**
     * 手机号
     **/
    String PHONE_NUMBER_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber";
    /**
     * 获取Token
     **/
    String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 小程序码
     **/
    String WX_QR_CODE_UN_LIMIT_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";

    @Getter
    enum CODE {

        SUCCESS(0);
        private final Integer code;

        CODE(Integer code) {
            this.code = code;
        }
    }
}
