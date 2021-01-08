package cn.zealon.notes.vo;

import lombok.Data;

/**
 * OAuth2登录响应数据
 * @author: zealon
 * @since: 2021/1/7
 */
@Data
public class OAuth2LoginUserVO extends LoginUserVO {
    /** 类型:1 注册，2 绑定 */
    private Integer type;
    /** 已绑定的name是否注册过 */
    private Boolean registered;
    /** 首次绑定OAuth2的name是否被其它账户注册过 */
    private Boolean otherAccountRegistered;
    /** 要绑定OAuth2的name是否被其它账户绑定过 */
    private Boolean otherAccountBind;
    private String clientName;
    private String initUserId;
    private String initUserName;
    private String initAvatarUrl;
}
