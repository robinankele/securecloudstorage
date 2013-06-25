package com.securecloudstorage.datastore.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.apache.commons.io.output.ByteArrayOutputStream;
import com.google.appengine.api.datastore.Blob;
import com.securecloudstorage.datastore.GoogleFile;
import com.securecloudstorage.datastore.GoogleUnit;

/**
 * A Utility for manipulating the {@link GoogleUnit} and {@link GoogleFile} data models.<br/>
 * 
 * 2009/11/6 Thanks Zhang Yu for fixing the bug of getGoogleFileById() to be compatible with 
 * the latest SDK including 1.2.5, 1.2.6
 * 
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 * 
 */
public class DatastoreUtils {
	/**
	 * The allowed maximum entity size (1MB) which is limited by Google datastore.
	 * <p>See http://code.google.com/intl/en/appengine/docs/java/datastore/overview.html#Quotas_and_Limits</p>
	 */
	public final static int ENTITY_SIZE = 1024000; //1MB
	
	/** This class should not be instantiated. */
	private DatastoreUtils() {}

	/**
	 * Check if the id conforms to the Key format of the Google datastore.
	 * 
	 * @param id The Google datastore's Key.
	 * 
	 * @return <code>true</code> or <code>false</code>
	 */
	public static boolean isKey(String id) {
		boolean isKeyFormat = true;
		if (id == null) {
			//id == null (violate the limitation of com.google.appengine.api.datastore.Key)
			isKeyFormat = false;
		} else {
			id = id.trim();
			if (id.length()<2) {
				//id.length()<2 (violate the limitation of com.google.appengine.api.datastore.Key)
				isKeyFormat = false;
			} else {
				if (Character.isDigit(id.charAt(0))) {
					//id starts with a digit (violate the limitation of com.google.appengine.api.datastore.Key)
					isKeyFormat = false;
				}
			}
		}
		return isKeyFormat;
	}
	
	/**
	 * Get a {@link GoogleFile} instance from the primary key.
	 * 
	 * @param fileId The primary key in {@link GoogleFile}, and is provided as String.
	 * 
	 * @return A {@link GoogleFile} instance.
	 */
	public static GoogleFile getGoogleFileById(String fileId) {
		GoogleFile g = null;
		if (DatastoreUtils.isKey(fileId)) {
			// get data from Google datastore
    		PersistenceManager pm = PMF.get().getPersistenceManager();
    		try {
    			g = pm.getObjectById(GoogleFile.class, fileId);
    			// another trick for Blod (fixed by Zhang Yu)
    			for (GoogleUnit gu: g.getGoogleUnits()) {
    				gu.getData(); //trick
    			}
    			g.setGoogleUnits(g.getGoogleUnits()); //trick
    		} catch (JDOObjectNotFoundException e) {
	    		//no entities in Google datastore
			} finally {
    			pm.close();
    		}
		}
		return g;
	}
	
	/**
	 * Remove a {@link GoogleFile} instance from the primary key.
	 * 
	 * @param fileId The primary key in {@link GoogleFile}, and is provided as String.
	 * 
	 * @return <code>true</code> or <code>false</code>
	 */
	public static boolean deleteGoogleFileById(String fileId) {
		boolean isRemoved = false;
		GoogleFile g = null;
		if (DatastoreUtils.isKey(fileId)) {
			// get data from Google datastore
    		PersistenceManager pm = PMF.get().getPersistenceManager();
    		try {
    			g = pm.getObjectById(GoogleFile.class, fileId);
    			pm.deletePersistent(g);
    			isRemoved = true;
    		} catch (JDOObjectNotFoundException e) {
	    		//no entities in Google datastore
			} finally {
    			pm.close();
    		}
		}
		return isRemoved;
	}

	/**
	 * Save data into {@link GoogleFile} from the file input stream.
	 * 
	 * @param fileId		The primary key in {@link GoogleFile}, and is provided as String.
	 * @param fileOwner		The property name in {@link GoogleFile}, and is provided as String.
	 * @param fileName		The property name in {@link GoogleFile}, and is provided as String.
	 * @param contentType	The property name in {@link GoogleFile}, and is provided as String.
	 * @param fileStream	The input stream which will be converted into a list of {@link GoogleUnit} instances.
	 * 
	 * @return The size of successfully saved file, 
	 *         or -1 if the supplied parameters exist any errors, 
	 *         or -2 if the primary key is duplicated in {@link GoogleFile}.
	 * 
	 * @throws IOException
	 */
	public static int insertGoogleFile(String fileId, String fileOwner, String fileName, 
			String contentType, InputStream fileStream) throws IOException {
		int fileSize = -1;
		boolean isAllowed = false;
		if (DatastoreUtils.isKey(fileId)) {
			GoogleFile g = getGoogleFileById(fileId);
			if (g == null) {
				isAllowed = true;
			} else {
				// check if duplicated primary key or not
				if (!g.getId().equals(fileId))
					isAllowed = true;
				else
					fileSize = -2; //primary key is duplicated
			}
		}
		if (isAllowed) {
			List<GoogleUnit> googleUnits = toGoogleUnits(fileStream);
	    	fileSize = insertGoogleFile(fileId, fileOwner, fileName, contentType, googleUnits);
		}
		return fileSize;
	}
	
	/**
	 * Save data into {@link GoogleFile} from a list of {@link GoogleUnit} instances.
	 * 
	 * @param fileId		The primary key in {@link GoogleFile}, and is provided as String.
	 * @param fileOwner		The property name in {@link GoogleFile}, and is provided as String.
	 * @param fileName		The property name in {@link GoogleFile}, and is provided as String.
	 * @param contentType	The property name in {@link GoogleFile}, and is provided as String.
	 * @param googleUnits	The property name in {@link GoogleFile}, and is provided as a list of {@link GoogleUnit} instances.
	 * 
	 * @return The total file size.
	 * 
	 * @throws IOException
	 */
	private static int insertGoogleFile(String fileId, String fileOwner, String fileName, 
			String contentType, List<GoogleUnit> googleUnits) {
		int fileSize = getGoogleFileSize(googleUnits);
		GoogleFile entity = new GoogleFile(fileId, fileOwner, fileName, fileSize, contentType, googleUnits);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(entity);
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		return fileSize;
	}

	/**
	 * Get the total size of the {@link GoogleFile} composed of a list of {@link GoogleUnit} instances.
	 * 
	 * @param list A list of {@link GoogleUnit} instances.
	 * 
	 * @return The total file size.
	 */
	private static int getGoogleFileSize(List<GoogleUnit> list) {
		int count = -1;
		int size = list.size();
		if (size > 0) {
			count = (size-1) * ENTITY_SIZE;
			GoogleUnit gUnit = list.get(size-1);
			count += gUnit.getData().getBytes().length;
		}
		return count;
	}
	
	/**
	 * Convert the input stream into a list of {@link GoogleUnit} instances.
	 * 
	 * @param input The input stream.
	 * 
	 * @return A list of {@link GoogleUnit} instances.
	 * 
	 * @throws IOException
	 */
	private static List<GoogleUnit> toGoogleUnits(InputStream input) throws IOException {
		List<GoogleUnit> list = new ArrayList<GoogleUnit>();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int pBytesRead = 0; //The total number of bytes, which have been read so far. (Range: 0 ~ ENTITY_SIZE)
		long pContentLength = 0; //The total number of bytes, which are being read. (The total file size)
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			pBytesRead += n;
			pContentLength += n;
			if (pBytesRead >= ENTITY_SIZE) {
				output.write(buffer, 0, n-(pBytesRead-ENTITY_SIZE));
				//put data of ENTITY_SIZE bytes into GoogleUnit
				GoogleUnit gUnit = new GoogleUnit(new Blob(output.toByteArray()));
            	list.add(gUnit);
            	output.reset();
            	if (pBytesRead-ENTITY_SIZE > 0) {
            		output.write(buffer, n-(pBytesRead-ENTITY_SIZE), pBytesRead-ENTITY_SIZE);
            		//recounter pBytesRead
            		pBytesRead = pBytesRead-ENTITY_SIZE;
            	}
			} else {
				output.write(buffer, 0, n);
			}
        }
        if (pContentLength < ENTITY_SIZE || pBytesRead < ENTITY_SIZE) {
        	//put data of less than ENTITY_SIZE bytes into GoogleUnit
        	GoogleUnit gUnit = new GoogleUnit(new Blob(output.toByteArray()));
        	list.add(gUnit);
        }
        output.close();
		return list;
	}
	
}