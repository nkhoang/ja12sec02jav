package com.nkhoang.gae.model;

import javax.persistence.*;

/**
 * Represents an gallery picture.
 * 
 * @author hnguyen93
 * 
 */
@Entity
public class ItemPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // id of an item.

    @Basic
    private String description; // a description for a picture.
    //@off
    @Basic
    private String url; // url to the image of this picture. 

    @Basic
    private Long itemId; // id of the item to which the picture belongs.

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

}
