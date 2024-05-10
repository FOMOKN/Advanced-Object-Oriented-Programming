// NumberleModel.java
// Imports necessary Java utility and IO classes
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// The main model class for the Numberle game implementing the observer pattern
public class NumberleModel extends Observable implements INumberleModel {
    private final boolean showErrorOnInvalidEquation = true; // Flag to display an error if the equation input is invalid
    private final boolean displayEquationForTesting = true; // Flag to display the equation for testing
    private final boolean randomlySelectEquation = true; // Flag to choose equations randomly or use a fixed one
    private String targetEquation; // Stores the current target equation
    private int remainingAttempts; // Counter for remaining guesses
    private boolean gameWon; // Indicates if the game has been won
    private List<String> equations = new ArrayList<>(); // List to store all valid equations
    private String errorMessage = ""; // Store the different errorMessage when user input the invalid equation
    private String[] colors; // Different colors for numbers and math symbols
    private Map<Character, String> buttonColors; // Color coding for the virtual keyboard buttons
    private Set<Character> unusedChars = new HashSet<>(); // Set of numbers and symbols

    /**
     * Initializes the game by loading equations, selecting a target equation, and resetting attempts.
     * @ invariant equations.size() > 0 : "There must be at least one equation available at all times."
     * @ ensures gameWon == false && remainingAttempts == MAX_ATTEMPTS
     *   && unusedChars.containsAll(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
     *   '+', '-', '*', '/', '='))
     *   && targetEquation.length() == EQUATION_LENGTH
     */
    @Override
    public void initialize() {
        loadEquations();
        selectTargetEquation();
        initializeRemainingAttempts(); //Initialize the number of remaining attempts
        initializeUnusedChars(); // Initialize the set of unused characters
        gameWon = false;
        setChanged(); // Set the flag indicating that the model has changed
        notifyObservers(); // Notify observers that the model has changed
    }

    /**
     * Loads equations from a specified file.
     * @ invariant !equations.isEmpty() : "Equations list should not be empty after loading."
     * @ ensures \forall String eq; eq \in equations; eq.length() == EQUATION_LENGTH
     */
    public void loadEquations() {
        try (BufferedReader reader = new BufferedReader(new FileReader(EQUATION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                assert line.length() == EQUATION_LENGTH : "Equation length is incorrect";
                equations.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    /**
     * Selects the target equation for the game, either randomly or a predetermined one for testing.
     * @ requires !equations.isEmpty() : "Equations list must not be empty"
     * @ ensures targetEquation != null && equations.contains(targetEquation)
     */
    public void selectTargetEquation() {
        assert !equations.isEmpty() : "Equations list must not be empty";
        if (randomlySelectEquation) {
            Random rand = new Random();
            int index = rand.nextInt(equations.size());
            targetEquation = equations.get(index);
        } else {
            targetEquation = equations.get(0);  // Set to a fixed equation
        }
    }

    /**
     * Display the target equation according to flag 2.
     */
    @Override
    public void displayTargetEquation() {
        if (displayEquationForTesting) {
            System.out.println("Target Equation: " + targetEquation);
        }
    }

    /**
     * Validates the user's input against a set of rules to ensure it forms a valid equation.
     * @param input The user's input equation.
     * @ invariant targetEquation.length() == EQUATION_LENGTH;
     * @ requires input != null;
     * @ ensures \result == (\result ? evaluateEquationSides(input) : false);
     */
    @Override
    public boolean checkInput(String input) {
        assert input != null : "Input should not be null";  // Assert to ensure input is not null

        // Various checks on the input string to ensure it is a valid equation
        // 1. Check if the length is not 7
        if (input.length() != getEquationLength()) {
            setError("The length of the equation must be 7");
            return false;
        }

        // 2. Check if the input contains an equal sign
        else if (!input.contains("=")) {
            setError("No equal '=' sign");
            return false;
        }

        // 3. Check if the input contains at least one -+*/ symbol
        else if (!input.matches(".*[-+*/].*")) {
            setError("There must be at least one sign +-*/");
            return false;
        }

        // 4. Check if the input contains consecutive -+*/ symbols
        else if (input.matches(".*[-+*/]{2,}.*")) {
            setError("Multiple math symbol in a row");
            return false;
        }

        // 5. Check if the input contains any characters other than digits, -+*/=
        else if (input.matches(".*[^0-9-+*/=].*")) {
            setError("Illegal math symbols detected");
            return false;
        }

        // 6. Split the expression by the first equal sign only.
        // If it contains more than one equals sign, it will be handled in evaluate method.
        else {
           return evaluateEquationSides(input);
        }
    }

    /**
     * Evaluates the equation sides to check if they are equal.
     * @param input User input containing the equation.
     * @return true if both sides of the equation evaluate to the same result, false otherwise.
     */
    private boolean evaluateEquationSides(String input) {
        int indexOfEqual = input.indexOf('=');
        String leftPart = input.substring(0, indexOfEqual);
        String rightPart = input.substring(indexOfEqual + 1);

        try {
            int leftResult = compute(leftPart);
            int rightResult = compute(rightPart);

            // Handle division by zero specially. 8/0=9/0 can be validated.
            if (leftResult == Integer.MAX_VALUE && rightResult == Integer.MAX_VALUE) {
                return true; // Both sides are division by zero
            }

            if (leftResult != rightResult) {
                setError("The left side is not equal to the right side");
                return false;
            }
            return true;
        } catch (Exception e) {
            setError("Calculation error. Please enter a valid equation");
            return false;
        }
    }

    /**
     * Evaluates a mathematical expression and returns the calculated result.
     * @param expression The mathematical expression to evaluate.
     * @return The result of the expression as an integer.
     */
    private int compute(String expression) {
        // Arithmetic expressions are evaluated using BODMAS.
        // BODMAS stands for "Brackets, Orders (exponents), Division and multiplication, Addition and Subtraction."
        // This means that operations within brackets are performed first, followed by any exponents.
        // Then division and multiplication (from left to right).
        // And finally addition and subtraction (from left to right).
        try {
            expression = expression.replaceAll("\\s+", "");
            List<Object> tokens = new ArrayList<>();

            // Handle parentheses. An aspect that can be expanded in the future

            // Tokenize the expression
            StringBuilder numberBuffer = new StringBuilder();
            for (char ch : expression.toCharArray()) {
                if (Character.isDigit(ch)) {
                    numberBuffer.append(ch);
                } else {
                    if (numberBuffer.length() > 0) {
                        tokens.add(Integer.parseInt(numberBuffer.toString()));
                        numberBuffer = new StringBuilder();
                    }
                    tokens.add(ch);
                }
            }
            if (numberBuffer.length() > 0) {
                tokens.add(Integer.parseInt(numberBuffer.toString()));
            }

            // Handle exponentiation. An aspect that can be expanded in the future

            // Handle multiplication and division
            handleMultiplicationAndDivision(tokens);

            // Handle addition and subtraction
            return handleAdditionAndSubtraction(tokens);
        } catch (Exception e) {
            // If any exception is encountered
            throw new RuntimeException("Error in expression evaluation: " + e.getMessage(), e);
        }
    }

    /**
     * Handles multiplication and division in the list of tokens.
     * @param tokens List of tokens including numbers and operators.
     */
    private void handleMultiplicationAndDivision(List<Object> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i) instanceof Character) {
                char operator = (Character) tokens.get(i);
                if (operator == '*' || operator == '/') {
                    int left = (Integer) tokens.get(i - 1);
                    int right = (Integer) tokens.get(i + 1);
                    if (operator == '/' && right == 0) {
                        tokens.set(i - 1, Integer.MAX_VALUE); // Use MAX_VALUE to represent division by zero
                        tokens.remove(i);
                        tokens.remove(i);
                        i--;
                        continue;
                    }
                    int result = operator == '*' ? left * right : left / right;
                    tokens.set(i - 1, result);
                    tokens.remove(i);
                    tokens.remove(i);
                    i--;
                }
            }
        }
    }

    /**
     * Computes the final result from the list of tokens, supporting leading and unary plus and minus.
     * @param tokens List of tokens including numbers and operators.
     * @return Final calculation result as an integer.
     */
    private int handleAdditionAndSubtraction(List<Object> tokens) {
        // Initialize result and starting index
        int result;
        int i;

        // Handle leading plus or minus. For example, +5+6=11 or -1-2=-3 is valid equation
        if (tokens.get(0) instanceof Character && (tokens.get(0).equals('+') || tokens.get(0).equals('-'))) {
            // Start processing from the next operator after the unary plus
            if (tokens.get(0).equals('-')) {
                result = -(Integer) tokens.get(1); // Apply unary minus
            } else {
                result = (Integer) tokens.get(1); // Unary plus doesn't change the number
            }
            i = 2; // Start processing from the next operator after the unary minus
        } else {
            result = (Integer) tokens.get(0); // Normal case, start with the first number
            i = 1; // Start processing from the first operator
        }

        // Process the rest of the tokens
        for (; i < tokens.size(); i += 2) {
            if (i + 1 < tokens.size()) { // Ensure there is a number following the operator
                char operator = (Character) tokens.get(i);
                int right = (Integer) tokens.get(i + 1);
                if (operator == '+') {
                    result += right;
                } else if (operator == '-') {
                    result -= right;
                }
            }
        }

        return result;
    }

    /**
     * Processes the user's guess by comparing it against the target equation and updates the game state.
     * Decrements the remaining attempts and notifies observers of any changes.
     * @param input The user's guess to process.
     */
    @Override
    public void processInput(String input) {
        assert targetEquation != null : "Target equation must not be null";
        assert targetEquation.length() == EQUATION_LENGTH : "Target equation length is incorrect";
        compareEquations(input, targetEquation);
        remainingAttempts--;
        setChanged();
        notifyObservers();
    }

    /**
     * Compares the user's guess against the target equation and provides visual feedback.
     * Determines which characters are correctly placed, present but misplaced, or absent, and updates the color feedback accordingly.
     * @param currentGuess The user's current guess.
     * @param targetEquation The target equation to compare against.
     * @return An array of color codes indicating feedback for each character in the guess.
     */
    public String[] compareEquations(String currentGuess, String targetEquation) {
        StringBuilder feedback = new StringBuilder();
        colors = new String[EQUATION_LENGTH];
        buttonColors = new HashMap<>();
        Map<Character, Integer> targetCounts = new HashMap<>();
        Map<Character, Integer> guessCounts = new HashMap<>();

        // Count each character in the target equation to manage character frequencies
        for (char c : targetEquation.toCharArray()) {
            targetCounts.put(c, targetCounts.getOrDefault(c, 0) + 1);
        }

        // Remove used characters from the unused character set
        for (char c : currentGuess.toCharArray()) {
            unusedChars.remove(c);
        }

        // Check if the current guess exactly matches the target equation
        if (currentGuess.equals(targetEquation)) {
            String ANSI_RESET = "\033[0m"; // ANSI color code. Default Color
            String ANSI_GREEN = "\033[32m";   // Green color for correct guesses
            gameWon = true;
            Arrays.fill(colors, "Green");
            for (char c : currentGuess.toCharArray()) {
                buttonColors.put(c, "Green");
            }
            feedback.append(ANSI_GREEN).append(currentGuess).append(ANSI_RESET);
            System.out.println(feedback);
        } else {
            // Process exact matches
            for (int i = 0; i < EQUATION_LENGTH; i++) {
                char guessChar = currentGuess.charAt(i);
                if (guessChar == targetEquation.charAt(i) && targetCounts.get(guessChar) > 0) {
                    colors[i] = "Green";
                    buttonColors.put(guessChar, "Green");
                    targetCounts.put(guessChar, targetCounts.get(guessChar) - 1); // Decrease in count
                    guessCounts.put(guessChar, guessCounts.getOrDefault(guessChar, 0) + 1);
                }
            }
            // Then handle partial matches and non-existent characters
            for (int i = 0; i < EQUATION_LENGTH; i++) {
                if (colors[i] == null) { // Only processed if the position is not marked green
                    char guessChar = currentGuess.charAt(i);
                    if (targetCounts.getOrDefault(guessChar, 0) > 0) {
                        colors[i] = "Orange";
                        buttonColors.putIfAbsent(guessChar, "Orange");
                        targetCounts.put(guessChar, targetCounts.get(guessChar) - 1);
                        guessCounts.put(guessChar, guessCounts.getOrDefault(guessChar, 0) + 1);
                    } else {
                        colors[i] = "Grey";
                        buttonColors.putIfAbsent(guessChar, "Grey");
                    }
                }
            }
            // Build feedback based on the determined colors
            buildFeedback(currentGuess);
        }
        return colors; // Return the array of color feedback for each character
    }

    /**
     * Builds the visual feedback for the user's guess by appending ANSI color codes to each character.
     * Each character in the feedback string is colored.
     * The method also prints hints and a summary of characters that have not been used yet.
     * @param currentGuess The current guess string whose characters are evaluated against the target equation.
     */
    private void buildFeedback(String currentGuess) {
        StringBuilder feedback = new StringBuilder();
        for (int i = 0; i < EQUATION_LENGTH; i++) {
            char guessChar = currentGuess.charAt(i);
            // ANSI color code. Default Color
            String ANSI_RESET = "\033[0m";
            if (colors[i].equals("Green")) {
                // Green color for correct guesses
                String ANSI_GREEN = "\033[32m";
                feedback.append(ANSI_GREEN).append(guessChar).append(ANSI_RESET);
            } else if (colors[i].equals("Orange")) {
                // Existing but not in the right place, use orange color
                String ANSI_ORANGE = "\033[38;5;208m";
                feedback.append(ANSI_ORANGE).append(guessChar).append(ANSI_RESET);
            } else {
                // Not existed in the target equation, use gray color
                String ANSI_WHITE = "\033[90m";
                feedback.append(ANSI_WHITE).append(guessChar).append(ANSI_RESET);
            }
        }

        System.out.println("Hints:");
        System.out.println(feedback);
        System.out.println("Green indicates that numbers or math symbols are exist and are in the correct position");
        System.out.println("Orange indicates that numbers or math symbols are exist but not in the correct position");
        System.out.println("Gray represents numbers or math symbols not exist in the target equation");
        System.out.println("You have not used these numbers and symbols yet: " + unusedChars);
    }

    /**
     * Sets the error message to be displayed for invalid inputs.
     * @param error The error message to set.
     */
    public void setError(String error) {
        this.errorMessage = error;
    }

    /**
     * Retrieves the current error message.
     * @return The current error message if any.
     */
    @Override
    public String getError() {
        return this.errorMessage;
    }

    /**
     * Checks if the game is over, either because all attempts are used or the correct equation has been guessed.
     * @return true if the game is over, false otherwise.
     */
    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    /**
     * Checks if the game has been won.
     * @return true if the game is won, false otherwise.
     */
    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public boolean getFlag1() {
        return showErrorOnInvalidEquation; // Return the value of flag_1
    }

    @Override
    public boolean getFlag2() {
        return displayEquationForTesting; // Return the value of flag_2
    }

    @Override
    public String getTargetEquation() {
        return targetEquation;
    }

    @Override
    public int getEquationLength() {
        return EQUATION_LENGTH;
    }

    @Override
    public int getMAX_ATTEMPTS() {
        return MAX_ATTEMPTS; // Return the maximum attempts
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public String[] getColors() {
        return colors;
    }

    @Override
    public Map<Character, String> getButtonColors() {
        return buttonColors;
    }

    @Override
    public void startNewGame() {
        initialize();
    }

    private void initializeRemainingAttempts() {
        remainingAttempts = MAX_ATTEMPTS; // Initialize remaining attempts to maximum attempts
    }

    private void initializeUnusedChars() {
        unusedChars.addAll(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/', '='));
    }

}
