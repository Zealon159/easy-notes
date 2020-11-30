package cn.zealon.notes.vo;

import lombok.Data;

/**
 * @author: zealon
 * @since: 2020/11/27
 */
@Data
public class LoginUserVO extends UserInfoVO {
    private String token;
    private Boolean registered;
    private String initUserId;
    private String initUserName;
    private String initAvatarUrl;
}
