package com.securecloudstorage.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.*;

/**
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 */
public class InfoServlet extends HttpServlet {
	private static final long serialVersionUID = 8790039686044988963L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		out.write("<pre>");
		//System.getProperties().list(out);
		out.println("<b>System.getProperties()</b><br/>");
		out.println("You can modify appengine-web.xml to change the below values of properties whose colors are blue.<br/>");
		Properties properties = System.getProperties();
		String[] keys = new String[properties.size()];
		int i =0;
		for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
			keys[i] = e.nextElement().toString();
			i++;
		}
		Arrays.sort(keys);
		for (int j = keys.length-1; j>0; j--) {
			String keyname = keys[j];
			if (keyname.startsWith("upload"))
				keyname = "<font color=blue>"+keyname+"</font>";
			out.println(keyname + " = <font color=#999999>" + System.getProperty(keys[j]) + "</font>");
		}
		out.write("</pre>");
		out.close();
	}
}