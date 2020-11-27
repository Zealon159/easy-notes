package cn.zealon.notes.security.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.security.service.OAuthAcceptCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Oauth授权回调接口
 * @author: zealon
 * @since: 2020/11/25
 */
@RestController
public class OAuthAcceptCallbackController {

    @Autowired
    private OAuthAcceptCallbackService oauthAcceptCallbackService;

    @GetMapping("oauth/github/callback")
    public Result githubAcceptCallback(String code, String state){
        this.oauthAcceptCallbackService.githubAcceptCallback(code, state);
        return ResultUtil.success();
    }
}
