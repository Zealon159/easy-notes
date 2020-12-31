package cn.zealon.notes.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.controller.dto.RegisterBO;
import cn.zealon.notes.security.domain.LoginUserBean;
import cn.zealon.notes.service.UserService;
import cn.zealon.notes.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author: zealon
 * @since: 2020/11/20
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user-info")
    public Result getCurrentUser(Authentication authentication) {
        LoginUserBean loginUser = (LoginUserBean) authentication.getPrincipal();
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(loginUser.getUser().getUserId());
        vo.setUserName(loginUser.getUser().getUserName());
        vo.setClients(loginUser.getUser().getClients());
        return ResultUtil.success(vo);
    }

    /**
     * 注册用户
     * @param registerBO
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterBO registerBO) {
        return this.userService.register(registerBO);
    }
}
