package cn.zealon.notes.security.service.impl;

import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.domain.OAuth2AccessToken;
import cn.zealon.notes.security.domain.OAuth2AccountInfo;
import cn.zealon.notes.security.service.AccountInfoStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * QQ 账户信息策略实现类
 * @author: zealon
 * @since: 2021/1/22
 */
@Slf4j
@Service("qq")
public class AccountInfoStrategyQQ implements AccountInfoStrategy {

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ClientProperties.OAuth2Client client, String code, String state) {
        log.info("获取QQ OAuth令牌.");
        return null;
    }

    @Override
    public OAuth2AccountInfo getAccountInfo(String clientName, OAuth2AccessToken accessToken, String userInfoUri) {
        log.info("获取QQ OAuth账户.");
        return null;
    }
}
