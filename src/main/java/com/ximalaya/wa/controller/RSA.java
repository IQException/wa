package com.ximalaya.wa.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.Cipher;

import com.google.common.collect.Lists;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.misc.CharacterDecoder;
import sun.misc.CharacterEncoder;

@SuppressWarnings("restriction")
public class RSA {

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTbumGO8ieM5fpuXM+LshfZDbouKiJyxpRqm/v" + 
    		"Ddro6Xj49F2au5ZRBMj1hj7c1O120fCCbC+AKzXXoQs2pY977rtqAhH6rFbK1KDtuCoT3nNEDa4h" + 
    		"5r+yCbeDCYjlKYzJcZITylhpLj+XBVDRsyuKG24s94ZHL5Z7za5nPNiG7wIDAQAB";

    private static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJNu6YY7yJ4zl+m5cz4uyF9kNui4" + 
    		"qInLGlGqb+8N2ujpePj0XZq7llEEyPWGPtzU7XbR8IJsL4ArNdehCzalj3vuu2oCEfqsVsrUoO24\n" + 
    		"KhPec0QNriHmv7IJt4MJiOUpjMlxkhPKWGkuP5cFUNGzK4obbiz3hkcvlnvNrmc82IbvAgMBAAEC\n" + 
    		"gYAwWnHwem8Do7wxBYeMX1+MGZwAm397IceR/SoU5yBivx/T1qFUfC2CiOU30T2Qw6zWCWit/ble\n" + 
    		"ec2A2OqKk6YI5aHBBjK/bBEttzaMgooD6xIfUGtfFTCmlHqpsUkJvhAz+Qby1Q5k5CNeshlY2jqc\n" + 
    		"K7bYEX1Tyl3pHaw1WvwfAQJBAPxqyjO+Fx2PA0Ky9TsJmZnQraK5LUS4xLw20/bbySosQyCHhyeC\n" + 
    		"PNUjdUYbJA7M8HU+c/ORJS5BzyqoB6R6psECQQCVhqQ8tw7/LsI0qzRuqVhn9ehOe6rCMCOr9dr5\n" + 
    		"lZwpwSoXpXrv8/9MB35Fv3gZMTGAay54ivL8qlz8VhzP08mvAkBvz0X+IYfPuhexpd/m5Nu7PKqN\n" + 
    		"WAVrWYUb8hV2VekH209oy1/c6l3T877w7PjN7Y3eAWoPTA3yLYh81q17+DCBAkAbaRlGgBBhu0Ki\n" + 
    		"wRfViXnJZQz3BjihbI29gk5oHoMILH744r8MqeANIfqF8rmjdJ8seXmFY4Nzyg9NusOBXcPFAkBI\n" + 
    		"85+CmDsAgaE92RzPzk9DM0jqAHCTC5FbtAwl9yLMtGaVvkNHQysFegkDm3N7kGu03q66vlbD8azr\n" + 
    		"cIMFQWPT";

    private static final Charset UTF8 = Charset.forName("utf8");

    private static KeyFactory keyFactory;

    private static ThreadLocal<BASE64Decoder> base64DecoderThreadLocal = new ThreadLocal<BASE64Decoder>();

    private static ThreadLocal<BASE64Encoder> base64EncoderThreadLocal = new ThreadLocal<BASE64Encoder>();

    static {
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
        }
    }

    private RSAPublicKey publicKey;

    private ThreadLocal<Cipher> publicCipherThreadLocal = new ThreadLocal<Cipher>();

    private ThreadLocal<Cipher> publicDecryptCipherThreadLocal = new ThreadLocal<Cipher>();

    private int keyLen;

    private int keyLen2;

    private RSAPrivateKey privateKey;

    private ThreadLocal<Cipher> privateCipherThreadLocal = new ThreadLocal<Cipher>();

    private ThreadLocal<Cipher> privateEncryptCipherThreadLocal = new ThreadLocal<Cipher>();

    private int privateKeyLen;

    private int privateKeyLen2;

    public RSA() {
    }

    public RSA(RSAPublicKey publicKey) {
        setPublicKey(publicKey);
    }

    public RSA(RSAPrivateKey privateKey) {
        setPrivateKey(privateKey);
    }

    public RSA(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        if (publicKey != null)
            setPublicKey(publicKey);
        if (privateKey != null)
            setPrivateKey(privateKey);
    }

    public void setPublicKey(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
        keyLen2 = publicKey.getModulus().bitLength() / 8;
        keyLen = keyLen2 - 11;
    }

    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
        privateKeyLen2 = privateKey.getModulus().bitLength() / 8;
        privateKeyLen = privateKeyLen2 - 11;
    }

    /**
      * 实例化公钥
      */
    public static RSAPublicKey getPublicKey(String pubKey) {
        try {
            byte[] encodedKey = getBASE64Decoder().decodeBuffer(pubKey);
            KeySpec keySpec = new X509EncodedKeySpec(encodedKey);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static CharacterDecoder getBASE64Decoder() {
        BASE64Decoder base64Decoder = base64DecoderThreadLocal.get();
        if (base64Decoder == null) {
            base64Decoder = new BASE64Decoder();
            base64DecoderThreadLocal.set(base64Decoder);
        }
        return base64Decoder;
    }

    public static RSAPublicKey getPublicKey() {
        return getPublicKey(PUBLIC_KEY);
    }

    /**
     * 实例化私钥
     */
    public static RSAPrivateKey getPrivateKey(String priKey) {
        try {
            byte[] encodedKey = getBASE64Decoder().decodeBuffer(priKey);
            KeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPrivateKey getPrivateKey() {
        return getPrivateKey(PRIVATE_KEY);
    }

    /** 
     * 生成公钥和私钥 
     */
    public static RSAStringKeyPair generateKeys() {
        RSAStringKeyPair stringKeyPair = new RSAStringKeyPair();
        try {
            KeyPairGenerator keyPairGen;
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            String publicKey = getBASE64Encoder().encodeBuffer(keyPair.getPublic().getEncoded());
            String privateKey = getBASE64Encoder().encodeBuffer(keyPair.getPrivate().getEncoded());
            stringKeyPair.setPrivateKey(privateKey);
            stringKeyPair.setPublicKey(publicKey);
        } catch (NoSuchAlgorithmException e) {
        }
        return stringKeyPair;
    }

    private static CharacterEncoder getBASE64Encoder() {
        BASE64Encoder base64Encoder = base64EncoderThreadLocal.get();
        if (base64Encoder == null) {
            base64Encoder = new BASE64Encoder();
            base64EncoderThreadLocal.set(base64Encoder);
        }
        return base64Encoder;
    }

    /** 
     * 公钥加密 
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) {
        return new RSA(publicKey, null).encryptByPublicKey(data);
    }

    /** 
     * 私钥解密 
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) {
        return new RSA(privateKey).decryptByPrivateKey(data);
    }

    /** 
     * 公钥加密 
     */
    public String encryptByPublicKey(String data) {
        Cipher cipher = getPublicEncryptCipher();
        StringBuilder sb = new StringBuilder();
        byte[] bytes = data.getBytes(UTF8);
        int len = bytes.length > keyLen ? keyLen : bytes.length;
        for (int i = 0; i < bytes.length; i += keyLen) {
            len = len > bytes.length - i ? bytes.length - i : len;
            byte[] output = doFinal(bytes, i, len, cipher);
            sb.append(getBASE64Encoder().encodeBuffer(output));
        }
        return sb.toString();
    }

    private Cipher getPublicEncryptCipher() {
        Cipher cipher = publicCipherThreadLocal.get();
        if (cipher == null) {
            try {
                cipher = Cipher.getInstance("RSA");
                publicCipherThreadLocal.set(cipher);
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return cipher;
    }

    /** 
     * 私钥加密 
     */
    public String encryptByPrivateKey(String data) {
        Cipher cipher = getPrivateEncryptCipher();
        StringBuilder sb = new StringBuilder();
        byte[] bytes = data.getBytes(UTF8);
        int len = bytes.length > privateKeyLen ? privateKeyLen : bytes.length;
        for (int i = 0; i < bytes.length; i += privateKeyLen) {
            len = len > bytes.length - i ? bytes.length - i : len;
            byte[] output = doFinal(bytes, i, len, cipher);
            sb.append(getBASE64Encoder().encodeBuffer(output));
        }
        return sb.toString();
    }

    private Cipher getPrivateEncryptCipher() {
        Cipher cipher = privateEncryptCipherThreadLocal.get();
        if (cipher == null) {
            try {
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, privateKey);
                privateEncryptCipherThreadLocal.set(cipher);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return cipher;
    }

    /** 
     * 公钥解密 
     */
    public String decryptByPublicKey(String data) {
        Cipher cipher = getPublicDecryptCipher();
        byte[] bytes = decodeBuffer(data);
        List<byte[]> out = Lists.newLinkedList();
        int len = 0;
        for (int i = 0; i < bytes.length; i += keyLen2) {
            int length = keyLen2 > bytes.length - i ? bytes.length - i : keyLen2;
            byte[] src = doFinal(bytes, i, length, cipher);
            out.add(src);
            len += src.length;
        }
        byte[] bs = new byte[len];
        int destPos = 0;
        for (byte[] e : out) {
            System.arraycopy(e, 0, bs, destPos, e.length);
            destPos += e.length;
        }
        return new String(bs, UTF8);
    }

    private byte[] doFinal(byte[] input, int inputOffset, int inputLen, Cipher cipher) {
        try {
            return cipher.doFinal(input, inputOffset, inputLen);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Cipher getPublicDecryptCipher() {
        Cipher cipher = publicDecryptCipherThreadLocal.get();
        if (cipher == null) {
            try {
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, publicKey);
                publicDecryptCipherThreadLocal.set(cipher);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return cipher;
    }

    /** 
     * 私钥解密 
     */
    public String decryptByPrivateKey(String data) {
        Cipher cipher = getPrivateDecryptCipher();
        byte[] bytes = decodeBuffer(data);
        List<byte[]> out = Lists.newLinkedList();
        int len = 0;
        for (int i = 0; i < bytes.length; i += privateKeyLen2) {
            int length = privateKeyLen2 > bytes.length - i ? bytes.length - i : privateKeyLen2;
            byte[] src = doFinal(bytes, i, length, cipher);
            out.add(src);
            len += src.length;
        }
        byte[] bs = new byte[len];
        int destPos = 0;
        for (byte[] e : out) {
            System.arraycopy(e, 0, bs, destPos, e.length);
            destPos += e.length;
        }
        return new String(bs, UTF8);
    }

    private byte[] decodeBuffer(String data) {
        try {
            return getBASE64Decoder().decodeBuffer(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Cipher getPrivateDecryptCipher() {
        Cipher cipher = privateCipherThreadLocal.get();
        if (cipher == null) {
            try {
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                privateCipherThreadLocal.set(cipher);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return cipher;
    }

    public static class RSAStringKeyPair {

        private String publicKey;

        private String privateKey;

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

    }

    public static void main(String[] args) throws UnsupportedEncodingException {
    	
    	String queryData = "{\"USER_ACCOUNT\":\"61406015\",\"BEGINTIME\":\"1480776260\",\"ENDTIME\":\"1480776261\"}";
    	
    	String monitorData = "{\"USER_INTENRALID\":\"1\"}";
    	
    	RSA rsa1 = new RSA(getPublicKey(),getPrivateKey());
    	System.out.println(rsa1.encryptByPublicKey(queryData));
//    		172.31.19.81
    	String curl1 = "curl http://192.168.9.195:3387/wa/query?opCode=QUERYPAYMENTORDER\\&param={0}";
    	String curl2 = "curl http://192.168.9.195:3387/wa/monitor?opCode=ADDMONITORCHATSTATUS\\&param={0}";
//    	String curl2 = "http://172.31.19.81:8080/wa/query?opCode=QUERYACCOUNT&param={0}";
//    	System.out.println(curl2.replace("{0}",rsa1.encryptByPublicKey(data)));
    	System.out.println(curl1.replace("{0}", URLEncoder.encode(rsa1.encryptByPublicKey(queryData),"UTF-8")));
    	System.out.println(curl2.replace("{0}", URLEncoder.encode(rsa1.encryptByPublicKey(monitorData),"UTF-8")));
//    	System.out.println(curl2.replace("{0}", URLEncoder.encode(rsa1.encryptByPublicKey(data),"UTF-8")));
    	
    	
    }

}
