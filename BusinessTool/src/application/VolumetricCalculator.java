package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VolumetricCalculator {
	public static VBox createLayout(boolean darkTheme) {
		VBox container = new VBox(20);
		container.setPadding(new Insets(25));
		container.setAlignment(Pos.TOP_CENTER);
		container.getStyleClass().add("tool-container");
		container.setMaxWidth(600);
		
		Label headerLabel = new Label("Courier & Volumetric Shipping Weight Calculator");
		headerLabel.getStyleClass().add("tool-header");
		
		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setAlignment(Pos.CENTER);
		
		Label lengthLabel = new Label("Length (cm):");
		lengthLabel.getStyleClass().add("styled-label");
		TextField lengthField = new TextField();
		lengthField.setPromptText("e.g. 40");
		
		Label widthLabel = new Label("Width (cm):");
		widthLabel.getStyleClass().add("styled-label");
		TextField widthField = new TextField();
		widthField.setPromptText("e.g. 30");
		
		Label heightLabel = new Label("Length (cm):");
		heightLabel.getStyleClass().add("styled-label");
		TextField heightField = new TextField();
		heightField.setPromptText("e.g. 20");
		
		Label weightLabel = new Label("Actual Dead Weight (kg):");
		weightLabel.getStyleClass().add("styled-label");
		TextField weightField = new TextField();
		weightField.setPromptText("e.g. 1.5");
		
		// Calculation Button
		Button calcBtn = new Button("Calculate Billable Weight");
		calcBtn.getStyleClass().add("button-primary");
		calcBtn.setMaxWidth(Double.MAX_VALUE);
		
		// Output/Result Labels
		Label resultTitle = new Label("--- Shipping Weight Analysis ---");
		resultTitle.getStyleClass().add("tool-header");
		resultTitle.setPadding(new Insets(10, 0, 5, 0));
		
		Label actualWeightOut = new Label("Actual Dead Weight: 0.00 kg");
		actualWeightOut.getStyleClass().add("output-label");
		
		Label volOut = new Label("Volumetric Weight: 0.00 kg");
		volOut.getStyleClass().add("output-label");
		
		Label adviceOut = new Label("Courier billing factions will evaluate here.");
		adviceOut.getStyleClass().add("output-label");
		
		Label billableOut = new Label("Billable Weight: 0.00 kg");
		billableOut.getStyleClass().add("output-highlight");
		
		// Calculation Button Logic
		calcBtn.setOnAction(e -> {
			try {
				double length = Double.parseDouble(lengthField.getText().trim());
				double width = Double.parseDouble(widthField.getText().trim());
				double height = Double.parseDouble(heightField.getText().trim());
				double actualWeight = Double.parseDouble(weightField.getText().trim());
				
				if (length <= 0 || width <= 0 || height <= 0 || actualWeight <= 0) {
					billableOut.setText("Error: Dimensions must be positive numbers");
					return;
				}
				
				double volumetricWeight = (length * width * height) / 5000.0;
				double billableWeight = Math.max(actualWeight, volumetricWeight);
				
				actualWeightOut.setText(String.format("Actual Dead Weight: %,.2f kg", actualWeight));
				volOut.setText(String.format("Volumetric Weight: %,.2f kg", volumetricWeight));
				billableOut.setText(String.format("Billable Weight: %,.2f kg", billableWeight));
				
				if (volumetricWeight > actualWeight) {
					adviceOut.setText("Volumetric weight is high! Courier will bill you on box dimensions");
					adviceOut.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
				}
				else {
					adviceOut.setText("Dead weight is higher. Standard mass billing rules apply");
					adviceOut.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
				}
				
				String logDesc = String.format("Box [%s x %s x %s cm] Dead Weight: %skg", lengthField.getText(), widthField.getText(), heightField.getText(), weightField.getText());
				BusinessHistory.addEntry("Business - Logistics", logDesc, String.format("Billable: %.2f kg", billableWeight));
		
			} catch (NumberFormatException ex) {
				billableOut.setText("Error: Enter a valid numeric dimensions");
			}
		});
		
		grid.add(lengthLabel, 0, 0); grid.add(lengthField, 1, 0);
		grid.add(widthLabel, 0, 1); grid.add(widthField, 1, 1);
		grid.add(heightLabel, 0, 2); grid.add(heightField, 1, 2);
		grid.add(weightLabel, 0, 3); grid.add(weightField, 1, 3);
		
		GridPane.setHgrow(lengthField, Priority.ALWAYS);
		GridPane.setHgrow(widthField, Priority.ALWAYS);
		GridPane.setHgrow(heightField, Priority.ALWAYS);
		GridPane.setHgrow(weightField, Priority.ALWAYS);
		
		VBox outputBox = new VBox(10, resultTitle, actualWeightOut, volOut, adviceOut, billableOut);
		outputBox.setAlignment(Pos.CENTER_LEFT);
		outputBox.setPadding(new Insets(10, 0, 0, 10));
		
		container.getChildren().addAll(headerLabel, grid, calcBtn, outputBox);
		return container;
	}
}
