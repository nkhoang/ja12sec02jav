package com.nkhoang.gae.test;

import com.nkhoang.gae.action.BackupAction;
import com.nkhoang.gae.converter.ItemConverter;
import com.nkhoang.gae.converter.ItemPictureConverter;
import com.nkhoang.gae.model.Item;
import com.nkhoang.gae.model.ItemPicture;
import com.nkhoang.gae.service.impl.BackupServiceImpl;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import junit.framework.TestCase;

import java.util.*;

public class XstreamTest extends TestCase {
    public void testFun() {
        System.out.println("Just a test.");
    }

    public void _testXStreamTo() {
        XStream xstream = new XStream();

        xstream.registerConverter(new ItemConverter());
        xstream.alias("Item", Item.class);

        List<Item> items = new ArrayList<Item>();
        items.add(createItem(1L));
        items.add(createItem(2L));
        items.add(createItem(3L));

        String xml = xstream.toXML(items.get(0));

        System.out.println(xml);

        Item result = (Item) xstream.fromXML(xml);
        System.out.println(result);
    }

    public void _testXstreamGoogle() {
        BackupServiceImpl docsService = new BackupServiceImpl();
        String xml = docsService.getBackup("29/05/2010");
        if (xml != null) {
            xml = xml.replace(new String(BackupAction.LESS_THAN_CHAR), "<");
            xml = xml.replace(new String(BackupAction.GREATER_THAN_CHAR), ">");
            XStream xstream = new XStream();

            Map<String, String> map = (Map<String, String>) xstream.fromXML(xml);

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
                System.out.println(item.getCode());
            }

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
                System.out.println(ip.getUrl());
            }
            System.out.println(map);
        }
    }

    public void _testXstreamFrom() {
        String xml = "<list><Item><code>s001</code><description>A Description</description><price>123</price><thumbnail>https://docs.google.com/File?id=d5brrvd_932dsqqhrc8_b</thumbnail><thumbnailBig>http://docs.google.com/File?id=d5brrvd_1064htxmhbdd_b</thumbnailBig><pictureIds><long>1</long></pictureIds><deletedFlag>N</deletedFlag><dateAdded>Tue 25/05/2010 23:44:33</dateAdded><deletedDate>null</deletedDate><quantity>0</quantity><postId>null</postId></Item>"
                + "<Item><code>s001</code><description>A Description</description><price>123</price><thumbnail>https://docs.google.com/File?id=d5brrvd_932dsqqhrc8_b</thumbnail><thumbnailBig>http://docs.google.com/File?id=d5brrvd_1064htxmhbdd_b</thumbnailBig><pictureIds><long>2</long></pictureIds><deletedFlag>N</deletedFlag><dateAdded>Tue 25/05/2010 23:44:33</dateAdded><deletedDate>null</deletedDate><quantity>0</quantity><postId>null</postId></Item>"
                + "<Item><code>s001</code><description>A Description</description><price>123</price><thumbnail>https://docs.google.com/File?id=d5brrvd_932dsqqhrc8_b</thumbnail><thumbnailBig>http://docs.google.com/File?id=d5brrvd_1064htxmhbdd_b</thumbnailBig><pictureIds><long>3</long></pictureIds><deletedFlag>N</deletedFlag><dateAdded>Tue 25/05/2010 23:44:33</dateAdded><deletedDate>null</deletedDate><quantity>0</quantity><postId>null</postId></Item>"
                + "</list>";
        XStream xstream = new XStream();
        xstream.alias("Item", Item.class);
        xstream.registerConverter(new ItemConverter());

        List<Item> items = (List<Item>) xstream.fromXML(xml);
        System.out.println(items);
    }

    public void _testItemPicture() {
        List<ItemPicture> ips = new ArrayList<ItemPicture>(0);

        ItemPicture ip = new ItemPicture();
        ip.setItemId(1L);
        ip.setDescription("Hinh 1 ");
        ip.setUrl("http://docs.google.com");

        ips.add(ip);

        XStream xstream = new XStream();
        xstream.alias("ItemPicture", ItemPicture.class);
        xstream.registerConverter(new ItemPictureConverter());

        System.out.println(xstream.toXML(ips));
    }

    public void _testItemPictureFrom() {
        String xml = "<list><ItemPicture><itemId>1</itemId><description>Hinh 1 </description><url>http://docs.google.com</url></ItemPicture></list>";

        XStream xstream = new XStream();
        xstream.alias("ItemPicture", ItemPicture.class);
        xstream.registerConverter(new ItemPictureConverter());

        List<ItemPicture> ips = (List<ItemPicture>) xstream.fromXML(xml);
        System.out.println(ips);
    }

    public void _testMerge() {
        List<ItemPicture> ips = new ArrayList<ItemPicture>(0);

        ItemPicture ip = new ItemPicture();
        ip.setItemId(1L);
        ip.setDescription("Hinh 1 ");
        ip.setUrl("http://docs.google.com");

        ips.add(ip);

        List<Item> items = new ArrayList<Item>();
        items.add(createItem(1L));
        items.add(createItem(2L));
        items.add(createItem(3L));

        Map<String, String> dataMap = new HashMap<String, String>();

        dataMap.put("Item", "abc");
        dataMap.put("ItemPicture", "def");

        XStream xstream = new XStream();
        String xml = xstream.toXML(dataMap);

        System.out.println(xml);

        Map<String, List<?>> resultMap = (Map<String, List<?>>) xstream.fromXML(xml);
        System.out.println(resultMap);
    }

    public void _testJavabeanConverter() {
        XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()) {
            public boolean canConvert(Class type) {
                return type == Item.class;
            }
        });
        xstream.registerConverter(new DateConverter());
        xstream.alias("Item", Item.class);

        List<Item> items = new ArrayList<Item>();
        items.add(createItem(1L));
        items.add(createItem(2L));
        items.add(createItem(3L));
        System.out.println(xstream.toXML(items));
    }

    Item createItem(Long ipId) {
        Item item = new Item();
        item.setCode("s001");
        item.setDescription("A Description");
        item.setPrice(123L);
        item.setThumbnail("https://docs.google.com/File?id=d5brrvd_932dsqqhrc8_b");
        item.setThumbnailBig("http://docs.google.com/File?id=d5brrvd_1064htxmhbdd_b");

        Calendar calendar = Calendar.getInstance();

        item.setDateAdded(calendar.getTime());

        item.getPictureIds().add(ipId);

        return item;
    }

}
