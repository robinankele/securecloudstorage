package com.securecloudstorage.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.*;

import org.apache.commons.codec.binary.Base64;

import com.securecloudstorage.datastore.GoogleFile;
import com.securecloudstorage.datastore.GoogleUnit;
import com.securecloudstorage.datastore.util.DatastoreUtils;


/**
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 */
public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 5459219913588984489L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		out.print("<p>Error: The request method <code>"+req.getMethod()+"</code> is inappropriate for the URL <code>"+req.getRequestURI()+"</code></p>");
		out.close();
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	String fileId = req.getParameter("id");
    	System.out.println("FILEID:" + fileId);
    	if (DatastoreUtils.isKey(fileId)) {
	    	// Get data from Google datastore
    		GoogleFile g = DatastoreUtils.getGoogleFileById(fileId);
	    	if (g != null) {
	    		// To encode the correct UTF-8 downloaded filename
	    		String fileName = g.getFileName();	    		
	    		fileName = encodeFileName(fileName, req.getHeader("User-Agent"));
	    		
	    		// Display in the browser or pop up the save-as dialog according to the file type
		    	resp.setHeader("Content-Disposition", "inline;filename=" + fileName);
		    	// Force the browser to download the file (i.e. pop up the save-as dialog)
		    	//resp.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		    	
		    	// Set the ContentLength
		    	resp.setContentLength(g.getFileSize());
		    	
	    		// Set the ContentType as a binary file
	    		//resp.setContentType("application/octet-stream");
	    		// Set the ContentType from the datastore
		    	String contentType = g.getContentType();
		    	if (contentType == null) {
		    		resp.setContentType("application/octet-stream");
		    	} else {
		    		resp.setContentType(g.getContentType());
		    	}
		    	
	    		// Output
	    		OutputStream o = resp.getOutputStream();
	    		List<GoogleUnit> googleUnits = g.getGoogleUnits();
	    		for (GoogleUnit gUnit : googleUnits) {
	    			o.write(gUnit.getData().getBytes());
	    			o.flush();
	    		}
		    	o.close();
    		} else {
	    		// No entities in Google datastore
	        	PrintWriter out = resp.getWriter();
				out.print("ID \""+fileId+"\" not exists.");
				out.close();
	    	}
    	} else {
    		// Violate the limitation of com.google.appengine.api.datastore.Key
        	PrintWriter out = resp.getWriter();
			out.print("Access Denied");
			out.close();
    	}
    }
    
    /**
     * @param fileName 	The UTF-8 filename.
     * @param userAgent The User-Agent in HTTP request headers.
     * 
     * @return The encoded filename corresponding to different browsers.
     * 
     * @throws IOException
     */
    private String encodeFileName(String fileName, String userAgent) throws IOException {
    	//fileName="???????????????????????????.jpg"
    	if (null != userAgent && -1 != userAgent.indexOf("MSIE")) {
    		fileName = fileName.replace(" ", "_");
			// UTF-8 URL encoding only works in IE
			return URLEncoder.encode(fileName, "UTF-8");
			//return "%E7%B4%AB%E8%91%89%E6%A7%AD.jpg"
		} else if (null != userAgent && -1 != userAgent.indexOf("Mozilla")) {
			// Base64 encoding works in Firefox
			return "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
			//return "=?UTF-8?B?57Sr6JGJ5qetLmpwZw==?="
		} else {
			return fileName;
		}
	}
}
