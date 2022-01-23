import java.util.*;
import java.sql.*;

class credentials{
	private int u_id;
	private String pass;
	
	credentials(int u_id, String pass){
		this.u_id = u_id;
		this.pass = pass;
	}
	
	void setId(int u_id) {
		this.u_id = u_id;
	}
	
	void setPass(String pass) {
		this.pass = pass;
	}
	
	int getId() {
		return this.u_id;
	}
	
	String getPass() {
		return this.pass;
	}
}

public class App {
	public static void main(String args[]) {	
		// Scanner object to take input from the users
		Scanner sc = new Scanner(System.in);
		
		// Database URL and Credentials
		final String DB_URL = "jdbc:mysql://localhost:3306/addr_book";
		final String USER = "root";
		final String PASS = "";
		
		boolean ext_1 = true;
		while(ext_1) {
			// Welcome message
			System.out.println("Welcome to Address Book!\n");
			
			// Users hashmap to store all the users from database temporarily
			Map<String,credentials> users = new HashMap<String,credentials>();
			
			// Getting the list of users in DB and storing them in a users hashmap
			try {
				// Initializing DB connection 
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = con.createStatement();
				
				// Executing the query to get the users from the database
				String u_query = "SELECT * FROM users;";
				ResultSet rs = stmt.executeQuery(u_query);
				
				// Printing all the users from database and then adding them to a temporary users hashmap
				while (rs.next()) {
					int db_u_id = rs.getInt(1);
					String db_u_name = rs.getString(2);
					String db_u_pass = rs.getString(3);
					
					credentials cr = new credentials(db_u_id, db_u_pass);
					
					users.put(db_u_name, cr);
				}
				
				// Closing the connection
				con.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			
			// Sign-up or Login options for user to choose from
			System.out.println("1) New user? Sign-up!");
			System.out.println("2) Login");
			
			try {
				// Taking input from user to either sign-up or login
				int log_op = sc.nextInt();
				
				if(log_op==1) {
					try {
						boolean val_id = false;
						String new_user_name = "";
						while(val_id==false) {
							System.out.println("Type your username to sign-up with:");
							new_user_name = sc.next();
							
							if(users.containsKey(new_user_name)) {
								System.out.println("Username already exists. Please provide another username.");
							}else {
								System.out.println("Username is not taken.");
								val_id = true;
							}
						}			
						
						boolean val_pass = false;
						
						while(val_pass==false) {
							System.out.println("Type your password:");
							String password = sc.next();
							
							System.out.println("Confirm password:");
							String con_password = sc.next();
							
							if(password.equals(con_password)) {
								try {
									// Initializing DB connection 
									Class.forName("com.mysql.jdbc.Driver");
									Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
									Statement stmt = con.createStatement();
									
									// Executing the query to get the users from the database
									String query_new_user = "INSERT INTO users (user_name, pass) VALUES('" + new_user_name + "', '" + password + "');";
									stmt.executeUpdate(query_new_user);
									
									// Closing the connection
									con.close();
									
									val_pass = true;
									
									// Printing success statement
									System.out.println("Successfully added user to the address book.");
								}catch (Exception e) {
									System.out.println(e);
								}
							}else {
								System.out.println("Both the passwords do not match. Please check again.");
							}
						}
					}catch(Exception e) {
						System.out.println(e);
					}				
				}else if(log_op==2) {
					try {
						System.out.println("Kindly provide your credentials to login.");
						
						// Taking User Name input form user
						System.out.println("Username: (Enter 0 to exit)");
						String u_name = sc.next();
						int u_id = 0;
						String u_pass;
						
						// If input is 0 then exit/break
						if(u_name.equals("0")) {
							System.out.println("Exiting address book.");
							sc.close();
							System.exit(0);
						}else if(users.containsKey(u_name)) {
							System.out.println("Password:");
							u_pass = sc.next();
							String cr_pass = users.get(u_name).getPass();
							
							if(u_pass.equals(cr_pass)) {
								u_id = users.get(u_name).getId();
								System.out.println("Logged in successfully.");
							}else {
								System.out.println("Invalid password. ");
								System.out.println("Exiting address book.");
								System.exit(0);
							}
						}else {
							System.out.println("Invalid credentials.");
							System.out.println("Exiting address book.");
							System.exit(0);
						}
						
						System.out.println("Welcome " + u_name + "!");
						
						boolean ext_2 = true;
						
						while(ext_2) {
							System.out.println("Kindly select an operation from below:");
							// All the operations listed along with exit
							System.out.println("0) Exit");
							System.out.println("1) Show your address book.");
							System.out.println("2) Add a new contact to your address book.");
							System.out.println("3) Update a contact from your address book.");
							System.out.println("4) Delete a contact from your address book.");
							System.out.println("5) Find a contact in address book.");
							System.out.println("6) Change password.");
							System.out.println("7) Login from other username.");
							
							int op = sc.nextInt();
							
							// If operation is 0 then exit/break
							if(op==0) {
								System.out.println("Exiting address book.");
                                sc.close();
								System.exit(0);
							}else if(op==1) {
								try {
									System.out.println("Showing all the contacts from your address book:");
									
									// Initializing DB connection 
									Class.forName("com.mysql.jdbc.Driver");
									Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
									Statement stmt = con.createStatement();
									
									// Executing the query to get the users from the database
									String query1 = "SELECT c_name, ph_no, descr FROM contacts WHERE u_id=" + u_id + " ORDER BY c_name;";
									ResultSet rs = stmt.executeQuery(query1);
									
									// Printing all the contacts of that particular user from the DB
									System.out.println("+-----------------+----------------------+----------------------+");
									System.out.format("| %-15s | %-20s | %-20s |\n", "Name", "Phone Number", "Descrption");
									System.out.println("+-----------------+----------------------+----------------------+");
									while (rs.next())
										System.out.format("| %-15s | %-20s | %-20s |\n", rs.getString(1), rs.getString(2), rs.getString(3));
									System.out.println("+-----------------+----------------------+----------------------+");
									// Closing the connection
									con.close();
									
									// Option to exit
									System.out.println("Do you want to exit? (y/n)");
									String yn = sc.next();
									if(yn.equals("y")) {
										System.out.println("Exiting address book.");
										System.exit(0);
									}
								} catch (Exception e) {
									System.out.println(e);
									System.out.println("Exiting address book.");
									System.exit(0);
								}
							}else if(op==2) {
								try {
									System.out.println("Provide the following information to add new conatct:");
									
									// Taking input from user to add to database
									System.out.println("Contact Name:");
									String con_name = sc.next();
									
									System.out.println("Phone Number:");
									String phn_no = sc.next();
									
									System.out.println("Description (optional):");
									String descrip = sc.next();
									
									// Initializing DB connection 
									Class.forName("com.mysql.jdbc.Driver");
									Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
									Statement stmt = con.createStatement();
									
									// Executing the query to get the users from the database
									String query2 = "INSERT INTO contacts (u_id, c_name, ph_no, descr) VALUES(" + u_id + ", '" + con_name + "', '" + phn_no + "', '" + descrip + "');";
									stmt.executeUpdate(query2);
									
									// Closing the connection
									con.close();
									
									// Printing success statement
									System.out.println("Successfully added new contact to the address book.");
									
									// Option to exit
									System.out.println("Do you want to exit? (y/n)");
									String yn = sc.next();
									if(yn.equals("y")) {
										System.out.println("Exiting address book.");
										System.exit(0);
									}
								} catch (Exception e) {
									System.out.println("Failed to add new contact to the address book.");
									System.out.println(e);
									System.out.println("Exiting address book.");
									System.exit(0);
								}
							}else if(op==3) {
								try {
									System.out.println("Provide the following information to update the conatct details:");
									
									// Taking phone number as input to update that contact details
									System.out.println("Old Phone Number:");
									String old_phn_no = sc.next();
									
									// Taking new details input from user to update details in database
									System.out.println("New Contact Name:");
									String new_con_name = sc.next();
									
									System.out.println("New Phone Number:");
									String new_phn_no = sc.next();
									
									System.out.println("New Description:");
									String new_descrip = sc.next();
									
									// Initializing DB connection 
									Class.forName("com.mysql.jdbc.Driver");
									Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
									Statement stmt = con.createStatement();
									
									// Executing the query to get the users from the database
									String query3 = "UPDATE contacts SET c_name='" + new_con_name + "', ph_no='" + new_phn_no + "', descr='" + new_descrip + "' WHERE u_id=" + u_id + " AND " + "ph_no='" + old_phn_no + "';";
									stmt.executeUpdate(query3);
													
									// Closing the connection
									con.close();
									
									// Printing success statement
									System.out.println("Successfully updated the contact.");
									
									// Option to exit
									System.out.println("Do you want to exit? (y/n)");
									String yn = sc.next();
									if(yn.equals("y")) {
										System.out.println("Exiting address book.");
										System.exit(0);
									}
								} catch (Exception e) {
									System.out.println("Failed to update the contact in address book.");
									System.out.println(e);
									System.out.println("Exiting address book.");
									System.exit(0);
								}
							}else if(op==4) {
								try {
									System.out.println("Provide the following information to delete the conatct:");
									
									// Taking phone number as input to update that contact details
									System.out.println("Contact Phone Number:");
									String del_phn_no = sc.next();
									
									// Initializing DB connection 
									Class.forName("com.mysql.jdbc.Driver");
									Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
									Statement stmt = con.createStatement();
									
									// Executing the query to get the users from the database
									String query4 = "DELETE FROM contacts WHERE ph_no='" + del_phn_no + "';";
									stmt.executeUpdate(query4);
													
									// Closing the connection
									con.close();
									
									// Printing success statement
									System.out.println("Successfully deleted the contact.");
									
									// Option to exit
									System.out.println("Do you want to exit? (y/n)");
									String yn = sc.next();
									if(yn.equals("y")) {
										System.out.println("Exiting address book.");
										System.exit(0);
									}
								} catch (Exception e) {
									System.out.println("Failed to delete the contact from address book.");
									System.out.println(e);
									System.out.println("Exiting address book.");
									System.exit(0);
								}
							}else if(op==5) {
								try {
									System.out.println("How do you want to search for contact details:");
									System.out.println("1) By Contact Name");
									System.out.println("2) By Contact Number");
									
									// Taking input on how to search for contact details
									int ser_op = sc.nextInt();
									
									// Initializing DB connection 
									Class.forName("com.mysql.jdbc.Driver");
									Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
									Statement stmt = con.createStatement();
									
									ResultSet rs = null;
									
									if(ser_op==1) {
										// Taking phone number as input to update that contact details
										System.out.println("Contact Name:");
										String ser_con_name = sc.next();
										
										// Executing the query to get the users from the database
										String query5_1 = "SELECT c_name, ph_no, descr FROM contacts WHERE u_id=" + u_id + " AND UPPER(c_name) LIKE UPPER('%" + ser_con_name + "%') ORDER BY c_name;";
										rs = stmt.executeQuery(query5_1);
									}else if(ser_op==2) {
										// Taking phone number as input to update that contact details
										System.out.println("Phone Number:");
										String ser_phn_no = sc.next();
										
										// Executing the query to get the users from the database
										String query5_2 = "SELECT c_name, ph_no, descr FROM contacts WHERE u_id=" + u_id + " AND ph_no='" + ser_phn_no + "' ORDER BY c_name;";
										rs = stmt.executeQuery(query5_2);
									}
									
									int i = 0;
									
									// Printing all the contacts found according to the user input given in the DB
									System.out.println("+-----------------+----------------------+----------------------+");
									System.out.format("| %-15s | %-20s | %-20s |\n", "Name", "Phone Number", "Descrption");
									System.out.println("+-----------------+----------------------+----------------------+");
									while (rs.next()) {
										System.out.format("| %-15s | %-20s | %-20s |\n", rs.getString(1), rs.getString(2), rs.getString(3));
										i += 1;
									}
									System.out.println("+-----------------+----------------------+----------------------+");
									
									// Closing the connection
									con.close();
									
									if(i==0) {
										System.out.println("Could not find the contact with the provided details.");
									}
									
									// Option to exit
									System.out.println("Do you want to exit? (y/n)");
									String yn = sc.next();
									if(yn.equals("y")) {
										System.out.println("Exiting address book.");
										System.exit(0);
									}
									
								} catch (Exception e) {
									System.out.println(e);
									System.out.println("Exiting address book.");
									System.exit(0);
								}
							}else if(op==6){
								boolean val_ch_pass = false;
								
								while(val_ch_pass==false) {
									String current_pass = users.get(u_name).getPass();
									
									System.out.println("Type your current password:");
									String ch_current_pass = sc.next();
									
									System.out.println("Type your new password:");
									String ch_password = sc.next();
									
									System.out.println("Confirm new password:");
									String con_ch_password = sc.next();
									
									if(ch_password.equals(con_ch_password) && ch_current_pass.equals(current_pass)) {
										try {
											// Initializing DB connection 
											Class.forName("com.mysql.jdbc.Driver");
											Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
											Statement stmt = con.createStatement();
											
											// Executing the query to get the users from the database
											String query7 = "UPDATE users SET pass='" + ch_password + "' WHERE id=" + u_id + ";";
											stmt.executeUpdate(query7);
											
											// Closing the connection
											con.close();
											
											val_ch_pass = true;
											
											// Printing success statement
											System.out.println("Changed the password successfully.");
										}catch (Exception e) {
											System.out.println(e);
										}
									}else if(!ch_current_pass.equals(current_pass)){
										System.out.println("Inavlid current password. Please try again.");
									}else {
										System.out.println("Both the passwords do not match. Please check again.");
									}
								}
							}else if(op==7){
								break;
							}else {
								System.out.println("Invalid input.");
							}
						}
					}catch(Exception e) {
						System.out.println(e);
					}
				}else {
					System.out.println("Invalid input.");
					System.out.println("Exiting address book.");
					System.exit(0);
				}
			}catch(Exception e) {
				System.out.println("Invalid input.");
				System.out.println(e);
                sc.close();
				System.exit(0);
			}
		}
	}
}