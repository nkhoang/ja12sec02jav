package com.nkhoang.common.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @deprecated Replaced by {@link DOMEqualityUtil}
 */
@Deprecated
public class UnorderedNodeComparator extends NodeComparator{
  
  public static boolean areEqual(NodeList list1, NodeList list2) {
    if (list1 != null) {
      if (list2 != null) {
        if (list1.getLength() != list2.getLength()) {
          return false;
        }
        int length = list1.getLength();
        List<Node> nodes2 = new ArrayList<Node>(length);
        for(int i = 0; i < length; i++){
          nodes2.add(list2.item(i));
        }
        for (int i = 0; i < length; i++) {
          Node node1 = list1.item(i);
          Node match = null;
          for (Node candidate : nodes2) {
            if(areEqual(node1, candidate)){
              match = candidate;
              break;
            }
          }
          if(match == null){
            return false;
          }
          nodes2.remove(match);
        }
      }
      else{ //list1 not null, list2 null
        return false;
      }
    }
    else{ //list 1 null
      if(list2 != null){
        return false;
      }
    }
    return true;
  }
}
