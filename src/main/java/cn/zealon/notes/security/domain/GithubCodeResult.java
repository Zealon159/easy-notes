package cn.zealon.notes.security.domain;

import lombok.Data;

/**
 * OAuth2授权码对象
 * @author: zealon
 * @since: 2020/12/1
 */
@Data
public class GithubCodeResult {
    private String access_token;
    private String token_type;
    private String scope;
}
