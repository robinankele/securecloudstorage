package com.securecloudstorage.keystore;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class KeyStore {

	private static KeyStore instance;
	private List<KeyStoreEntry> table = new ArrayList<KeyStoreEntry>();
	
	private KeyStore() {
		
	}
	
	public static KeyStore getInstance(){
	   if(instance == null){
		   instance =  new KeyStore();
	   }
	   return instance;
	}
	
	public void addEntry(SecretKey key, byte[]iv, String owner, String fileid){
		KeyStoreEntry entry = new KeyStoreEntry(key.getEncoded(), iv, owner, fileid);
		table.add(entry);
	}
	
	public void addEntry(String owner, String fileid){
		KeyStoreEntry entry = new KeyStoreEntry(null, null, owner, fileid);
		table.add(entry);
	}
	
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
	}
}
