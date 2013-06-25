package com.securecloudstorage.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.securecloudstorage.datastore.GoogleFile;
import com.securecloudstorage.datastore.util.DatastoreUtils;


/**
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 */
public class FileManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 2190879304615239209L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		out.print("<p>Error: The request method <code>"+req.getMethod()+"</code> is inappropriate for the URL <code>"+req.getRequestURI()+"</code></p>");
		out.close();
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String resultUrl ="/file_list.jsp";
		PrintWriter out = resp.getWriter();
		String action = req.getParameter("action");
		String fileId = req.getParameter("id");
		// Check if the role that can delete files or not
		boolean isUserAllowedToDelete = false;
		UserService userService = UserServiceFactory.getUserService();
    	if (userService.isUserLoggedIn()) {
    		// Administrators can delete all files belong to any users.
    		if (userService.isUserAdmin()) {
    			isUserAllowedToDelete = true;
			}
    		// Users can just delete their own files.
    		if ("user".equals(System.getProperty("upload.allowed-role"))) { //see appengine-web.xml
    			if (DatastoreUtils.isKey(fileId)) {
    				GoogleFile g = DatastoreUtils.getGoogleFileById(fileId);
    				if (g != null) {
    					String fileOwner = g.getFileOwner();
	    				if (userService.getCurrentUser().getNickname().equals(fileOwner))
	    					isUserAllowedToDelete = true;
    				}
    			}
    		}
    	}
    	if (isUserAllowedToDelete) {
	    	// Delete
	    	if ("delete".equals(action)) {
		    	boolean isRemoved = false;
		    	if (DatastoreUtils.isKey(fileId)) {
		    		isRemoved = DatastoreUtils.deleteGoogleFileById(fileId);
				}
		    	if (fileId.indexOf("'") > -1)
		    		fileId = fileId.replaceAll("'", "\\\\'");
		    	if (isRemoved)
		    		out.println("<script>alert('FileId \""+fileId+"\" has been successfully removed.'); location.href='"+resultUrl+"';</script>");
		    	else
		    		out.println("<script>alert('Remove fail.'); location.href='"+resultUrl+"';</script>");
	    	} else {
	    		String queryString = req.getQueryString();
	    		if (queryString != null)
	    			queryString = "?"+URLEncoder.encode(queryString, "UTF-8");
	    		else
	    			queryString = "";
	    		out.println("<script>alert('Incorrect operation for the URL "+req.getRequestURI()+queryString+"'); location.href='"+resultUrl+"';</script>");
	    	}
    	} else { // !isUserAllowedToDelete
    		out.println("<script>alert('Permission deny.'); location.href='"+resultUrl+"';</script>");
    	}
    	out.close();
	}
}