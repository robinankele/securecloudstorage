package com.securecloudstorage.datastore;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * A data model (of Google datastore) for storing the large files 
 * of each size can be greater than 1MB.
 * (i.e. to break down barriers of the 1MB size limitation of each Google datastore entity)
 * 
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GoogleFile {
	@PrimaryKey
	@Persistent
	private String fileId;
	@Persistent
	private String fileOwner;
	@Persistent
	private String fileName;
	@Persistent
	private int fileSize;
	@Persistent
	private String contentType;
	@Persistent
	private List<GoogleUnit> googleUnits;
	@Persistent
	private Date date;

	public GoogleFile(String fileId, String fileOwner, String fileName, int fileSize, 
			String contentType, List<GoogleUnit> googleUnits) {
		this.fileId = fileId;
		this.fileOwner = fileOwner;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.contentType = contentType;
		this.googleUnits = googleUnits;
		this.date = new Date();
	}

	public String getId() {
		return fileId;
	}
	public void setId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileOwner() {
		return fileOwner;
	}
	public void setFileOwner(String fileOwner) {
		this.fileOwner = fileOwner;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public List<GoogleUnit> getGoogleUnits() {
		return googleUnits;
	}
	public void setGoogleUnits(List<GoogleUnit> googleUnits) {
		this.googleUnits = googleUnits;
	}

	public Date getDate() {
		return date;
	}
}