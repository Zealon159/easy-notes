package cn.zealon.notes.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.security.domain.LoginUserBean;
import cn.zealon.notes.vo.UserInfoVO;
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
    public Result getCurrentUser(Authentication authentication) {
        LoginUserBean loginUser = (LoginUserBean) authentication.getPrincipal();
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(loginUser.getUser().getUserId());
        vo.setUserName(loginUser.getUser().getUserName());
        return ResultUtil.success(vo);
    }
}
