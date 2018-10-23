package epmtl.github;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@SuppressWarnings("unused")
@Order(100)//Order = 3 by default
public class RedirectUriWebSecurityConfig  extends WebSecurityConfigurerAdapter {


        protected void configure (HttpSecurity http) throws Exception {
            http
                    .csrf()
                    .disable();

            http
                    // No session will be created (No JSESSIONID expected)
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .antMatcher("/auth_code")
                    .authorizeRequests()
                    .anyRequest()
                    .permitAll()
            ;
        }
}