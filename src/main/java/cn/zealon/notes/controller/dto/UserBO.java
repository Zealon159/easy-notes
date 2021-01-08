package cn.zealon.notes.controller.dto;

import lombok.Data;

/**
 * 用户信息
 * @author: zealon
 * @since: 2021/1/7
 */
@Data
public class UserBO {

    private String userId;

    private String userName;

    private String avatarUrl;

    /** OAuth2客户端名称 */
    private String clientName;
}
