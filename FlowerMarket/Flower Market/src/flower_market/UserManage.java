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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class UserManage {

	private Stage stage;
	private Scene scene;
	@FXML 
	private MenuBar menuBar;
	@FXML
	private TextField tf_userID;
	@FXML
	private TextField tf_username;
	@FXML
	private TextField tf_password;
	@FXML
	private TextField tf_email;
	@FXML
	private TextField tf_phoneNumber;
	@FXML
	private RadioButton male, female;
	@FXML
	private Spinner<Integer> sp_age;
	@FXML
	private TableView<User> tableUser;
	@FXML
	private TableColumn<User, String> userID;
	@FXML
	private TableColumn<User, String> username;
	@FXML
	private TableColumn<User, String> password;
	@FXML
	private TableColumn<User, String> gender;
	@FXML
	private TableColumn<User, String> email;
	@FXML
	private TableColumn<User, String> phoneNumber;
	@FXML
	private TableColumn<User, String> age;

	public final ObservableList<User> users = FXCollections.observableArrayList();

	public SpinnerValueFactory<Integer> svf;

	@FXML
	private void initialize() {
		loadData();
		refreshTableUser();

		svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(16, 50);
		svf.setValue(0);
		sp_age.setValueFactory(svf);

		tf_userID.setText(generateUserID());

		tableUser.setRowFactory( evt -> {
			TableRow<User> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (! row.isEmpty())) {
					User rowData = row.getItem();
					setDataForm(rowData);
				}
			});
			return row ;
		});
	}

	@FXML
	private void loadData() {
		userID.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getUserID()));
		username.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getUsername()));
		password.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getPassword()));
		gender.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getGender()));
		email.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getEmail()));
		phoneNumber.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getPhoneNumber()));
		age.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getAge()));
	}

	@FXML
	private void refreshTableUser() {
		users.clear();
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "SELECT * FROM user WHERE role = 'user'";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				users.add(
						new User(
								rs.getString("userID"), 
								rs.getString("username"), 
								rs.getString("password"), 
								rs.getString("gender"),
								rs.getString("email"),
								rs.getString("phoneNumber"),
								rs.getString("age")));
				tableUser.setItems(users);
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setDataForm(User user) {
		tf_userID.setText(user.getUserID());
		tf_username.setText(user.getUsername());
		tf_password.setText(user.getPassword());
		tf_email.setText(user.email);
		tf_phoneNumber.setText(user.phoneNumber);
		sp_age.getValueFactory().setValue(Integer.parseInt(user.age));
		if(user.getGender().equalsIgnoreCase("Male"))
			male.setSelected(true);
		else
			female.setSelected(true);

	}

	public void clearDataForm(ActionEvent event) throws IOException {
		clearDataFormAction();
	}

	public void clearDataFormAction() {
		tf_userID.setText(generateUserID());
		tf_username.setText("");
		tf_password.setText("");
		tf_email.setText("");
		tf_phoneNumber.setText("");
		sp_age.getValueFactory().setValue(0);
		male.setSelected(false);
		female.setSelected(false);		
	}

	public void DeleteUser(ActionEvent event) throws IOException {
		Alert alert = new Alert(null);
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "DELETE FROM user WHERE userID = ?";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, tf_userID.getText());
			if(stmt.executeUpdate() > 0) {
				alert.setAlertType(AlertType.INFORMATION);
				alert.setTitle("DELETE SUCCESS");
				alert.setHeaderText("Click OK to continue");
				alert.show();
				refreshTableUser();
			}
			else {
				alert.setAlertType(AlertType.ERROR);
				alert.setTitle("DELETE FAILED");
				alert.setHeaderText("Click OK to continue");
				alert.show();
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getGender() {
		if(female.isSelected())
			return female.getText();
		else
			return male.getText();
	}

	public void UpdateUser(ActionEvent event) throws IOException {
		String gender = getGender();
		Alert alert = new Alert(null);
		alert.setAlertType(AlertType.ERROR);
		alert.setHeaderText("Error");

		if(checkUserID()) {
			if(checkPassword()) {
				if(gender.length() > 0) {
					Database dc = new Database();
					Connection conn = dc.getConnection();
					String query = "UPDATE `user` SET `email`=?,`phoneNumber`=?,`age`=?,`gender`=? WHERE `userID` = ?";
					PreparedStatement stmt;
					try {
						stmt = conn.prepareStatement(query);
						stmt.setString(1, tf_email.getText());
						stmt.setString(2, tf_phoneNumber.getText());
						stmt.setInt(3, sp_age.getValue());
						stmt.setString(4, getGender());
						stmt.setString(5, tf_userID.getText());

						if(stmt.executeUpdate() > 0) {
							alert.setAlertType(AlertType.INFORMATION);
							alert.setTitle("UPDATE SUCCESS");
							alert.setHeaderText("Click OK to continue");
							alert.showAndWait();
							refreshTableUser();
						}
						else {
							alert.setAlertType(AlertType.ERROR);
							alert.setTitle("UPDATE FAILED");
							alert.setHeaderText("Click OK to continue");
							alert.show();
						}
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else {
					alert.setContentText("Gender must be selected either �Male� or �Female�!");
					alert.show();
				}
			}
			else {
				alert.setContentText("Password length must be between 5 - 10 characters!");
				alert.show();
			}
		}
		else {
			alert.setTitle("UPDATE FAILED");
			alert.setHeaderText("Please select the data you want to update!");
			alert.show();
		}
	}

	public String getMaxID() {
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "SELECT * FROM user ORDER BY userID DESC LIMIT 1";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				return rs.getString("userID");
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String generateUserID() {
		String id = "US";

		String tempID = getMaxID();
		if(tempID != null) {			
			String str = tempID.substring(2);
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

	public static boolean isAlphaNumeric(String str) {
		return str != null && str.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{6,20}$");
	}

	public void ItemManage(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("manageItem.fxml"));
		stage = (Stage)menuBar.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void Transaction(ActionEvent event) throws IOException {
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

	public void Logout(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
		stage = (Stage)menuBar.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public boolean checkUserID() {
		if(tf_username.getText().length() >= 5 && tf_username.getText().length() <= 15) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean checkPassword() {
		if(tf_password.getText().length() >= 5 && tf_password.getText().length() <= 10) {
			return true;
		}
		else {
			return false;
		}
	}
}

