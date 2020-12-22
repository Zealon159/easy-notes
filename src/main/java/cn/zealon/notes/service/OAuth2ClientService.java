package cn.zealon.notes.service;

import cn.zealon.notes.security.config.OAuth2ClientProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * OAuth2客户端服务
 * @author: zealon
 * @since: 2020/12/21
 */
@Service
public class OAuth2ClientService {

    @Autowired
    private OAuth2ClientProperties auth2ClientProperties;

    /** 获取全部客户端信息 */
    public Map<String, OAuth2ClientProperties.OAuth2Client> getClients() {
        return auth2ClientProperties.getClients();
    }

    /**
     * 获取指定客户端信息
     * @param clientId
     * @return
     */
    public OAuth2ClientProperties.OAuth2Client getOneClient(String clientId) {
        if (StringUtils.isNotBlank(clientId)) {
            Map<String, OAuth2ClientProperties.OAuth2Client> clients = this.getClients();
            return clients.get(clientId);
        }
        return null;
    }
}
