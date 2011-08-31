package com.nkhoang.common.xml;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;


/**
 * A combination of an XSAttributeUse declaration and an element with that
 * attribute
 */
public class XSAttributeInstance {
	private static final Log LOG = LogFactory.getLog(XSAttributeInstance.class);
	private Element        _element;
	private XSAttributeUse _attributeUse;

	public XSAttributeInstance(XSAttributeUse use, Element element) {
		_attributeUse = use;
		_element = element;
	}

	public XSAttributeUse getAttributeUse() {
		return _attributeUse;
	}

	public Element getElement() {
		return _element;
	}

	public Object getDefaultValue() {
		Object rtn = null;
		//This is nasty but necessary do to a bug in xerces - rather than
		//return null when there is no default (as the documentation states), getActualVC()
		//throws a NullPointerException
		try {
			rtn = _attributeUse.getActualVC();
		}
		catch (NullPointerException e) {
			LOG.warn("No default value specified for attribute: " + getAttributeName());
		}
		return rtn;

	}

	public String getAttributeName() {
		return _attributeUse.getAttrDeclaration().getName();
	}

	/**
	 * @return The value of the attribute on the element, or null if
	 *         the attribute is not found
	 */
	public String getValue() {
		if (_element == null) {
			return null;
		}
		XSAttributeDeclaration decl = _attributeUse.getAttrDeclaration();
		Attr attrNode = _element.getAttributeNodeNS(
			decl.getNamespace(), decl.getName());
		if (attrNode != null) {
			return attrNode.getValue();
		} else {
			return null;
		}
	}

	/**
	 * @param value Value to set the attribute to, or null to remove the attribute
	 *              from the element
	 */
	public void setValue(String value) {
		XSAttributeDeclaration decl = _attributeUse.getAttrDeclaration();
		if (!StringUtils.isEmpty(value)) {
			_element.setAttributeNS(decl.getNamespace(), decl.getName(), value);
		} else {
			_element.removeAttributeNS(decl.getNamespace(), decl.getName());
		}
	}

}
