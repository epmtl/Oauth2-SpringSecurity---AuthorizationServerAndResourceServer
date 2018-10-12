# Oauth2 Authorization Server and Resource Server

## Context

This project is an example of both Authorization and Resource Servers running on the same instance 
(This is not the recommended solution by the way but it is convenient for testing).  
The goal is to try getting an OAuth Access Token and use it to access secured resources.  


## Implementation details

### Flow

#### Authorization_code Grant 
Steps are like these:
1. Entity requests access to a resource for a specific client_id using `oauth/authorize` api.
2. Authorization Server redirects to the redirection URL which contains a code as parameter.
3. Entity requests the generation of an access token by providing the client_id and the previous code using the `oauth/token` api.
4. Server generates the access_token and refresh token (optional).
5. access_token is used in the authorization http header as bearer to access secured api (/api/v1).

#### Password Grant
Steps are like these:
1. Entity requests the generation of an access token by providing the client_id and its username and password.
(Note that `/oauth/token` requires to be fully authenticated, thus meaning to use basic auth too.)
2. Server generates the access_token and refresh token (optional).
3. access_token is used in the authorization http header as bearer to access secured api (/api/v1).


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



