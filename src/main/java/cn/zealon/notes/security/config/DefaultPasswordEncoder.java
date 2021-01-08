package cn.zealon.notes.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密组件
 * @author: zealon
 * @since: 2020/11/20
 */
@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
