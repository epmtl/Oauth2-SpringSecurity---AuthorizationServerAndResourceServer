package epmtl.github;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@SuppressWarnings("unused")
public class ResourceServerWebController {
    private Logger logger = LoggerFactory.getLogger(ResourceServerWebController.class);

    @RequestMapping("/unsecured")
    public String unsecured(){
        final String text = "This API is unsecured.";
        logger.info(text);
        return text;
    }

    // APIs Below should be accessed as authenticated
    @RequestMapping("/user")
    public String userDetails(Principal principal) {
        if (principal != null) {
            logger.info(principal.toString());
            return principal.toString();
        } else {
            return "No user details.";
        }
    }

    @RequestMapping("/auth_code")
    public String oauthCode(HttpServletRequest httpServletRequest,
                            HttpServletResponse httpServletResponse,
                            ModelMap modelMap,
                            HttpSession httpSession){
        // Returns the code given by the oauth/authorize
        // Used to request token with oauth/token
        return httpServletRequest.getParameter("code");
    }

    // API below is OAuth Secured
    @RequestMapping("/api/v1/read_access")
    public String oauthReadAccess() {
        final String text = "This API is secure with a Read Scope";
        logger.info(text);
        return text;
    }

    @RequestMapping("/api/v1/write_access")
    public String oauthWriteAccess() {
        final String text = "This API is secure with a Write Scope";
        logger.info(text);
        return text;
    }

    @RequestMapping("/denied")
    public String denied() {
        final String text = "This API Should not be available.";
        logger.info(text);
        return text;
    }


}