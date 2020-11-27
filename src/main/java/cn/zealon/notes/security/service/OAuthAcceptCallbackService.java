package cn.zealon.notes.security.service;

import cn.zealon.notes.security.config.OAuthClientConfig;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Oauth认证允许后的回调服务
 * @author: zealon
 * @since: 2020/11/25
 */
@Slf4j
@Service
public class OAuthAcceptCallbackService {

    @Autowired
    private OAuthClientConfig oauthClientConfig;

    @Autowired
    private RestTemplate restTemplate;

    public void githubAcceptCallback(String code, String state){
        Map<String, Object> body = new HashMap<>();
        body.put("client_id", oauthClientConfig.getClientId());
        body.put("client_secret", oauthClientConfig.getClientSecrets());
        body.put("code", code);
        body.put("redirect_uri", "");
        body.put("state", state);
        try {
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(body), this.getDefaultHttpRequestHeaders());
            String responseText = restTemplate.postForObject(oauthClientConfig.getAccessTokeUri(), formEntity, String.class);

            log.info(responseText);
        } catch (Exception ex){
            log.error("请求github获取授权码失败！data:{}", JSON.toJSONString(body), ex);
        }
    }

    /** 默认请求头 */
    private HttpHeaders getDefaultHttpRequestHeaders(){
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(mediaType);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return headers;
    }
}
