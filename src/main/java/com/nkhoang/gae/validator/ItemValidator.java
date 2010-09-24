package com.nkhoang.gae.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.nkhoang.gae.model.Item;
/**
 * Server-side Item Validation
 * @author hnguyen93
 *
 */
public class ItemValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Item.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors e) {
		// do not use ValidatorUtils b/c it will cause google to crash
		Item item = (Item) obj;
		
		// check required first.
		if (item.getCode() == null || item.getCode().isEmpty()) {
			e.rejectValue("code", "item.error.code.required");
		}
		if (item.getDescription() == null || item.getDescription().isEmpty()) {
			e.rejectValue("description", "item.error.description.required");
		}
		if (item.getPrice() == null) {
			e.rejectValue("price", "item.error.price.required");
		}
		if (item.getThumbnail() == null || item.getThumbnail().isEmpty()) {
			e.rejectValue("thumbnail", "item.error.thumbnail.required");
		}
		if (item.getThumbnailBig()== null || item.getThumbnailBig().isEmpty()) {
			e.rejectValue("thumbnailBig", "item.error.thumbnail.big.required");
		}
	}

}
