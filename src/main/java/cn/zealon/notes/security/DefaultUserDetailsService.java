package cn.zealon.notes.security;

import cn.zealon.notes.service.UserService;
import cn.zealon.notes.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 用户登录处理
 * @author: zealon
 * @since: 2020/11/16
 */
@Component
@Slf4j
public class DefaultUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private DefaultPasswordEncoder defaultPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            log.info("登录用户：{} 不存在", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        }

        // 查出密码
        UserVO userVO = userService.getUserByUserId(username);
        if (userVO == null) {
            log.info("登录用户：{} 不存在", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        }

        String password = defaultPasswordEncoder.encode(userVO.getPassword());
        return new User(username, password, AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
