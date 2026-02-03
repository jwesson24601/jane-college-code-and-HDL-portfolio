import java.util.List;
import java.util.Set;

/**
 * Filename: GraphADT.java 
 * Project: a3 
 * Author: Jake Wesson
 * 
 * This interface describes methods for a graph class.
 */
public interface GraphADT {

  /**
   * Add a new node to the graph.
   *
   * If node is null or already exists, method ends without adding a node or throwing an exception.
   * 
   * Valid argument conditions: 1. node is non-null 2. node is not already in the graph
   * 
   * @param node - the node to be added
   * @return 1 if node added successfully, 0 if node is in graph, -1 if null
   */
  public int addNode(Person node);


  /**
   * Remove a node and all associated edges from the graph.
   * 
   * If node is null or does not exist, method ends without removing a node, edges, or throwing an
   * exception.
   * 
   * Valid argument conditions: 1. node is non-null 2. node is not already in the graph
   * 
   * @param node - the node to be removed
   * @return 1 if the node removed successfully, 0 if not in graph, -1 if null
   */
  public int removeNode(Person node);


  /**
   * Add an edge between node1 and node2 to this graph.
   * 
   * If either node does not exist, node IS ADDED and then edge is created. No exception is thrown.
   *
   * If the edge exists in the graph, no edge is added and no exception is thrown.
   * 
   * Valid argument conditions: 1. neither node is null 2. the edge is not in the graph
   * 
   * @param node1 - the first node
   * @param node2 - the second node
   * @return 1 if edge added successfully, 0 if edge already in graph or node not in graph, -1 if
   *         either node is null
   */
  public int addEdge(Person node1, Person node2);


  /**
   * Remove the edge between node1 and node2 from this graph. If either node does not exist, or if
   * an edge between node1 and node2 does not exist, no edge is removed and no exception is thrown.
   * 
   * Valid argument conditions: 1. neither node is null 2. both nodes are in the graph 3. the edge
   * from node1 to node2 is in the graph
   * 
   * @param node1 the first node
   * @param node2 the second node
   * @return 1 if edge added successfully, 0 if edge not in graph, -1 if either node is null
   */
  public int removeEdge(Person node1, Person node2);


  /**
   * Returns a Set that contains all the nodes
   * 
   * @return a Set<String> which contains all the nodes in the graph
   */
  public Set<Person> getAllNodes();


  /**
   * Get all the neighbors of a node. Returns null if node not in graph or node is null.
   * 
   * @param node the specified node
   * @return an List<String> of all the adjacent nodes for specified node
   */
  public List<Person> getNeighbors(Person node);


  /**
   * This method returns the Person associated with the provided string.
   * 
   * @param node - the name of the Person being looked for
   * @return the person associated with that String, null if not in graph
   */
  public Person getNode(String node);


  /**
   * Returns the number of edges in this graph.
   * 
   * @return number of edges in the graph.
   */
  public int size();


  /**
   * Returns the number of nodes in this graph.
   * 
   * @return number of nodes in graph.
   */
  public int order();

}
