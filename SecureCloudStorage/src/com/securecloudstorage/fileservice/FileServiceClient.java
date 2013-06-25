package com.securecloudstorage.fileservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.SecretKey;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.securecloudstorage.keystore.KeyStore;

/**
 * A FileServiceClient for uploading one file into {@link GoogleDatastore}.
 * 
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 */
public class FileServiceClient {
	private String url = "https://securecloudservice.appspot.com/upload";
	private HttpClient httpClient;
	private HttpPost httpPost;
	private HttpGet httpGet;

	/**
	 * Protected Constructor.
	 * 
	 * @param url
	 *            The URL of the uploading Servlet entry point.
	 */
	protected FileServiceClient(String url) {
		// Set the URL of the uploading Servlet entry point.
		this.url = url;
		// Create an instance of HttpClient.
		// httpClient = new DefaultHttpClient();
		httpClient = trustEveryoneSslHttpClient();
	}

	/**
	 * @return The URL of the uploading Servlet entry point.
	 */
	public String getURL() {
		return url;
	}

	/**
	 * Set the URL of the uploading Servlet entry point used by the
	 * FileServiceClient.
	 * 
	 * @param url
	 *            The URL of the uploading Servlet entry point.
	 */
	public void setURL(String url) {
		this.url = url;
	}

	/**
	 * Submit <code>multipart/form-data</code> via HTTP POST method.
	 * 
	 * @param fileId
	 *            The primary key in Google datastore, and is provided as
	 *            String.
	 * @param fileOwner
	 *            The field name in Google datastore, and is provided as String.
	 * @param fileName
	 *            The field name in Google datastore, and is provided as String.
	 * @param fileData
	 *            The field name in Google datastore, and is provided as byte
	 *            array of file's content.
	 * 
	 * @return The size of successfully uploaded file, <div>or -1 if the server
	 *         throws FileUploadException or IOException, </div> <div>or -2 if
	 *         the server says that uploaded fields are empty or wrong formats,
	 *         </div> <div>or -3 if the server says that the client isn't in
	 *         allowed client IPs list, </div> <div>or -4 if the server doesn't
	 *         return correct HTTP status code: 200 OK (HTTP/1.0 - RFC 1945),
	 *         </div> <div>or -5 if the client connects to the server fail,
	 *         </div> <div>or -6 if the server doesn't return any data.</div>
	 * 
	 * @throws IOException
	 */
	public int submit(String fileId, String fileOwner, String fileName,
			byte[] fileData, String contentType, SecretKey key)
			throws IOException {

		httpPost = new HttpPost(url);

		MultipartEntity multipart = new MultipartEntity();
		multipart.addPart("fileId", new StringBody(fileId));
		multipart.addPart("fileOwner", new StringBody(fileOwner));

		File file = new File(fileName);
		EncryptedFileBody fb = null;
		if (key == null) {
			multipart.addPart("upfile", new FileBody(file, contentType));
		} else {
			fb = new EncryptedFileBody(file, key);
			multipart.addPart("upfile", fb);
		}
		httpPost.setEntity(multipart);

		// Submit
		try {
			// Execute the method.

			HttpResponse response = httpClient.execute(httpPost);

			if (key != null)
				KeyStore.getInstance().addEntry(key, fb.getIv(), fileOwner,
						fileId);
			else
				KeyStore.getInstance().addEntry(fileOwner, fileId);

			// Read the response status.
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				// Read the response body.
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream stream = entity.getContent();
					String responseBody = IOUtils.toString(stream);
					return Integer.parseInt(responseBody); // return ?
				} else {
					return -6; // return -6 (server problem)
				}
			} else {
				// Upload fail
				System.err.println("Error: " + statusLine);
				return -4; // return -4 (HTTP status codes problem)
			}
		} catch (IOException e) {
			// Upload fail
			System.err.println("Fatal transport error: " + e.getMessage());
			return -5; // return -5 (connection problem)
		}
	}

	public File download(String fileid) throws IOException {
		System.out.println("URL:" + url + "?id=" + fileid);
		httpGet = new HttpGet(url + "?id=" + fileid);

		File file = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);

			// Read the response status.
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream stream = entity.getContent();

					byte[] buffer = IOUtils.toByteArray(stream);
					file = File.createTempFile("tempfile", ".tmp");
					FileUtils.writeByteArrayToFile(file, buffer);
					return file;
				} else {
					return null; // return -6 (server problem)
				}
			} else {
				System.err.println("Error: " + statusLine);
				return null; // return -4 (HTTP status codes problem)
			}
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			return null; // return -5 (connection problem)
		}
	}

	/**
	 * When HttpClient instance is no longer needed, shut down the connection
	 * manager to ensure immediate deallocation of all system resources
	 */
	public void releaseConnection() {
		httpClient.getConnectionManager().shutdown();
	}

	private static HttpClient trustEveryoneSslHttpClient() {

		HttpClient httpClient = new DefaultHttpClient();
		SSLSocketFactory sf = (SSLSocketFactory) httpClient
				.getConnectionManager().getSchemeRegistry().getScheme("https")
				.getSocketFactory();
		sf.setHostnameVerifier(new AllowAllHostnameVerifier());

		return httpClient;

	}
}
