import epmtl.github.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
@SuppressWarnings("unused")
public class ApplicationTest {

    private Logger logger = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        logger.info("******* SETUP *******");
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain).build();
    }

    @SuppressWarnings("SameParameterValue")
    private String obtainAuthorization(String username,
                                       String password,
                                       String client_id,
                                       String response_type,
                                       String redirect_uri) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", client_id);
        params.add("response_type", response_type);
        params.add("redirect_uri", redirect_uri);

        ResultActions result
                = mockMvc.perform(post("/oauth/authorize")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Basic " +
                        Base64Utils.encodeToString((username  +  ":" + password).getBytes()))
                );
        //.andExpect(redirectedUrlPattern("/auth_code"));

        ResultActions redirected_result
                = mockMvc.perform(post(
                        Objects.requireNonNull(result.andReturn().getResponse().getRedirectedUrl()))
        );

        logger.info("********* Redirection content: " +
                redirected_result.andReturn().getResponse().getContentAsString());
        return redirected_result.andReturn().getResponse().getContentAsString();
    }

    @SuppressWarnings("SameParameterValue")
    private String obtainAccessToken(String username,
                                     String password,
                                     String code,
                                     String redirect_uri) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", redirect_uri);

        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                .params(params)
                .header(
                        HttpHeaders.AUTHORIZATION, "Basic " +
                        Base64Utils.encodeToString((username  +  ":" + password).getBytes())
                )
                .accept("application/json;charset=UTF-8")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }


    @Test
    public void givenNoToken_whenGetSecureRequest_thenUnauthorized() throws Exception {
        logger.info("******* TEST1 *******");
        mockMvc.perform(get("/api/v1/read_access"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenNoToken_whenGetUnSecureRequest_thenOK() throws Exception {
        logger.info("******* TEST2 *******");
        mockMvc.perform(get("/unsecured"))
                .andExpect(status().isOk());
    }

    @Test
    public void givenNoToken_whenGetDeniedRequest_thenDenied() throws Exception {
        logger.info("******* TEST3 *******");
        mockMvc.perform(get("/denied"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void givenToken_whenPostGetSecureRequest_thenOk() throws Exception {
        logger.info("******* TEST4 *******");
        final String redirect_uri = "http://localhost:8080/auth_code";
        String authorizationCode = obtainAuthorization(
                "admin",
                "password",
                "admin_client",
                "code",
                redirect_uri
        );
        logger.info("******* Authorization Code = " + authorizationCode);

        String accessToken = obtainAccessToken(
                "admin_client",
                "password_client",
                authorizationCode,
                redirect_uri
        );
        logger.info("******* Access Token = " + accessToken);

        mockMvc.perform(get("/api/v1/read_access")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
        .andExpect(content().string("Secured Read API by Scope"));
    }


}
