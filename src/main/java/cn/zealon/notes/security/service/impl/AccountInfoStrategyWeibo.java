package cn.zealon.notes.security.service.impl;

import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.domain.OAuth2AccessToken;
import cn.zealon.notes.security.domain.OAuth2AccountInfo;
import cn.zealon.notes.security.domain.WeiboCodeResult;
import cn.zealon.notes.security.service.AccountInfoStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 微博 账户信息策略实现类
 * @author: zealon
 * @since: 2021/1/18
 */
@Slf4j
@Service("weibo")
public class AccountInfoStrategyWeibo implements AccountInfoStrategy {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ClientProperties.OAuth2Client client, String code, String state) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", client.getClientId());
        params.add("client_secret", client.getClientSecret());
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", "http://notes.zealon.cn/login/oauth2/callback/weibo");
        try {
            WeiboCodeResult codeResult = restTemplate.postForObject(client.getAccessTokenUri(), params, WeiboCodeResult.class);
            OAuth2AccessToken accessToken = new OAuth2AccessToken();
            accessToken.setAccessToken(codeResult.getAccess_token());
            accessToken.setUid(codeResult.getUid());
            return accessToken;
        } catch (Exception ex) {
            log.error("获取{} OAuth2访问令牌失败！", client.getClientId(), ex);
            return null;
        }
    }

    @Override
    public OAuth2AccountInfo getAccountInfo(String clientName, OAuth2AccessToken accessToken, String userInfoUri) {
        OAuth2AccountInfo accountInfo = new OAuth2AccountInfo();
        ResponseEntity<String> userResult;
        try {
            userInfoUri += "?uid=" + accessToken.getUid() + "&access_token=" + accessToken.getAccessToken();
            userResult = restTemplate.getForEntity(userInfoUri, String.class);
        } catch (Exception ex) {
            log.error("获取{} OAuth2账户信息失败！", clientName, ex);
            return null;
        }

        // 获取值
        JSONObject authUser = JSON.parseObject(userResult.getBody());
        accountInfo.setAccountId(authUser.getString("idstr"));
        accountInfo.setName(authUser.getString("name"));
        accountInfo.setAvatarUrl(authUser.getString("profile_image_url"));
        return accountInfo;
    }
}
