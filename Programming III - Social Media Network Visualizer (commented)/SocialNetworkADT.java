import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * This interface describes how to create a social network to correctly interact with the GUI
 * defined in Main.java.
 * 
 * @author Nate Smith and Jake Wesson
 */
public interface SocialNetworkADT {

  /**
   * This method adds a friendship between two users. If either user is not in the network, they are
   * added and the friendship is created between them.
   * 
   * @param user - one of the people that a friendship will be created between
   * @param friend - the other person that a friendship will be created between
   * @return true if friendship added, false if not
   * @throws FileNotFoundException - thrown if log file disappears
   */
  public boolean addFriends(String user, String friend) throws FileNotFoundException;

  /**
   * This method adds a friendship between two users. If either user is not in the network, they are
   * added and the friendship is created between them.
   * 
   * @param user - one of the people that a friendship will be created between
   * @param friend - the other person that a friendship will be created between
   * @return true if friendship added, false if not
   * @throws FileNotFoundException - thrown if log file disappears
   */
  public boolean removeFriends(String user, String friend) throws FileNotFoundException;

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
  public boolean addUser(String user) throws FileNotFoundException;

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
  public boolean removeUser(String user) throws FileNotFoundException;

  /**
   * This method returns all of the friends of a specific user in a set.
   * 
   * @param user - the user to get the friends of
   * @return a set containing the friends of the user
   */
  public Set<Person> getFriends(String user);

  /**
   * This method finds mutual friends between user1 and user2.
   * 
   * @param user1 - first user to compare to
   * @param user2 - second user to compare to
   * @return set containing mutual friends between these two users
   */
  public Set<Person> getMutualFriends(String user1, String user2);

  /**
   * This method finds the shortest path between any two connected nodes in the graph.
   * 
   * @param user1 - the user to find the path from
   * @param user2 - the user to find the path to
   * @return the path taken to get between the two nodes
   */
  public List<Person> getShortestPath(String user1, String user2);

  /**
   * This method creates separate graphs out of the disconnected parts in the graphs, and it returns
   * them in a set.
   * 
   * @return a set of graphs created from the individual components split out from the main graph
   */
  public Set<Graph> getConnectedComponents();

  /**
   * This method loads input commands from a file and executes commands to create the graph.
   * 
   * @param filename - file name to load
   * @return - main user of type person if set, otherwise just returns null
   * @throws FileNotFoundException - thrown if log file disappears
   */
  public Person loadFromFile(File filename) throws FileNotFoundException;

  /**
   * This method saves the graph to a file by copying commands from the log file.
   * 
   * @param filename - file to save to
   * @throws FileNotFoundException - thrown if log file disappears
   * @throws IOException - thrown if file can't be written to
   */
  public void saveToFile(File filename) throws FileNotFoundException, IOException;
}
