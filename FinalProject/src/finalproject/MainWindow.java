package finalproject;

// Necessary imports
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/******************************************************************************
 *  This is the final project for CSC-251 for Forsyth Technical Community     *
 * college. It asks us to create a database of customers for a theoretical    *
 * coffee shop and allow the waitress the ability to create a new customer,   *
 * add an order to a customer, see a customer's data (including credit limit) *
 * and search through customer orders by filtering the database via querying. *
 *                                                                            * 
 * @author David Mills                                                        *
 * @version 1.0.2                                                             *
 ******************************************************************************/
public final class MainWindow extends JFrame implements WindowListener
{
    //---------- Database-Specific Variables ----------//
    private static final String DB_STRING 
            = "jdbc:derby://localhost:1527/dbCoffeeStoreData";
    private static final String DB_USERNAME_AND_PASS = "me";
    private static Connection databaseConnection;
    private static boolean isConnected = false, hasTables;
    private static MainWindow GUI;
    
    //---------- Common Resources ----------//
    public static final Color CLR_BACKGROUND = new Color(43, 43, 43);
    public static final Color CLR_LOGO_BACKGROUND = new Color(34, 34, 34);
    public static final Color CLR_PANEL_BACKGROUND = new Color(60, 63, 65);
    public static final Color CLR_BUTTON_TEXT = new Color(187, 187, 187);
    public static final Color CLR_TEXT = new Color(169, 183, 198);
    public static final Color CLR_HIGHLIGHTED_TEXT = new Color(235, 235, 235);
    
    //---------- JFrame Components ----------//
    private final JButton btnCreate;
    private final JButton btnReset;
    private final JButton btnCoffees;
    private final JButton btnCustomers;
    private final JButton btnOrdersForCustomer;
    private final JButton btnNewCustomer;
    private final JButton btnNewOrder;
    private final JLabel lblAbout;
    private final JLabel lblLogo;
    private final JPanel pnlAll;
    private final JPanel pnlLogo;
    private final JPanel pnlNav;
    private final JPanel pnlAbout;
    private final JPanel pnlButtons;
    private final JPanel pnlContent;
    
    public MainWindow()
    {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUpConnection();
        setTitle("Coffee Shop - Main Window");
        HighlightEffect highlightEffect = new HighlightEffect();
        UIManager.put("OptionPane.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("Panel.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", MainWindow.CLR_TEXT);
        UIManager.put("Button.background", MainWindow.CLR_LOGO_BACKGROUND);
        UIManager.put("Button.foreground", MainWindow.CLR_TEXT);
        
        //---------- JButton Setup ----------//
        btnCreate = new JButton("Create Database");
        btnReset = new JButton("Reset Database");
        btnCreate.setBackground(CLR_LOGO_BACKGROUND);
        btnCreate.setForeground(CLR_TEXT);
        btnReset.setBackground(CLR_LOGO_BACKGROUND);
        btnReset.setForeground(CLR_TEXT);
        btnCreate.addActionListener((ActionEvent) -> 
        {
            createDatabaseTables(false);
            JOptionPane.showConfirmDialog(this, "Database creation successful!",
                    "Success!", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            btnCreate.setEnabled(false);
            btnReset.setEnabled(true);
        });
        btnReset.addActionListener((ActionEvent) -> 
        {
            if (JOptionPane.showConfirmDialog(this,
                    "Do you want to reset the database back to its "
                            + "initial values? (This may take a minute or two.)"
                    , "Are you sure?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) 
                    == JOptionPane.YES_OPTION)
            {
                createDatabaseTables(true);
                JOptionPane.showConfirmDialog(this,
                        "Database reset successful!",
                        "Success!", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        if (hasTables)
            btnCreate.setEnabled(false);
        else
            btnReset.setEnabled(false);
        btnCoffees = new JButton("<html><style>p { width: 125px; }</style>"
                + "<p>Display Coffees</p></html>");
        btnCustomers = new JButton("<html><style>p { width: 125px; }</style>"
                + "<p>Display Customers</p></html>");
        btnOrdersForCustomer = new JButton("<html><style>p { width: 125px; }"
                + "</style><p>Display Orders for Customer</p></html>");
        btnNewCustomer = new JButton("<html><style>p { width: 125px; }</style>"
                + "<p>Add New Customer</p></html>");
        btnNewOrder = new JButton("<html><style>p { width: 125px; }</style>"
                + "<p>Add New Order</p></html>");
        btnCustomers.addActionListener((ActionEvent) -> 
        {
            if (hasTables)
            {
                CustomerDisplay display = new CustomerDisplay(this,
                        databaseConnection);
                setVisible(false);
                display.setVisible(true);
                display.addWindowListener(display);
            }
            else
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Database tables have not yet been created!", "Error!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCoffees.addActionListener((ActionEvent) -> 
        {
            if (hasTables)
            {
                CoffeeDisplay display = new CoffeeDisplay(this,
                        databaseConnection);
                setVisible(false);
                display.setVisible(true);
                display.addWindowListener(display);
            }
            else
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Database tables have not yet been created!", "Error!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });
        btnOrdersForCustomer.addActionListener((ActionEvent) -> 
        {
            if (hasTables)
            {
                OrdersByCustomer display = new OrdersByCustomer(this,
                        databaseConnection);
                setVisible(false);
                display.setVisible(true);
                display.addWindowListener(display);
            }
            else
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Database tables have not yet been created!", "Error!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });
        btnNewCustomer.addActionListener((ActionEvent) -> 
        {
            if (hasTables)
            {
                NewCustomer add = new NewCustomer(this, databaseConnection);
                setVisible(false);
                add.setVisible(true);
                add.addWindowListener(add);
            }
            else
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Database tables have not yet been created!", "Error!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });
        btnNewOrder.addActionListener((ActionEvent) -> 
        {
            if (hasTables)
            {
                NewOrder add = new NewOrder(this, databaseConnection);
                setVisible(false);
                add.setVisible(true);
                add.addWindowListener(add);
            }
            else
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Database tables have not yet been created!", "Error!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });
        //---------- JLabel Setup ----------//
        lblAbout = new JLabel("<html><h1 align=\"center\">"
                + "Welcome to the Coffee Shop!</h1>"
                + "<h2>&nbsp;&nbsp;Who We Are</h2>We are a small, local "
                + "business that focuses on our loyal customer base. We grind"
                + " our<br />beans fresh, in-store every morning in hopes "
                + "that you"
                + " will come join us for the best-tasting<br />"
                + "coffee experience"
                + " that you will likely ever experience! Guarenteed!<br />"
                + "<h2>&nbsp;&nbsp;What We Offer</h2>Our selection of coffees"
                + " borders the"
                + " unparalleled. We have the following options:<br />"
                + "<ul><li>Brewed Coffee from Arabica Beans</li>"
                + "<li>Foamy Cappuccinos</li><li>Sweet Mochas</li>"
                + "<li>Light Lattes</li><li>Strong Americanos</li></ul><br />"
                + "Of course, each item comes in chilled or frozen versions"
                + " too! Cold brew coming soon!<br />"
                + "<h2 align=\"center\">We Hope to See You Here!</h2></html>");
        lblAbout.setForeground(CLR_TEXT);
        lblLogo = new JLabel();
        lblLogo.setAlignmentX(CENTER_ALIGNMENT);
        try
        {
            Image logo = ImageIO.read(getClass()
                    .getResource("resources/JavaProject.png"));
            lblLogo.setIcon(new ImageIcon(logo));
        }
        catch (IOException exception)
        {
            JOptionPane.showConfirmDialog(getContentPane(),
                    "<html>The following error occurred:<br />" 
                            + exception.getMessage() + "</html>", "Error!",
                            JOptionPane.PLAIN_MESSAGE,
                            JOptionPane.ERROR_MESSAGE);
        }
        lblLogo.setOpaque(true);
        lblLogo.setBackground(CLR_BACKGROUND);
        
        //---------- JPanel Setup ----------//        
        pnlAbout = new JPanel();
        pnlAbout.add(lblAbout);
        pnlAbout.setBackground(CLR_BACKGROUND);
        
        pnlButtons = new JPanel();
        pnlButtons.add(btnCreate);
        pnlButtons.add(btnReset);
        pnlButtons.setBackground(CLR_BACKGROUND);
        
        pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.add(pnlAbout);
        pnlContent.add(pnlButtons);
        pnlContent.setBackground(CLR_BACKGROUND);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        pnlNav = new JPanel();
        pnlNav.setLayout(new BoxLayout(pnlNav, BoxLayout.Y_AXIS));
        pnlNav.add(Box.createRigidArea(new Dimension(0, 50)));
        pnlNav.add(btnCoffees);
        pnlNav.add(Box.createRigidArea(new Dimension(0, 50)));
        pnlNav.add(btnCustomers);
        pnlNav.add(Box.createRigidArea(new Dimension(0, 50)));
        pnlNav.add(btnOrdersForCustomer);
        pnlNav.add(Box.createRigidArea(new Dimension(0, 50)));
        pnlNav.add(btnNewCustomer);
        pnlNav.add(Box.createRigidArea(new Dimension(0, 50)));
        pnlNav.add(btnNewOrder);
        pnlNav.add(Box.createRigidArea(new Dimension(0, 50)));
        pnlNav.setBackground(CLR_PANEL_BACKGROUND);
        pnlNav.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 5,
            CLR_LOGO_BACKGROUND));
        
        pnlLogo = new JPanel();
        pnlLogo.add(lblLogo);
        pnlLogo.setBackground(CLR_BACKGROUND);
        pnlLogo.setBorder(BorderFactory.createMatteBorder(5, 5, 0, 5,
                CLR_LOGO_BACKGROUND));
        
        for (Component component : pnlNav.getComponents())
            if (component instanceof JButton)
            {
                ((JButton)component).setOpaque(false);
                ((JButton)component).setContentAreaFilled(false);
                ((JButton)component).setBorderPainted(false);
                ((JButton)component).setForeground(CLR_BUTTON_TEXT);
                ((JButton)component).setCursor(Cursor
                        .getPredefinedCursor(Cursor.HAND_CURSOR));
                ((JButton)component).addMouseListener(highlightEffect);
            }
        
        pnlAll = new JPanel();
        ((FlowLayout)pnlAll.getLayout()).setHgap(0);
        ((FlowLayout)pnlAll.getLayout()).setVgap(0);
        pnlAll.add(pnlNav);
        pnlAll.add(pnlContent);
        pnlAll.setBackground(CLR_BACKGROUND);
        pnlAll.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5,
                CLR_LOGO_BACKGROUND));
        
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(pnlLogo);
        add(pnlAll);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(getSize());
        setMaximumSize(getSize());
        setResizable(false);
    }
    
    // Executes SQL to create the tables and default values to test the
    // database.
    private void createDatabaseTables(boolean drop)
    {
        try (Statement createStatement = databaseConnection.createStatement();)
        {
            if (drop)
            {
                createStatement.execute("DROP TABLE Orders");
                createStatement.execute("DROP TABLE Coffees");
                createStatement.execute("DROP TABLE Customers");
            }
            //---------- Creating Tables ----------//
            createStatement.execute(
                    "CREATE TABLE   Customers"
                            + "("
                            + "Customer_ID  INTEGER"
                            + "     PRIMARY KEY"
                            + "     NOT NULL GENERATED ALWAYS AS IDENTITY "
                            + "     (START WITH 1, INCREMENT BY 1),"
                            + "First_Name   VARCHAR(25)"
                            + "     NOT NULL,"
                            + "Last_Name    VARCHAR(25)"
                            + "     NOT NULL,"
                            + "Street       VARCHAR(50)"
                            + "     NOT NULL,"
                            + "City         VARCHAR(25)"
                            + "     NOT NULL,"
                            + "State        VARCHAR(2)"
                            + "     NOT NULL,"
                            + "Zip_Code     INTEGER"
                            + "     NOT NULL,"
                            + "Phone_Number BIGINT"
                            + "     NOT NULL,"
                            + "Email_Address VARCHAR(50)"
                            + "     NOT NULL,"
                            + "Credit_Limit FLOAT"
                            + "     DEFAULT 200.0"
                            + ")");
            createStatement.execute(
                    "CREATE TABLE   Coffees"
                            + "("
                            + "Coffee_ID    INTEGER"
                            + "     PRIMARY KEY"
                            + "     NOT NULL GENERATED ALWAYS AS IDENTITY"
                            + "     (START WITH 1, INCREMENT BY 1),"
                            + "Name         VARCHAR(25)"
                            + "     NOT NULL,"
                            + "Description  VARCHAR(100)"
                            + "     NOT NULL,"
                            + "Price        FLOAT"
                            + "     NOT NULL,"
                            + "Number_In_Stock  INTEGER"
                            + "     NOT NULL"
                            + ")");
            createStatement.execute(
                    "CREATE TABLE   Orders"
                            + "("
                            + "Order_ID     INTEGER"
                            + "     PRIMARY KEY"
                            + "     NOT NULL GENERATED ALWAYS AS IDENTITY"
                            + "     (START WITH 1, INCREMENT BY 1),"
                            + "Customer_ID  INTEGER,"
                            + "Coffee_ID    INTEGER,"
                            + "Quantity     FLOAT"
                            + "     NOT NULL,"
                            + "Total        FLOAT"
                            + "     NOT NULL,"
                            + "CONSTRAINT customer_id_fk FOREIGN KEY "
                            + "(Customer_ID) REFERENCES "
                            + "Customers(Customer_ID),"
                            + "CONSTRAINT coffee_id_fk FOREIGN KEY "
                            + "(Coffee_ID) REFERENCES "
                            + "Coffees(Coffee_ID)"
                            + ")");
            try (PreparedStatement addCustomer =
                    databaseConnection.prepareStatement(
                    "INSERT INTO Customers(First_Name, Last_Name, Street, City,"
                            + " State, Zip_Code, Phone_Number, Email_Address,"
                            + " Credit_Limit) VALUES "
                            + "(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement addCoffee = databaseConnection.prepareStatement(
                    "INSERT INTO Coffees(Name, Description, Price,"
                            + " Number_In_Stock) VALUES (?, ?, ?, ?)");
            PreparedStatement addOrder = databaseConnection.prepareStatement(
                    "INSERT INTO Orders(Customer_ID, Coffee_ID, Quantity,"
                            + " Total) VALUES (?, ?, ?, ?)");)
            {
                //---------- Adding Default Customers ----------//
                addCustomer.setString(1, "David");
                addCustomer.setString(2, "Mills");
                addCustomer.setString(3, "1606 Glen Abbey Ct.");
                addCustomer.setString(4, "Clemmons");
                addCustomer.setString(5, "NC");
                addCustomer.setInt(6, 27012);
                addCustomer.setLong(7, 2527026250L);
                addCustomer.setString(8, "millsd2423@forsythtech.edu");
                addCustomer.setDouble(9, 200.15D);
                addCustomer.execute();

                addCustomer.setString(1, "Evan");
                addCustomer.setString(2, "Hardy");
                addCustomer.setString(3, "622 Prairie Grove Dr.");
                addCustomer.setString(4, "Clemmons");
                addCustomer.setString(5, "NC");
                addCustomer.setInt(6, 27012);
                addCustomer.setLong(7, 3367945123L);
                addCustomer.setString(8, "ehardy@hpu.edu");
                addCustomer.setDouble(9, 700.25D);
                addCustomer.execute();

                addCustomer.setString(1, "Jenna");
                addCustomer.setString(2, "Kelley");
                addCustomer.setString(3, "19810 Trundle Ln.");
                addCustomer.setString(4, "Winston-Salem");
                addCustomer.setString(5, "NC");
                addCustomer.setInt(6, 27016);
                addCustomer.setLong(7, 3360243516L);
                addCustomer.setString(8, "ilovecoffee1234@hotmail.com");
                addCustomer.setDouble(9, 50.50D);
                addCustomer.execute();

                addCustomer.setString(1, "Kelsey");
                addCustomer.setString(2, "Spruill");
                addCustomer.setString(3, "98462 Cherry St.");
                addCustomer.setString(4, "Winston-Salem");
                addCustomer.setString(5, "NC");
                addCustomer.setInt(6, 27013);
                addCustomer.setLong(7, 3361849497L);
                addCustomer.setString(8, "spruillk92@gmail.com");
                addCustomer.setDouble(9, 63.84D);
                addCustomer.execute();

                addCustomer.setString(1, "Jacob");
                addCustomer.setString(2, "Fischer");
                addCustomer.setString(3, "1220 Gamewood Dr.");
                addCustomer.setString(4, "Charleston");
                addCustomer.setString(5, "SC");
                addCustomer.setInt(6, 29403);
                addCustomer.setLong(7, 8431649874L);
                addCustomer.setString(8, "jfischer@hohwatertechnologies.com");
                addCustomer.setDouble(9, 62.50D);
                addCustomer.execute();

                //---------- Adding Default Coffees ----------//
                addCoffee.setString(1, "Brewed Coffee");
                addCoffee.setString(2, "Single-cup brewed coffee.");
                addCoffee.setDouble(3, 1.29D);
                addCoffee.setInt(4, 19);
                addCoffee.execute();

                addCoffee.setString(1, "Cappuccinno");
                addCoffee.setString(2, "Espresso with stretched milk and large"
                        + " amounts of foam.");
                addCoffee.setDouble(3, 2.09D);
                addCoffee.setInt(4, 12);
                addCoffee.execute();

                addCoffee.setString(1, "Americano");
                addCoffee.setString(2, "Espresso with hot water.");
                addCoffee.setDouble(3, 1.89D);
                addCoffee.setInt(4, 9);
                addCoffee.execute();

                addCoffee.setString(1, "Mocha");
                addCoffee.setString(2, "Espresso with stretched milk and"
                        + " chocolate.");
                addCoffee.setDouble(3, 1.69D);
                addCoffee.setInt(4, 5);
                addCoffee.execute();

                addCoffee.setString(1, "Latte");
                addCoffee.setString(2, "Espresso with stretched milk and little"
                        + " amounts of foam.");
                addCoffee.setDouble(3, 1.99D);
                addCoffee.setInt(4, 10);
                addCoffee.execute();

                //---------- Adding Default Orders ----------//
                addOrder.setInt(1, 1);
                addOrder.setInt(2, 1);
                addOrder.setDouble(3, 2.0D);
                addOrder.setDouble(4, (1.29D * 2.0D));
                addOrder.execute();

                addOrder.setInt(1, 1);
                addOrder.setInt(2, 3);
                addOrder.setDouble(3, 1.0D);
                addOrder.setDouble(4, 1.89D);
                addOrder.execute();

                addOrder.setInt(1, 2);
                addOrder.setInt(2, 2);
                addOrder.setDouble(3, 3.0D);
                addOrder.setDouble(4, (2.09D * 3.0D));
                addOrder.execute();

                addOrder.setInt(1, 2);
                addOrder.setInt(2, 5);
                addOrder.setDouble(3, 1.0D);
                addOrder.setDouble(4, 1.99D);
                addOrder.execute();

                addOrder.setInt(1, 3);
                addOrder.setInt(2, 4);
                addOrder.setDouble(3, 2.0D);
                addOrder.setDouble(4, (1.69D * 2.0D));
                addOrder.execute();

                addOrder.setInt(1, 3);
                addOrder.setInt(2, 5);
                addOrder.setDouble(3, 2.0D);
                addOrder.setDouble(4, (1.99D * 2.0D));
                addOrder.execute();

                addOrder.setInt(1, 4);
                addOrder.setInt(2, 2);
                addOrder.setDouble(3, 4.0D);
                addOrder.setDouble(4, (2.09D * 4.0D));
                addOrder.execute();

                addOrder.setInt(1, 4);
                addOrder.setInt(2, 3);
                addOrder.setDouble(3, 6.0D);
                addOrder.setDouble(4, (1.89D * 6.0D));
                addOrder.execute();

                addOrder.setInt(1, 5);
                addOrder.setInt(2, 4);
                addOrder.setDouble(3, 1.0D);
                addOrder.setDouble(4, 1.69D);
                addOrder.execute();

                addOrder.setInt(1, 5);
                addOrder.setInt(2, 5);
                addOrder.setDouble(3, 3.0D);
                addOrder.setDouble(4, (1.99D * 3.0D));
                addOrder.execute();
            }
            hasTables = true;
        }
        catch (SQLException exception)
        {
            JOptionPane.showConfirmDialog(this,
                    "<html>The following error occurred:<br />" 
                            + exception.getMessage() + "</html>", "Error!",
                            JOptionPane.PLAIN_MESSAGE,
                            JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Connects to the database and determines if the database exists and has
    // tables to read from.
    private void setUpConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.ClientDriver")
                    .getConstructor().newInstance();
            databaseConnection = DriverManager.getConnection(DB_STRING,
                    DB_USERNAME_AND_PASS, DB_USERNAME_AND_PASS);
            isConnected = true;
            DatabaseMetaData metaData = databaseConnection.getMetaData();
            String[] pleaseGiveMeTables = {"TABLE"};
            try (ResultSet getTables = metaData.getTables(null, null, null,
                    pleaseGiveMeTables))
            {
                hasTables = getTables.next();
            }
        }
        catch (ClassNotFoundException | NoSuchMethodException 
                | InstantiationException | IllegalAccessException
                | InvocationTargetException | SQLException exception)
        {
            JOptionPane.showConfirmDialog(this,
                    "<html>The following error occurred:<br />" 
                            + exception.getMessage() + "<br />Exiting "
                                    + "program...</html>", "Error!",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    // Closes the connection to the database.
    private void disconnectFromDatabase()
    {
        if (isConnected)
            try
            {
                databaseConnection.close();
            }
            catch (SQLException exception)
            {
                JOptionPane.showConfirmDialog(this,
                    "<html>The following error occurred:<br />" 
                            + exception.getMessage() + "<br />Exiting "
                                    + "program...</html>", "Error!",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            finally
            {
                isConnected = false;
            }
    }
    
    // Necessary overrides for the WindowListener implementation. For this
    // program, the only method we are truely overriding would be the closing
    // of the window. I force this to disconnect from the database and then
    // exit to avoid leaving the connection open despite the program being
    // closed. Although generally Java would still close it upon close, this
    // manual way ensures this occurs.
    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) { GUI.dispose();}

    @Override
    public void windowClosed(WindowEvent e) 
    {
        disconnectFromDatabase();
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) { }

    @Override
    public void windowDeiconified(WindowEvent e) { }

    @Override
    public void windowActivated(WindowEvent e) { }

    @Override
    public void windowDeactivated(WindowEvent e) { }
    
    // Simply changes the color of the foreground upon entering the components
    // that wish to have a highlight effect and then also resets it back to its
    // original color upon the mouse exiting the component.
    private class HighlightEffect implements MouseListener
    {

        @Override
        public void mouseClicked(MouseEvent e) { }

        @Override
        public void mousePressed(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) 
        {
            e.getComponent().setForeground(CLR_HIGHLIGHTED_TEXT);
        }

        @Override
        public void mouseExited(MouseEvent e) 
        {
            e.getComponent().setForeground(CLR_BUTTON_TEXT);
        }
        
    }
    
    /**
     * Entry point of the program.
     * @param args The command line arguments.
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> 
        {
            GUI = new MainWindow();
            GUI.setVisible(true);
            GUI.addWindowListener(GUI);
        });
    }
}
