import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.PriorityQueue;

/**
 * This class represents the social network that the GUI directly interfaces to.
 * 
 * @author Jake Wesson and Nate Smith
 */
public class SocialNetwork implements SocialNetworkADT {
  private Graph graph; // the graph containing all the info

  private File log; // the file being written to in order to save commands
  PrintStream writer; // the writer to the file

  /**
   * This is the constructor for the social network class. It just instantiates the fields and not
   * much else.
   */
  public SocialNetwork() {
    graph = new Graph();
    log = new File("log.txt");
    try {
      writer = new PrintStream(log);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * This method adds a friendship between two users. If either user is not in the network, they are
   * added and the friendship is created between them.
   * 
   * @param user - one of the people that a friendship will be created between
   * @param friend - the other person that a friendship will be created between
   * @return true if friendship added, false if not
   * @throws FileNotFoundException - thrown if log file disappears
   */
  @Override
  public boolean addFriends(String user, String friend) throws FileNotFoundException {
    // input checking, checks strings not null or empty
    if (user == null || friend == null || user.length() == 0 || friend.length() == 0) {
      return false;
    }
    // check if both people in graph
    Person per1 = graph.getNode(user);
    Person per2 = graph.getNode(friend);
    // if either person doesn't exist, create them
    if (per1 == null) {
      per1 = new Person(user);
    }
    if (per2 == null) {
      per2 = new Person(friend);
    }
    // adds edge and checks if successfully added
    if (graph.addEdge(per1, per2) == 1) {
      writer.println("a " + user + " " + friend);
      return true;
    }
    return false;
  }

  /**
   * This method adds a friendship between two users. If either user is not in the network, they are
   * added and the friendship is created between them.
   * 
   * @param user - one of the people that a friendship will be created between
   * @param friend - the other person that a friendship will be created between
   * @return true if friendship added, false if not
   * @throws FileNotFoundException - thrown if log file disappears
   */
  @Override
  public boolean removeFriends(String user, String friend) throws FileNotFoundException {
    // input checking, checks if input strings null or empty
    if (user == null || friend == null || user.length() == 0 || friend.length() == 0) {
      return false;
    }
    // check both people in graph
    Person per1 = graph.getNode(user);
    Person per2 = graph.getNode(friend);
    if (per1 == null) {
      return false;
    }
    if (per2 == null) {
      return false;
    }
    // remove edge and check if edge removed successfully
    if (graph.removeEdge(per1, per2) == 1) {
      writer.println("r " + user + " " + friend);
      return true;
    }
    return false;
  }

  /**
   * This method adds a user to the social network.
   * 
   * If the user is added successfully, it returns true, but if the user is already in the graph,
   * the empty string, or null, it returns false.
   * 
   * @param user - user to add
   * @return true if user added, false if null or already exists
   * @throws FileNotFoundException - thrown if log file disappears
   */
  @Override
  public boolean addUser(String user) throws FileNotFoundException {
    // check valid input
    if (user == null || user.length() == 0) {
      return false;
    }
    // create person and see if it's added successfully
    Person person = new Person(user);
    if (graph.addNode(person) == 1) {
      writer.println("a " + user);
      return true;
    }
    return false;
  }

  /**
   * This method adds a user to the social network.
   * 
   * If the user is added successfully, it returns true, but if the user is already in the graph,
   * the empty string, or null, it returns false.
   * 
   * @param user - user to add
   * @return true if user added, false if null or already exists
   * @throws FileNotFoundException - thrown if log file disappears
   */
  @Override
  public boolean removeUser(String user) throws FileNotFoundException {
    // check valid input
    if (user == null || user.length() == 0) {
      return false;
    }
    // check if person removed from graph successfully
    Person person = new Person(user);
    if (graph.removeNode(person) == 1) {
      writer.println("r " + user);
      return true;
    }
    return false;

  }

  /**
   * This method returns all of the friends of a specific user in a set.
   * 
   * @param user - the user to get the friends of
   * @return a set containing the friends of the user
   */
  @Override
  public Set<Person> getFriends(String user) {
    // check for valid input
    if (user == null || user.length() == 0) {
      return null;
    }
    // check if node in graph
    Person person = graph.getNode(user);
    if (person == null) {
      return null;
    }
    // get friends from graph, place in set
    List<Person> friendList = graph.getNeighbors(person);
    Set<Person> friends = new HashSet<Person>();
    for (Person p : friendList) {
      friends.add(p);
    }
    return friends;
  }

  /**
   * Returns all people in the social network.
   * 
   * @return a set containing all people
   */
  public Set<Person> getAllUsers() {
    return graph.getAllNodes();
  }

  /**
   * This method finds mutual friends between user1 and user2.
   * 
   * @param user1 - first user to compare to
   * @param user2 - second user to compare to
   * @return set containing mutual friends between these two users
   */
  @Override
  public Set<Person> getMutualFriends(String user1, String user2) {
    Set<Person> mutual = getFriends(user1);// get friends of user1 in a Set
    // compare two sets of friends and retain common friends
    mutual.retainAll(getFriends(user2));
    return mutual;
  }

  /**
   * This private class only exists to keep the distance and person together for the priority queue
   * to function correctly. Since the default java priority queue works on "natural ordering"
   * according to the API, this class implements Comparable in order to determine priority.
   * 
   * @author Jake Wesson
   *
   */
  private class inPq implements Comparable<inPq> {
    private int dist; // distance from start node
    private Person thePerson; // person object associated with distance

    /**
     * Constructor for this class.
     * 
     * @param p - person object
     * @param dist - distance from start node
     */
    public inPq(Person p, int dist) {
      thePerson = p;
      this.dist = dist;
    }

    /**
     * Overridden compareTo, it just compares the distances to determine priority.
     */
    @Override
    public int compareTo(inPq comp) {
      return dist - comp.dist;
    }
  }

  /**
   * This method implements Dijkstra's method to find the shortest path between any two connected
   * nodes in the graph.
   * 
   * @param user1 - the user to find the path from
   * @param user2 - the user to find the path to
   * @return the path taken to get between the two nodes
   */
  @Override
  public List<Person> getShortestPath(String person1, String person2) {
    // input checking
    if (person1 == null || person2 == null || person1.length() == 0 || person2.length() == 0) {
      return null;
    }
    // check nodes are in graph
    Person per1 = graph.getNode(person1);
    Person per2 = graph.getNode(person2);
    if (per1 == null || per2 == null) {
      return null;
    }
    // create five parallel arrays, the person being checked, its distance from starting person,
    // whether it's visited, its predecessor, and an array of priority queue objects
    List<Person> people = new LinkedList<Person>();
    for (Person p : graph.getAllNodes()) {
      people.add(p);
    }

    int[] distance = new int[people.size()];
    boolean[] visited = new boolean[people.size()];
    Person[] pred = new Person[people.size()];

    // initialize distance to uninitialized value
    for (int i = 0; i < distance.length; ++i) {
      distance[i] = -1;
    }

    // create all priority queue nodes, initialize, and add
    List<inPq> nodes = new LinkedList<inPq>();
    for (Person p : people) {
      inPq qAddition = new inPq(p, -1);
      nodes.add(qAddition);
    }
    // create p queue and set up starting node
    PriorityQueue<inPq> pq = new PriorityQueue<inPq>();
    for (inPq in : nodes) {
      if (in.thePerson.getName().equals(person1)) {
        pq.add(in);
        in.dist = 0;
        distance[people.indexOf(in.thePerson)] = 0;
        visited[people.indexOf(in.thePerson)] = true;
        break;
      }
    }
    // start of actual algorithm (first non-setup thing)F
    while (!pq.isEmpty()) {
      inPq currNode = pq.poll();
      visited[people.indexOf(currNode.thePerson)] = true;
      for (Person p : getFriends(currNode.thePerson.getName())) {
        int index = people.indexOf(p);
        // for each unvisited successor of currNode...
        if (!visited[index]) {
          if (distance[index] == -1 || distance[index] > (currNode.dist + 1)) {
            nodes.get(index).dist = currNode.dist + 1;
            distance[index] = currNode.dist + 1;
            pred[index] = currNode.thePerson;
            pq.add(nodes.get(index));
          }
        }
      }
    }
    // process the predecessor list into an order of nodes to visit
    int start = people.indexOf(per2);
    int end = people.indexOf(per1);
    // check if both per1 and per2 actually connected
    if (pred[start] == null) {
      return null;
    }
    int currIndex = start;
    // traverse the array by chaining together predecessor nodes until start of path node reached
    LinkedList<Person> theOrder = new LinkedList<Person>();
    theOrder.add(per2);
    while (currIndex != end) {
      theOrder.addFirst(pred[currIndex]);
      currIndex = people.indexOf(pred[currIndex]);
    }
    return theOrder;
  }

  /**
   * This method creates separate graphs out of the disconnected parts in the graphs, and it returns
   * them in a set.
   * 
   * @return a set of graphs created from the individual components split out from the main graph
   */
  @Override
  public Set<Graph> getConnectedComponents() {
    // setup stuff
    Set<Person> allPeople = graph.getAllNodes();
    Set<Graph> connectedGraphs = new HashSet<Graph>();
    // loop for adding graphs to connectedGraphs
    while (allPeople.size() != 0) {
      Graph currGraph = new Graph();
      Queue<Person> q = new LinkedList<Person>();
      q.add((Person) allPeople.toArray()[0]);
      // loop for building connected component into a graph, basically a BFS
      while (!q.isEmpty()) {
        Person currNode = q.poll();
        currGraph.addNode(currNode);
        allPeople.remove(currNode);
        // for each unvisited successor...
        for (Person p : graph.getNeighbors(currNode)) {
          if (currGraph.getNode(p.getName()) == null) {
            currGraph.addEdge(currNode, p);
            q.add(p);
            allPeople.remove(p);
          }
        }
      }
      connectedGraphs.add(currGraph);
    }
    return connectedGraphs;
  }

  /**
   * This method loads input commands from a file and executes commands to create the graph.
   * 
   * @param filename - file name to load
   * @return - main user of type person if set, otherwise just returns null
   * @throws FileNotFoundException - thrown if log file disappears
   */
  @Override
  public Person loadFromFile(File filename) throws FileNotFoundException, IllegalArgumentException {
    Scanner scnr = new Scanner(filename);
    Person mainUser = null;

    // loop through each line until run out of input
    while (scnr.hasNextLine()) {
      String input = scnr.nextLine();

      // split into seperate string and insert into array
      String[] command = input.split(" ");

      // interpret input
      switch (command[0]) {
        case "a":
          if (command.length == 3)// add friends on three inputs
            addFriends(command[1], command[2]);
          else if (command.length == 2)// add user on two inputs
            addUser(command[1]);
          // throw illegalArgumentException on invalid input
          else
            throw new IllegalArgumentException();
          break;
        case "r":
          if (command.length == 3)// remove friends on three inputs
            removeFriends(command[1], command[2]);
          else if (command.length == 2)// remove user on two inputs
            removeUser(command[1]);
          // throw illegalArgumentException on invalid inupt
          else
            throw new IllegalArgumentException();
          break;
        case "s":
          if (command.length == 2)// set main user on two inputs
            mainUser = graph.getNode(command[1]);
          // throw illegalArgumentException on invalid input
          else
            throw new IllegalArgumentException();
          break;
        // invalid first argument
        default:
          throw new IllegalArgumentException();
      }
    }
    scnr.close();
    return mainUser;
  }

  /**
   * Returns the total number of users in the social network.
   * 
   * @return the number of users
   */
  public int getNumUsers() {
    return graph.order();
  }

  /**
   * Returns the total number of friendships in the social network.
   * 
   * @return the number of friendships
   */
  public int getNumFriends() {
    return graph.size();
  }

  /**
   * This method saves the graph to a file by copying commands from the log file.
   * 
   * @param filename - file to save to
   * @throws FileNotFoundException - thrown if log file disappears
   * @throws IOException - thrown if file can't be written to
   */
  @Override
  public void saveToFile(File filename) throws IOException {

    FileInputStream log = new FileInputStream(this.log);
    FileOutputStream out = new FileOutputStream(filename);

    byte[] buffer = new byte[1024];
    int length;

    while ((length = log.read(buffer)) > 0) {
      out.write(buffer, 0, length);
    }

    out.close();
    log.close();
  }


}
