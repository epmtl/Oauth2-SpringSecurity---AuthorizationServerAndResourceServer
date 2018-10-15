# Oauth2 Authorization Server and Resource Server

## Context

This project is an example of both Authorization and Resource Servers running on the same instance 
(This is not the recommended solution by the way but it is convenient for testing).  
The goal is to try getting an OAuth Access Token and use it to access secured resources.  


## Implementation details

### Flow

#### Authorization_code Grant 
Steps are like these:
1. A (new) client requests access to a resource providing its client_id and registered redirect_uri to `oauth/authorize` api.
(Note that the client should be authenticated to access the api)
2. Authorization Server redirects to the redirection URL which contains an authorization code as parameter.
(Note that the consent phase of the resource owner is skipped due to the `autoApprove(true)`)
3. Client requests the generation of an access token by providing the client_id and the previous code using the `oauth/token` api.
4. Server authenticates the client and generates the access_token and refresh token (optional).
5. Access_token is used by the client in the authorization http header as bearer to access secured api (/api/v1).

#### Password Grant
Steps are like these:
1. Client (e.g. Trusted app or Gateway) requests the generation of an access token by providing the client_id and a resource owner username and password.
(Note that `/oauth/token` requires to be fully authenticated, thus meaning to use basic auth too.)
2. Server authenticates the resource owner with the Authentication Manager and the client and then generates the access_token and refresh token (optional).
3. Access_token is used by the client in the authorization http header as bearer to access secured api (/api/v1).


### Security Filters
HTTP Security is implemented in 3 differents files (2 extending WebSecurityConfigurerAdapter):
- AuthServerSecurityConfig is triggered first and secure the access to `oauth/authorize` api.
- ResourceServerConfig contains its own HTTP Security filter to restrict access to `api/v1`.
- WebSecurityConfig is the last one giving access to the remaining apis (`/unsecure`, `/auth_code`, `/denied`).

### Limitations
- Authorized Grant Types used and tested is authorization_code (refresh_token and client_credentials were not tested).
- There is still something to fix which is the removal of the session state (JSESSIONID) when accessing Oauth/* APIs.
(SessionCreationPolicy.STATELESS does not work... yet). 

## Testing
Postman requests are available in the `/postman` directory.
You can import them into your own postman for testing.

- `OAuth2_Token admin` and `OAuth2_Token user` requests have pre-scripts that trigger authorize requests before.
They will generate a JWT token to be used as bearer.
- You can then use the bearer in `OAuth2 api/v1/read_access` or `OAuth2 api/v1/write_access` and get the results
depending on the user (a simple `user` has no write access).

Few tests are available in ApplicationTest.java


### References

**OAuth2 specification:** https://tools.ietf.org/html/rfc6749



