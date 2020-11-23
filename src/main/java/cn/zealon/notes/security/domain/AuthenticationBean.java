package cn.zealon.notes.security.domain;

import lombok.Data;

/**
 * 认证对象
 * @author: zealon
 * @since: 2020/11/23
 */
@Data
public class AuthenticationBean {
    private String username;
    private String password;
}
