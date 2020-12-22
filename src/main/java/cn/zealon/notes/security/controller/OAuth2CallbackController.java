package cn.zealon.notes.security.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.security.service.OAuth2CallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Oauth2授权回调接口
 * @author: zealon
 * @since: 2020/11/25
 */
@RestController
public class OAuth2CallbackController {

    @Autowired
    private OAuth2CallbackService auth2CallbackService;

    @GetMapping("login/oauth2/callback/{clientId}")
    public Result authorizedCallback(@PathVariable("clientId") String clientId, String code, String state){
        return this.auth2CallbackService.authorized(clientId, code, state);
    }
}
