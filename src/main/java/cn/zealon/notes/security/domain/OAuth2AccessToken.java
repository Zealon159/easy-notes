package cn.zealon.notes.security.domain;

import lombok.Data;

/**
 * OAuth2 访问令牌
 * @author: zealon
 * @since: 2021/1/18
 */
@Data
public class OAuth2AccessToken {
    private String accessToken;
    private String tokenType;
    private String scope;
    private String uid;
}
