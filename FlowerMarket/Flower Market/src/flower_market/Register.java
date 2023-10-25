package flower_market;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class Register {

	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML
	private TextField userID;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private TextField email;
	@FXML
	private TextField phoneNumber;
	@FXML
	private Spinner<Integer> sp_age;
	@FXML
	private RadioButton male, female;
	
	public SpinnerValueFactory<Integer> svf;

	
	@FXML
	private void initialize() {
		// TODO Auto-generated method stub
		svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(16, 70);
		svf.setValue(0);
		sp_age.setValueFactory(svf);
		userID.setText(generateUserID());
	}
	
	public void Login(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("login.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public String getGender() {
		if(female.isSelected())
			return female.getText();
		else if(male.isSelected())
			return male.getText();
		return null;
	}

	public void Registration(ActionEvent event) throws IOException {
		Alert alert = new Alert(null);
		alert.setAlertType(AlertType.ERROR);
		alert.setTitle("REGISTRATION ERROR");

		if(username.getText().length() >= 5 && username.getText().length() <= 20) {
			if(password.getText().length() >= 5 && password.getText().length() <= 20) {
				if(isAlphaNumeric(password.getText().toString())) {
					if(email.getText().length() > 0 && validateEmail(email.getText())) {
						if(getGender() != null) {
							if(sp_age.getValue() != null) { 
								if(phoneNumber.getText().length() >= 9 && phoneNumber.getText().length() < 13){
									Database dc = new Database();
									Connection conn = dc.getConnection();
									String query = "INSERT INTO `user`(`userID`, `username`, `password`, `gender`, `email`, `phoneNumber`, `age`, `role`) VALUES (?,?,?,?,?,?,?,?)";
									PreparedStatement stmt;
									try {
										stmt = conn.prepareStatement(query);
										stmt.setString(1, userID.getText());
										stmt.setString(2, username.getText());
										stmt.setString(3, password.getText());
										stmt.setString(4, getGender());
										stmt.setString(5, email.getText());
										stmt.setString(6, phoneNumber.getText());
										stmt.setInt(7, sp_age.getValue());
										stmt.setString(8, "user");

										if(stmt.executeUpdate() > 0) {
											alert.setAlertType(AlertType.INFORMATION);
											alert.setTitle("REGISTRATION SUCCESS");
											alert.setHeaderText("Click OK to continue");
											alert.showAndWait();
											Login(event);
										}
										else {
											alert.setHeaderText("Invalid Data");
											alert.show();
										}
										stmt.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
									else {
										alert.setHeaderText("Phone Number must be between 9 � 12 characters.");
										alert.show();
									}
							}
							else {
								alert.setHeaderText("Age cant be empty");
								alert.show();
							}
						}
						else {
							alert.setHeaderText("Gender must be selected either �Male� or �Female�!");
							alert.show();
						}
					}
					else {
						alert.setHeaderText("Email not valid!");
						alert.show();
					}
				}
				else {
					alert.setHeaderText("Password must contain at least 1 character and number!");
					alert.show();
				}	
			}
			else {
				alert.setHeaderText("Password length must be between 5 - 20 characters!");
				alert.show();
			}
		}
		else {
			alert.setHeaderText("Username length must be between 5 - 20 characters!");
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
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	public static boolean validateEmail(String emailStr) {
	        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
	        return matcher.find();
	}

}

