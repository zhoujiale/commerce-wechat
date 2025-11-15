package io.github.zhoujiale.commerce.wechat.api.model.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatLogin extends WeChatBase {

    private String jsCode;
}
