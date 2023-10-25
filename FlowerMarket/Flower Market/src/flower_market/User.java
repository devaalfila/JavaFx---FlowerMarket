package flower_market;

public class User {
	public String userID;
	public String username;
	public String password;
	public String gender;
	public String email;
	public String phoneNumber;
	public String age;
	
	public User(String userID, String username, String password, String gender, String email, String phoneNumber,
			String age) {
		super();
		this.userID = userID;
		this.username = username;
		this.password = password;
		this.gender = gender;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.age = age;
	}
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	
	
}
