package cn.zealon.notes.security.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.domain.User;
import cn.zealon.notes.domain.UserInfo;
import cn.zealon.notes.domain.UserOAuth2Client;
import cn.zealon.notes.repository.UserRepository;
import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.domain.LoginUserBean;
import cn.zealon.notes.security.domain.OAuth2CodeResult;
import cn.zealon.notes.security.jwt.JwtAuthService;
import cn.zealon.notes.security.jwt.JwtTokenUtil;
import cn.zealon.notes.service.OAuth2ClientService;
import cn.zealon.notes.service.UserService;
import cn.zealon.notes.vo.OAuth2LoginUserVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth2服务
 * @author: zealon
 * @since: 2020/11/25
 */
@Slf4j
@Service
public class OAuth2Service {

    @Autowired
    private OAuth2ClientService auth2ClientService;

    @Autowired
    private DefaultUserDetailsService defaultUserDetailsService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Resource
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtAuthService jwtAuthService;

    /**
     * 认证
     * @param clientName
     * @param code
     * @param state
     * @return
     */
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
            OAuth2LoginUserVO loginUser = getLoginUser(clientName, codeResult.getAccess_token(), client.getUserInfoUri());
            return ResultUtil.success(loginUser);
        } catch (Exception ex){
            log.error("请求{}获取授权码失败！data:{}", clientName, JSON.toJSONString(body), ex);
            return ResultUtil.fail();
        }
    }

    private OAuth2LoginUserVO getLoginUser(String clientName, String accessToken, String userInfoUri){
        OAuth2LoginUserVO vo = new OAuth2LoginUserVO();
        HttpEntity<String> codeEntity = new HttpEntity<>("", this.getDefaultHttpRequestHeaders(accessToken));
        ResponseEntity<String> userResult;
        try {
            userResult = restTemplate.exchange(userInfoUri, HttpMethod.GET, codeEntity, String.class);
        } catch (Exception ex) {
            log.error("获取OAuth2账户信息失败！", ex );
            return null;
        }
        JSONObject authUser = JSON.parseObject(userResult.getBody());
        // 登录名
        String username = authUser.getString("login");
        // 是否绑定注册过
        boolean registered = false;
        // name是否被其它账户注册
        boolean otherAccountRegistered = false;
        // name是否被其它账户绑定
        boolean otherAccountBind = false;
        LoginUserBean userDetails = null;
        try {
            // 当前若为登录状态，直接进行绑定社交账户
            boolean bind = false;
            LoginUserBean loginUserBean = this.jwtAuthService.getLoginUserBean();
            if (loginUserBean != null && loginUserBean.getUser() != null) {
                vo.setType(2);
                // 校验name是否绑定过
                User userByOAuth2Client = this.userRepository.findUserByOAuth2Client(clientName, username);
                if (userByOAuth2Client != null) {
                    otherAccountBind = true;
                    vo.setOtherAccountBind(otherAccountBind);
                    return vo;
                }

                // 未绑定过，进行绑定处理
                List<UserOAuth2Client> clients = loginUserBean.getUser().getClients();
                for (UserOAuth2Client client : clients){
                    if (client.getClientName().equals(clientName)) {
                        bind = true;
                        break;
                    }
                }
                if (!bind) {
                    UserOAuth2Client registerOAuth2Client = this.userService.getRegisterOAuth2Client(clientName, username);
                    if (clients == null) {
                        clients = new ArrayList<>();
                    }
                    clients.add(registerOAuth2Client);
                    vo.setClients(clients);
                    String nowDateString = DateUtil.getNowDateString();
                    Update update = Update.update("update_time", nowDateString);
                    update.set("auth2_clients", loginUserBean.getUser().getClients());
                    this.userRepository.updateOne(loginUserBean.getUser().getUserId(), update);
                }
                return vo;
            }

            // 获取绑定用户信息
            User userByOAuth2Client = this.userRepository.findUserByOAuth2Client(clientName, username);
            if (userByOAuth2Client != null) {
                registered = true;
                userDetails = (LoginUserBean) defaultUserDetailsService.loadUserByUsername(userByOAuth2Client.getUserId());
                vo.setUpdateTime(userDetails.getUser().getUpdateTime());
            }

            // 首次注册并绑定，查询name是否被注册
            if (!registered) {
                User user = this.userRepository.findUserByUserId(username);
                if (user != null) {
                    log.info("用户[{}]首次OAuth2认证成功，但是账户被其它人使用了.", username);
                    otherAccountRegistered = true;
                }
            }
        } catch (UsernameNotFoundException une) {
            registered = false;
        }
        if (!registered) {
            // 新注册
            log.info("用户[{}]OAuth2认证成功，未注册.", username);
            vo.setInitUserId(username);
            vo.setClientName(clientName);
            vo.setInitUserName(authUser.getString("name"));
            vo.setInitAvatarUrl(authUser.getString("avatar_url"));
        } else {
            // 社交账户已绑定，返回jwt加密token
            log.info("用户[{}]OAuth2登录成功.", username);
            String token = jwtTokenUtil.generateToken(userDetails);
            vo.setClientName(clientName);
            vo.setToken(token);
            vo.setUserId(userDetails.getUser().getUserId());
            vo.setUserName(userDetails.getUser().getUserName());
            vo.setAvatarUrl(userDetails.getUser().getAvatarUrl());
        }
        vo.setType(1);
        vo.setRegistered(registered);
        vo.setOtherAccountRegistered(otherAccountRegistered);
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
