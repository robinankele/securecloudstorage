package com.securecloudstorage;

public class GoogleOAuth2ClientCredentials {
	/** Value of the "Client ID" shown under "Client ID for installed applications". */
	public static final String ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
	
	/** Value of the "Client ID" shown under "Client ID for installed applications". */
	public static final String CLIENT_ID = "89139955549.apps.googleusercontent.com";

	/** Value of the "Client secret" shown under "Client ID for installed applications". */
	public static final String CLIENT_SECRET = "ORnFqmynLjXATrfSbLYukr_D";

	/** OAuth 2 scope to use */
	public static final String SCOPE = "https://www.googleapis.com/auth/userinfo.email";

	/** OAuth 2 redirect uri */
	public static final String REDIRECT_URI = "http://localhost";
}
