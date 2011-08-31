package com.nkhoang.gae.converter;

import com.nkhoang.gae.model.Item;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

// @off
/**
 * Converter supports XStream to deserialize/serialize Item obj to/from XML.
 * @author hnguyen93
 *
 */
// @on
public class ItemConverter implements Converter {
    private final DateFormat df = new SimpleDateFormat("E dd/MM/yyyy kk:mm:ss");

    public boolean canConvert(Class clazz) {
        return clazz.equals(Item.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        Item item = (Item) value;
        // id
        writer.startNode("id");
        writer.setValue(item.getId() + "");
        writer.endNode();
        // code
        writer.startNode("code");
        writer.setValue(item.getCode());
        writer.endNode();
        // description
        writer.startNode("description");
        writer.setValue(item.getDescription());
        writer.endNode();
        // price
        writer.startNode("price");
        writer.setValue(item.getPrice().toString());
        writer.endNode();
        // thumbnail
        writer.startNode("thumbnail");
        writer.setValue(item.getThumbnail());
        writer.endNode();
        // thumbnail
        writer.startNode("thumbnailBig");
        writer.setValue(item.getThumbnailBig());
        writer.endNode();
        // pictureIds
        writer.startNode("pictureIds");
        context.convertAnother(item.getPictureIds());
        writer.endNode();
        // deletedFlag
        writer.startNode("deletedFlag");
        writer.setValue(item.getDeletedFlag());
        writer.endNode();
        // dateAdded
        writer.startNode("dateAdded");
        writer.setValue(df.format(item.getDateAdded()));
        writer.endNode();
        // deletedDate
        writer.startNode("deletedDate");
        if (item.getDeletedDate() == null) {
            writer.setValue("null");
        } else {
            writer.setValue(df.format(item.getDeletedDate()));
        }
        writer.endNode();
        // quantity
        writer.startNode("quantity");
        writer.setValue(item.getQuantity() + "");
        writer.endNode();
        // postId
        writer.startNode("postId");
        if (item.getPostId() == null) {
            writer.setValue("null");
        } else {
            writer.setValue(item.getPostId());
        }

        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        // List<Item> items = new ArrayList<Item>(0);
        // while (reader.hasMoreChildren()) {
        Item item = new Item();
        if (reader.getNodeName().equals("Item")) {
            reader.moveDown();
            item.setId(Long.parseLong(reader.getValue()));
            reader.moveUp();

            reader.moveDown();
            item.setCode(reader.getValue());
            reader.moveUp();

            reader.moveDown();
            item.setDescription(reader.getValue());
            reader.moveUp();

            reader.moveDown();
            item.setPrice(Long.parseLong(reader.getValue()));
            reader.moveUp();

            reader.moveDown();
            item.setThumbnail(reader.getValue());
            reader.moveUp();

            reader.moveDown();
            item.setThumbnailBig(reader.getValue());
            reader.moveUp();

            reader.moveDown();
            List<Long> pictureIds = new ArrayList<Long>(0);
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                pictureIds.add(Long.parseLong(reader.getValue()));
                reader.moveUp();
            }
            item.setPictureIds(pictureIds);
            reader.moveUp();

            reader.moveDown();
            item.setDeletedFlag(reader.getValue());
            reader.moveUp();

            reader.moveDown();
            try {
                item.setDateAdded(df.parse(reader.getValue()));
            } catch (Exception e) {
                throw new ConversionException(e);
            }
            reader.moveUp();

            reader.moveDown();
            try {
                if (reader.getValue().equals("null")) {
                    item.setDeletedDate(null);
                } else {
                    item.setDeletedDate(df.parse(reader.getValue()));
                }
            } catch (Exception e) {
                throw new ConversionException(e);
            }
            reader.moveUp();

            reader.moveDown();
            item.setQuantity(Integer.parseInt(reader.getValue()));
            reader.moveUp();

            reader.moveDown();
            if (reader.getValue().equals("null")) {
                item.setPostId(null);
            } else {
                item.setPostId(reader.getValue());
            }

            reader.moveUp();

            // items.add(item);
        }
        // }
        return item;
    }
}
