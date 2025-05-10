package org.github.zhoujiale.commerce.wechat.api.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * 微信登录响应
 */
@Data
public class WeChatLoginResponse {

    private String sessionKey;

    @JSONField(name = "unionid")
    private String unionId;

    @JSONField(name = "errmsg")
    private String errMsg;

    private String openid;

    @JSONField(name = "errcode")
    private Integer errCode;
}
