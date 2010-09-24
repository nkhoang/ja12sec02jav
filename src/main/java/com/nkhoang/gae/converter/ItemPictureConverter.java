package com.nkhoang.gae.converter;

import com.nkhoang.gae.model.ItemPicture;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

// @off
/**
 * Converter supports XStream to deserialize/serialize ItemPicture obj to/from XML.
 * @author hnguyen93
 *
 */
// @on
public class ItemPictureConverter implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(ItemPicture.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ItemPicture ip = (ItemPicture) value;
        // id
        writer.startNode("id");
        writer.setValue(ip.getId() + "");
        writer.endNode();
        // code
        writer.startNode("itemId");
        if (ip.getItemId() == null) {
            writer.setValue("null");
        } else
            writer.setValue(ip.getItemId() + "");
        writer.endNode();
        // description
        writer.startNode("description");
        if (ip.getDescription() == null) {
            writer.setValue("null");
        } else {
            writer.setValue(ip.getDescription());
        }
        writer.endNode();
        // price
        writer.startNode("url");
        writer.setValue(ip.getUrl());
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ItemPicture ip = new ItemPicture();
        if (reader.getNodeName().equals("ItemPicture")) {
            // read id
            reader.moveDown();
            ip.setId(Long.parseLong(reader.getValue()));
            reader.moveUp();

            reader.moveDown();
            if (reader.getValue().equals("null")) {
                ip.setItemId(null);
            } else {
                ip.setItemId(Long.parseLong(reader.getValue()));
            }
            reader.moveUp();

            reader.moveDown();
            String descriptionValue = reader.getValue();
            if (descriptionValue.equals("null")) {
                ip.setDescription(null);
            } else {
                ip.setDescription(reader.getValue());
            }
            reader.moveUp();

            reader.moveDown();
            ip.setUrl(reader.getValue());
            reader.moveUp();

        }
        return ip;
    }
}