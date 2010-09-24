/**
 * 
 */
package com.nkhoang.gae.service;

/**
 * <PRE>
 * Facebook functions service.
 * </PRE>
 * 
 * @version 1.0.0
 */
public interface FacebookService {

	/**
	 * Post a new content
	 * 
	 * @param caption caption.
	 * @param description description.
	 * @param imgURL image url.
	 * @param name name.
	 * @param linkToImg a link to image.
     * @param linkToWebsite a linke to website.
     * @return returned String.	 
	 */
	public String postContent( String caption,
			String description, String imgURL, String name, String linkToImg,
			String linkToWebsite);

	public boolean removePost(String postID);

}
