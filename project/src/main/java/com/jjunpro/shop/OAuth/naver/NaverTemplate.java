package com.jjunpro.shop.OAuth.naver;

import com.jjunpro.shop.OAuth.kakao.KakaoOAuthConstants;
import java.net.URI;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class NaverTemplate extends AbstractOAuth2ApiBinding {

    public NaverTemplate(String accessToken) {
        super(accessToken);
    }

    public <T> T fetchObject(Class<T> type, MultiValueMap<String, String> queryParameters) {
        URI uri = URIBuilder.fromUri(NaverOAuthConstants.USER_INFO_URI).queryParams(queryParameters).build();

        return getRestTemplate().getForObject(uri, type);
    }

    public <T> T fetchObject(Class<T> type, String... fields) {
        MultiValueMap<String, String> queryParameters = new LinkedMultiValueMap<String, String>();
        if(fields.length > 0) {
            String joinedFields = join(fields);
            queryParameters.set("property_keys", joinedFields);
        }
        return fetchObject(type, queryParameters);
    }

    private String join(String[] strings) {
        StringBuilder builder = new StringBuilder();
        if(strings.length > 0) {
            builder.append(strings[0]);
            for (int i = 1; i < strings.length; i++) {
                builder.append("," + strings[i]);
            }
        }
        return builder.toString();
    }
}
