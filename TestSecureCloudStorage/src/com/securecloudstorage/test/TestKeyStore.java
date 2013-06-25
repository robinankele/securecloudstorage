package com.securecloudstorage.test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.content.Context;
import android.test.AndroidTestCase;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.securecloudstorage.keystore.KeyStore;
import com.securecloudstorage.keystore.KeyStoreEntry;

import junit.framework.TestCase;

public class TestKeyStore extends AndroidTestCase {

	public TestKeyStore(String name) {
		
	}
	
	public void testAddAndGetEntry() {
		
		SecretKey key = null;
		byte[]iv = "iv".getBytes();
		String owner = "me";
		String fileid = "file.id";
		
		KeyGenerator kgen;
		try {
			kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			key = kgen.generateKey();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		KeyStore.getInstance().addEntry(key, iv, owner, fileid);
		KeyStoreEntry entry = KeyStore.getInstance().getEntry(fileid);
		
		assertEquals(key, entry.getKey());
		assertEquals(iv, entry.getIv());
		assertEquals(owner, entry.getOwners().get(0));
		assertEquals(fileid, entry.getFileid());
	}
	
	public void testAddAndGetEntry2(){
		String owner = "me";
		String fileid = "file.id";
		
		KeyStore.getInstance().addEntry(null, null, owner, fileid);
		KeyStoreEntry entry = KeyStore.getInstance().getEntry(fileid);
		
		assertEquals(owner, entry.getOwners().get(0));
		assertEquals(fileid, entry.getFileid());
	}
	/*
	public void removeEntry(String fileid){
		for(KeyStoreEntry e : table){
			if(e.getFileid() == fileid)
				table.remove(e);
		}
	}
	
	public KeyStoreEntry getEntry(String fileid){
		for(KeyStoreEntry e : table){
			if(e.getFileid().equals(fileid)){
				return e;
			}
		}
		return null;
	}
	
	public void listKeyStore(){
		for(KeyStoreEntry e : table){
			System.out.println("--------------------------------------");
			if(e.getFileid() != null)
				System.out.println("fileid: "+ e.getFileid());
			if(e.getOwners() != null)
				System.out.println("owners: " + e.getOwners());
			if(e.getKey() != null)
				System.out.println("key: " + e.getKey().toString());
		}
	}
	
	public String exportKeyStore() throws IOException {
		Gson gson = new Gson();
		
		Type listType = new TypeToken<List<KeyStoreEntry>>(){}.getType();
		String json =  gson.toJson(table, listType);
		System.out.println("JSON String: " + json);
		return json;
	}
	
	public void importKeyStore(String json, String owner, Context context) {
		Gson gson = new Gson();

		Type listType = new TypeToken<List<KeyStoreEntry>>(){}.getType();
		List<KeyStoreEntry> temp = gson.fromJson(json,listType);
		
		for(int i = 0; i < temp.size(); i++){
			if(temp.get(i).getOwners().contains(owner) && temp.get(0).getKey() != null){
				table.add(temp.get(i));
				Toast.makeText(context, "Added "+temp.get(0).getKey().toString() +" to Keystore", Toast.LENGTH_SHORT).show();
			}
		}
	}*/
}
