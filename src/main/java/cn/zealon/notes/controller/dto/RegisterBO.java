package cn.zealon.notes.controller.dto;

import lombok.Data;

/**
 * 注册信息
 * @author: zealon
 * @since: 2020/12/21
 */
@Data
public class RegisterBO {
    private String userId;
    private String userName;
    private String password;
    private String clientName;
    private String name;
}
