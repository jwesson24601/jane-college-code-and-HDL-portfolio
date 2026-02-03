import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Filename: Graph.java 
 * Project: Social Network Visualizer 
 * a3 Authors: Jake Wesson
 * 
 * This class is an undirected and unweighted graph implementation.
 */
public class Graph implements GraphADT {

  /**
   * This inner class represents each node added into the graph. It contains a person and a list of
   * friends associated with that person.
   * 
   * @author Jake Wesson
   */
  private class GraphNode {

    private List<GraphNode> neighbors; // list of successor nodes
    private Person person; // String associated with this node

    /**
     * Constructor for GraphNode, initializes all fields.
     * 
     * @param data - the String associated with this node
     */
    private GraphNode(Person person) {
      this.person = person;
      neighbors = new ArrayList<GraphNode>();
    }

  }

  private Set<GraphNode> nodes; // all nodes in the graph
  private int size; // number of edges
  private int order; // number of nodes

  /**
   * This constructor just initializes all the fields.
   */
  public Graph() {
    nodes = new HashSet<GraphNode>();
    size = 0;
    order = 0;
  }

  /**
   * Add new node to the graph.
   *
   * If node is null or already exists, method ends without adding a node or throwing an exception.
   * 
   * Valid argument conditions: 1. node is non-null 2. node is not already in the graph
   */
  public int addNode(Person node) {
    if (node == null) {
      return -1;
    }
    for (GraphNode nodeInGraph : nodes) {
      if (nodeInGraph.person.getName().equals(node.getName())) {
        return 0;
      }
    }
    nodes.add(new GraphNode(node));
    ++order;
    return 1;
  }

  /**
   * Remove a node and all associated edges from the graph.
   * 
   * If node is null or does not exist, method ends without removing a node, edges, or throwing an
   * exception.
   * 
   * Valid argument conditions: 1. node is non-null 2. node is not already in the graph
   */
  public int removeNode(Person node) {
    if (node == null) {
      return -1;
    }
    GraphNode removeNode = null;
    for (GraphNode nodeInGraph : nodes) {
      if (nodeInGraph.person.getName().equals(node.getName())) {
        removeNode = nodeInGraph;
        break;
      }
    }
    if (removeNode == null) {
      return 0;
    }
    for (GraphNode nodeInGraph : removeNode.neighbors) {
      nodeInGraph.neighbors.remove(removeNode);
      --size; // for every edge removed, size must be decremented
    }
    nodes.remove(removeNode);
    --order;
    return 1;
  }

  /**
   * Add the edge between node1 and node2 to this graph. If either node does not exist, add the
   * node, and add the edge, no exception is thrown. If the edge exists in the graph, no edge is
   * added and no exception is thrown.
   * 
   * Valid argument conditions: 1. neither node is null 2. both nodes are in the graph 3. the edge
   * is not in the graph
   */
  public int addEdge(Person node1, Person node2) {
    if (node1 == null || node2 == null) {
      return -1;
    }
    GraphNode n1 = null;
    GraphNode n2 = null;
    for (GraphNode nodeInGraph : nodes) {
      if (nodeInGraph.person.getName().equals(node1.getName())) {
        n1 = nodeInGraph;
      }
      if (nodeInGraph.person.getName().equals(node2.getName())) {
        n2 = nodeInGraph;
      }
      if (n1 != null && n2 != null) {
        break;
      }
    }
    if (n1 == null) {
      addNode(node1);
    }
    if (n2 == null) {
      addNode(node2);
    }
    for (GraphNode node : nodes) {
      if (node.person.getName().equals(node1.getName())) {
        n1 = node;
      }
      if (node.person.getName().equals(node2.getName())) {
        n2 = node;
      }
      if (n1 != null && n2 != null) {
        break;
      }
    }
    if (n1.person.getName().equals(n2.person.getName())) {
      return 0;
    }
    for (GraphNode n : n1.neighbors) {
      if (n.person.getName().equals(n2.person.getName())) {
        return 0;
      }
    }
    n1.neighbors.add(n2);
    n2.neighbors.add(n1);
    ++size;
    return 1;
  }

  /**
   * Remove the edge from node1 to node2 from this graph. (edge is directed and unweighted) If
   * either node does not exist, or if an edge from node1 to node2 does not exist, no edge is
   * removed and no exception is thrown.
   * 
   * Valid argument conditions: 1. neither node is null 2. both nodes are in the graph 3. the edge
   * from node1 to node2 is in the graph
   */
  public int removeEdge(Person node1, Person node2) {
    if (node1 == null || node2 == null) {
      return -1;
    }
    GraphNode n1 = null;
    GraphNode n2 = null;
    for (GraphNode node : nodes) {
      if (node.person.getName().equals(node1.getName())) {
        n1 = node;
      }
      if (node.person.getName().equals(node2.getName())) {
        n2 = node;
      }
      if (n1 != null && n2 != null) {
        break;
      }
    }
    if (n1 == null || n2 == null) {
      return 0;
    }
    boolean edgeFound = false;
    for (GraphNode n : n1.neighbors) {
      if (n.person.getName().equals(n2.person.getName())) {
        edgeFound = true;
        break;
      }
    }
    if (!edgeFound) {
      return 0;
    }
    n1.neighbors.remove(n2);
    n2.neighbors.remove(n1);
    --size;
    return 1;
  }

  /**
   * This method returns a Set that contains all the nodes.
   */
  public Set<Person> getAllNodes() {
    Set<Person> returnedSet = new HashSet<Person>();
    for (GraphNode n : nodes) {
      returnedSet.add(n.person);
    }
    return returnedSet;
  }

  /**
   * This method gets all the neighbors of a node.
   */
  public List<Person> getNeighbors(Person node) {
    if (node == null) {
      return null;
    }
    GraphNode adjVertOf = null;
    for (GraphNode n : nodes) {
      if (n.person.getName().equals(node.getName())) {
        adjVertOf = n;
      }
    }
    if (adjVertOf == null) {
      return null;
    }
    List<Person> neighbors = new ArrayList<Person>();
    for (GraphNode n : adjVertOf.neighbors) {
      neighbors.add(n.person);
    }
    return neighbors;
  }

  /**
   * This method returns the Person object based off of the string provided to it.
   */
  public Person getNode(String name) {
    if (name == null) {
      return null;
    }
    Person thePerson = null;
    for (GraphNode n : nodes) {
      if (n.person.getName().equals(name)) {
        thePerson = n.person;
      }
    }
    return thePerson;
  }

  /**
   * Returns the number of edges in this graph.
   */
  public int size() {
    return size;
  }

  /**
   * Returns the number of nodes in this graph.
   */
  public int order() {
    return order;
  }
}
