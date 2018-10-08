package epmtl.github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
@SuppressWarnings("unused")
// Order = 3 by default
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(ResourceServerConfig.class);

    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices());
    }

    @Bean
    @SuppressWarnings("WeakerAccess")
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    @SuppressWarnings("WeakerAccess")
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }


    @Bean
    @Primary
    @SuppressWarnings("WeakerAccess")
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }


    @Override
    public void configure(final HttpSecurity http) {
        try {
            // Note: covers only /api/v1/**  - users should be authenticated
            http
                    // No session will be created (No JSESSIONID expected)
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .antMatcher("/api/v1/**")
                    .authorizeRequests()
                    .antMatchers("/api/v1/read_access")
                    .access("#oauth2.hasScope('read')")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/v1/write_access")
                    .access("#oauth2.hasScope('write')")
                    //.authenticated()
                    ;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
