package cn.zealon.notes.security.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.domain.LoginUserBean;
import cn.zealon.notes.security.domain.OAuth2CodeResult;
import cn.zealon.notes.security.jwt.JwtTokenUtil;
import cn.zealon.notes.service.OAuth2ClientService;
import cn.zealon.notes.vo.LoginUserVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Oauth认证允许后的回调服务
 * @author: zealon
 * @since: 2020/11/25
 */
@Slf4j
@Service
public class OAuth2CallbackService {

    @Autowired
    private OAuth2ClientService auth2ClientService;

    @Autowired
    private DefaultUserDetailsService defaultUserDetailsService;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    JwtTokenUtil jwtTokenUtil;

    public Result authorized(String clientName, String code, String state){
        Map<String, Object> body = new HashMap<>();
        OAuth2ClientProperties.OAuth2Client client = auth2ClientService.getOneClient(clientName);
        if (client == null) {
            return ResultUtil.notFound().buildMessage("获取不到配置文件！clientName:" + clientName);
        }
        body.put("client_id", client.getClientId());
        body.put("client_secret", client.getClientSecret());
        body.put("code", code);
        body.put("redirect_uri", "");
        body.put("state", state);
        try {
            // 获取授权码
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(body), this.getDefaultHttpRequestHeaders(null));
            OAuth2CodeResult codeResult = restTemplate.postForObject(client.getAccessTokenUri(), formEntity, OAuth2CodeResult.class);

            // 获取用户信息
            LoginUserVO loginUser = getLoginUser(clientName, codeResult.getAccess_token(), client.getUserInfoUri());
            return ResultUtil.success(loginUser);
        } catch (Exception ex){
            log.error("请求github获取授权码失败！data:{}", JSON.toJSONString(body), ex);
            return ResultUtil.fail();
        }
    }

    private LoginUserVO getLoginUser(String clientName, String accessToken, String userInfoUri){
        LoginUserVO vo = new LoginUserVO();
        HttpEntity<String> codeEntity = new HttpEntity<>("", this.getDefaultHttpRequestHeaders(accessToken));
        ResponseEntity<String> userResult = restTemplate.exchange(userInfoUri, HttpMethod.GET, codeEntity, String.class);
        JSONObject authUser = JSON.parseObject(userResult.getBody());
        // 登录名
        String username = authUser.getString("login");
        // 是否注册过
        boolean registered = true;
        LoginUserBean userDetails = null;
        try {
            userDetails = (LoginUserBean) defaultUserDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException une) {
            registered = false;
        }
        if (!registered) {
            // 新注册
            log.info("用户[{}]OAuth认证成功，未注册.", username);
            vo.setInitUserId(username);
            vo.setClientName(clientName);
            vo.setInitUserName(authUser.getString("name"));
            vo.setInitAvatarUrl(authUser.getString("avatar_url"));
            vo.setRegistered(false);
        } else {
            // 社交已绑定，返回jwt加密token
            log.info("用户[{}]OAuth登录成功.", username);
            String token = jwtTokenUtil.generateToken(userDetails);
            vo.setClientName(clientName);
            vo.setRegistered(true);
            vo.setToken(token);
            vo.setUserId(userDetails.getUser().getUserId());
            vo.setUserName(userDetails.getUser().getUserName());
        }
        return vo;
    }

    /** 默认请求头 */
    private HttpHeaders getDefaultHttpRequestHeaders(String token){
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(mediaType);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        if (StringUtils.isNotBlank(token)) {
            headers.add("Authorization", "Bearer " + token);
        }
        return headers;
    }
}
