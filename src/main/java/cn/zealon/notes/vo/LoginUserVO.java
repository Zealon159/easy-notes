package cn.zealon.notes.vo;

import lombok.Data;

/**
 * @author: zealon
 * @since: 2020/11/27
 */
@Data
public class LoginUserVO extends UserInfoVO {
    private String token;
    /** 已绑定的name是否注册过 */
    private Boolean registered;
    /** 首次绑定 OAuth2登录成功的name是否被其它账户注册过 */
    private Boolean otherAccountRegistered;
    private String clientName;
    private String initUserId;
    private String initUserName;
    private String initAvatarUrl;
}
