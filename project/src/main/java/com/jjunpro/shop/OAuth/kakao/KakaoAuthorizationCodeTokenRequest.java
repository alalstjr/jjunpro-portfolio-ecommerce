package com.jjunpro.shop.OAuth.kakao;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public class KakaoAuthorizationCodeTokenRequest extends AuthorizationCodeTokenRequest {

    /**
     * @param transport HTTP transport
     * @param jsonFactory JSON factory
     * @param clientId client identifier issued to the client during the registration process
     * @param clientSecret client secret
     * @param code authorization code generated by the authorization server
     * @param redirectUri redirect URL parameter matching the redirect URL parameter in the
     *        authorization request (see {@link #setRedirectUri(String)}
     */
    public KakaoAuthorizationCodeTokenRequest(HttpTransport transport, JsonFactory jsonFactory,
            String clientId, String clientSecret, String code, String redirectUri) {
        this(transport, jsonFactory, KakaoOAuthConstants.TOKEN_SERVER_URL, clientId, clientSecret,
                code, redirectUri);
    }

    /**
     * @param transport HTTP transport
     * @param jsonFactory JSON factory
     * @param tokenServerEncodedUrl token server encoded URL
     * @param clientId client identifier issued to the client during the registration process
     * @param clientSecret client secret
     * @param code authorization code generated by the authorization server
     * @param redirectUri redirect URL parameter matching the redirect URL parameter in the
     *        authorization request (see {@link #setRedirectUri(String)}
     *
     * @since 1.12
     */
    public KakaoAuthorizationCodeTokenRequest(HttpTransport transport, JsonFactory jsonFactory,
            String tokenServerEncodedUrl, String clientId, String clientSecret, String code,
            String redirectUri) {
        super(transport, jsonFactory, new GenericUrl(tokenServerEncodedUrl), code);
        setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret));
        setRedirectUri(redirectUri);
    }

}
