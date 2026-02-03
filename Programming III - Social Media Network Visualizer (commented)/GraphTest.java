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
      fail("shid");
    }
    if (graph.getNode("John Flansburgh") == null || !graph.getNode("John Flansburgh").getName().equals("John Flansburgh")) {
      fail("oh no");
    }
    if (!graph.getAllNodes().contains(person)) {
      fail("dangit");
    }
    if (graph.getNeighbors(person).size() != 0) {
      fail("bitch");
    }
    if (graph.size() != 0) {
      fail("fuck");
    }
    if (graph.order() != 1) {
      fail("drongus");
    }
    graph.removeNode(person);
    if (graph.order() != 0) {
      fail("dingus");
    }
    if (graph.getNode("John Flansburgh") != null) {
      fail("oh my aching tentacles");
    }
    if (graph.getAllNodes().contains(person)) {
      fail("whatcha doing kirby");
    }
  }
  
  @Test
  void test01_empty() {
    Person person = new Person("John Flansburgh");
    if (graph.getNeighbors(person) != null) {
      fail("bitch");
    }
    if (graph.size() != 0) {
      fail("fuck");
    }
    if (graph.order() != 0) {
      fail("dingus");
    }
    if (graph.getNode("John Flansburgh") != null) {
      fail("oh my aching tentacles");
    }
    if (graph.getAllNodes().contains(person)) {
      fail("whatcha doing kirby");
    }
    if (graph.removeNode(person) != 0) {
      fail("ojuefoiasdf");
    }
  }
  
  @Test
  void test02_shit_ton_of_nodes() {
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
