package com.nkhoang.gae.action;

import com.nkhoang.gae.manager.ItemManager;
import com.nkhoang.gae.model.Item;
import com.nkhoang.gae.view.constant.ViewConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Item Controller.
 * 
 * @author hoangnk
 * 
 */
@Controller
@RequestMapping("/" + ViewConstant.FACEBOOK_NAMESPACE)
public class FacebookAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemAction.class);
    @Autowired
    private ItemManager itemService;

    @RequestMapping(value = "/item/{itemId}/index.html")
    public ModelAndView showItem(@PathVariable String itemId) {
        ModelAndView mav = new ModelAndView();

        LOGGER.info("Item id received: " + itemId);
        List<String> ips;
        Item item = itemService.get(Long.parseLong(itemId));
        if (item != null) {
            ips = item.getSubPictures();
            mav.addObject("pictures", ips);
            mav.setViewName("facebookView");
        } else {
            mav.setViewName("redirect:/");
        }

        return mav;
    }

    public void setItemService(ItemManager itemService) {
        this.itemService = itemService;
    }

    public ItemManager getItemService() {
        return itemService;
    }
}
