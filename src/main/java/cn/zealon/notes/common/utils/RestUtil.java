package cn.zealon.notes.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Rest请求工具
 * @author: zealon
 * @since: 2021/1/18
 */
public class RestUtil {

    /** 默认请求头 */
    public static HttpHeaders getDefaultHttpRequestHeaders(String token){
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(mediaType);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        if (StringUtils.isNotBlank(token)) {
            headers.add("Authorization", "Bearer " + token);
        }
        return headers;
    }
}
