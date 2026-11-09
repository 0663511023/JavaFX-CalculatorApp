package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FreelancerRateCalculator {
	
	public static VBox createLayout(boolean darkTheme) {
		VBox container = new VBox(20);
		container.setPadding(new Insets(25));
		container.setAlignment(Pos.TOP_CENTER);
		container.getStyleClass().add("tool-container");
		container.setMaxWidth(600);
		
		Label headerLabel = new Label("Freelancer Hourly Rate & Break-Even Tool");
		headerLabel.getStyleClass().add("tool-header");
		
		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setAlignment(Pos.CENTER);
		
		Label salaryLabel = new Label("Target Net Monthly Salary (R)");
		salaryLabel.getStyleClass().add("styled-label");
		TextField salaryField = new TextField();
		salaryField.setPromptText("e.g. 30000");
		
		Label overheadLabel = new Label("Monthly Business Overheads (R):");
		overheadLabel.getStyleClass().add("styled-label");
		TextField overheadField = new TextField();
		overheadField.setPromptText("e.g. 5000");
		
		Label hoursLabel = new Label("Billable Hours Per Week:");
		hoursLabel.getStyleClass().add("styled-label");
		TextField hoursField = new TextField();
		hoursField.setPromptText("e.g. 25");
		
		Button calcBtn = new Button("Calculate Minimum Hourly Rate");
		calcBtn.getStyleClass().add("button-primary");
		calcBtn.setMaxWidth(Double.MAX_VALUE);
		
		Label resultTitle = new Label("--- Rate Breakdown ---");
		resultTitle.getStyleClass().add("tool-header");
		resultTitle.setPadding(new Insets(10, 0, 5, 0));
		
		Label expensesOut = new Label("Total Monthly Revenue Needed: R 0.00");
		expensesOut.getStyleClass().add("output-label");
		
		Label annualOut = new Label("Annual Baseline Target: R 0.00");
		annualOut.getStyleClass().add("output-label");
		
		Label adviceOut = new Label("Assumes 48 billable weeks per year (4 weeks leave buffer).");
		adviceOut.getStyleClass().add("output-label");
		
		Label rateOut = new Label("Required Hourly Rate: R 0.00 / hr");
		rateOut.getStyleClass().add("output-highlight");
		
		calcBtn.setOnAction(e -> {
			try {
				double targetSalary = Double.parseDouble(salaryField.getText().trim());
				double overheads = Double.parseDouble(overheadField.getText().trim());
				double billableHoursPerWeek = Double.parseDouble(hoursField.getText().trim());
				
				if (targetSalary < 0 || overheads < 0 || billableHoursPerWeek <=0 || billableHoursPerWeek > 168) {
					rateOut.setText("Error: Hours must be a positive number between 1 and 168");
					return;
				}
				
				double monthlyRevenueNeeded = targetSalary + overheads;
				double annualRevenueNeeded = monthlyRevenueNeeded * 12;
				double totalAnnualBillableHours = billableHoursPerWeek * 48; // 48 active weeks
				double requiredHourlyRate = annualRevenueNeeded / totalAnnualBillableHours;
				
				expensesOut.setText(String.format("Total Mothly Revenue Needed: R %,.2f", monthlyRevenueNeeded));
				annualOut.setText(String.format("Annual Baseline Target: R %,.2f", annualRevenueNeeded));
				rateOut.setText(String.format("Required Hourly Rate: R %,.2f", requiredHourlyRate));
				
				String logInput = String.format("Salary: R%s + Overheads: R%s", salaryField.getText().trim(), overheadField.getText().trim());
				BusinessHistory.addEntry("Business - Freelance Rate", logInput, String.format("Rate: R%.2f/hr", requiredHourlyRate));
				
			} catch (NumberFormatException ex) {
				rateOut.setText("Error: Please enter valid numeric figures");
			}
		});
		
		grid.add(salaryLabel, 0, 0); grid.add(salaryField, 1, 0);
		grid.add(overheadLabel, 0, 1); grid.add(overheadField, 1, 1); 
		grid.add(hoursLabel, 0, 2); grid.add(hoursField, 1, 2);
		
		GridPane.setHgrow(salaryField, Priority.ALWAYS);
		GridPane.setHgrow(overheadField, Priority.ALWAYS);
		GridPane.setHgrow(hoursField, Priority.ALWAYS);
		
		VBox outputBox = new VBox(10, resultTitle, expensesOut, annualOut, adviceOut, rateOut);
		outputBox.setAlignment(Pos.CENTER_LEFT);
		outputBox.setPadding(new Insets(10, 0, 0, 10));
		
		container.getChildren().addAll(headerLabel, grid, calcBtn, outputBox);
		return container;
	}
}
