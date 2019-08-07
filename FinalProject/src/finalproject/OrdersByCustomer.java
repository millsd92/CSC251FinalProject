package finalproject;

// Necessary imports
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *  This is the form that allows the user to filter though and find orders by
 * customer data.
 * @author David Mills
 */
public final class OrdersByCustomer extends JFrame implements WindowListener
{
    private final MainWindow previousWindow;
    private final JLabel lblTop;
    private final JLabel lblMiddle;
    private final JLabel lblFilter;
    private final JTextField txtFilter;
    private final JButton btnCheck;
    private final JRadioButton radFirst;
    private final JRadioButton radLast;
    private final JRadioButton radPhone;
    private final JRadioButton radEmail;
    private final JPanel pnlTop;
    private final JPanel pnlSearch;
    private final JPanel pnlTable;
    private final JPanel pnlMiddle;
    private final JPanel pnlResults;
    private boolean one = false;
    private int customerID;
    
    public OrdersByCustomer(MainWindow previousWindow, Connection database)
    {
        this.previousWindow = previousWindow;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("Coffee Shop - Display Orders by Customer");
        UIManager.put("OptionPane.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("Panel.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", MainWindow.CLR_TEXT);
        UIManager.put("Button.background", MainWindow.CLR_LOGO_BACKGROUND);
        UIManager.put("Button.foreground", MainWindow.CLR_TEXT);
        
        pnlTable = new JPanel();
        pnlTable.setBackground(MainWindow.CLR_BACKGROUND);
        
        pnlTable.setBorder(BorderFactory.createLineBorder(Color.black, 5));
        
        lblTop = new JLabel("<html><style>h1, h2 {text-align: center;}</style>"
                + "<h1>Orders by Customer</h1><br />"
                + "<h2>Choose Customer</h2></html>");
        lblTop.setForeground(MainWindow.CLR_TEXT);
        lblTop.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        lblTop.setOpaque(true);
        
        lblMiddle = new JLabel("<html><h2>Results</h2></html>");
        lblMiddle.setForeground(MainWindow.CLR_TEXT);
        lblMiddle.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        lblMiddle.setOpaque(true);
        
        lblFilter = new JLabel("Filter By:");
        lblFilter.setForeground(MainWindow.CLR_TEXT);
        lblFilter.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        lblFilter.setOpaque(true);
        
        txtFilter = new JTextField();
        txtFilter.setOpaque(true);
        
        pnlMiddle = new JPanel();
        pnlMiddle.add(lblMiddle);
        pnlMiddle.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        pnlMiddle.setVisible(false);
        
        pnlTop = new JPanel();
        pnlTop.add(lblTop);
        pnlTop.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        
        pnlResults = new JPanel();
        pnlResults.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        pnlResults.setBorder(BorderFactory.createLineBorder(Color.black, 5));
        
        radFirst = new JRadioButton("First Name", true);
        radFirst.addItemListener(new ValueChanged());
        radLast = new JRadioButton("Last Name", false);
        radLast.addItemListener(new ValueChanged());
        radPhone = new JRadioButton("Phone Number", false);
        radPhone.addItemListener(new ValueChanged());
        radEmail = new JRadioButton("Email Address", false);
        radEmail.addItemListener(new ValueChanged());
        
        btnCheck = new JButton("Get Orders for This Customer");
        btnCheck.addActionListener((ActionEvent) -> 
        {
            if (!one)
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Please filter results to only one customer.",
                        "Error!", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE);
            else
            {
                showResults(database, null, null, true, pnlResults);
                pack();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(radFirst);
        group.add(radLast);
        group.add(radPhone);
        group.add(radEmail);
        
        pnlSearch = new JPanel();
        pnlSearch.setLayout(new BoxLayout(pnlSearch, BoxLayout.X_AXIS));
        pnlSearch.add(lblFilter);
        pnlSearch.add(Box.createRigidArea(new Dimension(30, 0)));
        pnlSearch.add(radFirst);
        pnlSearch.add(Box.createRigidArea(new Dimension(30, 0)));
        pnlSearch.add(radLast);
        pnlSearch.add(Box.createRigidArea(new Dimension(30, 0)));
        pnlSearch.add(radPhone);
        pnlSearch.add(Box.createRigidArea(new Dimension(30, 0)));
        pnlSearch.add(radEmail);
        pnlSearch.add(Box.createRigidArea(new Dimension(30, 0)));
        pnlSearch.add(txtFilter);
        pnlSearch.add(Box.createRigidArea(new Dimension(30, 0)));
        pnlSearch.add(btnCheck);
        pnlSearch.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        pnlSearch.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        for (Component component : pnlSearch.getComponents())
        {
            component.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
            component.setForeground(MainWindow.CLR_TEXT);
            if (component instanceof JRadioButton)
                ((JRadioButton)component).setOpaque(true);
        }
        
        btnCheck.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        
        txtFilter.setBackground(MainWindow.CLR_PANEL_BACKGROUND);
        txtFilter.setForeground(new Color(235, 235, 235));
        txtFilter.setCaretColor(MainWindow.CLR_TEXT);
        txtFilter.setBorder(BorderFactory.createLineBorder(
                Color.black, 2));
        txtFilter.getDocument().addDocumentListener(new DocumentListener()
        {
            public void update(DocumentEvent e)
            {
                String where = "LOWER(First_Name)", what = txtFilter.getText();
                if (radLast.isSelected())
                    where = "LOWER(Last_Name)";
                if (radPhone.isSelected())
                    where = "CAST(Phone_Number AS CHAR(10))";
                if (radEmail.isSelected())
                    where = "LOWER(Email_Address)";
                what = what.replaceAll("[^a-zA-Z0-9@]", "").toLowerCase();
                showResults(database, where, what, false, pnlTable);
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) { update(e); }

            @Override
            public void removeUpdate(DocumentEvent e) { update(e); }

            @Override
            public void changedUpdate(DocumentEvent e) { update(e); }
        });
        
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(pnlTop);
        add(pnlTable);
        add(pnlSearch);
        add(pnlMiddle);
        add(pnlResults);
        showResults(database, "First_Name", "", false, pnlTable);
        pnlTable.setMinimumSize(pnlTable.getSize());
        pnlTable.setPreferredSize(pnlTable.getSize());
        setBackground(MainWindow.CLR_BACKGROUND);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private class ValueChanged implements ItemListener
    {
        @Override
        public void itemStateChanged(ItemEvent e) { txtFilter.setText("");}
    }
    
    private void showResults(Connection database, String where,
            String filter, boolean forOne, JPanel thePanel)
    {
        try (Statement displayCustomers = database.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);)
        {
            ResultSet results;
            if (!forOne)
                results = displayCustomers
                    .executeQuery("SELECT * FROM Customers "
                        + "WHERE " + where + " LIKE '%" 
                        + filter + "%'");
            else
                results = displayCustomers.executeQuery("SELECT Order_ID,"
                        + " First_Name, Last_Name, Name, Quantity, Total "
                        + "FROM Orders INNER JOIN Customers ON "
                        + "Orders.Customer_ID = Customers.Customer_ID INNER"
                        + " JOIN Coffees ON Orders.Coffee_ID = "
                        + "Coffees.Coffee_ID WHERE Orders.Customer_ID = " 
                        + customerID);
            thePanel.removeAll();
            thePanel.repaint();
            ResultSetMetaData metaData = results.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            results.last();
            int numberOfRows = results.getRow();
            results.first();
            if (numberOfRows == 1 && !forOne)
            {
                one = true;
                customerID = results.getInt(1);
            }
            else if (!forOne)
                one = false;
            thePanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.ipadx = 30;
            gbc.insets = new Insets(2, 2, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            for (int i = 2; i <= numberOfColumns; i++)
            {
                gbc.gridx = i - 1;
                thePanel.add(new Header(metaData.getColumnName(i)), gbc);
            }
            gbc.ipady = 30;
            for (int i = 1; i <= numberOfRows; i++)
            {
                gbc.gridy = i + 1;
                for (int j = 2; j <= numberOfColumns; j++)
                {
                    gbc.gridx = j - 1;
                    String text = results.getObject(j).toString();
                    switch (metaData.getColumnName(j))
                    {
                        case "PHONE_NUMBER":
                            text = "(" + text.substring(0, 3) + ") "
                                    + text.substring(3, 6) + "-"
                                    + text.substring(6);
                            break;
                        case "CREDIT_LIMIT":
                        case "TOTAL":
                            NumberFormat formatter
                                    = NumberFormat.getCurrencyInstance();
                            text = formatter.format(Double.parseDouble(text));
                            break;
                        case "QUANTITY":
                            text = text.substring(0, text.indexOf('.'));
                            break;
                        default:
                            break;
                    }
                    if (i % 2 == 0)
                        thePanel.add(new Cell(text, true), gbc);
                    else
                        thePanel.add(new Cell(text, false), gbc);
                }
                results.next();
            }
            if (!forOne)
            {
                pnlMiddle.setVisible(false);
                pnlResults.setVisible(false);
            }
            else
            {
                if (numberOfRows > 0)
                {
                    pnlMiddle.setVisible(true);
                    pnlResults.setVisible(true);
                }
                else
                    JOptionPane.showConfirmDialog(getContentPane(),
                            "No orders for this customer were found!", "Error!",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.ERROR_MESSAGE);
            }
            results.close();
            pack();                        
        }
        catch (SQLException exception)
        {
            JOptionPane.showConfirmDialog(getContentPane(),
                    "<html>The following error occurred:<br />" 
                            + exception.getMessage() + "<br />"
                                    + "Closing this window...</html>", "Error!",
                            JOptionPane.PLAIN_MESSAGE,
                            JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    private final class Header extends JPanel
    {
        public final JLabel inside;
        public Header(String text)
        {
            String display = text.toLowerCase();
            if (display.equals("name"))
                display = "Type of Coffee";
            if (display.contains("_"))
                display = display.replace("_", " ");
            String[] words = display.split(" ");
            display = "";
            for (String word : words)
            {
                display += Character.toUpperCase(word.charAt(0)) 
                        + word.substring(1) + " ";
            }
            inside = new JLabel();
            inside.setText("<html><h2>" + display + "</h2></html>");
            inside.setOpaque(true);
            inside.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
            inside.setForeground(MainWindow.CLR_TEXT);
            setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED,
                    MainWindow.CLR_PANEL_BACKGROUND,
                    MainWindow.CLR_LOGO_BACKGROUND));
            inside.setAlignmentX(Component.CENTER_ALIGNMENT);
            setBackground(MainWindow.CLR_LOGO_BACKGROUND);
            add(inside);
            pack();
        }
    }
    
    private final class Cell extends JLabel
    {
        public Cell(String text, boolean accent)
        {
            setText("<html>&nbsp;" + text + "</html>");
            if (accent)
                setBackground(MainWindow.CLR_BACKGROUND);
            else
                setBackground(MainWindow.CLR_PANEL_BACKGROUND);
            setForeground(MainWindow.CLR_TEXT);
            setOpaque(true);
            setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED,
                    MainWindow.CLR_PANEL_BACKGROUND,
                    MainWindow.CLR_LOGO_BACKGROUND));
        }
    }

    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowClosing(WindowEvent e) { dispose(); }

    @Override
    public void windowClosed(WindowEvent e) { previousWindow.setVisible(true); }

    @Override
    public void windowIconified(WindowEvent e) { }

    @Override
    public void windowDeiconified(WindowEvent e) { }

    @Override
    public void windowActivated(WindowEvent e) { }

    @Override
    public void windowDeactivated(WindowEvent e) { }
    
}
