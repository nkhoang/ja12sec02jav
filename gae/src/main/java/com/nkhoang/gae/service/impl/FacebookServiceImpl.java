/**
 * 
 */
package com.nkhoang.gae.service.impl;

import com.google.code.facebookapi.Attachment;
import com.google.code.facebookapi.AttachmentMediaImage;
import com.google.code.facebookapi.FacebookJsonRestClient;
import com.nkhoang.gae.service.FacebookService;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FacebookServiceImpl implements FacebookService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FacebookServiceImpl.class);
 
	private String username;
	
	private String password;
	
	private String apiKey;

	private String apiSerect;

	private String facebookSessionId;

	private String urlLogin = "https://login.facebook.com/login.php?login_attempt=1";

	// @off
	/**
	 * Remove Facebook post.
	 * 
	 * @return true or false.
	 */
	// @on
	public boolean removePost(String postID) {
		LOGGER.info("Removing Facebook post from [username:" + username
				+ ", postId: " + postID + "].");
		boolean result = false;
		try {
			// String sessionId = "a2ba67b724a6814af1d5a257-100001077626983";
			String facebookId = username;
			String facebookPass = password;

			if (!GenericValidator.isBlankOrNull(facebookId)
					&& !GenericValidator.isBlankOrNull(facebookPass)) {

                FacebookJsonRestClient facebookClient = new FacebookJsonRestClient(
                        apiKey, apiSerect, facebookSessionId);

				String userId = postID.split("_")[0];

				result = facebookClient.stream_remove(postID,
						Long.parseLong(userId));
			} else {
				LOGGER.info("LOGIN - You have not set facebook account yet.");
			}
		} catch (Exception e) {
			LOGGER.info("Failed to remove Facebook post from [username:"
					+ username + ", postId: " + postID + "].");
			LOGGER.error("Error", e);
		}
		return result;
	}

	// @off
	/**
	 * Post content to Facebook.
	 * 
	 * @return post id string. or null.
	 */
	// @on
	public String postContent(String caption,
			String description, String imgURL, String name, String linkToImg,
			String linkToWebsite) {
		LOGGER.info("Posting content from [username:" + username + "].");
		// String sessionId = "a2ba67b724a6814af1d5a257-100001077626983";
		String postId = null;
		try {
			String facebookId = username;
			String facebookPass = password;
			if (!GenericValidator.isBlankOrNull(facebookId)
					&& !GenericValidator.isBlankOrNull(facebookPass)) {
				// create facebook client
				FacebookJsonRestClient client = new FacebookJsonRestClient(
						apiKey, apiSerect, facebookSessionId);

				Attachment attachment = new Attachment();
				if (caption != null) {
					attachment.setCaption(caption);
				}
				if (description != null) {
					attachment.setDescription(description);
				}
				if (imgURL != null) {
					attachment.setHref(imgURL);
				}
				if (name != null) {
					attachment.setName(name);
				}

				AttachmentMediaImage attach_media = new AttachmentMediaImage(
						linkToImg, linkToWebsite);

				attachment.setMedia(attach_media);

				postId = client.stream_publish("New Arrival", attachment, null,
						null, null);
				LOGGER.info("Posted Successfully [postId:" + postId + "].");
			}
		} catch (Exception e) {
			LOGGER.info("Failed to post content from [username:" + username
					+ "].");
			LOGGER.error("Error", e);
		}
		return postId;
	}

	public final void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public final void setApiSerect(String apiSerect) {
		this.apiSerect = apiSerect;
	}

	private Date parseToDate(String str) {
		Date parsed = null;
		SimpleDateFormat df = new SimpleDateFormat("MMMMM dd, yyyy");
		if (str != null && str.contains(",")) {
			try {
				parsed = df.parse(str);
			} catch (ParseException e) {
                LOGGER.error("parse Date exception.");
			}
		}
		return parsed;
	}

	public void setFacebookSessionId(String facebookSessionId) {
		this.facebookSessionId = facebookSessionId;
	}

	public String getFacebookSessionId() {
		return facebookSessionId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

}
