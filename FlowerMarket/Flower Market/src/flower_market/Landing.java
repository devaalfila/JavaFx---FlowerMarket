package flower_market;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;


public class Landing {

	private Stage stage;
	private Scene scene;
	@FXML
	private Label label_name;
	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu menuUser;
	@FXML
	private Menu menuAdmin;
	
	@FXML
	private void initialize() {
		label_name.setText("Welcome " + Session.Username);
		if(Session.Role.equalsIgnoreCase("Admin")) {
			menuAdmin.setVisible(true);
			menuUser.setVisible(false);
		}
		else {
			menuUser.setVisible(true);
			menuAdmin.setVisible(false);

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
	
	public void ManageItem(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("manageItem.fxml"));
		stage = (Stage)menuBar.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void UserManage(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("userManage.fxml"));
		stage = (Stage)menuBar.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}

