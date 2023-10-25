package flower_market;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class Login {

	private Stage stage;
	private Scene scene;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML

	public void Registration(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("registration.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public void Submit(ActionEvent event) throws IOException{
		Database dc = new Database();
		Connection conn = dc.getConnection();
		String query = "SELECT * FROM user WHERE username = ? AND password = ? LIMIT 1";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, username.getText());
			stmt.setString(2, password.getText());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				Session.ID = rs.getString(1);
				Session.Username = rs.getString(2);
				Session.Role = rs.getString("role");
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("LOGIN SUCCESS");
				alert.setHeaderText("Click OK to continue");
				alert.showAndWait();
				Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
			}
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("LOGIN FAILED");
				alert.setHeaderText("Username or Password cannot be null!");
				alert.show();
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

