package cn.zealon.notes.service;

import cn.zealon.notes.vo.UserVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author: tangyl
 * @since: 2020/11/17
 */
@Service
public class UserService {
    public UserVO getUserByUserId(String userId){
        if (userId.equals("zealon")) {
            UserVO user = new UserVO();
            user.setPwdLock(0);
            user.setEnable(1);
            user.setUserName("便携笔记");
            user.setPassword(new BCryptPasswordEncoder().encode("pass"));
            return user;
        }
        return null;
    }
}
