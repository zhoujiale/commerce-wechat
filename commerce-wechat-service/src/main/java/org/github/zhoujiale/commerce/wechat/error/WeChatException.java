package org.github.zhoujiale.commerce.wechat.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.github.zhoujiale.commerce.wechat.enums.WeChatErrorEnum;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatException extends RuntimeException{
    private Integer errCode;

    public WeChatException(String message, Integer errCode) {
        super(message);
        this.errCode = errCode;
    }
    public WeChatException(WeChatErrorEnum weChatErrorEnum){
        super(weChatErrorEnum.getErrMsg());
        this.errCode = weChatErrorEnum.getErrCode();
    }
}
