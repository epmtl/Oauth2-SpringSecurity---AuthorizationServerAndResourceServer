package epmtl.github;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@SuppressWarnings("unused")
@Configuration
@EnableWebSecurity
@Order(-1) // Takes precedence over AuthServer Config and other WebSecurityConfigurer
public class AuthServerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthServerBasicAuthProvider authServerBasicAuthProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authServerBasicAuthProvider);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    protected void configure(HttpSecurity http) throws Exception {
        String ROLE_ADMIN = "ADMIN";

        http.csrf().disable();
        http
                // TODO: Doesn't seem to work well, still get the JSessionID
                // No session will be created (No JSESSIONID expected)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // OAuth/authorize should be authenticated as ADMIN user
                .antMatcher("/oauth/authorize")
                .authorizeRequests()
                .anyRequest()
                //.hasRole(ROLE_ADMIN)
                .authenticated()
                .and()
                // required for http Basic Authentication
                .httpBasic()
                ;

    }

}
