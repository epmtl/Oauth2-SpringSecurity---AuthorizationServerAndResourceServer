# Oauth2 Authorization Server and Resource Server

## Context

This project is an example of both Authorization and Resource Servers running on the same instance 
(This is not the recommended solution by the way but it is convenient one for testing and discovery).  
The goal is to try getting an OAuth 2 Access Token and use it to access secured resources.  


## Implementation details

Multiple flows are supported.

### Grant Flows

#### Authorization_code Grant
Usage: An external app would like to access a resource owner Google or Facebook account).
 
Steps are like these:
1. A (new) client application requests access to a resource providing its client_id and registered redirect_uri to `oauth/authorize` api.
(Note that the client should be authenticated to access the api).
2. Authorization Server redirects to the redirection URL which contains an authorization code as parameter.
(Note that the consent phase of the resource owner is skipped due to the `autoApprove(true)`).
3. Client requests the generation of an access token by providing the client_id and the previous code using the `oauth/token` api.
4. Server authenticates the client and generates the access_token and refresh token (optional).
5. Access_token is used by the client in the authorization HTTP header as bearer to access secured api (`/api/v1`).

#### Password Grant
Usage: A trusted app developed/owned by the same entity as the auth server or resource owner.

Steps are like these:
1. Client (trusted system) requests the generation of an access token by providing the client_id and a resource owner username and password.
(Note that `/oauth/token` requires to be fully authenticated, thus the client_id and client_secret are used as HTTP Basic Auth credentials).
2. Server authenticates the resource owner with the Authentication Manager and the client_id against its client DB and then generates the access_token and refresh token (optional).
3. Access_token is used by the client in the authorization HTTP header as bearer to access secured api (`/api/v1`).

#### Client_credentials Grant
Usage: An internal client accessing an API.

Steps are like these:
1. Client (trusted system) requests the generation of an access token by providing the client_id and client_secret.
(Note that `/oauth/token` requires to be fully authenticated, thus the client_id and client_secret are used as HTTP Basic Auth credentials).
2. Server authenticates the client and then generates the access_token and refresh token (optional).
3. Access_token is used by the client in the authorization HTTP header as bearer to access secured api (`/api/v1`).

#### Implicit Grant
TBD.


### Security Filters
HTTP Security is implemented in 3 differents files (2 extending WebSecurityConfigurerAdapter):
- AuthServerSecurityConfig is triggered first and secure the access to `oauth/authorize` api.
- ResourceServerConfig contains its own HTTP Security filter to restrict access to `api/v1`.
- WebSecurityConfig is the last one giving access to the remaining apis (`/unsecure`, `/auth_code`, `/denied` and `/user`).
(Note: `/user` requires to be authenticated against the resource server ! - Intended to test various levels of authentication)

### Limitations
- No HTTPS enforcement.
- Implicit Grant type not tested yet.
- Refresh Token usage not tested yet.
- Session state (JSESSIONID) when accessing Oauth/* APIs still active (SessionCreationPolicy.STATELESS does not work yet). 


## Testing
Postman requests are available in the `/postman` directory.
You can import them into your own postman for testing.

Few tests are available in ApplicationTest.java


### References

**OAuth2 specification:** https://tools.ietf.org/html/rfc6749



