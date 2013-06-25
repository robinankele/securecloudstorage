package com.securecloudstorage.test;


import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.securecloudstorage.keystore.KeyStoreEntry;

import junit.framework.TestCase;

public class TestKeyStoreEntry extends TestCase {

	private byte[] key;
	private byte[] iv;
	private String owner;
	private String fileid;
	
	public TestKeyStoreEntry(String name) {
		super(name);
		
		SecretKey secret = null;
		KeyGenerator kgen;
		try {
			kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			secret = kgen.generateKey();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		this.key = secret.getEncoded();
		this.iv = "iv".getBytes();
		this.owner = "me";
		this.fileid = "file.id";
	}
	
	public void TestGetKey() {
		KeyStoreEntry entry = new KeyStoreEntry(key, iv, owner, fileid);
		assertEquals(key, entry.getKey());
	}
	
	public void TestSetKey() {
		KeyStoreEntry entry = new KeyStoreEntry(key, iv, owner, fileid);
		entry.setKey("key".getBytes());
		assertEquals("key".getBytes(), entry.getKey());
	}
	
	public void TestGetOwners() {
		KeyStoreEntry entry = new KeyStoreEntry(key, iv, owner, fileid);
		assertEquals(owner, entry.getOwners().get(0));
	}
	
	public void TestAddOwner() {
		KeyStoreEntry entry = new KeyStoreEntry(key, iv, owner, fileid);
		entry.addOwner("you");
		assertEquals("you", entry.getOwners().get(1));
	}
	
	public void TestGetFileId() {
		KeyStoreEntry entry = new KeyStoreEntry(key, iv, owner, fileid);
		assertEquals(fileid, entry.getFileid());
	}
	
	public void TestSetFileId() {
		KeyStoreEntry entry = new KeyStoreEntry(key, iv, owner, fileid);
		entry.setFileid("new file.id");
		assertEquals("new file.id", entry.getFileid());
	}
	
	public void TestGetIv() {
		KeyStoreEntry entry = new KeyStoreEntry(key, iv, owner, fileid);
		assertEquals(iv, entry.getIv());
	}
	
	public void TestSetIv() {
		KeyStoreEntry entry = new KeyStoreEntry(key, iv, owner, fileid);
		entry.setIv("newiv".getBytes());
		assertEquals("newiv".getBytes(), entry.getIv());
	}

}