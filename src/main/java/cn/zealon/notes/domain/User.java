package cn.zealon.notes.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Date;
import java.util.List;

/**
 * 用户实体
 * @author: zealon
 * @since: 2020/12/21
 */
@Data
@Document(collection = "users")
public class User {

    @Id
    private String userId;

    @Field("user_name")
    private String userName;

    @Field("password")
    private String password;

    @Field("avatar_url")
    private String avatarUrl;

    @Field("enable")
    private Integer enable;

    @Field("pwd_lock")
    private Integer pwdLock;

    @Field("auth2_clients")
    private List<UserOAuth2Client> auth2Clients;

    @Field("update_time")
    private Date updateTime;
}
