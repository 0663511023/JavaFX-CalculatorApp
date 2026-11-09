package application;

// Organize Imports
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


public class MultiToolBusinessApp extends Application {
	private boolean isDark = false;
	
	@Override
	public void start(Stage stage) {
		BorderPane root = new BorderPane();
		root.getStyleClass().add("light-theme");
		
		// Top Toolbar with Theme Buttons
		HBox topBar = new HBox(12);
		topBar.setPadding(new Insets(12, 20, 12, 20));
		topBar.setAlignment(Pos.CENTER_RIGHT);
		
		Button lightModeBtn = new Button("Light Mode");
		lightModeBtn.getStyleClass().add("theme-btn-light");
		
		Button darkModeBtn = new Button("Dark Mode");
		darkModeBtn.getStyleClass().add("theme-btn-dark");
		
		Scene scene = new Scene(root, 720, 640);
		
		BusinessTab bTab = new BusinessTab(isDark);
		root.setCenter(bTab.getRootPane());
		
		// Light Mode Logic
		lightModeBtn.setOnAction(e -> {
			if (isDark) {
				isDark = false;
				updateSceneTheme(scene);
				bTab.updateTheme(this.isDark);
			}
		});
		
		// Dark Mode Logic
		darkModeBtn.setOnAction(e -> {
			if (!isDark) {
				isDark = true;
				updateSceneTheme(scene);
				bTab.updateTheme(this.isDark);
			}
		});
		
		topBar.getChildren().addAll(darkModeBtn, lightModeBtn);
		root.setTop(topBar);
	
		String cssPath = getClass().getResource("application.css") != null
					   ? getClass().getResource("application.css").toExternalForm()
					   : "application.css";
		scene.getStylesheets().add(cssPath);
		
		try {
			Image appIcon = new Image(getClass().getResourceAsStream("MarginIQIcon.png"));
			stage.getIcons().add(appIcon);
		
		} catch (Exception e) {
			System.err.println("Could not load application icon: " + e.getMessage());
		}
		
		stage.setTitle("MarginIQ Suite");
		stage.setScene(scene); 
		stage.show();
	}
	
	private void updateSceneTheme(Scene scene) {
		scene.getRoot().getStyleClass().removeAll("light-theme", "dark-theme");
		scene.getRoot().getStyleClass().add(isDark ? "dark-theme" : "light-theme");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
