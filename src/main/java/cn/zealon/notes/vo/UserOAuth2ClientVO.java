package cn.zealon.notes.vo;

import lombok.Data;

/**
 * 社交绑定VO
 * @author: zealon
 * @since: 2021/1/7
 */
@Data
public class UserOAuth2ClientVO {
    /** OAuth2客户端名称 */
    private String clientName;
    /** 客户端名称(CN) */
    private String clientNameCn;
    /** OAuth2客户端用户名 */
    private String name;
    /** 绑定时间 */
    private String bindTime;
}
