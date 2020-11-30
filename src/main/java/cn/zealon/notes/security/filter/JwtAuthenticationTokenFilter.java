package cn.zealon.notes.security.filter;

import cn.zealon.notes.security.domain.LoginUserBean;
import cn.zealon.notes.security.jwt.JwtTokenUtil;
import cn.zealon.notes.security.service.DefaultUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt认证过滤器
 * @author: zealon
 * @since: 2020/11/17
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private DefaultUserDetailsService defaultUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
        }

        String jwtToken = request.getHeader(jwtTokenUtil.getHeader());
        if(!StringUtils.isEmpty(jwtToken)){
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

            if(username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null){
                LoginUserBean userDetails = (LoginUserBean) defaultUserDetailsService.loadUserByUsername(username);
                if(jwtTokenUtil.validateToken(jwtToken,userDetails)){
                    //给使用该JWT令牌的用户进行授权
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }



        filterChain.doFilter(request, response);
    }
}
