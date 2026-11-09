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

public class MerchantFeeCalculator {
	
	public static VBox createLayout(boolean darkTheme) {
		VBox container = new VBox(20);
		container.setPadding(new Insets(25));
		container.setAlignment(Pos.TOP_CENTER);
		container.getStyleClass().add("tool-container");
		container.setMaxWidth(600);
		
		Label headerLabel = new Label("E-Commerce & Merchant Fee Calculator");
		headerLabel.getStyleClass().add("tool-header");
		
		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setAlignment(Pos.CENTER);
		
		// Inputs
		Label amountLabel = new Label("Transaction Amount (R):");
		amountLabel.getStyleClass().add("styled-label");
		TextField amountField = new TextField();
		amountField.setPromptText("e.g. 1500.00");
		
		Label presetLabel = new Label("Gateway Preset:");
		presetLabel.getStyleClass().add("styled-label");
		ComboBox<String> presetCombo = new ComboBox<>();
		presetCombo.getItems().addAll(
				"Standard Credit Card (3.5% + R2.00)",
				"Instant EFT / Ozow (1.5% + R0.0)",
				"Custom Rates"
		);
		presetCombo.setValue("Standard Credit Card (3.5% + R2.00)");
		presetCombo.setMaxWidth(Double.MAX_VALUE);
		
		Label rateLabel = new Label("Percentage Fee (%):");
		rateLabel.getStyleClass().add("styled-label");
		TextField rateField = new TextField("3.5");
		rateField.setEditable(false);
		
		Label fixedLabel = new Label("Fixed Fee per Transaction (R):");
		fixedLabel.getStyleClass().add("styled-label");
		TextField fixedField = new TextField("2.00");
		fixedField.setEditable(false);
		
		// Preset action listener
		presetCombo.setOnAction(e -> {
			String selected = presetCombo.getValue();
			if (selected != null && selected.contains("Standard Credit Card")) {
				rateField.setText("3.5");
				rateField.setEditable(false);
				fixedField.setText("2.00");
				fixedField.setEditable(false);
			}
			else if (selected != null && selected.contains("Instant EFT")) {
				rateField.setText("1.5");
				rateField.setEditable(false);
				fixedField.setText("0.00");
				fixedField.setEditable(false);
			}
			else if (selected != null && selected.contains("Custom Rates")) {
				rateField.setText("0.00");
				rateField.setEditable(true);
				fixedField.setText("0.00");
				fixedField.setEditable(true);
			}
		});
		
		Button calcBtn = new Button("Calculate Payout & Fees");
		calcBtn.getStyleClass().add("button-primary");
		calcBtn.setMaxWidth(Double.MAX_VALUE);
		
		// Output Display Labels
		Label resultTitle = new Label("--- Fee Breakdown ---");
		resultTitle.getStyleClass().add("tool-header");
		resultTitle.setPadding(new Insets(10, 0, 5, 0));
		
		Label grossOut = new Label("Transaction Amount: R 0.00");
		grossOut.getStyleClass().add("output-label");
		
		Label pctFeeOut = new Label("Percentage Fee: R 0.00");
		pctFeeOut.getStyleClass().add("output-label");
		
		Label fixedFeeOut = new Label("Fixed Fee: R 0.00");
		fixedFeeOut.getStyleClass().add("output-label");
		
		Label vatOut = new Label("VAT on Fees (15%): R 0.00");
		vatOut.getStyleClass().add("output-label");
		
		Label totalFeeOut = new Label("Total Deductions: R 0.00");
		totalFeeOut.getStyleClass().add("output-label");
		
		Label netPayoutOut = new Label("Net Payout to Merchant: R 0.00");
		netPayoutOut.getStyleClass().add("output-highlight");
		
		// Calculation Logic
		calcBtn.setOnAction(e -> {
			try {
				double amount = Double.parseDouble(amountField.getText().trim());
				double pctRate = Double.parseDouble(rateField.getText().trim()) / 100.0;
				double fixedFee = Double.parseDouble(fixedField.getText().trim());
				
				if (amount < 0 || pctRate < 0 || fixedFee < 0) {
					netPayoutOut.setText("Error: Values cannot be negative");
					return;
				}
				
				double pctFeeAmount = amount * pctRate;
				double subtotalFees = pctFeeAmount + fixedFee;
				double vatOnFees = subtotalFees * 0.15; // 15% VAT on merchant service fees
				double totalFees = subtotalFees + vatOnFees;
				double netPayout = amount - totalFees;
				
				grossOut.setText(String.format("Transaction Amount: R %,.2f", amount));
				pctFeeOut.setText(String.format("Percentage Fee: R %,.2f", pctFeeAmount));
				fixedFeeOut.setText(String.format("Fixed Fee: R %,.2f", fixedFee));
				vatOut.setText(String.format("VAT on Fees: R %,.2f", vatOnFees));
				totalFeeOut.setText(String.format("Total Deduction: R %,.2f", totalFees));
				netPayoutOut.setText(String.format("Net Payout to Merchant: R %,.2f", netPayout));
				
				
				// Log to history tape
				BusinessHistory.addEntry("Business - Merchant Fee", "Amount: R" + amount, "Net Payout: R" + String.format("%.2f", netPayout));
			
			} catch (NumberFormatException ex) {
				netPayoutOut.setText("Error: Please enter valid numeric amounts");
			}
		});
		
		grid.add(amountLabel, 0, 0); grid.add(amountField, 1, 0);
		grid.add(presetLabel, 0, 1); grid.add(presetCombo, 1, 1);
		grid.add(rateLabel, 0, 2); grid.add(rateField, 1, 2);
		grid.add(fixedLabel, 0, 3); grid.add(fixedField, 1, 3);
		
		GridPane.setHgrow(amountField, Priority.ALWAYS);
		GridPane.setHgrow(presetCombo, Priority.ALWAYS);
		GridPane.setHgrow(rateField, Priority.ALWAYS);
		GridPane.setHgrow(fixedField, Priority.ALWAYS);
		
		VBox outputBox = new VBox(10, resultTitle, grossOut, pctFeeOut, fixedFeeOut, vatOut, totalFeeOut, netPayoutOut);
		outputBox.setAlignment(Pos.CENTER_LEFT);
		outputBox.setPadding(new Insets(10, 0, 0, 10));
		
		container.getChildren().addAll(headerLabel, grid, calcBtn, outputBox);
		return container;
	}
}
