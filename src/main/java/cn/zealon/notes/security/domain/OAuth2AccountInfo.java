package cn.zealon.notes.security.domain;

import lombok.Data;

/**
 * OAuth2 社交账户信息
 * @author: zealon
 * @since: 2021/1/18
 */
@Data
public class OAuth2AccountInfo {
    /** 账户ID */
    private String accountId;
    /** 昵称 */
    private String name;
    /** 头像 */
    private String avatarUrl;
}
