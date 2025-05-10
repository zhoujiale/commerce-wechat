package org.github.zhoujiale.commerce.wechat.api.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * 微信小程序码
 */
@Data
public class WeChatQrCode {

    private String scene;

    private String page;

    @JSONField(name = "check_path")
    private Boolean checkPath;

    @JSONField(name = "env_version")
    private String envVersion;

    private Integer width;
}
