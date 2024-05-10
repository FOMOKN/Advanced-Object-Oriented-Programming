import java.util.List;
import java.util.Map;

/**
 * Interface for the Numberle game model.
 * Defines the essential operations and properties that the model must implement.
 */
public interface INumberleModel {
    int MAX_ATTEMPTS = 6; // The maximum number of attempts allowed in the game
    int EQUATION_LENGTH = 7; // The fixed length of the mathematical equations to guess
    String EQUATION_FILE = "equations.txt";  // File path for loading potential target equations

    /**
     * Initializes or resets the game to its starting state.
     */
    void initialize();

    /**
     * Processes the user's input guess and updates the game state accordingly.
     * @param input the player's guessed equation as a String
     */
    void processInput(String input);

    /**
     * Retrieves the current error message if any validation fails.
     * @return current error message
     */
    String getError();

    /**
     * Checks if the game is over either through winning or exhausting all attempts.
     * @return true if the game is over, false otherwise
     */
    boolean isGameOver();

    /**
     * Checks if the game has been won.
     * @return true if the game is won, false otherwise
     */
    boolean isGameWon();

    /**
     * Retrieves the target equation for the current game.
     * @return the target equation as a String
     */
    String getTargetEquation();

    /**
     * Returns the fixed length of the equations used in the game.
     * @return the length of equations
     */
    int getEquationLength();

    /**
     * Returns the fixed length of the equations used in the game.
     * @return the length of equations
     */
    int getMAX_ATTEMPTS();

    /**
     * Provides the current color feedback for each character in the last guess.
     * @return an array of color codes as Strings, corresponding to the feedback for each character
     */
    String[] getColors();

    /**
     * Provides the color states of buttons in the GUI.
     * @return a map of characters to their corresponding color codes
     */
    Map<Character, String> getButtonColors();

    /**
     * Retrieves the status of the first flag concerning error handling on invalid inputs.
     * @return true if errors on invalid inputs should be shown, false otherwise
     */
    boolean getFlag1();

    /**
     * Retrieves the status of the second flag concerning display the target equation.
     * @return true if the target equation should be shown, false otherwise
     */
    boolean getFlag2();

    /**
     * Display the target equation if the flag 2 is true.
     */
    void displayTargetEquation();

    /**
     * Validates the user's input to check if it conforms to the rules of forming equations.
     * @param input the player's input equation to validate
     * @return true if the input is valid, false if invalid
     */
    boolean checkInput(String input);

    /**
     * Retrieves the number of remaining attempts the player has to guess the equation.
     * @return the number of remaining attempts
     */
    int getRemainingAttempts();

    /**
     * Starts a new game by reinitializing the game model.
     */
    void startNewGame();
}