package com.securecloudstorage;

import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.securecloudstorage.keystore.KeyStore;
import com.securecloudstorage.management.FileHandler;
import com.securecloudstorage.qrcode.Contents;
import com.securecloudstorage.qrcode.QRCodeEncoder;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class ManagementActivity extends Activity {

	private Button bn_upload;
	private Button bn_download;
	private Button bn_export;
	private Button bn_import;
	private CheckBox cb_encrypt;
	private CheckBox cb_decrypt;
	public static EditText tf_filename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_managment);
		
		initGUI();
		handleGUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.managment, menu);
		return true;
	}

	private void initGUI() {
		bn_upload = (Button) this.findViewById(R.id.bn_upload);
		bn_download = (Button) this.findViewById(R.id.bn_download);
		bn_export = (Button) this.findViewById(R.id.bn_export);
		bn_import = (Button) this.findViewById(R.id.bn_import);
		tf_filename = (EditText) this.findViewById(R.id.tb_filename);
		tf_filename.setText("");
		cb_encrypt = (CheckBox) this.findViewById(R.id.cb_encrypt);
		cb_decrypt = (CheckBox) this.findViewById(R.id.cb_decrypt);
	}

	private void handleGUI() {

		bn_upload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cb_encrypt.isChecked()) {
					System.out.println("Encryption");

					try {
						FileHandler.getInstance().fileupload(true,
								getApplicationContext());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					System.out.println("No Encryption");
					try {
						FileHandler.getInstance().fileupload(false,
								getApplicationContext());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		bn_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cb_decrypt.isChecked()) {
					System.out.println("Decryption");
					try {
						FileHandler.getInstance().filedownload(true,
								getApplicationContext());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("No Decryption");
					try {
						FileHandler.getInstance().filedownload(false,
								getApplicationContext());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		bn_import.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
					startActivityForResult(intent, 0);
				} catch (Exception e) {
					Uri marketUri = Uri
							.parse("market://details?id=com.google.zxing.client.android");
					Intent marketIntent = new Intent(Intent.ACTION_VIEW,
							marketUri);
					startActivity(marketIntent);
				}
			}
		});
		
		final Intent qrcode = new Intent(this, QRCodeActivity.class);		
		bn_export.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String export = "";
				try {
					export = KeyStore.getInstance().exportKeyStore();
				} catch (IOException e) {
				}
				System.out.println("EXPORT STRING " + export);
				QRCodeActivity.setQrcode(export);
				startActivity(qrcode);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				KeyStore.getInstance().importKeyStore(contents,
						MainActivity.getOwner(), getApplicationContext());
			}
		}
	}

}
