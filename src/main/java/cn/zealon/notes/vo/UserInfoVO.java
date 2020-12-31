package cn.zealon.notes.vo;

import cn.zealon.notes.domain.UserOAuth2Client;
import lombok.Data;

import java.util.List;

/**
 * @author: zealon
 * @since: 2020/11/27
 */
@Data
public class UserInfoVO {
    private String userId;
    private String userName;
    private String avatarUrl;
    private List<UserOAuth2Client> clients;
}
