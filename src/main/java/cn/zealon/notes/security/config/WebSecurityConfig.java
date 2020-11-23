package cn.zealon.notes.security.config;

import cn.zealon.notes.security.DefaultUserDetailsService;
import cn.zealon.notes.security.filter.CustomAuthenticationFilter;
import cn.zealon.notes.security.handler.DefaultAuthenticationSuccessHandler;
import cn.zealon.notes.security.handler.DefaultLogoutSuccessHandler;
import cn.zealon.notes.security.handler.DefaultAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全配置
 * @author: zealon
 * @since: 2020/11/16
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DefaultUserDetailsService defaultUserDetailsService;

    @Autowired
    private DefaultLogoutSuccessHandler defaultLogoutSuccessHandler;

    @Autowired
    private DefaultAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private DefaultAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationEntryPointConfig authenticationEntryPointConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 基础配置
        http
                .csrf().disable()
                // 表单认证
                .formLogin()
                // 认证成功处理
                .successHandler(authenticationSuccessHandler)
                // 认证失败处理
                .failureHandler(authenticationFailureHandler)

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointConfig)

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(defaultLogoutSuccessHandler)
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()

                .and()
                .authorizeRequests()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/auth/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        // 用重写的Filter替换掉原有的UsernamePasswordAuthenticationFilter
        http.addFilterAt(customAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
        // 设置UserDetailsService
        .userDetailsService(this.defaultUserDetailsService)
        // 使用BCrypt进行密码的hash
        .passwordEncoder(passwordEncoder());
    }

    /**
     * 装载BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setFilterProcessesUrl("/auth/login");
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }
}
