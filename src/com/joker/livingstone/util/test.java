package com.joker.livingstone.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String s = a("M0YxRTQzREZFRDg3NkQwNUM2RDVCNzE0NUVGNEE3MkI3QkVGRTE5NjEwN0JEQkMxMTY0OUIxMjg5NzFCMTU3NjQ2MEE3QUQyRUM1RjVGQUVFNzZCQTI3QzVBQzVCMjMy");
			System.out.println(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static byte[] a() throws Exception {
		return new byte[] { -128, 121, 94, 23, 58, -106, -113, -40, 31, 100,
				27, -53, -109, 111, 27, -58, 54, 98, 76, 97, 82, -32, -111,
				-57, -84, -128, -4, -115, 104, -19, 10, 25 };
	}

	public static String a(String paramString) throws Exception {
		String str = new String(Base64.decode(paramString, 0));
		return new String(a(a(), b(str)));
	}

	private static byte[] a(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
			throws Exception {
		SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramArrayOfByte1,
				"AES");
		Cipher localCipher = Cipher.getInstance("AES");
		localCipher.init(2, localSecretKeySpec);
		return localCipher.doFinal(paramArrayOfByte2);
	}

	public static byte[] b(String paramString) {
		int i = paramString.length() / 2;
		byte[] arrayOfByte = new byte[i];
		for (int j = 0;; j++) {
			if (j >= i)
				return arrayOfByte;
			arrayOfByte[j] = Integer.valueOf(
					paramString.substring(j * 2, 2 + j * 2), 16).byteValue();
		}
	}
}
