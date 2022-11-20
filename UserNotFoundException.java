/** An exception thrown when a User's Directory cannot be accessed.
 * @author Destin Groves
 * @version November 2022
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
