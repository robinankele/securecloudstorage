package com.securecloudstorage.management;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.securecloudstorage.fileservice.FileServiceClient;
import com.securecloudstorage.fileservice.FileServiceClientFactory;
import com.securecloudstorage.keystore.KeyStore;
import com.securecloudstorage.fileservice.DecryptedFileBody;
import com.securecloudstorage.keystore.KeyStoreEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;

import com.securecloudstorage.FileDownloadActivity;
import com.securecloudstorage.MainActivity;
import com.securecloudstorage.ManagementActivity;

public class FileHandler {

	private static FileHandler instance;

	private FileHandler() {

	}

	public static FileHandler getInstance() {
		if (instance == null) {
			instance = new FileHandler();
		}
		return instance;
	}

	public void fileupload(boolean isKey, Context context) throws IOException {
		SecretKey key = null;
		String filename = ManagementActivity.tf_filename.getText().toString();

		if (isKey) {
			KeyGenerator kgen;
			try {
				kgen = KeyGenerator.getInstance("AES");
				kgen.init(128);
				key = kgen.generateKey();
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
		}

		String url = "";
		// url = "http://localhost:8888/upload";
		url = "https://securecloudservice.appspot.com/upload";

		Date date = new Date();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><projectDescription><name>FileService</name></projectDescription>";
		String attachmentID = "mail" + String.valueOf(date.getTime());
		FileDownloadActivity.setItem(attachmentID, filename);
		String mimetype = "text/xml";
		byte[] attachmentBytes = xml.getBytes("UTF-8");
		String owner = MainActivity.getOwner();

		// Create an instance of FileServiceClient for Google App Engine
		FileServiceClient client = FileServiceClientFactory
				.getFileServiceClient(url);

		int fileSize = 0;
		fileSize = client.submit(attachmentID, owner, filename,
				attachmentBytes, mimetype, key);

		if (fileSize < 0) {
			// KeyStore.getInstance().removeEntry(attachmentID);
			System.out.println("error upload = " + fileSize);
			Toast.makeText(context, "Fileupload NOT successful!",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "Fileupload successful!",
					Toast.LENGTH_SHORT).show();
		}
		KeyStore.getInstance().listKeyStore();
	}

	public void filedownload(boolean isKey, Context context) throws IOException {
		String fileid = ManagementActivity.tf_filename.getText().toString();
		String url = "";
		// url = "http://localhost:8888/download";
		url = "https://securecloudservice.appspot.com/download";

		// Create an instance of FileServiceClient for Google App Engine
		FileServiceClient client = FileServiceClientFactory
				.getFileServiceClient(url);
		client.setURL(url);

		int fileSize = 0;
		File file = client.download(fileid);
		if (file == null) {
			System.out.println("FileID <" + fileid + "> NOT found!");
			Toast.makeText(context, "FileID <" + fileid + "> NOT found!",
					Toast.LENGTH_LONG).show();
			return;
		}
		fileSize = (int) file.length();

		if (fileSize >= 0) {
			KeyStoreEntry entry = KeyStore.getInstance().getEntry(fileid);

			if (entry == null) {
				System.out.println("FileID <" + fileid + "> NOT found!");
				Toast.makeText(context, "FileID <" + fileid + "> NOT found!",
						Toast.LENGTH_LONG).show();
				return;
			}

			String fileowners = "";
			for (int i = 0; i < entry.getOwners().size(); i++)
				fileowners += entry.getOwners().get(i);

			String owner = MainActivity.getOwner();
			if (!fileowners.contains(owner)) {
				System.out.println("Owner <" + owner + "> NOT valid!");
				Toast.makeText(context, "Owner <" + owner + "> NOT valid!",
						Toast.LENGTH_LONG).show();
				return;
			}

			File file2 = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/down.txt");
			if (isKey == true && entry.getKey() != null) {
				SecretKey secret = entry.getKey();

				DecryptedFileBody dec = new DecryptedFileBody(file, secret);
				dec.setIv(entry.getIv());

				OutputStream out = new FileOutputStream(file2);
				dec.writeTo(out);
			} else {
				FileOutputStream out = new FileOutputStream(file2);
				InputStream input = new FileInputStream(file);
				byte[] buffer = IOUtils.toByteArray(input);
				out.write(buffer);
				out.close();
			}
			Toast.makeText(context,
					"Filedownload successful <" + file.getName() + ">!",
					Toast.LENGTH_LONG).show();
		}
	}
}
