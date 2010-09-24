package com.nkhoang.gae.action;

import com.google.gson.Gson;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.ItemManager;
import com.nkhoang.gae.model.Item;
import com.nkhoang.gae.model.ItemPicture;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.service.FacebookService;
import com.nkhoang.gae.validator.ItemValidator;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Item Controller.
 * 
 * @author hoangnk
 * 
 */
@Controller
@RequestMapping("/" + ViewConstant.ITEM_NAMESPACE)
public class ItemAction {
    @Autowired
    private ItemManager itemService;
    @Autowired
    private FacebookService facebookService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    @Qualifier("itemValidator")
    private ItemValidator itemValidator;
    private static final Logger LOGGER = Logger.getLogger(ItemAction.class);
    private static final String ITEM_ID_REQUEST_PARAM = "itemID";

    @RequestMapping(value = "/" + ViewConstant.DELETE_ALL_ITEM_REQUEST, method = RequestMethod.POST)
    public ModelAndView deleteItems() {
        ModelAndView modelAndView = new ModelAndView();
        boolean result = false;
        // delete item
        result = itemService.markDeletedAll();

        Map<String, Object> jsonData = new HashMap<String, Object>();

        jsonData.put("result", result);
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;
    }

    @RequestMapping(value = "/" + ViewConstant.DELETE_ITEM_REQUEST, method = RequestMethod.POST)
    public ModelAndView deleteItem(@RequestParam(ITEM_ID_REQUEST_PARAM) Long id) {
        ModelAndView modelAndView = new ModelAndView();
        boolean result = false;
        // delete item
        if (id != null) {
            result = itemService.markDeleted(id);
        }
        Map<String, Object> jsonData = new HashMap<String, Object>();

        jsonData.put("result", result);
        if (!result) {
            jsonData.put("errorMessage", messageSource.getMessage("item.error.clearAll.id", null, null));
        }
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;
    }

    @RequestMapping(value = "/" + ViewConstant.CREATE_ITEM_REQUEST, method = RequestMethod.GET)
    public String getRegisterPage() {
        // get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String page = "";
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null && principal instanceof User) {
                page = ViewConstant.ITEM_REGISTRATION_VIEW;
            }
        } else {
            page = ViewConstant.AUTHORIZATION_ERROR_VIEW;
        }

        return page;
    }

    @RequestMapping(value = "/" + ViewConstant.CREATE_ITEM_REQUEST, method = RequestMethod.POST)
    public ModelAndView register(Item item, BindingResult result, HttpServletRequest request) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null && principal instanceof User) {
                isAdmin = true;
            }
        }
        Map<String, Object> model = new Hashtable<String, Object>();

        if (isAdmin) {
            Map<String, String> errorMessages = new HashMap<String, String>();

            // make sure that data is correctly inputted.
            itemValidator.validate(item, result);

            if (result.hasFieldErrors()) {
                // check if it has field error.
                // build up a message object contains errors.
                for (FieldError fe : result.getFieldErrors()) {
                    errorMessages.put(fe.getField(), messageSource.getMessage(fe.getCode(), null, null));
                }

                Gson gson = new Gson();

                String errorMessagesStr = gson.toJson(errorMessages);

                model.put("errorMessages", errorMessagesStr);
                model.put("isSuccess", false);
            } else {
                // save sub pictures
                LOGGER.debug("Sub Pictures size : " + item.getSubPictures().size());
                if (item.getSubPictures().size() > 0) {
                    for (String s : item.getSubPictures()) {
                        if (s != null && s.length() != 0) {
                            ItemPicture ip = itemService.saveItemPicture(s);

                            if (ip != null) {
                                // set id of this pic to the item
                                item.getPictureIds().add(ip.getId());
                            }
                        }
                    }
                }

                // before save add the date

                Calendar calendar = Calendar.getInstance();
                // SimpleDateFormat dateFormat = new
                // SimpleDateFormat("dd/MM/yyyy");

                item.setDateAdded(calendar.getTime());

                itemService.save(item);
                
                // starting facebook posting
                String postId = null;
                try {
                    postId = facebookService.postContent(null,
                            item.getDescription(), "http://hoangmy-chara.appspot.com/facebook/item/" + item.getId(), item.getCode(),
                            item.getThumbnail(), "http://hoangmy-chara.appspot.com/facebook/item/" + item.getId());
                } catch (Exception e) {
                    LOGGER.error(e);
                }

                // set post id
                item.setPostId(postId);
                // save again.
                itemService.save(item);

                model.put("isSuccess", true);
            }
        } else {
            // this will return a message to show that the user must login to
            // use this function
            String errorMessagesStr = messageSource.getMessage("login.failedAuthorization", null, null);

            model.put("errorMessages", errorMessagesStr);
            model.put("isSuccess", false);
        }
        return new ModelAndView(ViewConstant.ITEM_REGISTRATION_VIEW, model);
    }

    /**
     * Allow all access type to this link.
     * 
     * @return model and view.
     */
    @RequestMapping(value = "/" + ViewConstant.VIEW_ALL_REQUEST, method = RequestMethod.POST)
    public ModelAndView listAllItem() {
        // get authentication.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = false;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null && principal instanceof User) {
                isAdmin = true;
            }
        }
        ModelAndView modelAndView = new ModelAndView();
        List<Item> items = itemService.listAll();
        Map<String, Object> jsonData = new HashMap<String, Object>();

        jsonData.put("items", items);
        jsonData.put("admin", isAdmin);

        View jsonView = new JSONView();

        modelAndView.setView(jsonView);
        // construct data
        List<String> attrs = new ArrayList<String>();
        attrs.addAll(Arrays.asList(Item.SKIP_FIELDS));
        modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;
    }

    public void clearAllItem() {
        itemService.clearAll();
    }

    public ItemManager getItemService() {
        return itemService;
    }

    public void setItemService(ItemManager itemService) {
        this.itemService = itemService;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setFacebookService(FacebookService facebookService) {
        this.facebookService = facebookService;
    }
}
