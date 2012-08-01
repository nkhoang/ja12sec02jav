package com.nkhoang.gae.utils.web;

import com.nkhoang.gae.utils.FileUtils;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Special class supports downloading files from an URL.
 */
public class DownloadUtils {
    private static final Logger LOG = LoggerFactory
            .getLogger(DownloadUtils.class.getCanonicalName());

    final static int size = 1024;

    /**
     * Get all image URLs.
     *
     * @param theUrl the url to fetch images.
     * @return the list of image urls.
     */
    private static List<String> getImageUrls(String theUrl) {
        List<String> imageLinks = new ArrayList<String>();

        try {
            LOG.info(String.format("Connecting to ... [%s] ", theUrl));
            URL url = new URL(theUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");

            // get inputStream
            InputStream is = connection.getInputStream();
            // create source HTML
            Source source = new Source(is);
            if (source != null) {
                // for TDDB
                //List<Element> links = source.getAllElementsByClass("xlazyload");
                // for Naruto
                List<Element> links = source.getAllElements("img");
                for (Element element : links) {
                    String link = element.getStartTag().getAttributeValue("src");
                    imageLinks.add(link);
                }
            }
        } catch (Exception e) {
            LOG.error("Could not fetch image urls from this url: " + theUrl);
        }

        return imageLinks;
    }

    /**
     * Save image file from an url. Will be polished in the future.
     *
     * @param theUrl    the url to fetch all images.
     * @param targetDir the target directory to save all images.
     */
    public static void saveImages(String theUrl, String targetDir) {
        List<String> imageLinks = getImageUrls(theUrl);
        String previousName = null;

        if (CollectionUtils.isNotEmpty(imageLinks)) {
            int nameSuffix = 0;
            for (String imageLink : imageLinks) {
                // first we need to get the local file name.
                String localFileName = imageLink.substring(imageLink.lastIndexOf("/") + 1);
                // sometime all images in the same page have the same name so we need to append something else to the local name
                if (StringUtils.isNotBlank(previousName)) {
                    if (previousName.trim().equalsIgnoreCase(localFileName)) {
                        String[] fileNameParts = StringUtils.split(localFileName, ".");
                        localFileName = fileNameParts[0] + "_" + nameSuffix + "." + fileNameParts[1];
                        nameSuffix++;
                    } else {
                        nameSuffix = 0;
                        previousName = localFileName;
                    }
                } else {
                    nameSuffix = 0;
                    previousName = localFileName;
                }
                try {
                    saveFile(imageLink, localFileName, targetDir);
                } catch (FileNotFoundException fnfe) {
                    LOG.info("Don't panic");
                }
            }
        }
    }

    /**
     * Need to create a new chapter folder to store the new
     *
     * @param chapterUrl
     * @param stringToCheck
     * @param rootDir
     * @return the folder path.
     */
    private static String createFolderForChapter(String chapterUrl, String stringToCheck, String rootDir) {
        if (StringUtils.containsIgnoreCase(chapterUrl, stringToCheck)) {
            String chapterName =
                    chapterUrl.substring(chapterUrl.lastIndexOf(stringToCheck) + stringToCheck.length() + 1);
            if (chapterName.contains(".")) {
                // remove all dot.
                chapterName = chapterName.substring(0, chapterName.lastIndexOf("."));
            }
            String folderPath = rootDir + "/" + chapterName;
            boolean success = (new File(folderPath)).mkdir();

            if (success) {
                return folderPath + "/";
            }
        }
        return null;
    }

    public static void getCosmicBook(String theUrl, String rootDir) {
        LOG.info("# Get cosmic book from URL: " + theUrl);
        List<String> chapterLinks = getAllChapters(theUrl);
        for (int i = 0; i < chapterLinks.size() - 1; i++) {
            String folderPath = createFolderForChapter(chapterLinks.get(i), "tam-tan-ky-chap", rootDir);
            if (StringUtils.isNotEmpty(folderPath)) {
                saveImages(chapterLinks.get(i), folderPath);
            }
        }
    }


    /**
     * I want to get all chapters of a cosmic book.
     *
     * @param theUrl the url to get all chapter links.
     */
    public static List<String> getAllChapters(String theUrl) {
        LOG.info("-- # Get all chapters....");
        List<String> chapterLinks = new ArrayList<String>();
        try {
            LOG.debug(String.format("Connecting to ... [%s] ", theUrl));
            URL url = new URL(theUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");

            // get inputStream
            InputStream is = connection.getInputStream();
            // create source HTML
            Source source = new Source(is);
            if (source != null) {
                Element chapterContainer = source.getElementById("zoomtext");
                List<Element> links = chapterContainer.getAllElements("a");
                for (Element element : links) {
                    // first get 'href' property to check if it is _blank
                    String targetValue = element.getStartTag().getAttributeValue("target");
                    if (StringUtils.isNotBlank(targetValue) && targetValue.equalsIgnoreCase("_blank")) {
                        String hrefValue = element.getStartTag().getAttributeValue("href");
                        if (hrefValue.contains("vechai.info")) {
                            chapterLinks.add(hrefValue);
                        }
                    }

                }
            }
        } catch (Exception e) {
            LOG.error("Could not fetch image urls from this url: " + theUrl);
        }

        return chapterLinks;
    }

    /**
     * Save file from an URL to local disk path: {@code destinationDir } with the file name as {@code localFileName}
     *
     * @param fAddress       the address URL.
     * @param localFileName  the local file name.
     * @param destinationDir the destination directory.
     */
    public static void saveFile(String fAddress, String
            localFileName, String destinationDir) throws FileNotFoundException {
        OutputStream outStream = null;
        URLConnection uCon = null;

        InputStream is = null;
        try {
            URL Url;
            byte[] buf;
            int ByteRead, ByteWritten = 0;
            Url = new URL(fAddress);
            outStream = new BufferedOutputStream(new
                    FileOutputStream(destinationDir + "/" + localFileName));

            uCon = Url.openConnection();
            is = uCon.getInputStream();
            buf = new byte[size];
            while ((ByteRead = is.read(buf)) != -1) {
                outStream.write(buf, 0, ByteRead);
                ByteWritten += ByteRead;
            }
            LOG.info("Downloaded Successfully.");
            LOG.info
                    ("File name:\"" + localFileName + "\"\n No of bytes :" + ByteWritten);
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();

                if (outStream != null)
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
    public static void fileDownload(String fAddress, String destinationDir) throws Exception {

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
                try {
                    saveFile(fAddress, realName, destinationDir);
                } catch (Exception e) {

                }
            }
        } else {
            LOG.error("path or file name.");
        }
    }

}
