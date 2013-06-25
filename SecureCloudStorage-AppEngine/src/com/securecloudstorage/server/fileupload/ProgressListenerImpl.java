package com.securecloudstorage.server.fileupload;

import com.securecloudstorage.server.servlet.FileUploadServlet;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.ProgressListener;


/**
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 */
public class ProgressListenerImpl implements ProgressListener {
	private static final Logger log = Logger.getLogger(FileUploadServlet.class.getName());

    private HttpSession session;
    private final int KBytesRead_TO_SEESION = 500000; //500KB
    private long KBytes = -1;
    private Map<String, String> progressMap = null;
    
    public ProgressListenerImpl(HttpSession session){
    	this.session = session;
    }

    public void update(long bytesRead, long contentLength, int item) {
    	if (contentLength == -1) {
    		return;
    	}
    	if (bytesRead == contentLength) {
			progressMap = new HashMap<String, String>();
			progressMap.put("percent", "100");
			progressMap.put("bytesRead", String.valueOf(bytesRead));
			progressMap.put("contentLength", String.valueOf(contentLength));			
			session.setAttribute("progressMap", progressMap);
			log.info("(100, "+bytesRead+", "+contentLength+")");
		}

		long mBytesRead = bytesRead / KBytesRead_TO_SEESION;
    	if (KBytes == mBytesRead) {
    		return;
    	}
    	// Execute the following code while receiving per 500KB or above
    	KBytes = mBytesRead;
    	
    	String strPercent = NumberFormat.getPercentInstance().format((double)bytesRead /(double)contentLength);
    	int percent = Integer.parseInt(strPercent.substring(0, strPercent.length()-1));

		progressMap = new HashMap<String, String>();
		progressMap.put("percent", String.valueOf(percent));
		progressMap.put("bytesRead", String.valueOf(bytesRead));
		progressMap.put("contentLength", String.valueOf(contentLength));		
		session.setAttribute("progressMap", progressMap);
		
		// Toggle Breakpoint here to debug file upload progress in JSP
		log.info("("+percent+", "+bytesRead+", "+contentLength+")");
	}
}