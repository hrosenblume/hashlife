package com.gethashlife.hashlife;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import javax.crypto.Cipher;

/**
 * Created by hunter on 1/17/15.
 */
public class Encryption {
    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    private static String publicKeyString;
    private static String privateKeyString;

    private static String otherUserPublicKey;

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static void generateKeys() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();

            try {
                publicKeyString = savePublicKey(publicKey);
            } catch (Exception e) {
                Log.d("ERROR", "ISSUE WITH NEW ALG " + e.toString());
            }

            byte[] privateKeyArray = privateKey.getEncoded();
            privateKeyString = new String(privateKeyArray);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
    }

    public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = fact.getKeySpec(publ,
                X509EncodedKeySpec.class);
        byte[] boo = spec.getEncoded();
        //Base64.encode(boo, Base64.DEFAULT);
        String result = new String(Base64.encode(boo, Base64.DEFAULT));
        result = result.replace("\n", "").replace("\r", "");
        return result;
        //return TokenDecoder.base64Encode(boo);
    }

    public static PublicKey stringToPublicKey(String s) {
        byte[] c = null;
        KeyFactory keyFact = null;
        PublicKey returnKey = null;

        try {
            c = Base64.decode(s, Base64.DEFAULT);
            keyFact = KeyFactory.getInstance("RSA");
        } catch (Exception e) {
            System.out.println("Error in Keygen");
            e.printStackTrace();
        }


        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(c);
        try {
            returnKey = keyFact.generatePublic(x509KeySpec);
        } catch (Exception e) {

            System.out.println("Error in Keygen2");
            e.printStackTrace();

        }

        return returnKey;

    }

    public static byte[] publicKeyToByte(String input) throws GeneralSecurityException {
        byte[] result = null;

        return result;
    }

    public static PublicKey getPublicKey() {
        return publicKey;
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static String getPrivateKeyString() {return privateKeyString;}

    public static String getPublicKeyString() {return publicKeyString;}

    public static String getOtherUserPublicKey() {return otherUserPublicKey;}
    public static void setOtherUserPublicKey(String input) {otherUserPublicKey = input;}

//    public static byte[] generateKey(String password) throws Exception {
//        byte[] keyStart = password.getBytes("UTF-8");
//
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
//        sr.setSeed(keyStart);
//        kgen.init(128, sr);
//        SecretKey skey = kgen.generateKey();
//        return skey.getEncoded();
//    }
//
//    public static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception {
//        SecretKeySpec skeySpec = new SecretKeySpec(key, "RSA");
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//
//        byte[] encrypted = cipher.doFinal(fileData);
//        return encrypted;
//    }
//
//    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception {
//        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//
//        byte[] decrypted = cipher.doFinal(fileData);
//        return decrypted;
//    }
//
    public static void decryptTestFile(File file) {
        FileInputStream in = null;
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            in = new FileInputStream(file);
            in.read(bytes);
            String recoveredFromMess = new String(cipher.doFinal(Base64.decode(bytes, Base64.DEFAULT)), "UTF-8");
            in.close();
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(recoveredFromMess.getBytes());
            stream.close();
        } catch(Exception e) {
        }
    }
    public static void encryptTestFile(File file) {

        int length = (int) file.length();

        byte[] bytes = new byte[length];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
            in.close();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            PublicKey funkyPublicKey = Encryption.stringToPublicKey(getOtherUserPublicKey());
            cipher.init(Cipher.ENCRYPT_MODE, funkyPublicKey);
            String encodedMess = new String(Base64.encode(cipher.doFinal(bytes), Base64.DEFAULT));
            Log.d("FYREBUG", encodedMess);
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(encodedMess.getBytes());
            stream.close();
        } catch(Exception e) {
            Log.d("ERROR", e.toString());
        }
    }
}
