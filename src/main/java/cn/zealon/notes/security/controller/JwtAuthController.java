package cn.zealon.notes.security.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.security.jwt.JwtAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * JWT登录认证接口
 * @author: zealon
 * @since: 2020/11/27
 */
@RestController
public class JwtAuthController {

    @Autowired
    private JwtAuthService jwtAuthService;

    /**
     * 登录认证接口
     * @param map
     * @return
     */
    @RequestMapping(value = "/auth/authentication")
    public Result login(@RequestBody Map<String,String> map){
        String username  = map.get("username");
        String password = map.get("password");

        if(StringUtils.isEmpty(username)
                || StringUtils.isEmpty(password)){
            return ResultUtil.paramError();
        }
        return jwtAuthService.login(username, password);
    }

    /**
     * 刷新token接口
     * @param token
     * @return
     */
    @RequestMapping(value = "/auth/refresh-token")
    public Result refresh(@RequestHeader("${jwt.header}") String token){
        return ResultUtil.success(jwtAuthService.refreshToken(token));
    }
}
