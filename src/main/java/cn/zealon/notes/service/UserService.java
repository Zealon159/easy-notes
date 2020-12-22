package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.controller.dto.RegisterDTO;
import cn.zealon.notes.domain.User;
import cn.zealon.notes.domain.UserOAuth2Client;
import cn.zealon.notes.repository.UserRepository;
import cn.zealon.notes.security.config.DefaultPasswordEncoder;
import cn.zealon.notes.domain.UserInfo;
import cn.zealon.notes.security.config.OAuth2ClientProperties;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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

    /**
     * 注册用户
     * @param registerDTO
     * @return
     */
    public Result register(RegisterDTO registerDTO){
        try {
            User dbUser = this.userRepository.getUserByUserId(registerDTO.getUserId());
            if (dbUser != null) {
                return ResultUtil.verificationFailed().buildMessage("该用户已存在!");
            }

            User user = new User();
            user.setEnable(1);
            user.setAvatarUrl("");
            user.setPassword(defaultPasswordEncoder.encode(registerDTO.getPassword()));
            user.setPwdLock(0);
            user.setUserId(registerDTO.getUserId());
            user.setUserName(registerDTO.getUserName());
            user.setUpdateTime(new Date());

            // 处理OAuth2客户端信息
            UserOAuth2Client registerOAuth2Client = this.getRegisterOAuth2Client(registerDTO);
            if (registerOAuth2Client != null) {
                List<UserOAuth2Client> clients = new ArrayList<>();
                clients.add(registerOAuth2Client);
                user.setAuth2Clients(clients);
            }

            this.userRepository.insertOne(user);
        } catch (Exception ex) {
            return ResultUtil.fail();
        }
        return ResultUtil.success();
    }

    /**
     * 用户ID查询
     * @param userId
     * @return
     */
    public UserInfo getUserByUserId(String userId){
        UserInfo userInfo = null;
        User user = this.userRepository.getUserByUserId(userId);
        if (user != null) {
            userInfo = new UserInfo();
            BeanUtils.copyProperties(user, userInfo);
            return userInfo;
        }
        return userInfo;
    }

    /**
     * 获取OAuth2注册客户端
     * @param registerDTO
     * @return
     */
    private UserOAuth2Client getRegisterOAuth2Client(RegisterDTO registerDTO){
        UserOAuth2Client client = null;
        OAuth2ClientProperties.OAuth2Client auth2Client = auth2ClientService.getOneClient(registerDTO.getClientName());
        if (auth2Client != null) {
            client = new UserOAuth2Client();
            client.setBindTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            client.setClientName(registerDTO.getClientName());
            client.setName(registerDTO.getName());
        }
        return client;
    }
}
