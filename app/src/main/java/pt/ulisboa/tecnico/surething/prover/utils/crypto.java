package pt.ulisboa.tecnico.surething.prover.utils;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.surething.prover.R;

public class crypto {

    private static final String KEYSET = "my_keyset.json";
    private static final String AESKEY = "key.txt";
    private static final int IV_SIZE = 128;
    private static final String SEED = "Uk9YQVMK";
    private static final String ALIAS = "POSEKEY";
    private static final String PRIVATE_KEY = "user_private_key.pem";

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private KeyStore keyStore;

    private void initKeyStore() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*******************************************************/

    public static void generateRSAKeys(Context context){
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            byte[] publicKey = pair.getPublic().getEncoded();
            Log.d("pub", new String(Base64.encode(publicKey, Base64.DEFAULT)));
            byte[] privateKey = pair.getPrivate().getEncoded();
            Log.d("priv", new String(Base64.encode(privateKey, Base64.DEFAULT)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static RSAPrivateKey loadPrivateKey(Context context) {
        InputStream input = context.getResources().openRawResource(R.raw.user_private_key);
        try {
            byte[] data = new byte[input.available()];
            input.read(data);
            String key = new String(data);
            String privateKeyPEM = key
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "");
            Log.d("key", privateKeyPEM);

            byte[] decoded = Base64.decode(privateKeyPEM.getBytes(), Base64.DEFAULT);
            Log.d("aiaiai", new String(decoded));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RSAPublicKey loadPublicKey(Context context) {
        InputStream input = context.getResources().openRawResource(R.raw.user_public_key);
        try {
            byte[] data = new byte[input.available()];
            input.read(data);
            String key = new String(data);
            String publicKeyPEM = key
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PUBLIC KEY-----", "");

            byte[] encoded = Base64.decode(publicKeyPEM, Base64.DEFAULT);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SecretKey loadAESKey(Context context){
        InputStream input = context.getResources().openRawResource(R.raw.aeskey);
        try {
            byte[] data = new byte[input.available()];
            input.read(data);
            String key = new String(data);
            Log.d("AES", key);

            byte[] decoded = Base64.decode(key.getBytes(), Base64.DEFAULT);

            SecretKey originalKey = new SecretKeySpec(decoded, 0, decoded.length, "AES");
            return originalKey;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] sign(Context context, byte[] plaintext){
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            MGF1ParameterSpec mgf1ParameterSpec = new MGF1ParameterSpec("SHA-256");
            //PSSParameterSpec pssParameterSpec = new PSSParameterSpec("SHA-256", "MGF1", mgf1ParameterSpec , 0, 1);
            //signature.setParameter(pssParameterSpec);
            signature.initSign(loadPrivateKey(context));
            Log.d("sign plaintext", new String(Base64.encode(plaintext, Base64.DEFAULT)));
            Log.d("size", String.valueOf(plaintext.length));
            signature.update(plaintext);
            byte[] sig = signature.sign();
            Log.d("sign final", new String(Base64.encode(sig, Base64.DEFAULT)));
            Log.d("size", String.valueOf(sig.length));


            return sig;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void verify(Context context, byte[] plaintext, byte[] signature){
        try {
            Signature sig = Signature.getInstance("SHA256WithRSA");
            sig.initVerify(loadPublicKey(context));
            sig.update(plaintext);
            sig.verify(signature);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }

    public static byte[] aeadEncrypt(Context context, byte[] associated_data, byte[] plaintext, byte[] nonce){

        GCMParameterSpec s = new GCMParameterSpec(IV_SIZE, nonce);
        try {
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, loadAESKey(context), s);

            cipher.updateAAD(associated_data);

            return cipher.doFinal(plaintext);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] aeadDecrypt(Context context, byte[] associated_data, byte[] ciphertext, byte[] nonce){

        GCMParameterSpec s = new GCMParameterSpec(IV_SIZE, nonce);
        try {
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, loadAESKey(context), s);
            //prints

            cipher.updateAAD(associated_data);

            return cipher.doFinal(ciphertext);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
