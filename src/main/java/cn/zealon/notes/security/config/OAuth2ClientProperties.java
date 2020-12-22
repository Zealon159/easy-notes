package cn.zealon.notes.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Oauth2客户端配置
 * @author: zealon
 * @since: 2020/11/25
 */
@Configuration
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2ClientProperties {

    /**
     * OAuth clients.
     */
    private final Map<String, OAuth2Client> clients = new HashMap<>();

    public Map<String, OAuth2Client> getClients() {
        return this.clients;
    }

    @PostConstruct
    public void validate() {
        this.getClients().values().forEach(this::validateRegistration);
    }

    private void validateRegistration(OAuth2Client client) {
        if (!StringUtils.hasText(client.getClientId())) {
            throw new IllegalStateException("Client id must not be empty.");
        }
    }

    public static class OAuth2Client {

        /** 客户端ID */
        private String clientId;
        /** 客户端名称 */
        private String clientName;
        /** 秘钥 */
        private String clientSecret;
        /** 获取token的地址 */
        private String accessTokenUri;
        /** 登陆之后询问的地址 */
        private String userAuthorizationUri;
        /** 请求用户信息的API */
        private String userInfoUri;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getAccessTokenUri() {
            return accessTokenUri;
        }

        public void setAccessTokenUri(String accessTokenUri) {
            this.accessTokenUri = accessTokenUri;
        }

        public String getUserAuthorizationUri() {
            return userAuthorizationUri;
        }

        public void setUserAuthorizationUri(String userAuthorizationUri) {
            this.userAuthorizationUri = userAuthorizationUri;
        }

        public String getUserInfoUri() {
            return userInfoUri;
        }

        public void setUserInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
        }
    }

}
