package cn.zealon.notes.security.repository;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

/**
 * @author: zealon
 * @since: 2020/12/1

@Component
public class ExtendClientRegistrationRepository implements ClientRegistrationRepository {

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        ClientRegistration clientRegistration = null;
        if ("qq".equals(registrationId)) {
            clientRegistration = ClientRegistration.withRegistrationId("qq")
                    .clientId("qq")
                    .clientName("QQ")
                    .authorizationUri("")
                    .tokenUri("")
                    .userInfoUri("")
                    .build();
        } else if ("github".equals(registrationId)) {
            clientRegistration = ClientRegistration.withRegistrationId("github").build();
        }
        return clientRegistration;
    }
}
 */