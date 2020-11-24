package cn.zealon.notes.controller;

import cn.zealon.notes.security.domain.LoginUserBean;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: zealon
 * @since: 2020/11/20
 */
@Controller
public class UserController {

    @GetMapping("/info")
    @ResponseBody
    public Object getCurrentUser(Authentication authentication) {
        LoginUserBean loginUser = (LoginUserBean) authentication.getPrincipal();
        return loginUser.getUser();
    }
}
