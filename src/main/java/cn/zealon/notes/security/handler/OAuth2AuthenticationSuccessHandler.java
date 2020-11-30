package cn.zealon.notes.security.handler;

import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.security.domain.LoginUserBean;
import cn.zealon.notes.security.jwt.JwtTokenUtil;
import cn.zealon.notes.security.service.DefaultUserDetailsService;
import cn.zealon.notes.vo.LoginUserVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OAuth2 登录成功处理
 * @author: zealon
 * @since: 2020/11/17
 */
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DefaultUserDetailsService defaultUserDetailsService;

    @Resource
    JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User authUser = (DefaultOAuth2User) authentication.getPrincipal();
        // 登录名
        String username = authUser.getAttributes().get("login").toString();
        // 是否注册过
        boolean registered = true;
        LoginUserBean userDetails = null;
        try {
            userDetails = (LoginUserBean) defaultUserDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException une) {
            registered = false;
        }

        LoginUserVO vo = new LoginUserVO();
        if (!registered) {
            // 新注册
            log.info("用户[{}]OAuth认证成功，未注册.", username);
            vo.setInitUserId(username);
            vo.setInitUserName(authUser.getAttributes().get("name").toString());
            vo.setInitAvatarUrl(authUser.getAttributes().get("avatar_url").toString());
            vo.setRegistered(false);
        } else {
            // 社交已绑定，返回jwt加密token
            log.info("用户[{}]OAuth登录成功.", username);
            String token = jwtTokenUtil.generateToken(userDetails);
            vo.setRegistered(true);
            vo.setToken(token);
            vo.setUserId(userDetails.getUser().getUserId());
            vo.setUserName(userDetails.getUser().getUserName());
        }

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(ResultUtil.success(vo)));
    }
}
