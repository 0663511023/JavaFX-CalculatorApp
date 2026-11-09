package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InterestCalculator {

	public static VBox createLayout(boolean darkTheme) {
		VBox container = new VBox(20);
		container.setPadding(new Insets(25));
		container.setAlignment(Pos.TOP_CENTER);
		container.getStyleClass().add("tool-container");
		container.setMaxWidth(600);
		
		Label headerLabel = new Label("Simple vs. Compound Interest Growth Engine");
		headerLabel.getStyleClass().add("tool-header");
		
		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setAlignment(Pos.CENTER);
		
		Label principalLabel = new Label("Principal Amount (R):");
		principalLabel.getStyleClass().add("styled-label");
		TextField principalField = new TextField();
		principalField.setPromptText("e.g. 10000");
		
		Label rateLabel = new Label("Annual Interest Rate (%):");
		rateLabel.getStyleClass().add("styled-label");
		TextField rateField = new TextField();
		rateField.setPromptText("e.g. 10.5");
		
		Label timeLabel = new Label("Term Duration (Years):");
		timeLabel.getStyleClass().add("styled-label");
		TextField timeField = new TextField();
		timeField.setPromptText("e.g. 5");
		
		Button calcBtn = new Button("Project Capital Growth");
		calcBtn.getStyleClass().add("button-primary");
		calcBtn.setMaxWidth(Double.MAX_VALUE);
		
		Label resultTitle = new Label("--- Financial Growth Projections ---");
		resultTitle.getStyleClass().add("tool-header");
		resultTitle.setPadding(new Insets(10, 0, 0, 10));
		
		Label simpleOut = new Label("Simple Interest Total: R 0.00");
		simpleOut.getStyleClass().add("output-label");
		
		Label compoundOut = new Label("Compound Interest Total: R 0.00");
		compoundOut.getStyleClass().add("output-label");
		
		Label varianceOut = new Label("Compounding Variance: R 0.00");
		varianceOut.getStyleClass().add("output-highlight");
		
		calcBtn.setOnAction(e -> {
			try {
				double principal = Double.parseDouble(principalField.getText().trim());
				double annualRate = Double.parseDouble(rateField.getText().trim()) / 100.0;
				double years = Double.parseDouble(timeField.getText().trim());
				
				if (principal < 0 || annualRate < 0 || years < 0) {
					varianceOut.setText("Error: Values cannot be negative");
					return;
				}
				
				double simpleTotal = principal * (1 + (annualRate * years));
				double compoundTotal = principal * Math.pow((1 + annualRate), years);
				double growthVariance = compoundTotal - simpleTotal;
				
				simpleOut.setText(String.format("Simple Interest Total: R %,.2f", simpleTotal));
				compoundOut.setText(String.format("Compound Interest Total: R %,.2f", compoundTotal));
				varianceOut.setText(String.format("Compounding Variance Earnings: R %,.2f", growthVariance));
				
				String logInput = String.format("R%s @ %s%% for %s Yrs",
						principalField.getText().trim(), rateField.getText().trim(), timeField.getText().trim());
				BusinessHistory.addEntry("Business - Interest Engine", logInput, String.format("Compound: R%.2f", compoundTotal));
			
			} catch (NumberFormatException ex) {
				varianceOut.setText("Error: Please enter valid numeric figures");
			}
		});
		
		grid.add(principalLabel, 0, 0); grid.add(principalField, 1, 0);
		grid.add(rateLabel, 0, 1); grid.add(rateField, 1, 1);
		grid.add(timeLabel, 0, 2); grid.add(timeField, 1, 2);
		
		GridPane.setHgrow(principalField, Priority.ALWAYS);
		GridPane.setHgrow(rateField, Priority.ALWAYS);
		GridPane.setHgrow(timeField, Priority.ALWAYS);
		
		VBox outputBox = new VBox(10, resultTitle, simpleOut, compoundOut, varianceOut);
		outputBox.setAlignment(Pos.CENTER_LEFT);
		outputBox.setPadding(new Insets(10, 0, 0, 10));
		
		container.getChildren().addAll(headerLabel, grid, calcBtn, outputBox);
		return container;
	}
}
