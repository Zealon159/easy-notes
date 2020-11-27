package cn.zealon.notes.security.service;

import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedPrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;

import java.util.Map;

/**
 * @author: zealon
 * @since: 2020/11/26
 */
public class AbstractPrincipalExtractor implements PrincipalExtractor {

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        //OAuth2ClientAuthenticationProcessingFilter
        return null;
    }
}
