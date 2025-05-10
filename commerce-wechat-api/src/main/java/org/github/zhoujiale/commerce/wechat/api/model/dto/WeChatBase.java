package org.github.zhoujiale.commerce.wechat.api.model.dto;

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
