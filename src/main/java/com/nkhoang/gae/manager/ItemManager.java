package com.nkhoang.gae.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nkhoang.gae.dao.CategoryDao;
import com.nkhoang.gae.dao.ItemDao;
import com.nkhoang.gae.dao.ItemPictureDao;
import com.nkhoang.gae.model.Category;
import com.nkhoang.gae.model.Item;
import com.nkhoang.gae.model.ItemPicture;
import com.nkhoang.gae.service.FacebookService;

/**
 * Contain any business logic which work with item.
 * 
 * @author hoangnk
 * 
 */
public class ItemManager implements BaseManager<Item, Long> {
    private static final String ITEM_ALL_ID = "all_item";
    private static final Logger LOGGER = Logger.getLogger(ItemManager.class);

    private ItemDao itemDao;
    private ItemPictureDao itemPictureDao;
    private FacebookService facebookService;
    private CategoryDao categoryDao;

    public Item get(Long id) {
        LOGGER.info("Get item with [id:" + id + "].");
        Item item = itemDao.get(id);
        if (item != null) {
            List<String> itemPicURLs = new ArrayList<String>(0);
            for (Long ipId : item.getPictureIds()) {
                ItemPicture ip = itemPictureDao.get(ipId);
                if (ip != null) {
                    itemPicURLs.add(ip.getUrl());
                }
            }
            item.setSubPictures(itemPicURLs);

        }
        return item;
    }

    public boolean delete(Long id) {
        boolean result = itemDao.delete(id);
        if (result) {
            DataCenter.statusChanged(ITEM_ALL_ID);
        }
        return result;
    }

    public List<ItemPicture> getAllItemPictures() {
        return itemPictureDao.getAll();
    }

    public Item update(Item o) {
        return itemDao.update(o);
    }

    public boolean markDeleted(Long id) {
        Item item = itemDao.get(id);
        boolean removeResult = false;
        if (item != null) {
            boolean result = itemDao.markDeleted(id);
            if (result) {
                // starting facebook post removing
                if (item.getPostId() != null) {
                    removeResult = facebookService.removePost(item.getPostId());
                }

                DataCenter.statusChanged(ITEM_ALL_ID);
            }
        } else {
            LOGGER.info("Marked deleted process end. [no item found].");
        }
        return removeResult;
    }

    // @off
    /**
     * Save item sub pictures.
     * 
     * @param url
     *            url for the picture.
     * @return an obj
     * 		   or 
     *         null.
     */
	// @on
    public ItemPicture saveItemPicture(String url) {
        // create a new item picture
        ItemPicture itemPic = new ItemPicture();
        itemPic.setUrl(url);

        return itemPictureDao.save(itemPic);
    }

    public Item save(Item o) {
        // update item cache
        if (DataCenter.getData(ITEM_ALL_ID) == null) {
        	LOGGER.info("First tme cache.");
            DataCenter.updateCache(ITEM_ALL_ID, null);
        }
        DataCenter.statusChanged(ITEM_ALL_ID);

        return itemDao.save(o);
    }

    /**
     * Save an object.
     * 
     * @param o
     *            obj to be saved.
     * @return an obj or null.
     */
    public ItemPicture save(ItemPicture o) {
        return itemPictureDao.save(o);
    }

    /**
     * Save a new category
     * 
     * @param cat
     *            category to be saved.
     * @return an obj or null.
     */
    public Category saveCategory(Category cat) {
        return categoryDao.save(cat);
    }

    /**
     * List all items.
     * 
     * @return a list or an empty list.
     */
    public List<Item> listAll() {
        boolean mustChange = true;
        List<Item> items = new ArrayList<Item>(0);
        // get data from data center first

        if (DataCenter.getData(ITEM_ALL_ID) != null) {
            if (!DataCenter.getModifiedStatus(ITEM_ALL_ID)) {
                LOGGER.info("Cache found for all items... loading ...");
                items = (List<Item>) DataCenter.getData(ITEM_ALL_ID);
                mustChange = false;
            }
        }
        if (mustChange) {
            LOGGER.info("Update cache for all items ...");
            // populate data
            items = itemDao.getAll();

            if (items != null) {
                for (Item i : items) {
                    List<String> itemPicURLs = new ArrayList<String>(0);
                    for (Long ipId : i.getPictureIds()) {
                        ItemPicture ip = itemPictureDao.get(ipId);
                        if (ip != null) {
                            itemPicURLs.add(ip.getUrl());
                        }
                    }
                    i.setSubPictures(itemPicURLs);
                }

                DataCenter.updateCache(ITEM_ALL_ID, items);
            } else {
                items = new ArrayList<Item>(0);
            }
        }

        return items;
    }

    /**
     * List all items ( deleted included ).
     * 
     * @return a list or an empty list.
     */
    public List<Item> listAllWithDeleted() {
        boolean mustChange = true;
        List<Item> items = new ArrayList<Item>(0);
        // get data from data center first

        if (DataCenter.getData(ITEM_ALL_ID) != null) {
            if (!DataCenter.getModifiedStatus(ITEM_ALL_ID)) {
                LOGGER.info("Cache found for all items... loading ...");
                items = (List<Item>) DataCenter.getData(ITEM_ALL_ID);
                mustChange = false;
            }
        }
        if (mustChange) {
            LOGGER.info("Update cache for all items ...");
            // populate data
            items = itemDao.getAllWithDeleted();
            if (items != null) {
                for (Item i : items) {
                    List<String> itemPicURLs = new ArrayList<String>(0);
                    for (Long ipId : i.getPictureIds()) {
                        ItemPicture ip = itemPictureDao.get(ipId);
                        if (ip != null) {
                            itemPicURLs.add(ip.getUrl());
                        }
                    }
                    i.setSubPictures(itemPicURLs);
                }

                DataCenter.updateCache(ITEM_ALL_ID, items);
            } else {
                items = new ArrayList<Item>(0);
            }
        }
        return items;
    }

    /**
     * Mark all items as deleted.
     * 
     * @return true or false.
     */
    public boolean markDeletedAll() {
        LOGGER.info("Mark deleted all item ...");
        boolean result = false;
        List<Item> items = listAll();
        for (Item item : items) {
            itemDao.markDeleted(item.getId());
        }
        List<Item> loadedItems = itemDao.getAll();
        if (loadedItems != null) {
            int size = itemDao.getAll().size();
            if (size == 0) {
                result = true;
            }

            if (result) {
                DataCenter.statusChanged(ITEM_ALL_ID);
            }
        }

        return result;
    }

    /**
     * Clear all or delete all items.
     * 
     * @return true or false.
     */
    public boolean clearAll() {
        LOGGER.info("Clear all item ...");
        boolean result = false;
        List<Item> items = listAll();
        // delete item first ? - no problem because datastore don't have
        // relation
        for (Item item : items) {
            itemDao.delete(item.getId());
        }
        // delete item pictures.
        List<ItemPicture> ips = itemPictureDao.getAll();
        for (ItemPicture ip : ips) {
            itemPictureDao.delete(ip.getId());
        }

        List<Item> storedItems = itemDao.getAll();
        if (storedItems != null) {
            if (storedItems.size() == 0) {
                result = true;
            }

            if (result) {
                DataCenter.statusChanged(ITEM_ALL_ID);
            }
        }

        return result;
    }

    public ItemPictureDao getItemPictureDao() {
        return itemPictureDao;
    }

    public void setItemPictureDao(ItemPictureDao itemPictureDao) {
        this.itemPictureDao = itemPictureDao;
    }

    public ItemDao getItemDao() {
        return itemDao;
    }

    public void setItemDao(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public FacebookService getFacebookService() {
        return facebookService;
    }

    public void setFacebookService(FacebookService facebookService) {
        this.facebookService = facebookService;
    }
}
