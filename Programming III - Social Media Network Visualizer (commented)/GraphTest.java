import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphTest {
  Graph graph;
  
  @BeforeEach
  void setup() throws Exception {
    graph = new Graph();
  }
  
  @Test
  void test00_one_node() {
    Person person = new Person("John Flansburgh");
    graph.addNode(person);
    if (graph.order() != 1) {
      fail("wrong order");
    }
    if (graph.getNode("John Flansburgh") == null || !graph.getNode("John Flansburgh").getName().equals("John Flansburgh")) {
      fail("created node is null or has wrong name");
    }
    if (!graph.getAllNodes().contains(person)) {
      fail("getAllNodes did not find node");
    }
    if (graph.getNeighbors(person).size() != 0) {
      fail("node should have no neighbors");
    }
    if (graph.size() != 0) {
      fail("number of vertices should be zero");
    }
    if (graph.order() != 1) {
      fail("number of nodes should be one");
    }
    graph.removeNode(person);
    if (graph.order() != 0) {
      fail("number of nodes did not decrease after removal");
    }
    if (graph.getNode("John Flansburgh") != null) {
      fail("found node after node removed");
    }
    if (graph.getAllNodes().contains(person)) {
      fail("list of nodes included node after removal");
    }
  }
  
  @Test
  void test01_empty() {
    Person person = new Person("John Flansburgh");
    if (graph.getNeighbors(person) != null) {
      fail("getNeighbors returned wrong value for empty");
    }
    if (graph.size() != 0) {
      fail("empty graph number of vertices is non-zero");
    }
    if (graph.order() != 0) {
      fail("empty graph number of nodes is non-zero");
    }
    if (graph.getNode("John Flansburgh") != null) {
      fail("getNode returned non-null for empty graph");
    }
    if (graph.getAllNodes().contains(person)) {
      fail("empty graph contained a node");
    }
    if (graph.removeNode(person) != 0) {
      fail("removeNode returned wrong for empty graph");
    }
  }
  
  @Test
  void test02_lots_of_nodes() {
    Person person1 = new Person("John Flansburgh");
    Person person2 = new Person("John Linnel");
    Person person3 = new Person("Ian Paice");
    Person person4 = new Person("Bill Bruford");
    Person person5 = new Person("Mitch Mitchell");
    Person person6 = new Person("John Bonham");
    Person person7 = new Person("Bernard Purdie");
    Person person8 = new Person("Jeff Porcaro");
    graph.addEdge(person1, person2);
    graph.addEdge(person1, person3);
    graph.addEdge(person2, person3);
    graph.addNode(person4);
    graph.addEdge(person4, person3);
    graph.addNode(person5);
    graph.addEdge(person6, person7);
    graph.removeEdge(person6, person7);
    graph.addEdge(person8, person6);
    graph.addEdge(person7, person8);
  }

}

