package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

	public static Stage currentStage(ActionEvent event /* o evento que o botão recebeu */) {
		// Downcasting de Object pra Node.
		// Downcasting de Window pra Stage.
		return (Stage) ((Node) event.getSource()).getScene().getWindow();

	}
}
