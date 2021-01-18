package cn.zealon.notes.security.service.impl;

import cn.zealon.notes.common.utils.RestUtil;
import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.domain.GithubCodeResult;
import cn.zealon.notes.security.domain.OAuth2AccessToken;
import cn.zealon.notes.security.domain.OAuth2AccountInfo;
import cn.zealon.notes.security.service.AccountInfoStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Github 账户信息策略实现类
 * @author: zealon
 * @since: 2021/1/18
 */
@Slf4j
@Service("github")
public class AccountInfoStrategyGithub implements AccountInfoStrategy {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ClientProperties.OAuth2Client client, String code, String state) {
        Map<String, Object> body = new HashMap<>();
        body.put("client_id", client.getClientId());
        body.put("client_secret", client.getClientSecret());
        body.put("code", code);
        body.put("redirect_uri", "");
        body.put("state", state);
        try {
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(body), RestUtil.getDefaultHttpRequestHeaders(null));
            GithubCodeResult codeResult = restTemplate.postForObject(client.getAccessTokenUri(), formEntity, GithubCodeResult.class);
            OAuth2AccessToken accessToken = new OAuth2AccessToken();
            accessToken.setAccessToken(codeResult.getAccess_token());
            accessToken.setScope(codeResult.getScope());
            accessToken.setTokenType(codeResult.getToken_type());
            return accessToken;
        } catch (Exception ex) {
            log.error("获取{} OAuth2访问令牌失败！", client.getClientId(), ex);
            return null;
        }
    }

    @Override
    public OAuth2AccountInfo getAccountInfo(String clientName, String accessToken, String userInfoUri) {
        OAuth2AccountInfo accountInfo = new OAuth2AccountInfo();
        HttpEntity<String> codeEntity = new HttpEntity<>("", RestUtil.getDefaultHttpRequestHeaders(accessToken));
        ResponseEntity<String> userResult;
        try {
            userResult = restTemplate.exchange(userInfoUri, HttpMethod.GET, codeEntity, String.class);
        } catch (Exception ex) {
            log.error("获取{} OAuth2账户信息失败！", clientName, ex);
            return null;
        }

        // 获取值
        JSONObject authUser = JSON.parseObject(userResult.getBody());
        accountInfo.setAccountId(authUser.getString("login"));
        accountInfo.setName(authUser.getString("name"));
        accountInfo.setAvatarUrl(authUser.getString("avatar_url"));
        return accountInfo;
    }
}
