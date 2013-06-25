package com.securecloudstorage.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.securecloudstorage.datastore.util.DatastoreUtils;
import com.securecloudstorage.server.fileupload.ProgressListenerImpl;

/**
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 */
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 8367618333138027430L;
	private static final Logger log = Logger.getLogger(FileUploadServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		out.print("<p>Error: The request method <code>"+req.getMethod()+"</code> is inappropriate for the URL <code>"+req.getRequestURI()+"</code></p>");
		out.close();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		log.info("START FILEUPLOAD");

		// See appengine-web.xml
		long maxsize = Long.parseLong(System.getProperty("upload.allowed-maxsize"));
		
		// Check if User-Agent is HTTP client (Apache-HttpClient/4.0.2) or not
		boolean isHttpClient = false; 
		if (req.getHeader("User-Agent").indexOf("HttpClient") > -1)
			isHttpClient = true;
		log.info("ISHTTPCLIENT " + isHttpClient);
		boolean isAllowedUser = false;  
		boolean isFailureSubmit = false;
		boolean isDuplicatedId = false;
		boolean isSizeLimitExceeded = false;
		String fileId = "";
		String fileOwner = "";
		String fileName = "";
		int fileSize = -1;
		String contentType = "";
		
    	// HttpClient can upload data via allowed client IPs (see appengine-web.xml), 
    	// and web form can upload data via user login as administrator.
    	if (isHttpClient) {
    		//via HTTP client
    		isAllowedUser = true;
    	} else {
    		//via web form
        	UserService userService = UserServiceFactory.getUserService();
        	if (userService.isUserLoggedIn()) {
        		if ("admin".equals(System.getProperty("upload.allowed-role"))) { //see appengine-web.xml
        			if (userService.isUserAdmin()) {
        				isAllowedUser = true;
        			}
        		} else { //System.getProperty("upload.allowed-role")="user"
        			isAllowedUser = true;
        		}
        	}
    	}
    	log.info("ISALLOWEDUSER " + isAllowedUser);
    	
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			isFailureSubmit = true;
			isAllowedUser = false;
		}
		
    	// Check if allowed user or not
    	if (isAllowedUser) {
			try {
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload();
				// Set overall request size constraint: the default value of -1 indicates that there is no limit.
				upload.setSizeMax(maxsize); //the maximum allowed size, in bytes.
				// Set the UTF-8 encoding to grab the correct uploaded filename, especially for Chinese
				upload.setHeaderEncoding("UTF-8");
				
				// Create a progress listener
				ProgressListenerImpl progressListener = new ProgressListenerImpl(req.getSession());
				upload.setProgressListener(progressListener);
				
				// Parse the request
				FileItemIterator iter = upload.getItemIterator(req);
				if (!isHttpClient) {
					log.info("######## uploading ... ########");
				}
				while (iter.hasNext()) {
				    FileItemStream item = iter.next();
					InputStream stream = item.openStream();
				    String fieldName = item.getFieldName();
				    if (item.isFormField()) {
				    	// Process a regular form field
				        if (fieldName.equals("fileId"))
				        	//set the UTF-8 encoding to grab the correct string
				        	fileId = Streams.asString(stream, "UTF-8");
				        if (fieldName.equals("fileOwner"))
				        	//set the UTF-8 encoding to grab the correct string
				        	fileOwner = Streams.asString(stream, "UTF-8");
				    } else {
				    	// Process a file upload
				        fileName = item.getName();
				        if (fileName != null)
				        	fileName= FilenameUtils.getName(fileName);
				        contentType = item.getContentType();
				        if (contentType == null)
				        	contentType = "application/octet-stream";
				        if (fieldName.equals("upfile")) {
				        	// Check if the fileId conforms to the Key format of the Google datastore 
			        		// and all other uploaded fields are not empty.
				        	if (DatastoreUtils.isKey(fileId) && fileOwner.length() > 0 && fileName.length() > 0) {
					        	// Save into Google datastore
					        	fileSize = DatastoreUtils.insertGoogleFile(fileId, fileOwner, fileName, contentType, stream);
					        	if (fileSize < 0) {
					        		//fileSize == -1 or -2
					        		isFailureSubmit = true;
					        		if (fileSize == -2)
						        		isDuplicatedId = true;
					        	}
				        	} else {
				        		isFailureSubmit = true;
				        	}
				        }
				    }
				} //end while
			} catch (IOException e) {
				// Upload fail
				if (isHttpClient) {
					//via HTTP client
					isFailureSubmit = true;
					out.print(-1); //return -1 (IOException)
					out.close();
				} else {
					//via web form
					isFailureSubmit = true;
//					e.printStackTrace();
				}
			} catch (FileUploadException e) {
				// Upload fail
				if (isHttpClient) {
					//via HTTP client
					isFailureSubmit = true;
					out.print(-1); //return -1 (FileUploadException)
					out.close();
				} else {
					//via web form
					isFailureSubmit = true;
					isSizeLimitExceeded = true;
//					e.printStackTrace();
				}
			}
    	} else {
    		isFailureSubmit = true;
    	}
    	
		if (!isFailureSubmit) {
			log.info("fileId: " + fileId);
			log.info("fileOwner: " + fileOwner);
			log.info("fileName: " + fileName);
			log.info("fileSize: " + fileSize);
			log.info("ContentType: " + contentType);
		}

		// Return result
		String resultUrl = "/file_list.jsp";
		if (!isFailureSubmit) {
			// Upload success
			if (isHttpClient) {
				//via HTTP client
				out.print(fileSize); //return fileSize
			} else {
				//via web form
				resp.sendRedirect(resultUrl+"?msg=Upload success.");
				log.info("######## upload successfully ########");
			}
		} else {
			// Upload fail while the fileId violates the limitation of com.google.appengine.api.datastore.Key
			if (isHttpClient) {
				//via HTTP client
				out.print(-2); //return -2 (fields are empty or wrong formats)
			} else {
				//via web form
				String msg = "?msg=[Upload Fail]: ";
				if (!isAllowedUser)
					resp.sendRedirect(resultUrl + msg +"No permissions, please login as a administrator.");
				else if (isDuplicatedId)
					resp.sendRedirect(resultUrl + msg +"Duplicated primary key.");
				else if (isSizeLimitExceeded)
					resp.sendRedirect(resultUrl + msg +"Exceeds file size limitation: "+String.valueOf(maxsize/1024)+" KB.");
				else
					resp.sendRedirect(resultUrl + msg +"The uploaded fields are empty or wrong formats.");
			}
		}
		out.close();
	}
}