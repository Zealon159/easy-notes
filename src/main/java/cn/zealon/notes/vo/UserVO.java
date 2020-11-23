package cn.zealon.notes.vo;

import lombok.Data;

/**
 * @author: tangyl
 * @since: 2020/11/17
 */
@Data
public class UserVO {
    private String userName;
    private String password;
    private Integer enable;
    private Integer pwdLock;
}
