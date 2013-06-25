package com.securecloudstorage;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FileDownloadActivity extends Activity {

	private String selectedItem;
	private static StableArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_download);

		final ArrayList<String> files = new ArrayList<String>();
		final ListView listview = (ListView) findViewById(R.id.lv_filelist);

		adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, files);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				selectedItem = (String) parent.getItemAtPosition(position);
				System.out.println("ITEM SELECTED : " + selectedItem);
				ManagementActivity.tf_filename.setText(selectedItem);
				MainActivity.tabHost.setCurrentTab(1);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_download, menu);
		return true;
	}

	public static void setItem(String item, String filename) {
		if (item == null)
			return;

		adapter.add(item);
		adapter.setItem(item);
	}
}
