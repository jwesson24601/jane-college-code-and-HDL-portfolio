/**
 * This class represents one user in the social network.
 * 
 * @author Jake Wesson
 */
public class Person {
  private String name; // name of user
  
  /**
   * This is the only constructor for the person object.
   * 
   * @param name - name of new person object
   */
  public Person(String name) {
    this.name = name;
  }
  
  /**
   * This method returns the name of this person.
   * 
   * @return - the name associated with this person
   */
  public String getName() {
    return name;
  }

  /**
   * This method sets the name of this person.
   * 
   * @param name - the name to be associated with this person
   */
  public void setName(String name) {
    this.name = name;
  }
}

