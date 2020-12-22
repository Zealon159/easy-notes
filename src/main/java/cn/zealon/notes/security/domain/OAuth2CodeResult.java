package cn.zealon.notes.security.domain;

import lombok.Data;

/**
 * @author: zealon
 * @since: 2020/12/1
 */
@Data
public class OAuth2CodeResult {
    private String access_token;
    private String token_type;
    private String scope;
}
