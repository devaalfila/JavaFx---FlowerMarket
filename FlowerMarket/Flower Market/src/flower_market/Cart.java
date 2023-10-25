package flower_market;

public class Cart {
	public String itemID;
	public String itemName;
	public String itemDescription;
	public String itemPrice;
	public String itemQuantity;
	public String totalPrice;
	
	public Cart(String itemID, String itemName, String itemDescription, String itemPrice, String itemQuantity,
			String totalPrice) {
		super();
		this.itemID = itemID;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		this.itemPrice = itemPrice;
		this.itemQuantity = itemQuantity;
		this.totalPrice = totalPrice;
	}
	
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String userID) {
		this.itemID = userID;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	public String getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(String itemPrice) {
		this.itemPrice = itemPrice;
	}
	public String getItemQuantity() {
		return itemQuantity;
	}
	public void setItemQuantity(String itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
	
}