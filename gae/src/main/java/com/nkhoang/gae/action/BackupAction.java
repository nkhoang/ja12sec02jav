package com.nkhoang.gae.action;

import com.nkhoang.gae.converter.ItemConverter;
import com.nkhoang.gae.converter.ItemPictureConverter;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.ItemManager;
import com.nkhoang.gae.model.Item;
import com.nkhoang.gae.model.ItemPicture;
import com.nkhoang.gae.service.BackupService;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/" + ViewConstant.BACKUP_NAMEPSACE)
public class BackupAction {
    @Autowired
    private ItemManager itemService;
    @Autowired
    private BackupService docsService;
    private static final String REVISION_REQUEST_PARAM = "revision";
    public static final byte[] LESS_THAN_CHAR = {-62, -85};
    public static final byte[] GREATER_THAN_CHAR = {-62, -69};

    @RequestMapping(value = "/" + ViewConstant.BACKUP_REQUEST, method = RequestMethod.GET)
    public String getRegisterPage() {
        return ViewConstant.BACKUP_VIEW;
    }

    @RequestMapping(value = "/" + ViewConstant.RESTORE_REQUEST, method = RequestMethod.POST)
    public ModelAndView restoreDate(@RequestParam(REVISION_REQUEST_PARAM) String revision) {
        ModelAndView modelAndView = new ModelAndView();
        boolean result = false;
        // get data first
        String xml = docsService.getBackup(revision);
        if (xml != null) {
            // now we can clear all the old data
            itemService.clearAll();

            // don't know why I have to do this but I should
            xml = xml.replace(new String(LESS_THAN_CHAR), "<");
            xml = xml.replace(new String(GREATER_THAN_CHAR), ">");
            XStream xstream = new XStream();

            Map<String, String> map = (Map<String, String>) xstream.fromXML(xml);

            Map<Long, Long> itemPicsMap = new HashMap<Long, Long>();

            // get item picture
            String itemPictureXML = map.get("ItemPicture");
            // split item picture
            String[] itemPictureXMLArr = itemPictureXML.split(",");
            // build converter
            xstream = new XStream();
            xstream.alias("ItemPicture", ItemPicture.class);
            xstream.registerConverter(new ItemPictureConverter());
            for (String s : itemPictureXMLArr) {
                ItemPicture ip = (ItemPicture) xstream.fromXML(s);
                Long oldId = ip.getId();

                // reset id
                ip.setId(null);
                // save to DB
                ItemPicture newItem = itemService.save(ip);

                itemPicsMap.put(oldId, newItem.getId());
            }

            // get item
            String itemXML = map.get("Item");
            // split
            String[] itemXMLArr = itemXML.split(",");
            // build converter
            xstream = new XStream();
            xstream.alias("Item", Item.class);
            xstream.registerConverter(new ItemConverter());
            for (String s : itemXMLArr) {
                Item item = (Item) xstream.fromXML(s);

                // change item picture id
                List<Long> oldPictureIds = item.getPictureIds();
                List<Long> newPictureIds = new ArrayList<Long>();

                for (Long id : oldPictureIds) {
                    newPictureIds.add(itemPicsMap.get(id));
                }

                // set it back to item

                item.setPictureIds(newPictureIds);
                itemService.save(item);
            }

            // done
            result = true;
        }

        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(GSONStrategy.DATA, result);

        return modelAndView;
    }

    /**
     * List all available revisions from Google Docs The format of the revision
     * is the date after the file backup name see DATE_PATTERN. Used to
     * retrieved a list of revision in GUI.
     *
     * @return a Spring View.
     */
    @RequestMapping(value = "/" + ViewConstant.REVISION_REQUEST, method = RequestMethod.POST)
    public ModelAndView listBackupRevisions() {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, Object> jsonData = new HashMap<String, Object>();

        List<String> documentRevision = docsService.listBackupRevisions();

        jsonData.put("data", documentRevision);

        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;
    }

    @RequestMapping(value = "/" + ViewConstant.BACKUP_ALL_REQUEST, method = RequestMethod.POST)
    public ModelAndView backupData() {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, String> backupData = new HashMap<String, String>();
        Map<String, Object> jsonData = new HashMap<String, Object>();

        // get all items
        List<Item> items = itemService.listAllWithDeleted();

        XStream xstream = new XStream();
        xstream.registerConverter(new ItemConverter());
        xstream.alias("Item", Item.class);

        StringBuilder itemXMLBuilder = new StringBuilder();
        int count = 0;
        for (Item item : items) {
            itemXMLBuilder.append(xstream.toXML(item));
            count++;
            if (count != items.size()) {
                itemXMLBuilder.append(',');
            }
        }

        // put to data
        backupData.put("Item", itemXMLBuilder.toString());
        // get all pic
        List<ItemPicture> itemPics = itemService.getAllItemPictures();
        if (itemPics != null && itemPics.size() > 0) {
            xstream = new XStream();
            xstream.registerConverter(new ItemPictureConverter());
            xstream.alias("ItemPicture", ItemPicture.class);

            StringBuilder itemPicsXML = new StringBuilder();
            // build data
            count = 0;
            for (ItemPicture ip : itemPics) {
                itemPicsXML.append(xstream.toXML(ip));
                ++count;
                if (count != itemPics.size()) {
                    itemPicsXML.append(',');
                }
            }

            // put to data
            backupData.put("ItemPicture", itemPicsXML.toString());
        }
        xstream = new XStream();
        String xml = xstream.toXML(backupData);

        boolean result = docsService.backup(xml);

        jsonData.put("result", result);

        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;
    }

    public ItemManager getItemService() {
        return itemService;
    }

    public void setItemService(ItemManager itemService) {
        this.itemService = itemService;
    }

    public BackupService getDocsService() {
        return docsService;
    }

    public void setDocsService(BackupService docsService) {
        this.docsService = docsService;
    }

}
