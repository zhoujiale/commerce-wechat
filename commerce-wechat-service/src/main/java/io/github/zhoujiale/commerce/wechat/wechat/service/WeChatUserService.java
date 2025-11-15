package io.github.zhoujiale.commerce.wechat.wechat.service;

import com.alibaba.fastjson2.JSONObject;
import io.github.zhoujiale.commerce.common.constant.CacheConstant;
import io.github.zhoujiale.commerce.common.enums.BaseErrorEnum;
import io.github.zhoujiale.commerce.common.error.ServiceException;
import io.github.zhoujiale.commerce.wechat.api.model.model.dto.WeChatBase;
import io.github.zhoujiale.commerce.wechat.api.model.model.dto.WeChatLogin;
import io.github.zhoujiale.commerce.wechat.api.model.model.dto.WeChatPhoneNumber;
import io.github.zhoujiale.commerce.wechat.api.model.model.dto.WeChatQrCode;
import io.github.zhoujiale.commerce.wechat.api.model.model.vo.WeChatLoginResponse;
import io.github.zhoujiale.commerce.wechat.wechat.constants.WeChatConstant;
import io.github.zhoujiale.commerce.wechat.wechat.error.WeChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeChatUserService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final MediaType JSON = MediaType.get("application/json");

    /**
     * 小程序登录
     */
    public WeChatLoginResponse login(WeChatLogin weChatLogin) {
        String url = WeChatConstant.CODE_SESSION_URL +
                "?appid=" + weChatLogin.getAppid() +
                "&secret=" + weChatLogin.getSecret() +
                "&js_code=" + weChatLogin.getJsCode() +
                "&grant_type=authorization_code";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String result = response.body().string();
            log.info("code_session:{}", result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.containsKey(WeChatConstant.ERROR_CODE) &&
                    jsonObject.getIntValue(WeChatConstant.ERROR_CODE) != WeChatConstant.CODE.SUCCESS.getCode()) {
                //异常
                throw new WeChatException(jsonObject.getString(WeChatConstant.ERROR_MSG),
                        jsonObject.getIntValue(WeChatConstant.ERROR_CODE));
            }
            return JSONObject.parseObject(result, WeChatLoginResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(BaseErrorEnum.REQUEST_ERROR);
        }
    }

    /**
     * 获取手机号
     **/
    public String getPhoneNumber(WeChatPhoneNumber weChatPhoneNumber) {
        String url = WeChatConstant.PHONE_NUMBER_URL +
                "?access_token=" + this.getAccessToken(weChatPhoneNumber);
        JSONObject reqJson = new JSONObject();
        reqJson.put("code", weChatPhoneNumber.getCode());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson.toJSONString(), JSON))
                .build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String result = response.body().string();
            log.info("phone result:{}", result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.containsKey(WeChatConstant.ERROR_CODE) &&
                    jsonObject.getIntValue(WeChatConstant.ERROR_CODE) != WeChatConstant.CODE.SUCCESS.getCode()) {
                //异常
                throw new WeChatException(jsonObject.getString(WeChatConstant.ERROR_MSG),
                        jsonObject.getIntValue(WeChatConstant.ERROR_CODE));
            }
            JSONObject phoneInfo = jsonObject.getJSONObject("phone_info");
            return phoneInfo.getString("phoneNumber");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(BaseErrorEnum.REQUEST_ERROR);
        }
    }

    /**
     * 获取token
     **/
    public String getAccessToken(WeChatBase weChatBase) {
        Object o = redisTemplate.opsForValue().get(CacheConstant.getCacheKey(WeChatConstant.ACCESS_TOKEN));
        if (null != o) {
            return (String) o;
        }
        String url = WeChatConstant.ACCESS_TOKEN_URL +
                "?appid=" + weChatBase.getAppid() +
                "&secret=" + weChatBase.getSecret() +
                "&grant_type=client_credential";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String result = response.body().string();
            log.info("access_token:{}", result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.containsKey(WeChatConstant.ERROR_CODE) &&
                    jsonObject.getIntValue(WeChatConstant.ERROR_CODE) != WeChatConstant.CODE.SUCCESS.getCode()) {
                //异常
                throw new WeChatException(jsonObject.getString(WeChatConstant.ERROR_MSG),
                        jsonObject.getIntValue(WeChatConstant.ERROR_CODE));
            }
            redisTemplate.opsForValue()
                    .set(CacheConstant.getCacheKey(WeChatConstant.ACCESS_TOKEN),
                            jsonObject.getString(WeChatConstant.ACCESS_TOKEN), 7200, TimeUnit.SECONDS);
            return jsonObject.getString(WeChatConstant.ACCESS_TOKEN);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(BaseErrorEnum.REQUEST_ERROR);
        }
    }

    /**
     * 获取小程序码
     **/
    public byte[] qrCode(WeChatQrCode weChatQrCode, WeChatBase weChatBase) {
        String accessToken = this.getAccessToken(weChatBase);
        String url = WeChatConstant.WX_QR_CODE_UN_LIMIT_URL +
                "?access_token=" + accessToken;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSONObject.toJSONString(weChatQrCode),JSON))
                .build();
        try (Response response = client.newCall(request).execute()){
            assert response.body() != null;
            if (!Objects.equals(response.header(HttpHeaders.CONTENT_TYPE),
                    org.springframework.http.MediaType.IMAGE_JPEG_VALUE)) {
                log.error("get wechat qrcode fail:{}", response.body().string());
                return null;
            } else {
                return response.body().bytes();
            }
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            throw new ServiceException(BaseErrorEnum.REQUEST_ERROR);
        }
    }

    /**
     * 解密数据
     **/
    public String deciphering(String encryptedData, String iv, String sessionKey) {
        byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(sessionKeyBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new String(cipher.doFinal(encryptedDataBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("微信解密数据异常");
            log.error(e.getMessage(), e);
            throw new WeChatException("微信解密数据异常", 500);
        }
    }
}
