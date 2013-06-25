package com.securecloudstorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
//import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the
 * request. After the request is authorized by the user, the callback URL will
 * be intercepted here.
 * 
 */
public class OAuthAccessTokenActivity extends Activity {

	final String TAG = getClass().getName();
	static String authorizationCode;
	private String accessToken;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);

		
		

	}

	@Override
	protected void onResume() {
		super.onResume();
		WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setVisibility(View.VISIBLE);
		setContentView(webview);

		/* WebViewClient must be set BEFORE calling loadUrl! */
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap bitmap) {
				System.out.println("onPageStarted : " + url);
				
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				System.out.println("onPageFinished : " + url);
				authorizationCode = getAuthorizationCode(url, view);
				if(authorizationCode == null){
					//TODO error handling
				}
				else{
					//finish();
				}
			}
		});

		webview.loadUrl(authorizationRequestUrl());
		webview.setInitialScale(75);
	}
	
	
		public String authorizationRequestUrl() {
			String response_type = "code";

			return GoogleOAuth2ClientCredentials.ENDPOINT + "?response_type="
					+ response_type + "&client_id="
					+ GoogleOAuth2ClientCredentials.CLIENT_ID + "&redirect_uri="
					+ GoogleOAuth2ClientCredentials.REDIRECT_URI + "&scope="
					+ GoogleOAuth2ClientCredentials.SCOPE;
		}

	
		public String extractCodeFromUrl(String url) {
			return url.substring(
					GoogleOAuth2ClientCredentials.REDIRECT_URI.length() + 7,
					url.length());
		}

		
		public String getAuthorizationCode(String url, View view) {
			String authorizationCode = null;
			if (url.startsWith(GoogleOAuth2ClientCredentials.REDIRECT_URI)) {
				view.setVisibility(View.INVISIBLE);
				if (url.indexOf("code=") != -1) {
					authorizationCode = extractCodeFromUrl(url);
					System.out.println("#### AUTHORIZATION CODE: "
							+ authorizationCode);
				} else if (url.indexOf("error=") != -1) {
					// TODO errorhandling
					return null;
				}
			}
			return authorizationCode;
		}
}
