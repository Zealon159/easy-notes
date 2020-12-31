package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.controller.dto.RegisterBO;
import cn.zealon.notes.domain.User;
import cn.zealon.notes.domain.UserOAuth2Client;
import cn.zealon.notes.repository.UserRepository;
import cn.zealon.notes.security.config.DefaultPasswordEncoder;
import cn.zealon.notes.domain.UserInfo;
import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.jwt.JwtAuthService;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: tangyl
 * @since: 2020/11/17
 */
@Service
public class UserService {

    @Autowired
    private DefaultPasswordEncoder defaultPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuth2ClientService auth2ClientService;

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private InitNotesDataService initNotesDataService;

    /**
     * 注册用户
     * @param registerBO
     * @return
     */
    public Result register(RegisterBO registerBO){
        try {
            User dbUser = this.userRepository.findUserByUserId(registerBO.getUserId());
            if (dbUser != null) {
                return ResultUtil.verificationFailed().buildMessage("该用户已存在!");
            }

            User user = new User();
            user.setEnable(1);
            user.setAvatarUrl(registerBO.getAvatarUrl());
            user.setPassword(defaultPasswordEncoder.encode(registerBO.getPassword()));
            user.setPwdLock(0);
            user.setUserId(registerBO.getUserId());
            user.setUserName(registerBO.getUserName());
            String nowDateString = DateUtil.getNowDateString();
            user.setUpdateTime(nowDateString);
            user.setCreateTime(nowDateString);

            // 处理OAuth2客户端信息
            UserOAuth2Client registerOAuth2Client = this.getRegisterOAuth2Client(registerBO);
            if (registerOAuth2Client != null) {
                List<UserOAuth2Client> clients = new ArrayList<>();
                clients.add(registerOAuth2Client);
                user.setAuth2Clients(clients);
            }

            this.userRepository.insertOne(user);
            // 初始化用户数据
            this.initNotesDataService.initData(user.getUserId());
            // 直接登录
            return this.jwtAuthService.login(registerBO.getUserId(), registerBO.getPassword());
        } catch (Exception ex) {
            return ResultUtil.fail();
        }
    }

    /**
     * 用户ID查询
     * @param userId
     * @return
     */
    public UserInfo getUserByUserId(String userId){
        UserInfo userInfo = null;
        User user = this.userRepository.findUserByUserId(userId);
        if (user != null) {
            userInfo = new UserInfo();
            BeanUtils.copyProperties(user, userInfo);
            return userInfo;
        }
        return userInfo;
    }

    /**
     * 获取OAuth2注册客户端
     * @param registerBO
     * @return
     */
    private UserOAuth2Client getRegisterOAuth2Client(RegisterBO registerBO){
        UserOAuth2Client client = null;
        OAuth2ClientProperties.OAuth2Client auth2Client = auth2ClientService.getOneClient(registerBO.getClientName());
        if (auth2Client != null) {
            client = new UserOAuth2Client();
            client.setBindTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            client.setClientName(registerBO.getClientName());
            client.setName(registerBO.getName());
        }
        return client;
    }
}
