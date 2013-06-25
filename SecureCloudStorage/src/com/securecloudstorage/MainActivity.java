package com.securecloudstorage;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * The Main Activity.
 * 
 * This activity starts up the RegisterActivity immediately, which communicates
 * with your App Engine backend using Cloud Endpoints. It also receives push
 * notifications from backend via Google Cloud Messaging (GCM).
 * 
 * Check out RegisterActivity.java for more details.
 */
public class MainActivity extends TabActivity {
	
	public static TabHost tabHost;
	public static String owner = "robin.ankele";	// FIXME
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//login();
		initGUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void login(){
		Intent login = new Intent(this, OAuthAccessTokenActivity.class);
		startActivity(login);
	}
	
	private void initGUI(){
		tabHost = getTabHost();
        
        // Tab for Upload
        TabSpec upload = tabHost.newTabSpec("Upload");
        // setting Title and Icon for the Tab
        upload.setIndicator("", getResources().getDrawable(R.drawable.upload));
        Intent uploadIntent = new Intent(this, FileUploadActivity.class);
        upload.setContent(uploadIntent);
        
        // Tab for Management
        TabSpec management = tabHost.newTabSpec("Management");       
        management.setIndicator("", getResources().getDrawable(R.drawable.fileicon));
        Intent managementIntent = new Intent(this, ManagementActivity.class);
        management.setContent(managementIntent);
         
        // Tab for Download
        TabSpec download = tabHost.newTabSpec("Download");       
        download.setIndicator("", getResources().getDrawable(R.drawable.download));
        Intent downloadIntent = new Intent(this, FileDownloadActivity.class);
        download.setContent(downloadIntent);
         
        // Adding all TabSpec to TabHost
        tabHost.addTab(upload); 
        tabHost.addTab(management);
        tabHost.addTab(download); 
        
        tabHost.setCurrentTab(0);
        tabHost.setCurrentTab(2);
        tabHost.setCurrentTab(1);
	}

	public static String getOwner() {
		return owner;
	}
}
