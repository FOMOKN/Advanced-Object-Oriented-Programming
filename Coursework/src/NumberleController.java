import java.awt.*;
import java.util.Map;

/**
 * Controller class for the Numberle game.
 * Mediates interactions between the model and the view.
 */
public class NumberleController {
    private INumberleModel model;
    private NumberleView view;

    /**
     * Constructor to initialize the controller with a model.
     * @param model The model object that handles the game logic.
     */
    public NumberleController(INumberleModel model) {
        this.model = model;
    }

    /**
     * Sets the view for this controller.
     * @param view The view that displays the game UI.
     */
    public void setView(NumberleView view) {
        this.view = view;
    }

    /**
     * Processes the user's input through the model.
     * @param input The equation guess from the user.
     */
    public void processInput(String input) {
        model.processInput(input);
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, otherwise false.
     */
    public boolean isGameOver() {
        return model.isGameOver();
    }

    /**
     * Checks if the game has been won.
     * @return true if the game has been won, otherwise false.
     */
    public boolean isGameWon() {
        return model.isGameWon();
    }

    /**
     * Retrieves the target equation from the model.
     * @return The target equation.
     */
    public String getTargetEquation() {
        return model.getTargetEquation();
    }

    /**
     * Gets the number of remaining attempts from the model.
     * @return The number of remaining attempts.
     */
    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    /**
     * Retrieves the color feedback for each character of the last guess from the model.
     * @return An array of color codes as strings.
     */
    public String[] getColors() {
        return model.getColors();
    }

    /**
     * Retrieves the color states of buttons in the GUI from the model.
     * @return A map of characters to their corresponding color codes.
     */
    public Map<Character, String> getButtonColors() {
        return model.getButtonColors();
    }

    /**
     * Starts a new game by reinitializing the model.
     */
    public void startNewGame() {
        model.startNewGame();
    }

    /**
     * Gets the maximum number of attempts allowed from the model.
     * @return The maximum number of attempts.
     */
    public int getMAX_ATTEMPTS() {
        return model.getMAX_ATTEMPTS();
    }

    /**
     * Retrieves the status of the first flag concerning error handling on invalid inputs.
     * @return true if errors on invalid inputs should be shown, false otherwise.
     */
    public boolean getFlag1() {return model.getFlag1();}

    /**
     * Retrieves the status of the second flag concerning display the target equation.
     * @return true if the target equation should be shown, false otherwise
     */
    public boolean getFlag2() {return model.getFlag2();}

    /**
     * Validates the user's input to check if it conforms to the rules of forming equations.
     * @param input The player's input equation to validate.
     * @return true if the input is valid, false if invalid.
     */
    public boolean checkInput(String input) {
        return model.checkInput(input);
    }

    /**
     * Retrieves the current error message if any validation fails.
     * @return Current error message.
     */
    public String getError() {return model.getError();}
}