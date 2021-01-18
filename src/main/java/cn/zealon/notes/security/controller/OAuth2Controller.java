package cn.zealon.notes.security.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.security.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2授权回调接口
 * @author: zealon
 * @since: 2020/11/25
 */
@RestController
@RequestMapping("oauth2")
public class OAuth2Controller {

    @Autowired
    private OAuth2Service auth2Service;

    /**
     * OAuth2 授权成功回调接口
     * @param clientId
     * @param code
     * @param state
     * @return
     */
    @GetMapping("authorized/callback/{clientId}")
    public Result authorizedCallback(@PathVariable("clientId") String clientId, String code, String state){
        return this.auth2Service.authorized(clientId, code, state);
    }
}
