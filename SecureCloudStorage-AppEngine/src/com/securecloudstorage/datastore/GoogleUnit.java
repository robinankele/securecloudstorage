package com.securecloudstorage.datastore;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

/**
 * A data model for storing a single large file which 
 * is composed of a list of {@link GoogleUnit} instances (entities).
 * 
 * @author <a href="mailto:tytung@iis.sinica.edu.tw">Tsai-Yeh Tung</a>
 * @version 1.0
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GoogleUnit {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Blob data;
	
	public GoogleUnit(Blob data) {
		this.data = data;
	}
	
	public Key getKey() {
		return key;
	}
	
	public Blob getData() {
		return data;
	}
	
}