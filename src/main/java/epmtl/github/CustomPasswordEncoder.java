package epmtl.github;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
class CustomPasswordEncoder extends BCryptPasswordEncoder {
}
