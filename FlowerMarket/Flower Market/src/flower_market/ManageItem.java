package flower_market;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


public class ManageItem {
	private Stage stage;
	private Scene scene;
	@FXML
	private MenuBar menuBar;
	@FXML
	private TableView<Item> tableItem;
	@FXML
	private TableColumn<Item, String> itemID;
	@FXML
	private TableColumn<Item, String> itemName;
	@FXML
	private TableColumn<Item, String> itemDescription;
	@FXML
	private TableColumn<Item, String> itemPrice;
	@FXML
	private TableColumn<Item, String> itemQuantity;

	@FXML
	private TextField tf_itemID;
	@FXML
	private TextField tf_itemName;
	@FXML
	private TextField tf_itemDescription;
	@FXML
	private Spinner<Integer> sp_itemQuantity;
	@FXML
	private Spinner<Integer> sp_itemPrice;
	@FXML
	private Button btn_insert, btn_update, btn_delete;

	@FXML
	private RadioButton acoustic, bass, ukulele, classic, electric;

	public final ObservableList<Item> item = FXCollections.observableArrayList();
	public SpinnerValueFactory<Integer> vf_spinner_price;
	public SpinnerValueFactory<Integer> vf_spinner_quantity;

	@FXML
	private void initialize() {
		loadData();
		refreshTableGuitar();
		tf_itemID.setText(generateItemID());

		vf_spinner_price = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0, 1000);
		vf_spinner_price.setValue(0);

		vf_spinner_quantity = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE);
		vf_spinner_quantity.setValue(0);

		sp_itemPrice.setValueFactory(vf_spinner_price);
		sp_itemQuantity.setValueFactory(vf_spinner_quantity);

		tableItem.setRowFactory( tv -> {
			TableRow<Item> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (! row.isEmpty())) {
					Item rowData = row.getItem();
					setDataForm(rowData);
				}
			});
			return row ;
		});
	}

	@FXML
	private void loadData() {
		itemID.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemID()));
		itemName.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemName()));
		itemDescription.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemDescription()));
		itemPrice.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getPrice()));
		itemQuantity.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getQuantity()));
	}

	@FXML
	private void refreshTableGuitar() {
		item.clear();
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "SELECT * FROM item";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				item.add(
						new Item(
								rs.getString("itemID"), 
								rs.getString("itemName"), 
								rs.getString("itemDescription"),
								rs.getString("price"), 
								rs.getString("quantity")
								));
				tableItem.setItems(item);
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setDataForm(Item gt) {
		tf_itemID.setText(gt.getItemID());
		tf_itemName.setText(gt.getItemName());
		tf_itemDescription.setText(gt.getItemDescription());
		sp_itemPrice.getValueFactory().setValue(Integer.parseInt(gt.getPrice()));
		sp_itemQuantity.getValueFactory().setValue(Integer.parseInt(gt.getQuantity()));
		btn_delete.setDisable(false);
		btn_update.setDisable(false);
	}

	public void clearDataForm(ActionEvent event) throws IOException {
		clearDataFormAction();
	}

	public void clearDataFormAction() {
		tf_itemID.setText(generateItemID());
		tf_itemName.setText("");
		tf_itemDescription.setText("");
		sp_itemPrice.getValueFactory().setValue(0);
		sp_itemQuantity.getValueFactory().setValue(0);
		btn_delete.setDisable(true);
		btn_update.setDisable(true);
	}

	public void DeleteItem(ActionEvent event) throws IOException {
		Alert alert = new Alert(null);
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "DELETE FROM `item` WHERE itemID = ?";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, tf_itemID.getText());
			if(stmt.executeUpdate() > 0) {
				refreshTableGuitar();
				alert.setAlertType(AlertType.INFORMATION);
				alert.setHeaderText("DELETE SUCCESS");
				alert.showAndWait();
				clearDataFormAction();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void UpdateItem(ActionEvent event) throws IOException {
		Alert alert = new Alert(null);
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "UPDATE `item` SET `itemName`= ?,`itemDescription`= ?,`price`= ?,`quantity`= ? WHERE `itemID` = ?";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, tf_itemName.getText());
			stmt.setString(2, tf_itemDescription.getText());
			stmt.setInt(3, sp_itemPrice.getValue());
			stmt.setInt(4, sp_itemQuantity.getValue());
			stmt.setString(5, tf_itemID.getText());
			if(stmt.executeUpdate() > 0) {
				refreshTableGuitar();
				alert.setAlertType(AlertType.INFORMATION);
				alert.setHeaderText("UPDATE SUCCESS");
				alert.showAndWait();
				clearDataFormAction();
			}
			else {
				alert.setAlertType(AlertType.ERROR);
				alert.setHeaderText("UPDATE FAILED");
				alert.showAndWait();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			alert.setAlertType(AlertType.ERROR);
			alert.setHeaderText("UPDATE FAILED");
			alert.showAndWait();
			e.printStackTrace();
		}
	}

	public void InsertItem(ActionEvent event) throws IOException {
		Alert alert = new Alert(null);
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "INSERT INTO `item`(`itemID`, `itemName`, `itemDescription`, `price`, `quantity`) VALUES (?,?,?,?,?)";
		PreparedStatement stmt;
		if(tf_itemName.getText().length() <= 100 && tf_itemName.getText().length() >= 3) {
			if(tf_itemName.getText().contains(":")) {
				try {
					if(sp_itemPrice.getValue() > 0) {
						if(sp_itemQuantity.getValue() > 0) {		
							try {							
								stmt = conn.prepareStatement(query);
								stmt.setString(1, tf_itemID.getText());
								stmt.setString(2, tf_itemName.getText());
								stmt.setString(3, tf_itemDescription.getText());
								stmt.setInt(4, sp_itemPrice.getValue());
								stmt.setInt(5, sp_itemQuantity.getValue());
								if(stmt.executeUpdate() > 0) {
									refreshTableGuitar();
									alert.setAlertType(AlertType.INFORMATION);
									alert.setHeaderText("Add Item Successfully, Click OK to continue");
									alert.showAndWait();
									clearDataFormAction();
								}
								else {
									alert.setAlertType(AlertType.ERROR);
									alert.setHeaderText("Failed to insert item");
									alert.showAndWait();
								}
								stmt.close();
							}
							catch (Exception e) {
								alert.setAlertType(AlertType.ERROR);
								alert.setHeaderText("Failed to insert item");
								alert.showAndWait();
							}
						}
						else {
							alert.setAlertType(AlertType.ERROR);
							alert.setHeaderText("Quantity must be greater than 0!");
							alert.show();
						}
					}
					else {
						alert.setAlertType(AlertType.ERROR);
						alert.setHeaderText("Price must be greater than 0!");
						alert.show();
					}
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
					alert.setAlertType(AlertType.ERROR);
					alert.setHeaderText("Price must be a number!");
					alert.show();
				}
			}
			else {
				alert.setAlertType(AlertType.ERROR);
				alert.setHeaderText("Item Name must consist of at least 2 words containing the game name and item name then separated by (:)");
				alert.showAndWait();
			}
		}
		else {
			alert.setAlertType(AlertType.ERROR);
			alert.setHeaderText("ItemName must be between 3 - 100 characters!");
			alert.showAndWait();
		}
	}

	public Item getMaxID() {
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "SELECT * FROM item ORDER BY itemID DESC LIMIT 1";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				return new Item(
						rs.getString("itemID"), 
						rs.getString("itemName"), 
						rs.getString("itemDescription"),
						rs.getString("price"), 
						rs.getString("quantity")
						);
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String generateItemID() {
		String id = "IT";

		Item tempItem = getMaxID();
		if(tempItem != null) {			
			String str = tempItem.getItemID().substring(2);
			int currentID = Integer.parseInt(str) + 1;
			str = String.valueOf(currentID);
			if(currentID >= 100)
				id += String.valueOf(currentID);
			else if(currentID >= 10 && currentID < 100)
				id += "0" + String.valueOf(currentID);
			else
				id += "00" + String.valueOf(currentID);
		}
		else {
			id += "001";
		}
		return id;
	}

	public void Logout(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
		stage = (Stage)menuBar.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}


	public void ManageUser(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("userManage.fxml"));
		stage = (Stage)menuBar.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void TransactionHistory(ActionEvent event) throws IOException {
		Parent root;
		if(Session.Role.equalsIgnoreCase("Admin")) {
			root = FXMLLoader.load(getClass().getResource("transactionHistoryAdmin.fxml"));
		}
		else {
			root = FXMLLoader.load(getClass().getResource("transactionHistory.fxml"));
		}
		stage = (Stage)menuBar.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
