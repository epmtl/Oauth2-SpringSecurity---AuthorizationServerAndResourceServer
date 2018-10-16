package epmtl.github;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
@EnableWebSecurity
@SuppressWarnings("unused")
// Order = 100 by default
public class ResourceServerWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final String ROLE_ADMIN = "ADMIN";
    private final String ROLE_USER = "USER";

    @Autowired
    private CustomPasswordEncoder passwordEncoder;

    @Bean
    @SuppressWarnings("WeakerAccess")
    public UserDetailsService resourceServerUserDetailsService() {
        final String ADMIN_USERNAME = "admin";
        final String ADMIN_PASSWORD = "password";
        final String USER_USERNAME = "user";
        final String USER_PASSWORD = "password";
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername(ADMIN_USERNAME)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .roles(ROLE_ADMIN).build());
        manager.createUser(User
                .withUsername(USER_USERNAME)
                .password(passwordEncoder.encode(USER_PASSWORD))
                .roles(ROLE_USER).build());
        return manager;
    }

    @Bean
    @SuppressWarnings("WeakerAccess")
    public DaoAuthenticationProvider resourceServerAuthenticationProvider() {
        DaoAuthenticationProvider authProvider
                = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(resourceServerUserDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(resourceServerAuthenticationProvider());
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