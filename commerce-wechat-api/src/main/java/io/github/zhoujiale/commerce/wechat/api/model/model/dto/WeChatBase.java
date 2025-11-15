package io.github.zhoujiale.commerce.wechat.api.model.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeChatBase {

    protected String appid;

    protected String secret;
}
