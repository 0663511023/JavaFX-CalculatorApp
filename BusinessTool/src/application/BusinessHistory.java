package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

// Organize the imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class BusinessHistory {
	private static final ObservableList<String> historyList = FXCollections.observableArrayList();
	private static final String HISTORY_FILE = "business_tape_history.json";
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	static {
		loadHistoryFromFile();
	}
	
	public static void addEntry(String calculatorType, String expression, String result) {
		String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
		// Clean display layout mapping: Type | Time | Expression -> Result
		String entry = String.format("[%s] (%s)  %s -> %s", calculatorType, timestamp, expression, result);
		historyList.add(0, entry); // Add newest item on top
		saveHistoryToFile();
	}
	
	public static void removeEntry(int index) {
		if (index >= 0 && index < historyList.size()) {
			historyList.remove(index);
			saveHistoryToFile();
		}
	}
	
	public static void updateEntry(int index, String updateEntry) {
		if (index >= 0 && index < historyList.size()) {
			historyList.set(index, updateEntry);
			saveHistoryToFile();
		}
	}
	
	public static ObservableList<String> getHistory() {
		return historyList;
	}
	
	public static void clearHistory() {
		historyList.clear();
		saveHistoryToFile();
	}
	
	public static StackPane createHistoryView(boolean darkTheme) {
		VBox container = new VBox(15);
		container.setPadding(new Insets(20));
		container.setAlignment(Pos.CENTER);
		container.getStyleClass().add("tool-container");
		container.setMaxWidth(750);
		container.setMaxHeight(Double.MAX_VALUE);
		
		Label headerLabel = new Label("Calculation Ledger and Tape History");
		headerLabel.getStyleClass().add("tool-header");
		
		// Real-time Visual Status/Error Banner Display
		Label errorStatusLabel = new Label("");
		errorStatusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 13px;");
		errorStatusLabel.setWrapText(true);
		
		ListView<String> listView = new ListView<>(historyList);
		VBox.setVgrow(listView, Priority.ALWAYS);
		listView.getStyleClass().add("list-view");
		
		// Hover Effects on Cells
		listView.setCellFactory(lv -> new ListCell<String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
					setStyle("-fx-background-color: transparent;");
				}
				else {
					setText(item);
					
					if (container.getStyleClass().contains("dark-theme") || darkTheme) {
						setTextFill(Color.web("#cbd5e1"));
					}
					else {
						setTextFill(Color.web("#334155"));
					}
					setStyle("-fx-background-color: transparent; -fx-transition: background-color 0.2s ease;");
					
					// Real-time hover state listeners
					setOnMouseEntered(e -> {
						if (container.getStyleClass().contains("dark-theme") || darkTheme) {
							setStyle("-fx-background-color: #1e293b; -fx-cursor: hand;");
							setTextFill(Color.web("#34d399"));
						}
						else {
							setStyle("-fx-background-color: #f1f5f9; -fx-cursor: hand;");
							setTextFill(Color.web("#10b981"));
						}
					});
					
					setOnMouseExited(e -> {
						if (container.getStyleClass().contains("dark-theme") || darkTheme) {
							setTextFill(Color.web("#cbd5e1"));
						}
						else {
							setTextFill(Color.web("#334155"));
						}
						setStyle("-fx-background-color: transparent;");
					});
				}
			}
		});
		
		TextField editField = new TextField();
		editField.setPromptText("Select a ledger above to edit...");
		HBox.setHgrow(editField, Priority.ALWAYS);
		
		Button updateBtn = new Button("Update Entry");
		updateBtn.getStyleClass().add("button-primary");
		
		updateBtn.setOnAction(e -> {
			int selectedIndex = listView.getSelectionModel().getSelectedIndex();
			if (selectedIndex >= 0) {
				String rawInput = editField.getText().trim();
				
				if (rawInput.isEmpty()) {
					errorStatusLabel.setText("Error: Ledger record modification cannot be empty!");
					return;
				}
				
				// Input Sanitization Validation Engine
				if (rawInput.contains("-") && (rawInput.contains("Gross:") || rawInput.contains("Amount:") || rawInput.contains("Cost"))) {
					errorStatusLabel.setText("Modification Aborted: Negative financial valuations are invalid.");
					return;
				}
				
				if (rawInput.matches(".*-\\s*\\d+.*") && !rawInput.contains("->")) {
					errorStatusLabel.setText("Modification Aborted: Negative baseline entries are restricted");
					return;
				}
				
				// Commit to Ledger Memory on success
				updateEntry(selectedIndex, rawInput);
				editField.clear();
				errorStatusLabel.setText(""); // Wipe banner alert state clean
			}
			else {
				errorStatusLabel.setText("Action Required: Select a calculation cell track from the  list first.");
			}
		});
		
		Button deleteBtn = new Button("Delete Entry");
		deleteBtn.getStyleClass().add("button-danger");
		deleteBtn.setOnAction(e -> {
			int selectedIndex = listView.getSelectionModel().getSelectedIndex();
			if (selectedIndex >= 0) {
				removeEntry(selectedIndex);
				editField.clear();
				errorStatusLabel.setText("");
			}
		});
		
		Button clearBtn = new Button("Clear All History");
		clearBtn.getStyleClass().add("button-danger");
		clearBtn.setOnAction(e -> {
			clearHistory();
			editField.clear();
			errorStatusLabel.setText("");
		});
		
		listView.setOnMouseClicked(e -> {
			String selected = listView.getSelectionModel().getSelectedItem();
			if (selected != null) {
				editField.setText(selected);
				errorStatusLabel.setText("");
			}
		});
		
		HBox editBox = new HBox(12, editField, updateBtn);
		editBox.setAlignment(Pos.CENTER);
		
		HBox actionBox = new HBox(12, deleteBtn, clearBtn);
		actionBox.setAlignment(Pos.CENTER_RIGHT);
		actionBox.setPadding(new Insets(5, 0, 0, 0));
		
		container.getChildren().addAll(headerLabel, errorStatusLabel, listView, editBox, actionBox);
		
		StackPane rootWrapper = new StackPane(container);
		rootWrapper.setPadding(new Insets(10));
		StackPane.setAlignment(container, Pos.CENTER);
		
		rootWrapper.getStyleClass().removeAll("light-theme", "dark-theme");
		rootWrapper.getStyleClass().add(darkTheme ? "dark-theme" : "light-theme");
		
		return rootWrapper;
	}
	
	private static void saveHistoryToFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(HISTORY_FILE)))) {
			writer.write("[\n");
			for (int i = 0; i < historyList.size(); i++) {
				String sanitized = historyList.get(i).replace("\\", "\\\\").replace("\"", "\\\"");
				writer.write("    \"" + sanitized + "\"");
				if (i < historyList.size() - 1) {
					writer.write(",");
				}
				writer.write("\n");
			}
			writer.write("]");
		
		} catch (Exception e){
			System.err.println("Could not save history tape to JSON: " + e.getMessage());
		}
	}
	
	public static void loadHistoryFromFile() {
		File file = new File(HISTORY_FILE);
		if (!file.exists()) return;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			historyList.clear();
			String line;
			while ((line = reader.readLine()) != null) {
				String trimmed = line.trim();
				// Ignore the JSON array brackets and commas
				if (trimmed.equals("[") || trimmed.equals("]") || trimmed.equals(",") || trimmed.endsWith(",")) {
					if (trimmed.endsWith(",")) {
						trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
					}
					else {
						continue;
					}
				}
				// Remove surrounding quotes and un-escape
				if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
					String content = trimmed.substring(1, trimmed.length() - 1);
					String unescaped = content.replace("\\\"", "\"").replace("\\\\", "\\");
					historyList.add(unescaped);
				}
			}
			
		} catch (Exception e) {
			System.err.println("Could not load history tape: " + e.getMessage());
		}
	}
}

