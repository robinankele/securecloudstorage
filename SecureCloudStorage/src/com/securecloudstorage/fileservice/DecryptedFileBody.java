package com.securecloudstorage.fileservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;

public class DecryptedFileBody extends AbstractContentBody {

	private final File file;
	private final SecretKey key;
	private byte[] iv;
	
	public DecryptedFileBody(final File file, SecretKey key) {
		super("application/octet-stream");
		if (key == null) {
			throw new IllegalArgumentException("File may not be null");
		}
		this.file = file;
		this.key = key;
	}
        
    public String getTransferEncoding() {
        return MIME.ENC_BINARY;
    }

    public String getCharset() {
        return null;
    }

    public long getContentLength() {
        return this.file.length();
    }
    
    public String getFilename() {
        return this.file.getName();
	}

	public byte[] getIv() {
		return iv;
	}

	public void setIv(byte[] iv) {
		this.iv = iv;
	}

	@Override
	public void writeTo(final OutputStream out) throws IOException {
		if (out == null) {
			throw new IllegalArgumentException("Output stream may not be null");
		}
		InputStream in = new FileInputStream(this.file);

		Cipher c = null;
		try {
			c = Cipher.getInstance("AES/CFB8/NoPadding");
			// c.getIV(); //todo: store this, set to known value..?

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			
			try {
				c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		@SuppressWarnings("resource")
		// Stream will be closed from the outside.
		CipherOutputStream cos = new CipherOutputStream(out, c);

		try {
			byte[] tmp = new byte[4096];
			int l;
			while ((l = in.read(tmp)) != -1) {

				cos.write(tmp, 0, l);
			}
			cos.flush();
		} finally {
			// cos.close();
			in.close();
		}
	}
}
