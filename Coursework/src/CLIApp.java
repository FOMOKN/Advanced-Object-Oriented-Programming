import java.util.Scanner;

class CLIApp {

     private INumberleModel model; // Game model

     private Scanner scanner; // Scanner object for reading user input

     public CLIApp() {
         model = new NumberleModel();
         scanner = new Scanner(System.in);
     }

     public void startGame() {
         System.out.println("Welcome to Numberle Game!");
         System.out.println("You have six chances in total."); // Some tips for players
         System.out.println("The equation you enter must satisfy the following conditions:");
         System.out.println("1. When calculating, players can use numbers (0-9) and arithmetic signs (+ - * / =). ");
         System.out.println("2. The length of the equation must be 7.");
         System.out.println("3. Must have equal sign '='.");
         System.out.println("4. There must be at least one sign +-*/ and multiple math symbol in a row is not allowed");
         System.out.println("5. Letters are not allowed.");
         System.out.println("----------------------------------------");

         model.startNewGame();
         model.displayTargetEquation();

         while (!model.isGameOver()) { // A while loop. Players are allowed to enter a valid equation six times
             System.out.println("Remaining attempts: " + model.getRemainingAttempts());
             System.out.print("Please enter your guess: ");
             String input = scanner.nextLine();

             while (!model.checkInput(input)) { // Check whether is a valid input.
                 if (model.getFlag1()) {
                   System.out.println(model.getError()); // Display specific error message.
                 }
                 System.out.print("Enter your guess again: ");
                 input = scanner.nextLine();
             }

             model.processInput(input); // Determining whether a player has won

             System.out.println("----------------------------------------");
         }

         if (model.isGameWon()) { // Check if the game is won
             System.out.println("You won!"); // Print victory message
         } else {
             System.out.println("You lost. The answer was: " + model.getTargetEquation());
         }

         System.out.println("Do you want to play again? (Y/N)");
         String playAgain = scanner.nextLine();

         if (playAgain.equalsIgnoreCase("Y") ||playAgain.equalsIgnoreCase("y")) {
             startGame(); // Start a new game loop
         } else {
             System.out.println("Bye");
         }
     }

     public static void main(String[] args) {
         CLIApp game = new CLIApp(); // Create a CLI object
         game.startGame(); // Start the game
    }
}
