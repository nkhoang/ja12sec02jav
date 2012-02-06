package com.nkhoang.gae.utils.web;

import com.nkhoang.gae.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/** Special class supports downloading files from an URL. */
public class DownloadUtils {
   private static final Logger LOG = LoggerFactory
         .getLogger(DownloadUtils.class.getCanonicalName());

   final static int size = 1024;

   /**
    * Save file from an URL to local disk path: {@code destinationDir } with the file name as {@code localFileName}
    *
    * @param fAddress       the address URL.
    * @param localFileName  the local file name.
    * @param destinationDir the destination directory.
    */
   public static void saveFile(String fAddress, String
         localFileName, String destinationDir) {
      OutputStream outStream = null;
      URLConnection uCon = null;

      InputStream is = null;
      try {
         URL Url;
         byte[] buf;
         int ByteRead, ByteWritten = 0;
         Url = new URL(fAddress);
         outStream = new BufferedOutputStream(new
               FileOutputStream(destinationDir + "\\" + localFileName));

         uCon = Url.openConnection();
         is = uCon.getInputStream();
         buf = new byte[size];
         while ((ByteRead = is.read(buf)) != -1) {
            outStream.write(buf, 0, ByteRead);
            ByteWritten += ByteRead;
         }
         LOG.info("Downloaded Successfully.");
         LOG.info
               ("File name:\"" + localFileName + "\"\nNo ofbytes :" + ByteWritten);
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            is.close();
            outStream.close();
         } catch (IOException e) {

         }
      }
   }

   /**
    * The main method to download file from an URL.
    *
    * @param fAddress       the address to download file.
    * @param destinationDir the destination folder.
    */
   public static void fileDownload(String fAddress, String destinationDir) {

      int slashIndex = fAddress.lastIndexOf('/');
      int periodIndex = fAddress.lastIndexOf('.');

      String fileName = fAddress.substring(slashIndex + 1);

      if (periodIndex >= 1 && slashIndex >= 0
            && slashIndex < fAddress.length() - 1) {
         saveFile(fAddress, fileName, destinationDir);
      } else {
         LOG.error("path or file name.");
      }
   }

   /**
    * Download file using the name specified.
    *
    * @param fAddress       the address to the file.
    * @param fileName       the file name.
    * @param destinationDir the destination URL.
    */
   public static void fileDownload(String fAddress, String fileName, String destinationDir) {
      int slashIndex = fAddress.lastIndexOf('/');
      int periodIndex = fAddress.lastIndexOf('.');
      if (periodIndex >= 1 && slashIndex >= 0
            && slashIndex < fAddress.length() - 1) {

         String realName = fAddress.substring(slashIndex + 1);
         realName = realName.replace(realName.substring(0, realName.lastIndexOf('.')), fileName);

         if (FileUtils.checkFileExistence(destinationDir + "\\" + realName)) {
            LOG.info("File is existing.");
         } else {
            saveFile(fAddress, realName, destinationDir);
         }
      } else {
         LOG.error("path or file name.");
      }
   }

}
