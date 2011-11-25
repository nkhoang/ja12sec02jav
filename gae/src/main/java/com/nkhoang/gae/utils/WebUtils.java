package com.nkhoang.gae.utils;

import com.nkhoang.gae.exception.GAEException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.StringWriter;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WebUtils {
  private static final Logger LOG = LoggerFactory.getLogger(WebUtils.class.getCanonicalName());

  public static final String DEFAULT_WORD_DATE_FORMAT = "dd/MM/yyyy hh:mm";
  public static final String DEFAULT_CLIENT_DATE_FORMAT = "dd/MM/yyyy";
  public static SimpleDateFormat _formatter = new SimpleDateFormat(
      DEFAULT_WORD_DATE_FORMAT, Locale.US);
  // The algorithm used to authenticate internal communication.
  private static final String ALGORITHM_DSA = "DSA";
  // The private key used to generate DSA signature.
  private static final String ALGORITHM_DSA_PRIVATE_KEY
      = "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1_U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq_xfW6MPbLm1Vs14E7gB00b_JmYLdrmVClpJ-f6AR7ECLCT7up1_63xhv4O1fnxqimFQ8E-4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC_BYHPUCgYEA9-GghdabPd7LvKtcNrhXuXmUr7v6OuqC-VdMCz0HgmdRWVeOutRZT-ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN_C_ohNWLx-2J6ASQ7zKTxvqhRkImog9_hWuWfBpKLZl6Ae1UlZAFMO_7PSSoEFgIUVgB6nRdSF24K0hh_cR_PzAmnwjU";
  private static final String ALGORITHM_DSA_PUBLIC_KEY
      = "MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1_U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq_xfW6MPbLm1Vs14E7gB00b_JmYLdrmVClpJ-f6AR7ECLCT7up1_63xhv4O1fnxqimFQ8E-4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC_BYHPUCgYEA9-GghdabPd7LvKtcNrhXuXmUr7v6OuqC-VdMCz0HgmdRWVeOutRZT-ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN_C_ohNWLx-2J6ASQ7zKTxvqhRkImog9_hWuWfBpKLZl6Ae1UlZAFMO_7PSSoDgYUAAoGBAPY1Lsym-QK474xbuQ_TwEBn-Mh5HkqJPDck1NDvjR3-QPDa3Meuf_QOjRoxQw60qmM1HxNgio20t6QReHeIKWwGHtq0PTnoUYHj72aKpDdiP8CHi5VPBh9wyyihP0uVB6yZVfP1w7SPfbZSzQmeWn6lSIl4LdJJ0Iuh0dql72sl";


  /**
   * Create signature for internal WS by using DSA algorithm.
   *
   * @param signatureValue the signature to be encrypted.
   * @return encrypted signature.
   * @throws GAEException any exception is wrapped in {@link GAEException}.
   */
  public static String createWSSignature(String signatureValue) throws GAEException {
    String encryptedSig = null;
    try {
      KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_DSA);
      byte[] privateKeyBytes = Base64.decodeBase64(ALGORITHM_DSA_PRIVATE_KEY);
      EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

      Signature sig = Signature.getInstance("DSA");
      sig.initSign(keyFactory.generatePrivate(privateKeySpec));
      sig.update(signatureValue.getBytes());

      encryptedSig = Base64.encodeBase64URLSafeString(sig.sign());
    } catch (NoSuchAlgorithmException nsaex) {
      throw new GAEException("Could not find DSA algorithm.", 1, nsaex);
    } catch (InvalidKeySpecException invalidKeyEx) {
      throw new GAEException("Invalid private key for DSA algorithm.", 2, invalidKeyEx);
    } catch (InvalidKeyException invalidKeyEx) {
      throw new GAEException("Invalid private key for DSA algorithm.", 2, invalidKeyEx);
    } catch (SignatureException signatureEx) {
      throw new GAEException("Could not create signature with information provided.", 3, signatureEx);
    }
    return encryptedSig;
  }

  /**
   * Verify signature and compare it with the valid signature.
   *
   * @param validSignature    the valid signature.
   * @param receivedSignature the signature to be checked.
   * @return true if the signature is valid otherwise false.
   * @throws GAEException if any.
   */
  public static boolean verifySignature(String validSignature, String receivedSignature) throws GAEException {
    boolean result = false;
    try {
      Signature signatureFactory = Signature.getInstance(ALGORITHM_DSA);
      KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_DSA);
      byte[] publicKeyBytes = Base64.decodeBase64(ALGORITHM_DSA_PUBLIC_KEY);
      EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
      PublicKey decodePublicKey = keyFactory.generatePublic(publicKeySpec);
      signatureFactory.initVerify(decodePublicKey);
      signatureFactory.update(validSignature.getBytes());

      result = signatureFactory.verify(Base64.decodeBase64(receivedSignature));
    } catch (NoSuchAlgorithmException nsaex) {
      throw new GAEException("Could not find DSA algorithm.", 1, nsaex);
    } catch (InvalidKeySpecException invalidKeyEx) {
      throw new GAEException("Invalid private key for DSA algorithm.", 2, invalidKeyEx);
    } catch (InvalidKeyException invalidKeyEx) {
      throw new GAEException("Invalid private key for DSA algorithm.", 2, invalidKeyEx);
    } catch (SignatureException signatureEx) {
      throw new GAEException("Could not create signature with information provided.", 3, signatureEx);
    }

    return result;
  }

  /**
   * Convert from date in string format to Date object in specific pattern specified by <i></i>
   *
   * @param date       the date to be converted.
   * @param dateFormat format string.
   * @return a Date object.
   * @throws ParseException if failing to convert date string to Date object.
   */
  public static Date parseDate(String date, String dateFormat) throws ParseException {
    _formatter = new SimpleDateFormat(dateFormat, Locale.US);
    _formatter.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
    return _formatter.parse(date);
  }

  public static String sendMail(
      String msgBody, String senderEmail, String subject, String recipEmail) {
    String message = "";
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(senderEmail));
      msg.addRecipient(
          Message.RecipientType.TO, new InternetAddress(recipEmail));
      msg.setSubject(subject);
      msg.setContent(msgBody, "text/html");
      Transport.send(msg);
    } catch (MessagingException mse) {
      message = mse.getMessage();
      LOG.error(String.format("Could not send email to [%s].", recipEmail), mse);
    }
    return message;
  }

  /**
   * Build mail body message from a template by providing these following information:
   *
   * @param context      the {@link javax.servlet.ServletContext} object to get the template path.
   * @param templateName the template name used to build the message body.
   * @param data         the root data for templating.
   * @return a message body based on a specific template.
   */
  public static String buildMail(ServletContext context, String templateName, Map<String, Object> data) {
    try {
      Template template = TemplateUtils.getConfiguration(context).getTemplate(templateName);
      StringWriter writer = new StringWriter();
      template.process(data, writer);

      return writer.toString();
    } catch (IOException ioe) {
      LOG.error(String.format("Could not load template '[%s]'", templateName), ioe);
    } catch (TemplateException tpe) {
      LOG.error("Could not build template because: ", tpe);
    }
    return "";
  }

  public static KeyPair generateKeyPair() throws GAEException {
    KeyPair keyPair = null;
    try {
      // Generate a 1024-bit Digital Signature Algorithm (DSA) key pair
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM_DSA);
      keyGen.initialize(1024);
      keyPair = keyGen.genKeyPair();
    } catch (NoSuchAlgorithmException nsaex) {
      throw new GAEException("Could not find DSA algorithm.", 1, nsaex);
    }
    return keyPair;
  }

  public static String encodePrivateKey(KeyPair keyPair) {
    PrivateKey privateKey = keyPair.getPrivate();
    byte[] privateKeyEncoded = privateKey.getEncoded();
    return Base64.encodeBase64URLSafeString(privateKeyEncoded);

  }

  public static String encodePublicKey(KeyPair keyPair) {
    PublicKey publicKey = keyPair.getPublic();
    byte[] publicKeyEncoded = publicKey.getEncoded();
    return Base64.encodeBase64URLSafeString(publicKeyEncoded);
  }

}
