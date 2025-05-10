package org.github.zhoujiale.commerce.wechat.api.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatPhoneNumber extends WeChatBase {

    private String code;

    private String openid;
}
