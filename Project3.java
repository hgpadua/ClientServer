package project3;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

/**
 * Huey Padua
 * Project 3: Two-Tier Client-Server Application 
 * Enterprise Computing
 * July 1, 2018
 */
public class Project3 {
    
    //class constants in pixels
    private static final int window_width = 610;
    private static final int window_height = 550;
    
    //window for GUI
    private JFrame window = new JFrame("Padua SQL Client GUI");
    private JPanel panel = new JPanel();
    
    private JComboBox jdbcDriver = new JComboBox(new String[]{"com.mysql.cj.jdbc.Driver", 
                "oracle.jdbc.driver.OracleDriver", "com.ibm.db2.jdbc.netDB2Driver"});
    private JComboBox databaseURL = new JComboBox
        (new String[]{"jdbc:mysql://localhost:3306/project3?"
                + "useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&"
                + "useLegacyDatetimeCode=false&serverTimezone=UTC", "jdbc:mysql://localhost:3306/project3"});
    
    private JTextField usernameTF = new JTextField();
    private JPasswordField passwordTF = new JPasswordField();
    
    private JButton connectDB = new JButton("Connect to Database");
    private JButton clearSQL = new JButton("Clear SQL Command");
    private JButton executeSQL = new JButton("Execute SQL Command");
    private JButton clearResults = new JButton("Clear Result Window");
    private JButton exitWindow = new JButton("Exit SQL Client");
    
    private JLabel databaseTag= new JLabel("Enter Database Information");
    private JLabel commandTag = new JLabel("Enter an SQL Command");
    private JLabel resultsTag = new JLabel("SQL Execution Result Window");
    
    private JLabel driverTag = new JLabel("JDBC Driver:");
    private JLabel urlTag = new JLabel("Database URL:");
    private JLabel usernameTag = new JLabel("Username:");
    private JLabel passwordTag = new JLabel("Password:");
    private JLabel connectionTag = new JLabel("No Connection Now");
    
    private JTextArea sqlCommands = new JTextArea(6, 25);
    private JTable sqlResults = new JTable();
    private JScrollPane resultScroll = new JScrollPane();
    private JScrollPane commandScroll = new JScrollPane();
    
    private static dbConnection dbConnect = null;
    private static sqlDB sql;

    
    //Constructor for gui window
    public Project3() {
        
        //configure GUI
        window.setSize(window_width, window_height);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(panel);
        panel.setSize(window_width, window_height);
        
        Container contentPane = window.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        
        contentPane.add(driverTag);
        contentPane.add(urlTag);
        contentPane.add(usernameTag);
        contentPane.add(passwordTag);
        
        jdbcDriver.setPreferredSize(new Dimension(150, 20));
        databaseURL.setPreferredSize(new Dimension(150, 20));
        usernameTF.setPreferredSize(new Dimension(150, 20));
        passwordTF.setPreferredSize(new Dimension(150, 20));
              
        contentPane.add(databaseTag);
        contentPane.add(jdbcDriver);
        contentPane.add(databaseURL);
        contentPane.add(usernameTF);
        contentPane.add(passwordTF); 
        contentPane.add(connectionTag);
        
        resultScroll = new JScrollPane(sqlResults);
	commandScroll = new JScrollPane(sqlCommands);
        resultScroll.setPreferredSize(new Dimension(590, 200));

        contentPane.add(commandTag);
        contentPane.add(commandScroll);
       
        contentPane.add(connectDB);
        contentPane.add(clearSQL);
        contentPane.add(executeSQL);
        
        contentPane.add(resultsTag);
        contentPane.add(resultScroll);
        
        contentPane.add(clearResults);
        contentPane.add(exitWindow);
        
        clearSQL.setEnabled(false);
        executeSQL.setEnabled(false);
        clearResults.setEnabled(false);
        
        //Spring Layouts for front end positioning
        //Layout for database information
        layout.putConstraint(SpringLayout.NORTH, databaseTag, 5, SpringLayout.SOUTH, panel);
	layout.putConstraint(SpringLayout.WEST, databaseTag, 10, SpringLayout.WEST, panel);
        
        layout.putConstraint(SpringLayout.NORTH, driverTag, 30, SpringLayout.SOUTH, panel);
	layout.putConstraint(SpringLayout.WEST, driverTag, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, jdbcDriver, 30, SpringLayout.WEST, panel);
	layout.putConstraint(SpringLayout.WEST, jdbcDriver, 100, SpringLayout.WEST, panel);
        
        layout.putConstraint(SpringLayout.NORTH, urlTag, 10, SpringLayout.SOUTH, driverTag);
	layout.putConstraint(SpringLayout.WEST, urlTag, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, databaseURL, 55, SpringLayout.WEST, panel);
	layout.putConstraint(SpringLayout.WEST, databaseURL, 100, SpringLayout.WEST, panel);
        
        layout.putConstraint(SpringLayout.NORTH, usernameTag, 10, SpringLayout.SOUTH, urlTag);
	layout.putConstraint(SpringLayout.WEST, usernameTag, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, usernameTF, 80, SpringLayout.WEST, panel);
	layout.putConstraint(SpringLayout.WEST, usernameTF, 100, SpringLayout.WEST, panel);
        
        layout.putConstraint(SpringLayout.NORTH, passwordTag, 10, SpringLayout.SOUTH, usernameTag);
	layout.putConstraint(SpringLayout.WEST, passwordTag, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, passwordTF, 105, SpringLayout.WEST, panel);
	layout.putConstraint(SpringLayout.WEST, passwordTF, 100, SpringLayout.WEST, panel);
        
        //layout for sql commands
        layout.putConstraint(SpringLayout.NORTH, commandTag, 5, SpringLayout.SOUTH, panel);
	layout.putConstraint(SpringLayout.WEST, commandTag, 360, SpringLayout.WEST, panel);       
        layout.putConstraint(SpringLayout.NORTH, commandScroll, 30, SpringLayout.SOUTH, panel);
	layout.putConstraint(SpringLayout.WEST, commandScroll, 300, SpringLayout.WEST, panel); 
        
        //layouts for connection status/ buttons
        layout.putConstraint(SpringLayout.NORTH, connectionTag, 15, SpringLayout.SOUTH, passwordTag);
	layout.putConstraint(SpringLayout.WEST, connectionTag, 10, SpringLayout.WEST, panel);         
        
        layout.putConstraint(SpringLayout.NORTH, connectDB, 20, SpringLayout.SOUTH, connectionTag);
	layout.putConstraint(SpringLayout.WEST, connectDB, 10, SpringLayout.WEST, panel); 
        
        layout.putConstraint(SpringLayout.NORTH, clearSQL, 20, SpringLayout.SOUTH, connectionTag);
	layout.putConstraint(SpringLayout.WEST, clearSQL, 200, SpringLayout.WEST, panel);  
        
        layout.putConstraint(SpringLayout.NORTH, executeSQL, 20, SpringLayout.SOUTH, connectionTag);
	layout.putConstraint(SpringLayout.WEST, executeSQL, 380, SpringLayout.WEST, panel); 
        
        //layouts for sql execution result window      
        layout.putConstraint(SpringLayout.NORTH, resultsTag, 100, SpringLayout.SOUTH, passwordTag);
	layout.putConstraint(SpringLayout.WEST, resultsTag, 10, SpringLayout.WEST, panel);  
        
        layout.putConstraint(SpringLayout.NORTH, resultScroll, 10, SpringLayout.SOUTH, resultsTag);
	layout.putConstraint(SpringLayout.WEST, resultScroll, 10, SpringLayout.WEST, panel); 
        
        //layout for clear result window/exit window
        layout.putConstraint(SpringLayout.NORTH, clearResults, 20, SpringLayout.SOUTH, resultScroll);
	layout.putConstraint(SpringLayout.WEST, clearResults, 20, SpringLayout.WEST, panel); 
        
        layout.putConstraint(SpringLayout.NORTH, exitWindow, 20, SpringLayout.SOUTH, resultScroll);
	layout.putConstraint(SpringLayout.WEST, exitWindow, 380, SpringLayout.WEST, panel);        
        
        panel.setLayout(layout);
        
        //display GUI
        window.show();
        
    }
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //initialize client-server gui
        Project3 gui = new Project3();
        gui.buttonTapped();
    }
    
    //Button functionalities
    public void buttonTapped() {
        
        connectDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
         
                //Checking whether all fields have been filled out
                if (usernameTF.getText().equals("") || passwordTF.getText().equals("")) {
                    JOptionPane.showMessageDialog(panel, "Please fill out all fields.");
                }
                //Connect to database with credentials
                else {
                    String username = usernameTF.getText();
                    String password = passwordTF.getText();
                    String connectionURL = "jdbc:mysql://localhost:3306/project3?"
                            + "useSSL=false&useUnicode=true&"
                            + "useJDBCCompliantTimezoneShift=true&"
                            + "useLegacyDatetimeCode=false&serverTimezone=UTC";
                    
                    dbConnect = new dbConnection(connectionURL, username, password);
                    
                    try {
                        dbConnect.EstablishConnection();
                    } catch (SQLException ex) {
                        System.out.println("Error: Could not establish connection!");
                        JOptionPane.showMessageDialog(panel, "Could not establish connection!");
                    }
                    if (dbConnect.getConnection() != null) {
                        System.out.println("Connection Established!");
                        connectionTag.setText("Connected to: " + connectionURL);
                        clearSQL.setEnabled(true);
                        executeSQL.setEnabled(true);
                        clearResults.setEnabled(true);
                    }
                    
                }
            }
            
        });
        //execute sql commands
        executeSQL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userQuery = sqlCommands.getText();
                sql = new sqlDB(dbConnect.getConnection());
                Vector<String> columns = new Vector<String>();
                Vector<Vector<String>> results = new Vector<Vector<String>>();
                
                //statement to set sql command to lower case
                //
                if(userQuery.toLowerCase().startsWith("select")) {
                    try {
                        results = sql.runQuery(userQuery);
                        columns = sql.getColumns();
                        sqlResults.setModel(new DefaultTableModel(results, columns));
                    } catch (SQLException ex) {
//                        System.out.println("Error: Could not execute query!");
                        JOptionPane.showMessageDialog(panel, "Error: Could not execute query!");
                    }
                }
                else {
                    try {
                        sql.runUpdate(userQuery);
                        sqlResults.setModel(new DefaultTableModel(new String[][]
                        {new String[]{"Row Updated!"}}, new String[]{""}));
                    } catch (SQLException ex) {
//                        System.out.println("Error: Could not execute query!");
                        JOptionPane.showMessageDialog(panel, "Error: Could not execute query!");
                    }
                }
            }
            
        });
        //clear Sql commands inputed
        clearSQL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sqlCommands.setText("");
            }
            
        });
        
        //clear results from sql command
        clearResults.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sqlResults.setModel(new DefaultTableModel(new String[][]{new String[]{""}},
                new String[]{""}));
            }
            
        });
        
        //exit GUI
        exitWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispose();
            }
            
        });
    }
}

class dbConnection  {
    
	private String mURL;
	private String mUser;
	private String mPassword;
	private Connection mConnection;
	
	//Constructor for database connection
        //USER OR CLIENT
	public dbConnection(String URL, String Username, String Password){
		this.mURL = URL;
		this.mUser = Username;
		this.mPassword = Password;
	}
	
	public void EstablishConnection() throws SQLException{
		mConnection = (Connection) DriverManager.getConnection(this.mURL, this.mUser, this.mPassword);
	}

	public void CloseConnection() throws SQLException{
		mConnection.close();
	}

	public Connection getConnection(){
		return this.mConnection;
	}
}

class sqlDB {

	private Connection connection;
	private ResultSetMetaData metaData;
	private Vector<String> columns;
	
        //Constructor for sqlDB to get established connection
	public sqlDB(Connection Connection){
		this.connection = Connection;
	}
	
	//Funtion that runs query
        //returns vector of vectors of the sql command results
	public Vector<Vector<String>> runQuery(String mQuery) throws SQLException{
		Vector<Vector<String>> mResults = new Vector<Vector<String>>();
                //create a statement for sql command
		Statement mStatement = (Statement) this.connection.createStatement();
                //Execute the sql command statement
		ResultSet results = mStatement.executeQuery(mQuery);
		metaData = results.getMetaData();
		
		int numCols = metaData.getColumnCount();
		setColumns(numCols,metaData);
                
                //Iterate through the results and print 
		while(results.next()){
			Vector<String> row = new Vector<String>();
			for(int i = 1; i <= numCols; i++){
				row.add(results.getString(i));
			}
			mResults.add(row);
		}
		return mResults;
	}
	
        //Function that gets returned columnds from prev query 
	public Vector<String> getColumns() throws SQLException{
		return this.columns;
	}
	
        //Helper function that updates query
        //check to see if command is read properly
	public int runUpdate(String mQuery) throws SQLException{
		Statement mStatement = this.connection.createStatement();
		return mStatement.executeUpdate(mQuery);
	}
	
	//Helper function that sets the column vars
	public void setColumns(int mNumColumns, ResultSetMetaData mMetaData) throws SQLException{
		columns = new Vector<String>();
		for(int i = 1; i <= mNumColumns; i++){
			columns.add(mMetaData.getColumnName(i));
		}
	}
}
