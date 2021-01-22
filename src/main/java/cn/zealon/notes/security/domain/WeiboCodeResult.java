package cn.zealon.notes.security.domain;

import lombok.Data;

/**
 * 微博授权码对象
 * @author: zealon
 * @since: 2021/1/22
 */
@Data
public class WeiboCodeResult {
    private String access_token;
    private Long expires_in;
    private String remind_in;
    private String uid;
}
