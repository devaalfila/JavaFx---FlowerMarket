package flower_market;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


public class CartForm {
	private Stage stage;
	private Scene scene;
	private String selectedID = "";
	@FXML
	private MenuBar menuBar;
	@FXML
	private TableView<Cart> tableCart;
	@FXML
	private TableColumn<Cart, String> itemID;
	@FXML
	private TableColumn<Cart, String> itemName;
	@FXML
	private TableColumn<Cart, String> itemPrice;
	@FXML
	private TableColumn<Cart, String> itemQuantity;
	@FXML
	private TableColumn<Cart, String> itemDescription;
	@FXML
	private TableColumn<Cart, String> totalPrice;
	@FXML
	private Button btn_remove;
	@FXML
	private Button btn_checkout;

	public final ObservableList<Cart> cart = FXCollections.observableArrayList();

	@FXML
	private void initialize() {
		loadData();
		refreshTableCart();

		tableCart.setRowFactory( tv -> {
			TableRow<Cart> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
					Cart rowData = row.getItem();
					selectedID = rowData.getItemID();
					btn_remove.setDisable(false);
				}
			});
			return row ;
		});
	}

	@FXML
	private void loadData() {
		itemID.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemID()));
		itemName.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemName()));
		itemPrice.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemPrice()));
		itemQuantity.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemQuantity()));
		itemDescription.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemDescription()));
		totalPrice.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getTotalPrice()));
	}

	@FXML
	private void refreshTableCart() {
		cart.clear();
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "SELECT * FROM `cart` LEFT JOIN item USING(itemID) WHERE userID = ?";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, Session.ID);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				cart.add(
						new Cart(
								rs.getString("itemID"), 
								rs.getString("itemName"), 
								rs.getString("itemDescription"),
								rs.getString("price"), 
								rs.getString("quantity"), 
								String.valueOf(Integer.parseInt(rs.getString("price")) * Integer.parseInt(rs.getString("quantity")))));
				tableCart.setItems(cart);
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(cart.size() > 0)
			btn_checkout.setDisable(false);
		else
			btn_checkout.setDisable(true);
	}

	public void removeFromCart(ActionEvent event) throws IOException {
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "DELETE FROM `cart` WHERE userID = ? AND itemID = ?";
		PreparedStatement stmt;
		try {			
			stmt = conn.prepareStatement(query);
			stmt.setString(1, Session.ID);
			stmt.setString(2, selectedID);

			if(stmt.executeUpdate() > 0) {
				refreshTableCart();
				Alert alert = new Alert(null);
				alert.setAlertType(AlertType.INFORMATION);
				alert.setHeaderText("Successfully remove items, Click OK to continue");
				alert.show();
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void Checkout(ActionEvent event) throws IOException {
		Alert alert = new Alert(null);
		if(cart.size() > 0) {
			Database dc = new Database();
			Connection conn = dc.getConnection();
			String query;
			for(Cart c : cart) {
				if(Integer.parseInt(getProductByID(c.itemID).getQuantity()) >= Integer.parseInt(c.itemQuantity)) {
					String transaction_id = generateTransactionID();
					PreparedStatement stmt2;
					query = "INSERT INTO `transaction`(`transactionID`, `userID`, `transactionDate`) VALUES (?, ?, ?)";
					try {
						LocalDate dateObj = LocalDate.now();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						String date = dateObj.format(formatter);
						Date dt = Date.valueOf(date); 

						stmt2 = conn.prepareStatement(query);
						stmt2.setString(1, transaction_id);
						stmt2.setString(2, Session.ID);
						stmt2.setDate(3, dt);

						if(stmt2.executeUpdate() > 0) {
							PreparedStatement stmt;
							query = "INSERT INTO `transactiondetail`(`transactionID`, `itemID`, `quantity`) VALUES (?, ?, ?)";
							try {
								stmt = conn.prepareStatement(query);
								stmt.setString(1, transaction_id);
								stmt.setString(2, c.itemID);
								stmt.setInt(3, Integer.parseInt(c.itemQuantity));

								if(stmt.executeUpdate() > 0) {
									PreparedStatement stmt3;
									query = "UPDATE `item` SET `quantity`= `quantity` - ? WHERE `itemID` = ?";
									try {
										stmt3 = conn.prepareStatement(query);
										stmt3.setInt(1, Integer.parseInt(c.itemQuantity));
										stmt3.setString(2, c.itemID);

										if(stmt3.executeUpdate() > 0) {
											removeCartByID(c.itemID);
											alert.setAlertType(AlertType.INFORMATION);
											alert.setHeaderText("Successfully checkout items with item name (" + c.itemName + ") and item id (" + c.itemID + "), click OK to continue");
											alert.showAndWait();
										}
										stmt3.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								stmt.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						stmt2.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else {
					alert.setAlertType(AlertType.ERROR);
					alert.setHeaderText("The item you ordered (" + c.itemName + ") with the itemID (" + c.itemID + ") is out of stock");
					alert.showAndWait();
					removeCartByID(c.itemID);
				}
			}
		}
		else {
			alert.setAlertType(AlertType.ERROR);
			alert.setHeaderText("Error");
			alert.setContentText("Cart is empty!");
			alert.show();
		}
		System.out.println("done");
		refreshTableCart();
	}

	public Item getProductByID(String item_id) {
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "SELECT * FROM `item` WHERE itemID = ? LIMIT 1";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, item_id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return new Item(rs.getString("itemID"), rs.getString("itemName"), rs.getString("itemDescription"), rs.getString("price"), rs.getString("quantity"));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void removeCartByID(String itemID) {
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "DELETE FROM `cart` WHERE userID = ? AND itemID = ?";
		PreparedStatement stmt;
		try {			
			stmt = conn.prepareStatement(query);
			stmt.setString(1, Session.ID);
			stmt.setString(2, itemID);
			if(stmt.executeUpdate() > 0) {
//				refreshTableCart();
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getMaxID() {
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "SELECT * FROM transaction ORDER BY transactionID DESC LIMIT 1";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				return rs.getString("transactionID");
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String generateTransactionID() {
		String id = "TR";

		String tempItem = getMaxID();
		if(tempItem != null) {			
			String str = tempItem.substring(2);
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

	public void ItemMarket(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("itemMarket.fxml"));
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
