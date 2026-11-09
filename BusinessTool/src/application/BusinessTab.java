package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class BusinessTab {
	
	private BorderPane rootPane;
	private TabPane tabPane;
	
	private StackPane sarsPayePane;
	private StackPane merchantPane;
	private StackPane volumetricPane;
	private StackPane freelancerPane;
	private StackPane interestPane;
	private StackPane historyPane;
	
	public BusinessTab(boolean darkTheme) {
		rootPane = new BorderPane();
		rootPane.setPadding(new Insets(10));
		
		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		
		// 1. SARS PAYE Module
		VBox sarsPayeLayout = SarsPayeEstimator.createLayout(darkTheme);
		sarsPayePane = new StackPane(sarsPayeLayout);
		sarsPayePane.setPadding(new Insets(10));
		StackPane.setAlignment(sarsPayeLayout, Pos.TOP_CENTER);
		Tab sarsPayeTab = new Tab("SARS PAYE & Tax", sarsPayePane);
		sarsPayeTab.getStyleClass().add("tab");
		
		// 2. Merchant Fees Module
		VBox merchantLayout = MerchantFeeCalculator.createLayout(darkTheme);
		merchantPane = new StackPane(merchantLayout);
		merchantPane.setPadding(new Insets(10));
		StackPane.setAlignment(merchantLayout, Pos.TOP_CENTER);
		Tab merchantTab = new Tab("Merchant Fee", merchantPane);
		merchantTab.getStyleClass().add("tab");
		
		// 3. Volumetric Shipping Module
		VBox volumetricLayout = VolumetricCalculator.createLayout(darkTheme);
		volumetricPane = new StackPane(volumetricLayout);
		volumetricPane.setPadding(new Insets(10));
		StackPane.setAlignment(volumetricLayout, Pos.TOP_CENTER);
		Tab volumetricTab = new Tab("Courier & Volumetric Shipping", volumetricPane);
		volumetricTab.getStyleClass().add("tab");
		
		// 4. Hourly Freelancer Rate Module
		VBox freelancerLayout = FreelancerRateCalculator.createLayout(darkTheme);
		freelancerPane = new StackPane(freelancerLayout);
		freelancerPane.setPadding(new Insets(10));
		StackPane.setAlignment(freelancerLayout, Pos.TOP_CENTER);
		Tab freelancerTab = new Tab("Freelance Hourly Rate", freelancerPane);
		freelancerTab.getStyleClass().add("tab");
		
		// 5. Simple vs. Compound Interest Growth Module
		VBox interestLayout = InterestCalculator.createLayout(darkTheme);
		interestPane = new StackPane(interestLayout);
		interestPane.setPadding(new Insets(10));
		StackPane.setAlignment(interestLayout, Pos.TOP_CENTER);
		Tab interestTab = new Tab("Simple vs. Compound Interest", interestPane);
		interestTab.getStyleClass().add("tab");
		
		// 6. Business History Tape Panel Module
		historyPane = BusinessHistory.createHistoryView(darkTheme);
		Tab historyTab = new Tab("History & Tape", historyPane);
		historyTab.getStyleClass().add("tab");
		
		
		tabPane.getTabs().setAll(sarsPayeTab, merchantTab, volumetricTab, freelancerTab, interestTab, historyTab);
		
		rootPane.setCenter(tabPane);
		BorderPane.setMargin(rootPane, new Insets(5));
	}
	
	public void updateTheme(boolean darkTheme) {
		String addTheme = darkTheme ? "dark-theme" : "light-theme";
		String removeTheme = darkTheme ? "light-theme" : "dark-theme";
		
		StackPane[] allPanels = {sarsPayePane, merchantPane, volumetricPane, freelancerPane, interestPane, historyPane};
		for (StackPane pane : allPanels) {
			if (pane != null) {
				pane.getStyleClass().removeAll(removeTheme, addTheme);
				pane.getStyleClass().add(addTheme);
			}
		}
	}
	
	public BorderPane getRootPane() {
		return rootPane;
	}
}
