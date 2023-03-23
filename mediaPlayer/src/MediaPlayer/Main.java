package MediaPlayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.getIcons()
				.add(new Image(getClass().getResourceAsStream("/icon/playericon.png"), 40, 40, false, false));
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PlayerLayout.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);

		PrimaryStageMinsize(primaryStage, 720.0, 437.0);
		primaryStage.setScene(scene);

		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public static void PrimaryStageMinsize(Stage Stage, Double width, Double height) {
		Stage.setMinWidth(width);
		Stage.setMinHeight(height);
	}

}
