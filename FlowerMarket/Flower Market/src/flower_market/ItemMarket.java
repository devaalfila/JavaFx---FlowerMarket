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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


public class ItemMarket {
	private Stage stage;
	private Scene scene;
	public int max = 0;
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
	private TextField tf_itemPrice;
	@FXML
	private TextField tf_itemDescription;
	@FXML
	private Button btn_add;
	@FXML
	private Spinner<Integer> sp_itemQuantity;
	
	public final ObservableList<Item> item = FXCollections.observableArrayList();
	public SpinnerValueFactory<Integer> vf_spinner;
	
	@FXML
	private void initialize() {
		loadData();
		refreshTableGuitar();
		
		vf_spinner = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99);
		vf_spinner.setValue(0);
		sp_itemQuantity.setValueFactory(vf_spinner);
		
		tableItem.setRowFactory( tv -> {
			TableRow<Item> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
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
		itemPrice.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getPrice()));
		itemDescription.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemDescription()));
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
		tf_itemPrice.setText(gt.getPrice());
		tf_itemDescription.setText(gt.getItemDescription());
//		sp_itemQuantity.getValueFactory().setValue(Integer.parseInt(gt.getQuantity()));
		max = Integer.parseInt(gt.getQuantity());
		btn_add.setDisable(false);
	}
	
	public void clearDataForm(ActionEvent event) throws IOException {
		tf_itemID.setText("");
		tf_itemName.setText("");
		tf_itemPrice.setText("");
		tf_itemDescription.setText("");
		sp_itemQuantity.getValueFactory().setValue(0);
		max = 0;
		btn_add.setDisable(true);
		
	}
	
	public void addToCart(ActionEvent event) throws IOException {
		Alert alert = new Alert(null);
		try {
			String item_id = tf_itemID.getText(); 
			try {
				int qty = sp_itemQuantity.getValue();
				if(qty > 0) {	
					if(qty <= max) {
						Database dc = new Database();
						Connection conn = dc.getConnection();
						String query = "SELECT * FROM `cart` LEFT JOIN `item` USING(itemID) WHERE userID = ? AND itemID = ?";
						PreparedStatement stmt;
						try {
							stmt = conn.prepareStatement(query);
							stmt.setString(1, Session.ID);
							stmt.setString(2, item_id);
							ResultSet rs = stmt.executeQuery();
							if(rs.next()) {
								PreparedStatement stmt2;
								qty += rs.getInt("quantity");
								query = "UPDATE `cart` SET `quantity`= ? WHERE `userID` = ? AND `itemID` = ?";
								try {
									stmt2 = conn.prepareStatement(query);
									stmt2.setInt(1, qty);
									stmt2.setString(2, Session.ID);
									stmt2.setString(3, item_id);
	
									if(stmt2.executeUpdate() > 0) {
										alert.setAlertType(AlertType.INFORMATION);
										alert.setHeaderText("Success");
										alert.setHeaderText("Successfully added item to cart, Click OK to continue");
										alert.show();
										refreshTableGuitar();
									}
	
									stmt2.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							else {
								PreparedStatement stmt2;
								query = "INSERT INTO `cart`(`userID`, `itemID`, `quantity`) VALUES (?,?,?)";
								try {
									stmt2 = conn.prepareStatement(query);
									stmt2.setString(1, Session.ID);
									stmt2.setString(2, item_id);
									stmt2.setInt(3, qty);
	
									if(stmt2.executeUpdate() > 0) {
										alert.setAlertType(AlertType.INFORMATION);
										alert.setHeaderText("Successfully added item to cart, Click OK to continue");
										alert.show();
										refreshTableGuitar();
									}
									stmt2.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							stmt.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					else {
						alert.setAlertType(AlertType.ERROR);
						alert.setTitle("Error");
						alert.setHeaderText("The quantity you input cannot exceed the existing quantity!");
						alert.show();
					}
				}
				else {
					alert.setAlertType(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Quantity must be greater than 0!");
					alert.show();
				}
			} catch (NullPointerException e) {
				alert.setAlertType(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Quantity must be greater than 0!");
				alert.show();
			}
		} catch (Exception e) {
			alert.setAlertType(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Click table row first!");
			alert.show();
		}
	}
	
	public void Logout(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
		stage = (Stage)menuBar.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	
	public void CartForm(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("cartForm.fxml"));
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
