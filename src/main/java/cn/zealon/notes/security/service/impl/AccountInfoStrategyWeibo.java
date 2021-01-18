package cn.zealon.notes.security.service.impl;

import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.domain.OAuth2AccessToken;
import cn.zealon.notes.security.domain.OAuth2AccountInfo;
import cn.zealon.notes.security.service.AccountInfoStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微博 账户信息策略实现类
 * @author: zealon
 * @since: 2021/1/18
 */
@Slf4j
@Service("weibo")
public class AccountInfoStrategyWeibo implements AccountInfoStrategy {

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ClientProperties.OAuth2Client client, String code, String state) {
        log.info("获取微博OAuth2访问令牌.");
        return null;
    }

    @Override
    public OAuth2AccountInfo getAccountInfo(String clientName, String accessToken, String userInfoUri) {
        log.info("获取微博OAuth2账户信息.");
        return null;
    }
}
