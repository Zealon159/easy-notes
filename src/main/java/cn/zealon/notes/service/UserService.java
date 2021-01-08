package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.controller.dto.RegisterBO;
import cn.zealon.notes.controller.dto.UserBO;
import cn.zealon.notes.controller.dto.UserPwdBO;
import cn.zealon.notes.domain.User;
import cn.zealon.notes.domain.UserOAuth2Client;
import cn.zealon.notes.repository.UserRepository;
import cn.zealon.notes.security.config.DefaultPasswordEncoder;
import cn.zealon.notes.domain.UserInfo;
import cn.zealon.notes.security.config.OAuth2ClientProperties;
import cn.zealon.notes.security.jwt.JwtAuthService;
import cn.zealon.notes.vo.LoginUserVO;
import cn.zealon.notes.vo.UserOAuth2ClientVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: tangyl
 * @since: 2020/11/17
 */
@Slf4j
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
            UserOAuth2Client registerOAuth2Client = this.getRegisterOAuth2Client(registerBO.getClientName(), registerBO.getName());
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
            log.error("注册用户失败了!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 修改密码
     * @param user
     * @return
     */
    public Result updatePwd(UserPwdBO user) {
        try {
            // 校验在用密码正确性
            User dbUser = this.userRepository.findUserByUserId(user.getUserId());
            if (!this.defaultPasswordEncoder.matches(user.getCurrentPassword(),dbUser.getPassword())) {
                return ResultUtil.verificationFailed().buildMessage("当前密码输入错误啦！");
            }

            String nowDateString = DateUtil.getNowDateString();
            Update update = Update.update("update_time", nowDateString);
            update.set("password", this.defaultPasswordEncoder.encode(user.getPassword()));
            this.userRepository.updateOne(user.getUserId(), update);
            return ResultUtil.success().buildMessage("修改成功，下次请使用新密码登录");
        } catch (Exception ex) {
            log.error("更新密码异常!", ex);
            return ResultUtil.fail();
        }
    }

    public Result update(UserBO user) {
        try {
            LoginUserVO vo = new LoginUserVO();
            String nowDateString = DateUtil.getNowDateString();
            Update update = Update.update("update_time", nowDateString);
            if (StringUtils.isNotBlank(user.getUserName())) {
                update.set("user_name", user.getUserName());
            }
            this.userRepository.updateOne(user.getUserId(), update);
            vo.setUpdateTime(nowDateString);
            vo.setUserId(user.getUserId());
            return ResultUtil.success(vo);
        } catch (Exception ex) {
            log.error("更新用户异常!", ex);
            return ResultUtil.fail();
        }
    }

    public Result removeBind(UserBO user) {
        try {
            UserInfo dbUser = this.getUserByUserId(user.getUserId());
            if (dbUser == null || dbUser.getClients() == null) {
                return ResultUtil.success();
            }

            String nowDateString = DateUtil.getNowDateString();
            Update update = Update.update("update_time", nowDateString);
            List<UserOAuth2Client> userClients = dbUser.getClients();
            for (int i = 0; i < userClients.size(); i++) {
                UserOAuth2Client client = userClients.get(i);
                if (client.getClientName().equals(user.getClientName())) {
                    userClients.remove(i);
                    break;
                }
            }
            update.set("auth2_clients", userClients);
            this.userRepository.updateOne(user.getUserId(), update);
            return ResultUtil.success(this.getUserOAuth2Clients(userClients));
        } catch (Exception ex) {
            log.error("解绑用户异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 获取用户社交绑定信息
     * @param userId
     * @return
     */
    public Result getAccountBindList(String userId){
        // 查询用户
        UserInfo user = this.getUserByUserId(userId);
        List<UserOAuth2ClientVO> list = this.getUserOAuth2Clients(user.getClients());
        return ResultUtil.success(list);
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
            userInfo.setClients(user.getAuth2Clients());
            return userInfo;
        }
        return userInfo;
    }

    /**
     * 获取OAuth2注册客户端
     * @param clientName
     * @param name
     * @return
     */
    public UserOAuth2Client getRegisterOAuth2Client(String clientName, String name){
        UserOAuth2Client client = null;
        OAuth2ClientProperties.OAuth2Client auth2Client = auth2ClientService.getOneClient(clientName);
        if (auth2Client != null) {
            client = new UserOAuth2Client();
            client.setBindTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            client.setClientName(clientName);
            client.setName(name);
        }
        return client;
    }

    /**
     * 获取用户社交绑定信息
     * @param userClients
     * @return
     */
    private List<UserOAuth2ClientVO> getUserOAuth2Clients(List<UserOAuth2Client> userClients){
        List<UserOAuth2ClientVO> list = new ArrayList<>();
        Map<String, OAuth2ClientProperties.OAuth2Client> clients = auth2ClientService.getClients();
        Iterator<Map.Entry<String, OAuth2ClientProperties.OAuth2Client>> iterator = clients.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, OAuth2ClientProperties.OAuth2Client> next = iterator.next();
            OAuth2ClientProperties.OAuth2Client value = next.getValue();
            UserOAuth2ClientVO vo = new UserOAuth2ClientVO();
            vo.setClientName(next.getKey());
            vo.setClientNameCn(value.getClientNameCn());
            // 查询用户是否绑定
            if (userClients != null) {
                for (UserOAuth2Client client : userClients){
                    if (client.getClientName().equals(next.getKey())) {
                        vo.setBindTime(client.getBindTime());
                        vo.setName(client.getName());
                        break;
                    }
                }
            }
            list.add(vo);
        }
        return list;
    }
}
