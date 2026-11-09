package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SarsPayeEstimator {
	
	public static VBox createLayout(boolean darkTheme) {
		VBox container = new VBox(20);
		container.setPadding(new Insets(25));
		container.setAlignment(Pos.TOP_CENTER);
		container.getStyleClass().add("tool-container");
		container.setMaxWidth(600);
		
		Label headerLabel = new Label("SARS PAYE & Income Tax Estimator (2026/27)");
		headerLabel.getStyleClass().add("tool-header");
		
		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setAlignment(Pos.CENTER);
		
		// Inputs
		Label ageLabel = new Label("Age Group:");
		ageLabel.getStyleClass().add("styled-label");
		ComboBox<String> ageCombo = new ComboBox<>();
		ageCombo.getItems().addAll("Under 65", "65 to 75", "75 and Above");
		ageCombo.setValue("Under 65");
		ageCombo.setMaxWidth(Double.MAX_VALUE);
		
		Label incomeTypeLabel = new Label("Income Frequency:");
		incomeTypeLabel.getStyleClass().add("styled-label");
		ComboBox<String> freqCombo = new ComboBox<>();
		freqCombo.getItems().addAll("Monthly", "Annual");
		freqCombo.setValue("Monthly");
		freqCombo.setMaxWidth(Double.MAX_VALUE);
		
		Label amountLabel = new Label("Gross Earnings (R):");
		amountLabel.getStyleClass().add("styled-label");
		TextField amountField = new TextField();
		amountField.setPromptText("e.g 25000");
		
		Button calcBtn = new Button("Calculate Tax & PAYE");
		calcBtn.getStyleClass().add("button-primary");
		calcBtn.setMaxWidth(Double.MAX_VALUE);
		
		// Output Display Labels
		Label resultTitle = new Label("--- Tax Breakdown ---");
		resultTitle.getStyleClass().add("tool-header");
		resultTitle.setPadding(new Insets(10, 0, 5, 0));
		
		Label grossOut = new Label("Gross Income: R 0.00");
		grossOut.getStyleClass().add("output-label");
		
		Label taxBeforeRebateOut = new Label("Tax Before Rebates: R 0.00");
		taxBeforeRebateOut.getStyleClass().add("output-label");
		
		Label rebateOut = new Label("Applicable Rebate: R 0.00");
		rebateOut.getStyleClass().add("output-label");
		
		Label netTaxOut = new Label("Net Tax Payable R 0.00");
		netTaxOut.getStyleClass().add("output-label");
		
		Label netTakeHomeOut = new Label("Net Take-Home Pay: R 0.00");
		netTakeHomeOut.getStyleClass().add("output-highlight");
		
		// Calculation Logic
		calcBtn.setOnAction(e -> {
			try {
				double inputVal = Double.parseDouble(amountField.getText().trim());
				if (inputVal < 0) {
					netTakeHomeOut.setText("Error: Earnings cannot be negative");
					return;
				}
				
				// Convert to Annualized Taxable Income for SARS calculation
				double annualIncome = freqCombo.getValue().equals("Monthly") ? inputVal * 12 : inputVal;
				
				// 1. Calculate Tax Before Rebate using 2026/27 brackets
				double taxBeforeRebate = calculateTaxBracket(annualIncome);
				
				// 2. Get Rebates based on age group
				double rebate = getRebate(ageCombo.getValue());
				
				// 3. Net Tax Payable (Cannot be less than 0)
				double netAnnualTax = Math.max(0, taxBeforeRebate - rebate);
				
				// Apply tax threshold  check
				double threshold = getThreshold(ageCombo.getValue());
				if (annualIncome <= threshold) {
					netAnnualTax = 0.0;
				}
				
				// Convert back to frequency format for display
				double displayGross = inputVal;
				double displayTax = freqCombo.getValue().equals("Monthly") ? netAnnualTax / 12 : netAnnualTax;
				double displayTakeHome = displayGross - displayTax;
				
				// Convert UI Labels
				grossOut.setText(String.format("Gross Income (%s): R %,.2f", freqCombo.getValue(), displayGross));
				taxBeforeRebateOut.setText(String.format("Tax Before Rebates (%s): R %,.2f", freqCombo.getValue(), (freqCombo.getValue().equals("Monthly") ? taxBeforeRebate / 12 : taxBeforeRebate)));
				rebateOut.setText(String.format("Applicable Rebate (%s): R %,.2f", freqCombo.getValue(), (freqCombo.getValue().equals("Monthly") ? rebate / 12 : rebate)));
				netTaxOut.setText(String.format("Net Tax Payable (%s): R %,.2f", freqCombo.getValue(), displayTax));
				netTakeHomeOut.setText(String.format("Net Take-Home Pay (%s): R %,.2f", freqCombo.getValue(), displayTakeHome));
				
				// Log to history tape
				BusinessHistory.addEntry("Business -SARS PAYE", freqCombo.getValue() + "Gross: R" + inputVal, "Net Take-Home: R" + String.format("%.2f", displayTakeHome));
				
			} catch (NumberFormatException ex) {
				netTakeHomeOut.setText("Error: Please enter a valid numeric amout");
			}
		});
		
		grid.add(ageLabel, 0, 0); grid.add(ageCombo, 1, 0); 
		grid.add(incomeTypeLabel, 0, 1); grid.add(freqCombo, 1, 1);
		grid.add(amountLabel, 0, 2); grid.add(amountField, 1, 2);
		GridPane.setHgrow(ageCombo, Priority.ALWAYS);
		GridPane.setHgrow(freqCombo, Priority.ALWAYS);
		GridPane.setHgrow(amountField, Priority.ALWAYS);
		
		VBox outputBox = new VBox(10, resultTitle, grossOut, taxBeforeRebateOut, rebateOut, netTaxOut, netTakeHomeOut);
		outputBox.setAlignment(Pos.CENTER_LEFT);
		outputBox.setPadding(new Insets(10, 0, 0, 10));
		
		container.getChildren().addAll(headerLabel, grid, calcBtn, outputBox);
		return container;
	}
	
	private static double calculateTaxBracket(double income) {
		// Official 2026/27 SARS Tax Brackets
		if (income <= 245100) {
			return income * 0.18;
		}
		else if (income <= 383100) {
			return 44118 + (income - 245100) * 0.26;
		}
		else if (income <= 530200) {
			return 79998 + (income - 383100) * 0.31;
		}
		else if (income <= 695800) {
			return 125599 + (income - 530200) * 0.36;
		}
		else if (income <= 887000) {
			return 185215 + (income - 695800) * 0.39;
		}
		else if (income <= 1878600) {
			return 259783 + (income - 887000) * 0.41;
		}
		else {
			return 666339 + (income - 1878600) * 0.45;
		}
	}
	
	private static double getRebate(String ageGroup) {
		// Official 2026/27 Rebates: Primary R17,820 | Secondary (65+) R9,765 | Tertiary (75+) R3,249
		double primary = 17820;
		double secondary = 9765;
		double tertiary = 3249;
		
		switch (ageGroup) {
			case "65 to 75":
				return primary + secondary;
			case "75 and Above":
				return primary + secondary + tertiary;
			default:
				return primary;
		}
	}
	
	private static double getThreshold(String ageGroup) {
		// Official 2026/27 Tax Thresholds
		switch (ageGroup) {
			case "65 to 75":
				return 153250;
			case "75 and Above":
				return 171300;
			default:
				return 99000;
		}
	}
}
