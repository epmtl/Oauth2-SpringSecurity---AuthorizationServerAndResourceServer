package epmtl.github;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
@EnableWebSecurity
@SuppressWarnings("unused")
// Order = 100 by default
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final String ROLE_ADMIN = "ADMIN";
    private final String ROLE_USER = "USER";

    @Bean
    @SuppressWarnings("WeakerAccess")
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername("admin")
                .password(passwordEncoder().encode("password"))
                .roles(ROLE_ADMIN).build());
        manager.createUser(User
                .withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles(ROLE_USER).build());
        return manager;
    }


    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                // No session will be created (No JSESSIONID expected)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .antMatcher("/**")
                .authorizeRequests()
                // unsecured API
                .antMatchers("/unsecured")
                .permitAll()
                // redirection URL
                .antMatchers("/auth_code")
                .permitAll()
                .antMatchers("/denied")
                .denyAll()
                .antMatchers("/user")
                .hasAnyRole(ROLE_ADMIN,ROLE_USER)
                .and()
                .httpBasic()
        ;

    }

}