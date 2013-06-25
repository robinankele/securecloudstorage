package com.securecloudstorage.fileservice;

/**
 * A factory for creating a singleton instance of {@link FileServiceClient}.
 * 
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 */
public class FileServiceClientFactory {
	private static FileServiceClient fileServiceClient;

	/** This class should not be instantiated. */
	private FileServiceClientFactory() {
	}

	/**
	 * Create a new {@link FileServiceClient} instance from the supplied URL
	 * parameter.
	 * 
	 * @param url
	 *            The URL of the uploading Servlet entry point.
	 * 
	 * @return an instance of {@link FileServiceClient}.
	 */
	public static synchronized FileServiceClient getFileServiceClient(String url) {
		if (fileServiceClient == null)
			fileServiceClient = new FileServiceClient(url);
		return fileServiceClient;
	}
}