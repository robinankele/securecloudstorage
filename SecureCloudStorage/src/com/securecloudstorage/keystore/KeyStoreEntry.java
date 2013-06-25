package com.securecloudstorage.keystore;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyStoreEntry {
	
	private byte[] key;
	private List<String> owners = new ArrayList<String>();
	private String fileid;
	private byte[] iv;
	
	public KeyStoreEntry(byte[] key, byte[] iv, String owner, String fileid){
		this.iv = iv;
		owners.add(owner);
		this.fileid = fileid;
		this.key = key;
	}

	public SecretKey getKey() {
		if(key == null)
			return null;
		return new SecretKeySpec(this.key, 0,
				this.key.length, "AES");
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public List<String> getOwners() {
		return owners;
	}

	public void addOwner(String owner) {
		owners.add(owner);
	}

	public String getFileid() {
		return fileid;
	}

	public void setFileid(String fileid) {
		this.fileid = fileid;
	}

	public byte[] getIv() {
		return iv;
	}

	public void setIv(byte[] iv) {
		this.iv = iv;
	}
}
