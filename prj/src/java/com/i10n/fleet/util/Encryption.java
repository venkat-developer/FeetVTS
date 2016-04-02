//package com.i10n.fleet.util;
//
//
//
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.security.Key;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//
//import sun.misc.BASE64Encoder;
//
//public class Encryption {
//	//private static Logger LOG=Logger.getLogger(Encryption.class);
//
//	private static final String ALGORITHM = "AES";
//	private static final byte[] keyValue = 
//			new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };
//
//	public static String encrypt(String valueToEnc) throws Exception {
//		Key key = generateKey();
//		Cipher c = Cipher.getInstance(ALGORITHM);
//		c.init(Cipher.ENCRYPT_MODE, key);
//		byte[] encValue = c.doFinal(valueToEnc.getBytes());
//		String encryptedValue = new BASE64Encoder().encode(encValue);
//		return encryptedValue;
//	}
//	private static Key generateKey() throws Exception {
//		Key key = new SecretKeySpec(keyValue, ALGORITHM);
//		// SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
//		// key = keyFactory.generateSecret(new DESKeySpec(keyValue));
//		return key;
//	}
//	/**
//	 * Command line arguments while building  
//	 * @param args
//	 * @throws Exception
//	 */
//	public static void main(String[] args) throws Exception {
//		System.out.println("Enter database password and press enter ");
//		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
//	//	System.out.print("PWd is  "+br.readLine());
//		String password=br.readLine();
//		String passwordEnc = encrypt(password);
//		writeToAfile(passwordEnc);
//	}
//	/**
//	 * Writing encrypted password to the properties file. 
//	 * @param passwordEnc
//	 */
//	private static void writeToAfile(String passwordEnc) {
//		try {
//			String s=System.getenv("CATALINA_HOME");
//			BufferedWriter out = new BufferedWriter(new FileWriter(s+"/webapps/encryptedFile.properties"));
//			out.write(passwordEnc);
//			out.close();
//		} catch (IOException e) {
//			System.out.println("Errror while writing to the file "+e);
//		}
//	}
//	/**
//	 * Encrypting DataBase Password
//	 *  and setting the password to the database. 
//	 */
//	/*public static void encryptDBPassword() {
//		String password=null;
//		Console console = System.console();
//		if (console == null) {
//			System.out.println("Unable to obtain console");
//			return;
//		}
//		password = new String (console.readPassword ("Enter Database password : "));
//		try {
//			DBConnectionUtils.setENCRYPTED_PASSWORD(encrypt(password));
//		} catch (Exception e) {
//			System.out.println("Error while encrypting password ",e);
//		}
//		//writeToAfile(passwordEnc);
//	}*/
//}