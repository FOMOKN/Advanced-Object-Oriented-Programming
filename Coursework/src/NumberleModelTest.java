import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Author: Ryan
 * Date: 2024/4/24
 */
class NumberleModelTest {
    NumberleModel model;

    @BeforeEach
    void setUp() {
        model = new NumberleModel();
        model.initialize(); // Initialize the game state before each test
    }

    @AfterEach
    void tearDown() {
        model = null; // Clean up after each test
    }

    /**
     * Test scenario 1: Validate Error Handling for Invalid Inputs
     * This test verifies that the model correctly handles various types of invalid inputs
     * by returning the appropriate error messages.
     */
    @Test
    void testInvalidInputHandling() {
        // Input too short
        String tooShortInput = "1+1=2";
        assertFalse(model.checkInput(tooShortInput),
                "Input that is too short should be rejected");
        assertEquals("The length of the equation must be 7", model.getError(),
                "Error message should indicate the equation is too short");

        // Input without an equals sign
        String noEqualsInput = "123+456";
        assertFalse(model.checkInput(noEqualsInput),
                "Input without an equals sign should be rejected");
        assertEquals("No equal '=' sign", model.getError(),
                "Error message should indicate missing '=' sign");

        // Input with invalid characters
        String invalidCharactersInput = "1^1+1=2";
        assertFalse(model.checkInput(invalidCharactersInput),
                "Input with invalid characters should be rejected");
        assertEquals("Illegal math symbols detected", model.getError(),
                "Error message should indicate invalid characters are not allowed");
    }

    /**
     * Test scenario 2: Verify Correct Input and Game Logic
     * This test ensures that a valid correct guess updates the game state appropriately,
     * decrementing the remaining attempts.
     */
    @Test
    void testCorrectInputAndGameLogic() {
        // Set a valid equation but not equals to the target equation for controlled testing
        String validGuess = "1+1+1=3";
        assertTrue(model.checkInput(validGuess), "Correct and valid input should be accepted");
        model.processInput(validGuess);
        assertFalse(model.isGameWon(), "Game should be won if correct guess is processed");
        assertEquals(model.getRemainingAttempts(), NumberleModel.MAX_ATTEMPTS - 1,
                "Remaining attempts should decrement by one after processing a guess");
    }

    /**
     * Test scenario 3: Evaluate Visual Feedback for Equation Comparison
     * This test checks the functionality of comparing a user's valid guess against the target equation to determine
     * the correctness of each character's position in the guess. It specifically tests for correct feedback in terms of
     * color coding which reflects correct positions (Green), incorrect positions but correct character (Orange), and
     * characters that do not exist in the target equation (Grey).
     * This ensures that the model provides accurate visual feedback to the user.
     */
    @Test
    void testCompareEquations() {
        String validGuess = "1+1-2=0";
        String targetEquation = "1+2+3=6"; // Directly setting for test purpose
        String[] feedback = model.compareEquations(validGuess, targetEquation);
        assertEquals("Green", feedback[0], "Color feedback for correct position should be 'Green'");
        assertEquals("Green", feedback[1], "Color feedback for correct position should be 'Green'");
        assertEquals("Grey", feedback[2], "Color feedback for not exist should be 'Grey'");
        assertEquals("Grey", feedback[3], "Color feedback for not exist should be 'Grey'");
        assertEquals("Orange", feedback[4], "Color feedback for incorrect position should be 'Orange'");
        assertEquals("Green", feedback[5], "Color feedback for correct position should be 'Green'");
        assertEquals("Grey", feedback[6], "Color feedback for not exist should be 'Grey'");
    }

}