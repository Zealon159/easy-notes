package cn.zealon.notes.security.service;

import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.domain.OAuth2AccessToken;
import cn.zealon.notes.security.domain.OAuth2AccountInfo;

/**
 * OAuth2用户API策略
 * @author: zealon
 * @since: 2021/1/18
 */
public interface AccountInfoStrategy {

    /**
     * 获取OAuth2访问令牌
     * @param client
     * @param code
     * @param state
     * @return
     */
    OAuth2AccessToken getAccessToken(OAuth2ClientProperties.OAuth2Client client, String code, String state);

    /**
     * 获取账户信息
     * @param clientName
     * @param accessToken
     * @param userInfoUri
     * @return
     */
    OAuth2AccountInfo getAccountInfo(String clientName, String accessToken, String userInfoUri);
}
