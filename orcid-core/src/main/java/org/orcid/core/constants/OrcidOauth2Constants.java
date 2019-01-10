package org.orcid.core.constants;

public class OrcidOauth2Constants {
    
    public static final String TOKEN_VERSION = "tokenVersion";
    public static final String NON_PERSISTENT_TOKEN = "0";
    public static final String PERSISTENT_TOKEN = "1";    
    public static final String GRANT_PERSISTENT_TOKEN = "grantPersistentToken";
    public static final String PERSISTENT = "persistent";
    public static final String IS_PERSISTENT = "isPersistent";
    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String DATE_CREATED = "date_created";
    public static final String CLIENT_ID = "client_id";
    public static final String ORCID = "orcid";
    public static final String NAME = "name";
    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String SCOPE_PARAM = "scope";
    public static final String STATE_PARAM = "state";
    public static final String RESPONSE_TYPE_PARAM = "response_type";
    public static final String REDIRECT_URI_PARAM = "redirect_uri";
    public static final String JUST_REGISTERED = "justRegistered";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String AUTHORIZATION = "authorization";
    public static final String REVOKE_OLD = "revoke_old";
    public static final String EXPIRES_IN = "expires_in";
    public static final String TOKEN_ID = "tokenId";
    
    //openid connect
    public static final String NONCE = "nonce";
    public static final String MAX_AGE = "max_age";
    public static final String ID_TOKEN = "id_token";
    public static final String PROMPT = "prompt";
    public static final Object PROMPT_CONFIRM = "confirm";    
    public static final Object PROMPT_LOGIN = "login";
    public static final Object PROMPT_NONE = "none";    
    public static final String AUTH_TIME = "auth_time";

    //OAuth 2 screens
    public static final String OAUTH_2SCREENS = "OAUTH_2SCREENS";
    public static final String OAUTH_QUERY_STRING = "queryString";
    public static final String IMPLICIT_GRANT_TYPE = "implicit";
    public static final String IMPLICIT_TOKEN_RESPONSE_TYPE = "token";
    
    //IETF exchange
    public static final String IETF_EXCHANGE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";
    public static final String IETF_EXCHANGE_ACCESS_TOKEN = "urn:ietf:params:oauth:token-type:access_token";
    public static final String IETF_EXCHANGE_ID_TOKEN = "urn:ietf:params:oauth:token-type:id_token";
    public static final String IETF_EXCHANGE_SUBJECT_TOKEN = "subject_token";
    public static final String IETF_EXCHANGE_SUBJECT_TOKEN_TYPE = "subject_token_type";
    public static final String IETF_EXCHANGE_REQUESTED_TOKEN_TYPE = "requested_token_type";
    
    public static final String CODE_RESPONSE_TYPE = "code";
}
