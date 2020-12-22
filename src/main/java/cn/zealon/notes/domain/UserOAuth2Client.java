package cn.zealon.notes.domain;

import lombok.Data;

/**
 * OAuth2客户端
 * @author: zealon
 * @since: 2020/12/21
 */
@Data
public class UserOAuth2Client {
    /** OAuth2客户端名称 */
    private String clientName;
    /** OAuth2客户端用户名 */
    private String name;
    /** 绑定时间 */
    private String bindTime;
}
