package epmtl.github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AuthServerBasicAuthProvider implements AuthenticationProvider {

    private Logger logger = LoggerFactory.getLogger(AuthServerBasicAuthProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        //TODO: Rework it.
        if (name.equals("admin") && password.equals("password")) {
            logger.info("********* Authentication OK!");
            return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
        } else return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}