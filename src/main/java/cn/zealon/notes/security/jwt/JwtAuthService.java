package cn.zealon.notes.security.jwt;

import cn.zealon.notes.common.exception.CustomException;
import cn.zealon.notes.common.exception.CustomExceptionType;
import cn.zealon.notes.common.result.HttpCodeEnum;
import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.security.domain.LoginUserBean;
import cn.zealon.notes.security.service.DefaultUserDetailsService;
import cn.zealon.notes.vo.LoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Slf4j
@Service
public class JwtAuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DefaultUserDetailsService defaultUserDetailsService;

    @Resource
    JwtTokenUtil jwtTokenUtil;

    /**
     * 登录认证换取JWT令牌
     * @return JWT
     */
    public Result login(String username, String password) throws CustomException {
        try {
            UsernamePasswordAuthenticationToken upToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            LoginUserBean userDetails = (LoginUserBean) defaultUserDetailsService.loadUserByUsername(username);

            // 生成Token
            String token = jwtTokenUtil.generateToken(userDetails);
            LoginUserVO vo = new LoginUserVO();
            vo.setToken(token);
            vo.setUserId(userDetails.getUser().getUserId());
            vo.setUserName(userDetails.getUser().getUserName());
            return ResultUtil.success(vo);
        } catch (Exception e){
            Result result;
            if (e instanceof BadCredentialsException) {
                // 密码错误
                log.info("[登录失败] - 用户[{}]密码错误", username);
                result = ResultUtil.custom(HttpCodeEnum.AUTH_PWD_ERROR);
            } else if (e instanceof CredentialsExpiredException) {
                // 密码过期
                log.info("[登录失败] - 用户[{}]密码过期", username);
                result = ResultUtil.custom(HttpCodeEnum.AUTH_EXPIRED);
            } else if (e instanceof DisabledException) {
                // 用户被禁用
                log.info("[登录失败] - 用户[{}]被禁用", username);
                result = ResultUtil.custom(HttpCodeEnum.AUTH_USER_DISABLED);
            } else if (e instanceof LockedException) {
                // 用户被锁定
                log.info("[登录失败] - 用户[{}]被锁定", username);
                result = ResultUtil.custom(HttpCodeEnum.AUTH_USER_LOCKED);
            } else {
                // 内部错误
                log.error(String.format("[登录失败] - [%s]内部错误", username), e);
                result = ResultUtil.fail();
            }
            return result;
        }
    }


    public String refreshToken(String oldToken){
        if(!jwtTokenUtil.isTokenExpired(oldToken)){
            return jwtTokenUtil.refreshToken(oldToken);
        }
        return null;
    }
}