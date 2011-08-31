package com.nkhoang.common.xml;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.w3c.dom.Element;

/**
 * Encapsulates an Element's namespace and name
 *
 */
public class ElementIdentifier {

	private String _namespace;
	private String _qName;

	public ElementIdentifier(String namespace, String qName) {
		_namespace = namespace;
		_qName = qName;
	}

	public ElementIdentifier(Element element) {
		_namespace = element.getNamespaceURI();
		_qName = element.getTagName();
	}

	public String getNamespace() {
		return _namespace;
	}

	public String getQName() {
		return _qName;
	}

	public String getLocalName() {
		int i = _qName.indexOf(":");
		if (i > -1) {
			return _qName.substring(i + 1);
		} else {
			return _qName;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ElementIdentifier)) {
			return false;
		}
		ElementIdentifier other = (ElementIdentifier) obj;
		if (_namespace != null) {
			if (!_namespace.equals(other._namespace)) {
				return false;
			}
		} else if (other._namespace != null) {
			return false;
		}
		String myLocalName = getLocalName();
		if (myLocalName != null) {
			String yourLocalName = other.getLocalName();
			if (yourLocalName != null) {
				if (!myLocalName.equals(yourLocalName)) {
					return false;
				}
			} else {
				return false;
			}
		} else if (other.getLocalName() != null) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(_namespace).toHashCode();
	}

	@Override
	public String toString() {
		return _qName;
	}

}
