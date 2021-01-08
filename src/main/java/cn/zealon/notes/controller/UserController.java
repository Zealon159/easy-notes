package cn.zealon.notes.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.controller.dto.RegisterBO;
import cn.zealon.notes.controller.dto.UserBO;
import cn.zealon.notes.controller.dto.UserPwdBO;
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

    /**
     * 更新用户
     * @param user
     * @return
     */
    @PostMapping("/user/update")
    public Result update(@RequestBody UserBO user) {
        return this.userService.update(user);
    }

    /**
     * 更新用户密码
     * @param user
     * @return
     */
    @PostMapping("/user/update-pwd")
    public Result updatePwd(@RequestBody UserPwdBO user) {
        return this.userService.updatePwd(user);
    }

    /**
     * 更新绑定信息
     * @param user
     * @return
     */
    @PostMapping("/user/remove-bind")
    public Result removeBind(@RequestBody UserBO user){
        return this.userService.removeBind(user);
    }

    /**
     * 获取用户社交绑定信息
     * @param userId
     * @return
     */
    @GetMapping("/user/account-bind-list")
    public Result getAccountBindList(String userId){
        return this.userService.getAccountBindList(userId);
    }
}
