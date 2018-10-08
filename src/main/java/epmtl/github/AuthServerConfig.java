package epmtl.github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Configuration
@EnableAuthorizationServer
@SuppressWarnings("unused")
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(AuthServerConfig.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @SuppressWarnings("WeakerAccess")
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }

    @Bean
    @SuppressWarnings("WeakerAccess")
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                // TODO: Doesn't seem to work well, still get the JSessionID
                // related to :
                // https://github.com/spring-projects/spring-security-oauth/issues/140
                .addInterceptor(new HandlerInterceptorAdapter() {
                    @Override
                    public void postHandle(HttpServletRequest request,
                                           HttpServletResponse response, Object handler,
                                           ModelAndView modelAndView) {
                        if (modelAndView != null && modelAndView.getView()
                                instanceof RedirectView) {
                            RedirectView redirect = (RedirectView) modelAndView.getView();
                            String url = redirect.getUrl();
                            if (url != null && (url.contains("code=") || url.contains("error="))) {
                                HttpSession session = request.getSession(false);
                                if (session != null) {
                                    logger.info("***** Invalidating the session...");
                                    session.invalidate();
                                }
                            }
                        }
                    }
                });
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // withClient and secret are used as basic Auth.
        clients
                .inMemory()
                .withClient("admin_client")
                .secret(passwordEncoder.encode("password_client"))
                .authorizedGrantTypes(
                        "authorization_code",
                        "client-credentials",
                        "password",
                        "refresh_token")
                .authorities("ROLE_ADMIN")
                .scopes("read", "write", "trust")
                .redirectUris("http://localhost:8080/auth_code")
                .autoApprove(true)
                .accessTokenValiditySeconds(5000)
                .refreshTokenValiditySeconds(50000)
                .and()
                // NOT TESTED YET
                .withClient("user_client")
                .secret(passwordEncoder.encode("password_client"))
                .authorizedGrantTypes(
                        "authorization_code",
                        "refresh_token")
                .authorities("ROLE_USER")
                .scopes("read")
                .redirectUris("http://localhost:8080/auth_code")
                // skip the approval page
                .autoApprove(true)
                .accessTokenValiditySeconds(5000)
                .refreshTokenValiditySeconds(50000);
    }



}