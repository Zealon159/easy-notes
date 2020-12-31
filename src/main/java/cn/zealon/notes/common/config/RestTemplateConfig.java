package cn.zealon.notes.common.config;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rest模板配置
 * @author: zealon
 * @since: 2020/8/17
 */
@Configuration
public class RestTemplateConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Bean("restTemplate")
    public RestTemplate restTemplate() {
        // 基于HttpClient
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setConnectTimeout(10000);
        httpComponentsClientHttpRequestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        // 乱码处理
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters().stream().map(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                return new StringHttpMessageConverter(Charset.forName("UTF-8"));
            }
            return httpMessageConverter;
        }).collect(Collectors.toList());
        restTemplate.setMessageConverters(messageConverters);
        // 拦截器统一打印日志
        restTemplate.setInterceptors(Lists.newArrayList(new RestTemplateConfig.MyRequestInterceptor()));
        return restTemplate;
    }

    class MyRequestInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) {
            HttpMethod method = request.getMethod();
            java.net.URI uri = request.getURI();
            String host = uri.getHost();
            String path = uri.getPath();
            String query = uri.getQuery();
            String body = new String(bytes, StandardCharsets.UTF_8);
            LOGGER.info("Request URL = [{}] http://{}{}?{}", method, host, path, query);
            if (HttpMethod.POST == method) {
                LOGGER.info("Request Body = {}", body);
            }
            ClientHttpResponse response = null;
            try {
                response = execution.execute(request, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }
}