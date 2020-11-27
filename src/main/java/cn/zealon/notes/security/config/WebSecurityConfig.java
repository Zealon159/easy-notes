package cn.zealon.notes.security.config;

import cn.zealon.notes.security.filter.JwtAuthenticationTokenFilter;
import cn.zealon.notes.security.service.DefaultUserDetailsService;
import cn.zealon.notes.security.handler.DefaultLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.Filter;
import java.util.Arrays;

/**
 * 安全配置
 * @author: zealon
 * @since: 2020/11/16
 */
@EnableWebSecurity
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /** 注入Oauth2.0客户端上下文 */
    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    //@Autowired
    //GithubPrincipalExtractor githubPrincipalExtractor;

    @Autowired
    private DefaultUserDetailsService defaultUserDetailsService;

    @Autowired
    private DefaultLogoutSuccessHandler defaultLogoutSuccessHandler;

    @Autowired
    private AuthenticationEntryPointConfig authenticationEntryPointConfig;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 基础配置
        http
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers("/auth/**")
                .and().cors().and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)

                // 未授权端点自定义处理
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointConfig)

                .and()
                .authorizeRequests()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/auth/**", "/oauth/**", "/error", "/favicon.ico").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()

                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //http.addFilterBefore(ssoFilter(), UsernamePasswordAuthenticationFilter.class);
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

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /*@Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setFilterProcessesUrl("/auth/login");
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }*/

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8888"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.applyPermitDefaultValues();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /*注册一个额外的Filter：OAuth2ClientContextFilter
     * 主要作用是重定向，当遇到需要权限的页面或URL，代码抛出异常，这时这个Filter将重定向到OAuth鉴权的地址
     */
    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(100);
        return registration;
    }

    //自定义过滤器，用于拦截oauth2第三方登录返回code的url,并根据code,clientid,clientSecret去授权服务器拿accace_token
    private Filter ssoFilter() {
        //OAuth2ClientAuthenticationProcessingFilter
        //它的构造器需要传入defaultFilterProcessesUrl，用于指定这个filter拦截哪个url。
        //它依赖OAuth2RestTemplate来获取token
        //还依赖ResourceServerTokenServices进行校验token
        String defaultFilterProcessesUrl = "/oauth/github/callback";
        OAuth2ClientAuthenticationProcessingFilter githubFilter = new OAuth2ClientAuthenticationProcessingFilter(defaultFilterProcessesUrl);
        //对rest template的封装，为获取token等提供便捷方法
        //DefaultUserInfoRestTemplateFactory实例了OAuth2RestTemplate,这个提供了OAuth2RestTemplate
        OAuth2RestTemplate githubTemplate = new OAuth2RestTemplate(github(), oauth2ClientContext);
        githubFilter.setRestTemplate(githubTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(githubResource().getUserInfoUri(), github().getClientId());
        tokenServices.setRestTemplate(githubTemplate);
        githubFilter.setTokenServices(tokenServices);
        return githubFilter;
    }

    @Bean
    @ConfigurationProperties("oauth-client.github")
    public AuthorizationCodeResourceDetails github() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("oauth-client.github.resource")
    public ResourceServerProperties githubResource() {
        return new ResourceServerProperties();
    }
}
