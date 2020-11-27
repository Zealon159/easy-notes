package cn.zealon.notes.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Oauth客户端配置
 * @author: zealon
 * @since: 2020/11/25
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "oauth-client.github")
public class OAuthClientConfig {
    /** 客户端ID */
    private String clientId;
    /** 秘钥 */
    private String clientSecrets;
    /** 获取token的地址 */
    private String accessTokeUri;
    /** 登陆之后询问的地址 */
    private String userAuthorizationUri;
    /** 请求用户信息的API */
    private String userInfoUri;
}
