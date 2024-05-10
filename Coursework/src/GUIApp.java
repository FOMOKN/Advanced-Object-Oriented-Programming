import javax.swing.*;

public class GUIApp {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        createAndShowGUI();
                    } // Create and show the graphical user interface
                }
        );
    }

    public static void createAndShowGUI() {
        INumberleModel model = new NumberleModel(); // Create a model of Numberle
        NumberleController controller = new NumberleController(model); // Create a controller of Numberle
        NumberleView view = new NumberleView(model, controller); // Create a view of Numberle and pass the model and controller
    }
}