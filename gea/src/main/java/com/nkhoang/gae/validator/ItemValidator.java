package com.nkhoang.gae.validator;

import com.nkhoang.gae.model.Item;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
/**
 * Server-side Item Validation
 * @author hnguyen93
 *
 */
public class ItemValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		return Item.class.equals(clazz);
	}
	
	public void validate(Object obj, Errors e) {
		// do not use ValidatorUtils b/c it will cause google to crash
		Item item = (Item) obj;
		
		// check required first.
    if (item.getCode() == null || StringUtils.isEmpty(item.getCode())) {
			e.rejectValue("code", "item.error.code.required");
		}
		if (item.getDescription() == null || StringUtils.isEmpty(item.getDescription())) {
			e.rejectValue("description", "item.error.description.required");
		}
		if (item.getPrice() == null) {
			e.rejectValue("price", "item.error.price.required");
		}
		if (item.getThumbnail() == null || StringUtils.isEmpty(item.getThumbnail())) {
			e.rejectValue("thumbnail", "item.error.thumbnail.required");
		}
		if (item.getThumbnailBig()== null || StringUtils.isEmpty(item.getThumbnailBig())) {
			e.rejectValue("thumbnailBig", "item.error.thumbnail.big.required");
		}
	}

}
