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
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;


public class TransactionForm {
	private Stage stage;
	private Scene scene;
	
	@FXML
	private MenuBar menuBar;
	@FXML
	private TableView<Transaction> tableTransaction;
	@FXML
	private TableColumn<Transaction, String> transactionID;
	@FXML
	private TableColumn<Transaction, String> itemName;
	@FXML
	private TableColumn<Transaction, String> itemPrice;
	@FXML
	private TableColumn<Transaction, String> itemQuantity;
	@FXML
	private TableColumn<Transaction, String> itemDescription;
	@FXML
	private TableColumn<Transaction, String> transactionDate;
	@FXML
	private TableColumn<Transaction, String> totalPrice;

	public final ObservableList<Transaction> transaction = FXCollections.observableArrayList();

	@FXML
	private void initialize() {
		loadData();
		refreshTableTransaction();
	}

	@FXML
	private void loadData() {
		transactionID.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getTransactionID()));
		itemName.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemName()));
		itemPrice.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemPrice()));
		itemDescription.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemDescription()));
		itemQuantity.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getItemQuantity()));
		transactionDate.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getTransactionDate()));
		totalPrice.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getTotalPrice()));
	}

	@FXML
	private void refreshTableTransaction() {
		transaction.clear();
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "SELECT *, item.quantity AS itemQuantity, transactiondetail.quantity AS transactionQuantity FROM `transaction` LEFT JOIN transactiondetail USING(transactionID) LEFT JOIN item USING(itemID) WHERE userID = ?";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, Session.ID);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
//				DateTimeFormatter dt_format = DateTimeFormatter.ofPattern("dd-MM-yyyy");  
//				LocalDate ld = LocalDate.parse(rs.getString("transactionDate"), dt_format);
				
				transaction.add(
						new Transaction(
								rs.getString("transactionID"), 
								Session.ID, 
								rs.getString("itemName"), 
								rs.getString("itemDescription"), 
								rs.getString("price"), 
								rs.getString("transactionQuantity"), 
								String.valueOf(Integer.parseInt(rs.getString("price")) * Integer.parseInt(rs.getString("transactionQuantity"))),
								rs.getString("transactionDate")));
				tableTransaction.setItems(transaction);
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	public void CartForm(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("cartForm.fxml"));
		stage = (Stage)menuBar.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
