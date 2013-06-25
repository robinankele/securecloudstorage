package com.securecloudstorage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.securecloudstorage.qrcode.Contents;
import com.securecloudstorage.qrcode.QRCodeEncoder;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.ImageView;

public class QRCodeActivity extends Activity {

	private static String qrcode  = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);
		
		// Encode with a QR Code image
		System.out.println("QRCODE = " + qrcode);
		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrcode, null,
				Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
				500);

		try {
			Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
			ImageView view = (ImageView) findViewById(R.id.qrcode);
			view.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.qccode, menu);
		return true;
	}

	public static String getQrcode() {
		return qrcode;
	}

	public static void setQrcode(String qrcode) {
		QRCodeActivity.qrcode = qrcode;
	}

}
