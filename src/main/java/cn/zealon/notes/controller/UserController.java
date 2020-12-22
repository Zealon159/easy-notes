package cn.zealon.notes.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.controller.dto.RegisterDTO;
import cn.zealon.notes.security.domain.LoginUserBean;
import cn.zealon.notes.service.UserService;
import cn.zealon.notes.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: zealon
 * @since: 2020/11/20
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user-info")
    @ResponseBody
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
     * @param registerDTO
     * @return
     */
    @PostMapping("/register")
    public Result register(RegisterDTO registerDTO) {
        return this.userService.register(registerDTO);
    }
}
