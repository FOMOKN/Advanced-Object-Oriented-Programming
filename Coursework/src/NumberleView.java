// NumberleView.java
import javax.swing.*;
import java.awt.*;
import java.util.Observer;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.geom.RoundRectangle2D;

/**
 * The view component of the MVC pattern for the Numberle game. This class handles the user interface,
 * including setup and updates to the visual components based on changes in the game state.
 */
public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private JPanel menuPanel; // Panel on the west side for game info
    private String targetEquation; // String target equation when visible
    private JPanel inputPanel; // Panel for the input text boxes
    private JTextField[][] equationFields; // Grid of text fields for equation input
    private Map<String, JButton> buttonMap = new HashMap<>(); // Map of buttons
    private JPanel mathPanel; // Panel for number and operator buttons (keyboard)
    private JButton restartButton; // Button to restart the game
    private JButton showTargetEquationButton; // Button to show target equation
    private KeyAdapter keyAdapter; // Adapter for keyboard input

    /**
     * Constructs a NumberleView instance with associated model and controller.
     *
     * @param model The game model.
     * @param controller The controller that mediates between model and view.
     */
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null); // Initial update to configure UI elements
    }

    /**
     * Initializes and displays the main game window.
     */
    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1000); // Resizing the window
        frame.setBackground(Color.white); // Setting the Layout

        setupMenuPanel();
        setInputPanels(); // Set the text box
        setKeyboard(); // Set the keyboard
        setupKeyboardListener(); // Set up Keyboard Listener

        frame.setVisible(true); // Display the window
    }

    /**
     * Sets up the north panel which contains two buttons.
     *  Button 1: show target equation.
     *  Button 2: restart the game
     */
    private void setupMenuPanel() {
        menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        menuPanel.setBackground(Color.WHITE); // Set background color

        // Create button 1
        // When flag 2 is true, button 1 is enabled.
        showTargetEquationButton = createMenuButton("Target Equation", controller.getFlag2());
        showTargetEquationButton.addActionListener(e -> {
            JLabel messageLabel = new JLabel(targetEquation);
            messageLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            JOptionPane.showMessageDialog(frame, messageLabel,
                    "Target Equation", JOptionPane.INFORMATION_MESSAGE);
        });

        // Create button 2
        restartButton = createMenuButton("Restart", false);
        restartButton.addActionListener(e -> {
            controller.startNewGame(); // Call the controller's startNewGame method when the button is clicked
            inputPanel.requestFocusInWindow();
            resetGameInterface();
        });

        // Add buttons to menuPanel
        menuPanel.add(showTargetEquationButton);
        menuPanel.add(restartButton);
        // Add the Start New Game button to the interface
        frame.add(menuPanel,BorderLayout.NORTH); // Add the panel to the north side of the window
    }

    private JButton createMenuButton(String text, boolean enabled) {
        JButton button = new JButton(text);
        button.setEnabled(enabled);
        button.setFont(new Font("SansSerif", Font.BOLD, 21));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(230, 230, 230));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(210, 210, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(230, 230, 230));
            }
        });
        return button;
    }

    /**
     * Resets the game interface to its initial state.
     */
    private void resetGameInterface() {
        Color inputBackgroudColor = Color.WHITE;  // Setting the default background color
        Color keyboardBackgroudColor = new Color(218, 223, 235);

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                equationFields[row][col].setText("");  // Clear the text of all input fields
                equationFields[row][col].setBackground(inputBackgroudColor);
                equationFields[row][col].setForeground(new Color(89, 98, 117));
            }
        }

        for (JButton button : buttonMap.values()) {
            button.setBackground(keyboardBackgroudColor);
            button.setForeground(new Color(89, 98, 117));
        }
    }

    /**
     * Sets up the input panel for entering guesses.
     */
    private void setInputPanels() {
        // The input square panel, 6 rows and 7 columns
        inputPanel = new JPanel(new GridLayout(6, 7, 10, 10)); // Spacing of 2
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 245, 50, 245));
        inputPanel.setBackground(Color.WHITE); // Set background color
        inputPanel.requestFocusInWindow();
        equationFields = new JTextField[6][7];

        Font textFieldFont = new Font("SansSerif", Font.BOLD, 24); // Set up the size and font
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                equationFields[row][col] = new RoundedTextField();
                equationFields[row][col].setEditable(false);
                equationFields[row][col].setHorizontalAlignment(JTextField.CENTER); // Horizontal centering of text
                equationFields[row][col].setFont(textFieldFont);
                equationFields[row][col].setBackground(new Color(249, 250, 253));
                equationFields[row][col].setForeground(new Color(89, 98, 117));
                inputPanel.add(equationFields[row][col]);
            }
        }
        frame.add(inputPanel, BorderLayout.CENTER);
    }

    /**
     * Sets up the keyboard panel for number and operator input.
     */
    private void setKeyboard() {
        // Initialize the math panel with extra vertical space between button rows
        mathPanel = new JPanel();
        mathPanel.setLayout(new BoxLayout(mathPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical stacking
        mathPanel.setBorder(BorderFactory.createEmptyBorder(0, 80, 80, 80));
        mathPanel.setBackground(Color.WHITE);

        // Add number buttons panel
        addButtonsPanel("number", new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"}, 10);

        // Add an empty panel as a vertical spacer
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(800, 10)); // Set the height to 10 for vertical spacing
        spacerPanel.setOpaque(false); // Make the spacer panel transparent
        mathPanel.add(spacerPanel);

        // Add operator buttons panel
        addButtonsPanel("operator", new String[]{"Delete", "+", "-", "*", "/", "=", "Enter"}, 7);

        frame.add(mathPanel, BorderLayout.SOUTH);
    }

    /**
     * Adds a panel of buttons with the specified labels and layout configuration.
     * @param type The type of buttons, e.g., "number" or "operator".
     * @param buttons An array of strings representing the button labels.
     * @param columns The number of columns for the grid layout.
     */
    private void addButtonsPanel(String type, String[] buttons, int columns) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, columns, 10, 10));
        buttonsPanel.setPreferredSize(new Dimension(800, 90));
        buttonsPanel.setBackground(Color.WHITE);

        Font buttonFont = new Font("SansSerif", Font.BOLD, 24);
        Color backgroundColor = new Color(218, 223, 235);

        for (String buttonText : buttons) {
            JButton button = createInputButton(buttonText, buttonFont, backgroundColor);
            if ("Delete".equals(buttonText) || "Enter".equals(buttonText)) {
                configureSpecialButton(button, buttonText);
            } else {
                button.addActionListener(e -> updateInputPanel(getInputText() + button.getText()));
            }
            buttonsPanel.add(button); // Add the button directly to the button panel
            buttonMap.put(buttonText, button);
        }
        mathPanel.add(buttonsPanel);
    }

    /**
     * Creates a round button with specified text, font, and background color.
     * @param text The text to display on the button.
     * @param font The font of the button text.
     * @param bgColor The background color of the button.
     * @return Button The newly created round button.
     */
    private RoundedButton createInputButton(String text, Font font, Color bgColor) {
        RoundedButton button = new RoundedButton(text);
        button.setEnabled(true);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(new Color(89, 98, 117)); // Customize text color
        return button;
    }

    /**
     * Configures the action listener for special buttons like 'Delete' and 'Enter'.
     * @param button The button to configure.
     * @param type The type of the button which determines its functionality.
     */
    private void configureSpecialButton(JButton button, String type) {
        button.addActionListener(e -> {
            if ("Delete".equals(type)) {
                String currentText = getInputText();
                if (!currentText.isEmpty()) {
                    updateInputPanel(currentText.substring(0, currentText.length() - 1));
                }
            } else if ("Enter".equals(type)) {
                if (controller.checkInput(getInputText())) {
                    controller.processInput(getInputText());
                    updateInputColors();
                    updateKeyboardColor();
                    checkGameOver();
                } else {
                    if (controller.getFlag1()) {
                        JLabel errorLabel = new JLabel(controller.getError());
                        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
                        JOptionPane.showMessageDialog(frame, errorLabel);
                    }
                }
            }
        });
    }

    /**
     * Retrieves the text from the input fields for the current guess.
     * This method compiles the text from each JTextField in the row corresponding to the current attempt.
     * @return A string composed of the characters currently entered the input fields.
     */
    private String getInputText() {
        // Get the content of the text box panel from the current guess row
        int currentGuess = controller.getMAX_ATTEMPTS() - controller.getRemainingAttempts();
        int startRow = currentGuess % 6; // Row number corresponding to the current guess count

        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < 7; col++) { // Since there are 7 columns in each row
            JTextField textField = equationFields[startRow][col];
            String text = textField.getText();
            sb.append(text);
        }
        return sb.toString();
    }

    /**
     * Updates the text fields in the input panel based on the current user input.
     * This method sets each JTextField in the row for the current guess to display the appropriate characters.
     *
     * @param text The current string to display in the input fields.
     */
    private void updateInputPanel(String text) {
        inputPanel.requestFocusInWindow();
        // Determine the current attempt and calculate the starting row
        int currentGuess = controller.getMAX_ATTEMPTS() - controller.getRemainingAttempts();
        int startRow = currentGuess % 6;  // Assuming the currentGuess is zero-based

        // Calculate the starting and ending indices for this row
        int startIndex = 0;  // Always starts at the first column of the row
        int endIndex = 7;    // Ends at the last column of the row (since there are 7 columns)

        // Update the text fields in the specified row
        JTextField[] rowFields = equationFields[startRow];
        for (int i = startIndex; i < endIndex; i++) {
            if (i < text.length()) {
                rowFields[i].setText(String.valueOf(text.charAt(i)));
            } else {
                rowFields[i].setText("");  // Clear the field if there is no character to display
            }
        }
    }

    /**
     * Updates the background colors of the text fields based on feedback from the game model.
     * Colors are used to indicate the correctness of each character in the guess.
     */
    private void updateInputColors() {
        String[] colors = controller.getColors(); // Get an array of colors in the model
        int currentGuess = controller.getMAX_ATTEMPTS() - controller.getRemainingAttempts() -1;
        int startRow = currentGuess % 6; // Calculate the line number corresponding to the current attempt

        JTextField[] rowFields = equationFields[startRow]; // Get all text fields of the current line
        for (int i = 0; i < rowFields.length; i++) {
            JTextField textField = rowFields[i];
            textField.setForeground(Color.WHITE);
            if (i < colors.length) {
                switch (colors[i]) {
                    case "Green":
                        textField.setBackground(new Color(47, 191, 164));
                        break;
                    case "Orange":
                        textField.setBackground(new Color(245, 153, 110));
                        break;
                    case "Grey":
                        textField.setBackground(new Color(163, 173, 194));
                        break;
                    default:
                        textField.setBackground(Color.WHITE);
                        break;
                }
            } else {
                textField.setBackground(Color.WHITE);
            }
        }
    }

    /**
     * Updates the colors of the keyboard buttons based on their usage in the game.
     * This function uses the feedback from the model to highlight keys that have been used correctly, incorrectly, or are close.
     */
    private void updateKeyboardColor() {
        Map<Character, String> colors = controller.getButtonColors();
        for (Map.Entry<String, JButton> entry : buttonMap.entrySet()) {
            String key = entry.getKey();
            JButton button = entry.getValue();
            if (key.length() == 1 && colors.containsKey(key.charAt(0))) {
                Color currentColor = button.getBackground();
                String newColorCode = colors.get(key.charAt(0));
                button.setForeground(Color.WHITE);

                // Check if the current color is already green, if so, do not make any changes
                if (!currentColor.equals(new Color(47, 191, 164))) {
                    switch (newColorCode) {
                        case "Green":
                            button.setBackground(new Color(47, 191, 164));
                            break;
                        case "Orange":
                            button.setBackground(new Color(245, 153, 110));
                            break;
                        case "Grey":
                            button.setBackground(new Color(163, 173, 194));
                            break;
                        default:
                            button.setBackground(Color.WHITE);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Sets up the listener for physical keyboard inputs.
     * This method allows the view to respond to keystrokes, updating the game state accordingly.
     */
    private void setupKeyboardListener() {
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char keyChar = e.getKeyChar();
                String currentText = getInputText();
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    // Handling the backspace button
                    if (!currentText.isEmpty()) {
                        updateInputPanel(currentText.substring(0, currentText.length() - 1));
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Handling the Enter button
                    if (controller.checkInput(currentText)) {
                        controller.processInput(currentText);
                        updateInputColors();
                        updateKeyboardColor();
                        checkGameOver();
                    } else {
                        if (controller.getFlag1()) {
                            JLabel errorLabel = new JLabel(controller.getError());
                            errorLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
                            JOptionPane.showMessageDialog(frame, errorLabel);
                        }
                    }
                } else {
                    // Handling number and operator buttons
                    if (Character.isDigit(keyChar) || "+-*/=".indexOf(keyChar) != -1) {
                        updateInputPanel(currentText + keyChar);
                    }
                }
            }
        };

        // Adding a key listener to the component
        inputPanel.addKeyListener(keyAdapter);
        // Set focus to receive keyboard input
        inputPanel.setFocusable(true);
        // Set focus to inputPanel
        inputPanel.requestFocusInWindow();
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        updateRestartButton();
        updateTargetEquation();
    }

    /**
     * Updates the visibility and text of the target equation.
     */
    private void updateTargetEquation() {
        targetEquation = controller.getTargetEquation();
    }

    /**
     * Enables or disables the restart button based on game state.
     */
    private void updateRestartButton() {
        // Update logic for the restart button: Unable to start a new game when the user has not completed the first attempt
        // Once the user has completed the first attempt, a new game can be started at any time
        restartButton.setEnabled(controller.getMAX_ATTEMPTS() - controller.getRemainingAttempts() >= 1);
    }

    /**
     * Checks if the game is over and prompts the user accordingly.
     */
    private void checkGameOver() {
        // Check if the game is over
        if (controller.isGameOver()) {
            String messageText;
            String title;

            if (controller.isGameWon()) {
                // Win the game
                messageText = "<html>You won!<br>Would you like to play again?</html>";
                title = "Game Won";
            } else {
                // loss the game
                messageText = "<html>You lost. The equation was: " + controller.getTargetEquation() +
                        "<br>Would you like to play again?</html>";
                title = "Game Lost";
            }

            // Create a JLabel with custom font for the message
            JLabel message = new JLabel(messageText);
            message.setFont(new Font("Sans Serif", Font.BOLD, 20));
            Object[] options = {"Yes", "No"};
            // A confirmation dialog box is displayed asking if the user wants to restart the game
            int response = JOptionPane.showOptionDialog(frame, message, title, JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (response == JOptionPane.YES_OPTION) {
                controller.startNewGame();  // The user selects yes to restart the game
                resetGameInterface();  // Reset the game interface
            } else {
                frame.dispose();  // User selects No to close the game window
            }
        }
    }

    /**
     * Custom text field with rounded corners.
     */
    private static class RoundedTextField extends JTextField {
        private Shape shape;

        public RoundedTextField() {
            setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            super.paintComponent(g2);
        }
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Define the border width
            int borderWidth = 3;
            g2.setStroke(new BasicStroke(borderWidth));
            g2.setColor(new Color(230, 230, 230));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
        }
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
            return shape.contains(x, y);
        }
    }

    /**
     * Custom button rounded corners.
     */
    private static class RoundedButton extends JButton {
        private Shape shape;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false); // Make the button transparent
            setFocusPainted(false); // Remove the focus border
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            super.paintComponent(g2);
        }
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Define the border width
            int borderWidth = 2;
            g2.setStroke(new BasicStroke(borderWidth));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
        }
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
            return shape.contains(x, y);
        }
    }
}