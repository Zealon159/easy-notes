package cn.zealon.notes.controller.dto;

import lombok.Data;

/**
 * 用户信息
 * @author: zealon
 * @since: 2021/1/7
 */
@Data
public class UserPwdBO {

    private String userId;

    /**
     * 当前密码
     */
    private String currentPassword;

    /**
     * 新密码
     */
    private String password;
}
