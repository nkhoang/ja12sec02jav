package com.nkhoang.common.xml.xdiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.nkhoang.common.xml.DOMUtil;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


class HintHandler {
  //pattern for 'starts with *, then anything'
  private static Pattern _identifierPattern = Pattern.compile("\\*.*");
  
  /**
   * This is a special case method that is designed to greatly speed up the 
   * cases that are difficult to handle otherwise.  Namely a large number of 
   * nodes with the same underlying structure.  Currently it only supports hints
   * where the structure is an element node immediately containing text valued
   * element nodes.  The idea is that in these cases, when the input nodes are
   * guaranteed to have the same children elements, the algorithm can be quite
   * a bit smarter about how it processes things.
   * 
   * @param nodes1
   * @param nodes2
   * @param distanceMap
   * @param tuples
   * @param hint 
   *            The hint contains structure information about a given node.  The
   *            provided list is the fixed list of children these Nodes will 
   *            contain.  This lets the algorithm determine min and max distance
   *            values immediately, and thereby allows it to take a greedy
   *            approach to solving the problem.  Additionally, using a hint 
   *            allows you to indicate certain leaf nodes that must match 
   *            exactly in order for their parents to match.  Any String in the 
   *            hint starting with * will be treated this way.  For instance if
   *            you have an element that contains data about a tax id, you don't
   *            want to match that element up to another tax id element where 
   *            the id itself is different but everything else is the same.  It
   *            would be much better to match it to a node where the id was the
   *            same even if the other values changed.  Thus you would want to 
   *            pass *tax_id as the hint instead of tax_id.
   * @return
   */
  public static List<Tuple<Node>> getMinCostMapping(List<Node> nodes1,
          List<Node> nodes2, Map<Tuple<Node>, Integer> distanceMap, 
          List<Tuple<Node>> tuples, List<String> hint) {
    String[] hintArray = hint.toArray(new String[hint.size()]);
    List<Tuple<Node>> result = new ArrayList<Tuple<Node>>();
    
    //the idea here is that since we know for a fact the underlying structure
    //of the nodes we're considering, we can greedily grab all the distance 0
    //matches, then the distance 1 matches, and so on up the distances.  This
    //may NOT provide the optimal solution in all cases, but it should provide
    //very reasonable solutions.  We don't want to take max distance mappings, 
    //what that really means is that we're removing top level elements and / or
    //inserting new ones.
    for(int i = 0; i < hint.size(); i++){
      List<Node> remove1 = new ArrayList<Node>();
      for(Node node1 : nodes1){
        List<Node> remove2 = new ArrayList<Node>();
        for(Node node2 : nodes2){
          Tuple<Node> key = XDiffUtil.computeMatchKey(node1, node2);
          if(!tuples.contains(key)){
            continue;
          }
          if(i == getDistance(node1, node2, hintArray, distanceMap)){
            remove1.add(node1);
            remove2.add(node2);
            result.add(key);
          }
        }
        nodes2.removeAll(remove2);
      }
      nodes1.removeAll(remove1);
    }
    
    return result;
  }
  
  private static int getDistance(Node node1, Node node2, String[] hint, 
          Map<Tuple<Node>, Integer> distanceMap){
    int distance = 0;
    for(String childName : hint){
      boolean isID = false;
      if(_identifierPattern.matcher(childName).matches()){
        childName = childName.substring(1);  //chop off the *
        isID = true;
      }
      Element child1 = DOMUtil.getChildByName(node1, childName);
      Element child2 = DOMUtil.getChildByName(node2, childName);
      if(child1 == child2){
        distanceMap.put(XDiffUtil.computeMatchKey(child1, child2), 0);
      }
      else if(child1 == null || child2 == null){
        distance++;
      }
      else if(!StringUtils.equals(child1.getTextContent(),
              child2.getTextContent())){
        //it's an identifier, so it HAS TO match
        if(isID){
          distanceMap.put(XDiffUtil.computeMatchKey(node1, node2), hint.length);
          return hint.length;
        }
        distance++;
      }
      else{
        distanceMap.put(XDiffUtil.computeMatchKey(child1, child2), 0);
      }
    }
    distanceMap.put(XDiffUtil.computeMatchKey(node1, node2), distance);
    return distance;
  }
}
