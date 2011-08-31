package com.nkhoang.common.xml;

/**
 *  This class contains constants for the <code>type</code> property of an
 *  <code>org.w3c.dom.events.Event</code>. Registering oneself as an event 
 *  listener requires specific string values to be passed to <code>
 *  EventTarget.addEventListener</code>, yet the JDK doesn't define or document
 *  those strings anywhere ... they're buried in a
 *  <a href="http://www.w3.org/TR/DOM-Level-2-Events/events.html">W3C document
 *  </a> that's several clicks away.
 */
public class DOMEventTypes {
  /**
   *  This is a general event for notification of all changes to the
   *  document. It can be used instead of the more specific events
   *  listed below. It may be fired after a single modification to
   *  the document or, at the implementation's discretion, after
   *  multiple changes have occurred. The latter use should generally
   *  be used to accomodate multiple changes which occur either
   *  simultaneously or in rapid succession. The target of this event
   *  is the lowest common parent of the changes which have taken place.
   *  This event is dispatched after any other events caused by the
   *  mutation have fired.
   */
  public final static String SUBTREE_MODIFIED = "DOMSubtreeModified";


  /**
   *  Fired when a node has been added as a child of another node.
   *  This event is dispatched after the insertion has taken place.
   *  The target of this event is the node being inserted.
   */
  public final static String NODE_INSERTED = "DOMNodeInserted";


  /**
   *  Fired when a node is being removed from its parent node. This
   *  event is dispatched before the node is removed from the tree.
   *  The target of this event is the node being removed.
   */
  public final static String NODE_REMOVED = "DOMNodeRemoved";


  /**
   *  Fired when a node is being removed from a document, either
   *  through direct removal of the Node or removal of a subtree in
   *  which it is contained. This event is dispatched before the
   *  removal takes place. The target of this event is the Node being
   *  removed. If the Node is being directly removed the
   *  <code>DOMNodeRemoved</code> event will fire before the
   *  <code>DOMNodeRemovedFromDocument</code> event.
   */
  public final static String NODE_REMOVED_FROM_DOCUMENT = "DOMNodeRemovedFromDocument";


  /**
   *  Fired when a node is being inserted into a document, either
   *  through direct insertion of the Node or insertion of a subtree
   *  in which it is contained. This event is dispatched after the
   *  insertion has taken place. The target of this event is the node
   *  being inserted. If the Node is being directly inserted the
   *  <code>DOMNodeInserted</code> event will fire before the
   *  <code>DOMNodeInsertedIntoDocument</code> event.
   */
  public final static String NODE_INSERTED_INTO_DOCUMENT = "DOMNodeInsertedIntoDocument";


  /**
   *  Fired after an Attr has been modified on a node. The target
   *  of this event is the Node whose Attr changed. The value of
   *  attrChange indicates whether the Attr was modified, added,
   *  or removed. The value of relatedNode indicates the Attr node
   *  whose value has been affected. It is expected that string
   *  based replacement of an Attr value will be viewed as a
   *  modification of the Attr since its identity does not change.
   *  Subsequently replacement of the Attr node with a different
   *  Attr node is viewed as the removal of the first Attr node
   *  and the addition of the second.
   */
  public final static String ATTR_MODIFIED = "DOMAttrModified";


  /**
   *  Fired after CharacterData within a node has been modified
   *  but the node itself has not been inserted or deleted. This
   *  event is also triggered by modifications to PI elements.
   *  The target of this event is the CharacterData node.
   */
  public final static String TEXT_MODIFIED = "DOMCharacterDataModified";


}
