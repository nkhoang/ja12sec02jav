package com.nkhoang.common.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;


/**
 *  This class contains static methods for testing equality of DOM nodes.
 */
public class DOMEqualityUtil {

  /**
   *  Determines whether two Elements are equal. Equality considers namespace,
   *  local name, attributes, and content (recursively). May be configured to
   *  ignore empty text children, ignore comments and processing instructions,
   *  and to compare children in an ordered or unordered way.
   *  <p>
   *  Element attributes are compared for namespace and value, ignoring any
   *  namespace definition attributes. See {@link #isEqual} for more info.
   *  <p>
   *  Adjacent nodes will be combined, after optionally ignoring any empty
   *  nodes. Text nodes in mixed content, where there is an intervening
   *  element, will not be combined.
   *
   *  @param  ignoreEmptyText       If <code>true</code>, ignores any Text
   *                                children that are empty once trimmed of
   *                                whitespace. This is useful when comparing
   *                                pretty-printed documents that have
   *                                whitespace between elements.
   *  @param  ignoreComments        If <code>true</code>, ignores any Comment
   *                                children.
   *  @param  ignorePI              If <code>true</code>, ignores any children
   *                                that are processing instructions.
   *  @param  unorderedChildCompare If <code>true</code>, non-text children are
   *                                examined without regard to order. This has
   *                                limited usefulness.
   *
   */
  public static boolean isEqual(
          Node n1, Node n2,
          boolean ignoreEmptyText, boolean ignoreComments, boolean ignorePI,
          boolean unorderedChildCompare)
  {
    if (n1 == n2) {
      return true;
    }
    if ((n1 == null) || (n2 == null)) {
      return false;
    }
    if (n1.getNodeType() != n2.getNodeType()) {
      return false;
    }

    if (!DOMUtil.getLocalName(n1).equals(DOMUtil.getLocalName(n2))
            || !getNamespace(n1).equals(getNamespace(n2))) {
      return false;
    }

    if (!(n1 instanceof Element)) {
      return ObjectUtils.equals(n1.getNodeName(), n2.getNodeName())
             && ObjectUtils.equals(n1.getNodeValue(), n2.getNodeValue());
    }

    // element-specific tests follow
    if (!isEqual(n1.getAttributes(), n2.getAttributes())) {
      return false;
    }
    if (!isTextEqual(n1, n2, ignoreEmptyText)) {
      return false;
    }

    // this will call recursively
    return unorderedChildCompare
           ? areNonTextChildrenEqualUnordered(
                   n1, n2,
                   ignoreEmptyText, ignoreComments, ignorePI,
                   unorderedChildCompare)
           : areNonTextChildrenEqualOrdered(
                   n1, n2,
                   ignoreEmptyText, ignoreComments, ignorePI,
                   unorderedChildCompare);
  }


  /**
   *  Convenience call for <code>isEqual(Node)</code>, that performs an
   *  ordered child comparison, and ignores empty text, comments, and processing
   *  instructions.
   */
  public static boolean isEqual(Node n1, Node n2) {
    return isEqual(n1, n2, true, true, true, false);
  }


  /**
   *  Determines whether two <code>NamedNodeMaps</code> -- typically used for
   *  holding element attributes -- are equal. Equality considers total size
   *  of the maps, per-element local name, namespace, and value. The namespace
   *  and value checks are optional.
   *
   *  @param  ignoreNamespace   If <code>true</code>, ignores the namespace of
   *                            a map entry.
   *  @param  ignoreNSDef       If <code>true</code>, ignores any attributes
   *                            that start with "xmlns". This is generally a
   *                            desirable thing, as these attributes do not
   *                            affect the content of an XML document, and
   *                            semantically equivalent documents may place
   *                            these definitions of different elements.
   *  @param  ignoreValue       If <code>true</code>, ignores the value of a
   *                            map entry (ie, just compare map keys).
   */
  public static boolean isEqual(
          NamedNodeMap m1, NamedNodeMap m2,
          boolean ignoreNamespace, boolean ignoreNSDef, boolean ignoreValue)
  {
    if (m1 == m2) {
      return true;
    }
    if ((m1 == null) || (m2 == null)) {
      return false;
    }

    // phase 1 figures out what elements match, and drops out if no match
    boolean[] matches = new boolean[m2.getLength()];
    for (int ii = 0 ; ii < m1.getLength() ; ii++) {
      Node n1 = m1.item(ii);
      if (ignoreNSDef && n1.getNodeName().startsWith("xmlns")) {
        continue;
      }
      int matchIdx = lookForMatch(n1, m2, ignoreNamespace, ignoreValue);
      if (matchIdx < 0) {
        return false;
      }
      matches[matchIdx] = true;
    }

    // phase 2 goes back, and drops out if anything didn't get matched
    for (int ii = 0 ; ii < matches.length ; ii++) {
      if (!matches[ii]
          && (!ignoreNSDef || !m2.item(ii).getNodeName().startsWith("xmlns"))) {
        return false;
      }
    }

    // got past all the checks, must be equal
    return true;
  }


  /**
   *  Convenience call for <code>isEqual(NamedNodeMap)</code>, which checks
   *  namespace and value, and ignores "xmlns" attributes.
   */
  public static boolean isEqual(NamedNodeMap m1, NamedNodeMap m2) {
    return isEqual(m1, m2, false, true, false);
  }


  /**
   *  Compares the text children of the passed elements, and determines whether
   *  they are equivalent. Concatenates adjacent text nodes, and optionally
   *  ignores empty text nodes
   *
   *  @param  ignoreEmptyText       If <code>true</code>, ignores any Text
   *                                children that are empty once trimmed of
   *                                whitespace. This is useful when comparing
   *                                pretty-printed documents that have
   *                                whitespace between elements.
   */
  public static boolean isTextEqual(
          Node n1, Node n2, boolean ignoreEmptyText)
  {
    return extractText(n1, ignoreEmptyText)
           .equals(extractText(n2, ignoreEmptyText));
  }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

  /**
   *  Returns the namespace for a node, the empty string is the node doesn't
   *  have a namespace. This allows us to do comparisons without worrying
   *  about null pointers.
   */
  private static String getNamespace(Node n) {
    String ns = n.getNamespaceURI();
    return (ns == null) ? "" : ns;
  }


  /**
   *  The inner look for <code>isEqual(NamedNodeMap)</code>. Returns the
   *  index of the matching node in m2, -1 if no match.
   */
  private static int lookForMatch(
          Node n1, NamedNodeMap m2,
          boolean ignoreNamespace, boolean ignoreValue)
  {
    for (int ii = 0 ; ii < m2.getLength() ; ii++) {
      Node n2 = m2.item(ii);
      boolean nameEqual = DOMUtil.getLocalName(n1).equals(DOMUtil.getLocalName(n2));
      boolean nsEqual = ignoreNamespace || getNamespace(n1).equals(getNamespace(n2));
      boolean valEqual = ignoreValue || n1.getNodeValue().equals(n2.getNodeValue());
      if (nameEqual && nsEqual && valEqual) {
        return ii;
      }
    }
    return -1;
  }


  /**
   *  Extracts text nodes from the passed document, coalescing adjacent non-
   *  blank nodes.
   */
  private static List<String> extractText(Node n, boolean ignoreEmptyText) {
    List<String> result = new ArrayList<String>();
    NodeList children = n.getChildNodes();
    int lastTextIndex = Integer.MIN_VALUE;

    for (int ii = 0 ; ii < children.getLength() ; ii++) {
      Node child = children.item(ii);
      if (!(child instanceof Text)) {
        continue;
      }
      String text = child.getNodeValue();
      if (ignoreEmptyText && StringUtils.isEmpty(text)) {
        continue;
      }

      // tentatively add
      result.add(text);

      // coalesce if appropriate
      if (lastTextIndex == (ii - 1)) {
        String prevText = result.get(result.size() - 2);
        if (!StringUtils.isEmpty(prevText) && !StringUtils.isEmpty(text)) {
          result.remove(result.size() - 1);
          result.remove(result.size() - 1);
          result.add(prevText + text);
        }
      }

      lastTextIndex = ii;
    }
    return result;
  }


  /**
   *  Performs an ordered comparsion of the non-text children of an element,
   *  optionally ignoring comments and processing instructions.
   */
  private static boolean areNonTextChildrenEqualOrdered(
          Node n1, Node n2,
          boolean ignoreEmptyText, boolean ignoreComments, boolean ignorePI,
          boolean unorderedChildCompare)
  {
    Iterator<Node> itx1 = DOMUtil.nodeIterable(n1.getChildNodes()).iterator();
    Iterator<Node> itx2 = DOMUtil.nodeIterable(n2.getChildNodes()).iterator();
    while (itx1.hasNext()) {
      if (!isEqual(
              nextNodeForComparison(itx1, ignoreComments, ignorePI),
              nextNodeForComparison(itx2, ignoreComments, ignorePI),
              ignoreEmptyText, ignoreComments, ignorePI, unorderedChildCompare)) {
        return false;
      }
    }
    if (nextNodeForComparison(itx2, ignoreComments, ignorePI) != null) {
      return false;
    }
    return true;
  }


  /**
   *  Performs an unordered comparsion of the non-text children of an element,
   *  optionally ignoring comments and processing instructions.
   */
  private static boolean areNonTextChildrenEqualUnordered(
          Node n1, Node n2,
          boolean ignoreEmptyText, boolean ignoreComments, boolean ignorePI,
          boolean unorderedChildCompare)
  {
    if(n1 == n2){
      return true;
    }
    else if(n1 == null){
      return false;
    }
    else if(n2 == null){
      return false;
    }
    
    NodeList c1 = n1.getChildNodes();
    NodeList c2 = n2.getChildNodes();
    
    if(c1 == c2){
      return true;
    }
    else if(c1 == null){
      return false;
    }
    else if(c2 == null){
      return false;
    }
    
    //build up two lists of the nodes we actually care about
    List<Node> children1 = new ArrayList<Node>();
    for(int i = 0; i < c1.getLength(); i++){
      Node node = c1.item(i);
      if(considerNode(node, ignoreComments, ignorePI)
              && node.getNodeType() != Node.TEXT_NODE){
        children1.add(node);
      }
    }
    
    List<Node> children2 = new ArrayList<Node>();
    for(int i = 0; i < c2.getLength(); i++){
      Node node = c2.item(i);
      if(considerNode(node, ignoreComments, ignorePI)
              && node.getNodeType() != Node.TEXT_NODE){
        children2.add(node);
      }
    }

    if(children1.size() != children2.size()){
      return false;
    }
    
    //step through each node in the first list
    for(Node current : children1){
      Node match = null;

      //compare it against nodes in the second list
      for(Node candidate : children2){
        
        //if it's a match then we can move on to the next node in c1
        if(isEqual(current, candidate, ignoreEmptyText, 
                ignoreComments, ignorePI, unorderedChildCompare)){
          match = candidate;
          break;
        }
      }
      
      //no match means they're not equal
      if(match == null){
        return false;
      }
      
      //otherwise lets take the match out of children2 because we've used it
      else{
        children2.remove(match);
      }
    }
    
    //if we get this far we've matched up everything that we were supposed to
    return true;
  }


  /**
   *  Support code for the child comparators. Returns the next node that should
   *  be compared, <code>null</code> if there isn't one.
   */
  private static Node nextNodeForComparison(
          Iterator<Node> itx, boolean ignoreComments, boolean ignorePI)
  {
    while (itx.hasNext()) {
      Node child = itx.next();
      if ((child instanceof Text)
              || (ignoreComments && (child instanceof Comment))
              || (ignorePI && (child instanceof ProcessingInstruction))) {
        continue;
      }
      return child;
    }
    return null;
  }
  
  private static boolean considerNode(Node node, boolean ignoreComments, 
          boolean ignorePI){
    if(node.getNodeType() == Node.COMMENT_NODE && ignoreComments){
      return false;
    }
    if(node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && ignorePI){
      return false;
    }
    return true;
  }
}
