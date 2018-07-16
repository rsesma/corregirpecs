package corregirpecs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Corregir extends Application {

	private final String C_CORREGIR = "Corregir PECs";
	
	@Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Analitzar.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setTitle(C_CORREGIR);
        stage.setScene(scene);
        stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
