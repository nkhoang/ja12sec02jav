package com.nkhoang.gae.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an item.
 * 
 * @author hnguyen93
 * 
 */
@SuppressWarnings({"JpaAttributeTypeInspection"})
@Entity
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // id of an item.
    @Basic
    private String code; // item code.
    @Basic
    private String description; // description for this item.
    @Basic
    private Long price; // price.
    @Basic
    private String thumbnail; // a picture represents for an item.
    @Basic
    private String thumbnailBig; // a mouse hover picture.
    @Basic
    private List<Long> pictureIds = new ArrayList<Long>(0);// @off list of gallery picture ids.
    @Basic
    private String deletedFlag = DELETED_N; // flag that dedicate an item has been deleted.
    @Basic
    private Date dateAdded; // Added date for an item.
    @Basic
    private Date deletedDate; // @off The date that an item has been deleted. Used for statistic.
    @Basic
    private int quantity; // Number of items in store.
    @Basic
    private String postId; // Facebook post id.
    @Basic
    private List<Long> categoryIds = new ArrayList<Long>(0); // Category Ids for this item.

    private List<String> subPictures = new ArrayList<String>(0); // @off A list contains gallery pictures of this item. @on

    // static constants
    public static final String DELETED_Y = "Y";
    private static final String DELETED_N = "N";
    // @off
    public static final String SKIP_FIELDS[] = { 
        "jdoDetachedState", 
        "pictureIds", 
        "deleted", 
        "dateAdded",
        "deletedDate", 
        "quantity", 
        "categoryIds", 
        "postId" 
    }; //holds a list of fields which will be skipped from being serialized to JSON value.

    // @on
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Long> getPictureIds() {
        return pictureIds;
    }

    public void setPictureIds(List<Long> pictureIds) {
        this.pictureIds = pictureIds;
    }

    public List<String> getSubPictures() {
        return subPictures;
    }

    public void setSubPictures(List<String> subPictures) {
        this.subPictures = subPictures;
    }

    public String getThumbnailBig() {
        return thumbnailBig;
    }

    public void setThumbnailBig(String thumbnailBig) {
        this.thumbnailBig = thumbnailBig;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(String deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
