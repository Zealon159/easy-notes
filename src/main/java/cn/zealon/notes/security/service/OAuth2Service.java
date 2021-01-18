package cn.zealon.notes.security.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.domain.User;
import cn.zealon.notes.domain.UserOAuth2Client;
import cn.zealon.notes.repository.UserRepository;
import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.domain.LoginUserBean;
import cn.zealon.notes.security.domain.OAuth2AccessToken;
import cn.zealon.notes.security.domain.OAuth2AccountInfo;
import cn.zealon.notes.security.jwt.JwtAuthService;
import cn.zealon.notes.security.jwt.JwtTokenUtil;
import cn.zealon.notes.service.OAuth2ClientService;
import cn.zealon.notes.service.UserService;
import cn.zealon.notes.vo.OAuth2LoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
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
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Resource
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtAuthService jwtAuthService;

    /** OAuth2 策略接口工厂类，Bean交给Spring管理 */
    @Autowired
    private Map<String, AccountInfoStrategy> accountInfoStrategyMap;

    /**
     * 认证处理
     * @param clientName
     * @param code
     * @param state
     * @return
     */
    public Result authorized(String clientName, String code, String state){
        OAuth2ClientProperties.OAuth2Client client = auth2ClientService.getOneClient(clientName);
        if (client == null) {
            return ResultUtil.notFound().buildMessage("获取不到配置文件！clientName:" + clientName);
        }

        try {
            AccountInfoStrategy accountInfoStrategy = accountInfoStrategyMap.get(clientName);
            // 获取访问令牌
            OAuth2AccessToken accessToken = accountInfoStrategy.getAccessToken(client, code, state);
            if (accessToken == null) {
                return ResultUtil.fail();
            }

            // 获取OAuth2账户信息
            OAuth2AccountInfo accountInfo = accountInfoStrategy.getAccountInfo(clientName, accessToken.getAccessToken(), client.getUserInfoUri());
            if (accountInfo == null) {
                return ResultUtil.fail();
            }
            return ResultUtil.success(this.getLoginUser(clientName, accountInfo));
        } catch (Exception ex){
            log.error("OAuth2认证失败！clientName:{}", clientName, ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 处理OAuth2登录注册、绑定操作
     * @param clientName
     * @param accountInfo
     * @return
     * 1、登录注册：成功，返回Token，校验失败直接返回
     * 2、绑定：返回绑定的状态
     */
    private OAuth2LoginUserVO getLoginUser(String clientName, OAuth2AccountInfo accountInfo){
        OAuth2LoginUserVO vo = new OAuth2LoginUserVO();
        // 登录名
        String username = accountInfo.getAccountId();
        // 是否绑定注册过
        boolean registered = false;
        // name是否被其它账户注册
        boolean otherAccountRegistered = false;
        // name是否被其它账户绑定
        boolean otherAccountBind;
        LoginUserBean userDetails = null;
        try {
            // 当前若为登录状态，直接进行绑定社交账户
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

                // 绑定OAuth2社交账户
                this.bindOAuth2Account(clientName, vo, username, loginUserBean);
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
            vo.setInitUserName(accountInfo.getName());
            vo.setInitAvatarUrl(accountInfo.getAvatarUrl());
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

    /**
     * 绑定社交账户
     * @param clientName
     * @param vo
     * @param username
     * @param loginUserBean
     */
    private void bindOAuth2Account(String clientName, OAuth2LoginUserVO vo, String username, LoginUserBean loginUserBean) {
        boolean bind = false;
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
    }
}
