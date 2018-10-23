package epmtl.github;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
@Order(101)
public class ResourceServerWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomPasswordEncoder passwordEncoder;

    @Bean
    @SuppressWarnings("WeakerAccess")
    public UserDetailsService resourceServerUserDetailsService() {
        final String ADMIN_USERNAME = "resource_admin";
        final String ADMIN_PASSWORD = "pass123";
        final String USER_USERNAME = "resource_user";
        final String USER_PASSWORD = "pass123";
        final String ROLE_ADMIN = "ADMIN";
        final String ROLE_USER = "USER";
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
                    .antMatchers("/unsecured")
                    .permitAll()
                    .antMatchers("/denied")
                    .denyAll()
                    .antMatchers("/user")
                    .authenticated()
                .and()
                    .httpBasic()
        ;

    }

}