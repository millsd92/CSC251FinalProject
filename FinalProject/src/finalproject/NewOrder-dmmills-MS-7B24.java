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
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
 *  This is the form that will be validated and added to the database as an
 * order for an existing customer.
 * @author David Mills
 */
public final class NewOrder extends JFrame implements WindowListener
{
    private final MainWindow previousWindow;
    private final JLabel lblTop;
    private final JLabel lblEnter;
    private final JLabel lblMiddle;
    private final JLabel lblFilter;
    private final JLabel lblQuantity;
    private final JLabel lblType;
    private final JLabel lblPrice;
    private final JTextField txtFilter;
    private final JTextField txtQuantity;
    private final JTextField txtPrice;
    private final JComboBox cbxCoffees;
    private final JButton btnAdd;
    private final JButton btnCalcDefault;
    private final JRadioButton radFirst;
    private final JRadioButton radLast;
    private final JRadioButton radPhone;
    private final JRadioButton radEmail;
    private final JPanel pnlTop;
    private final JPanel pnlSearch;
    private final JPanel pnlTable;
    private final JPanel pnlForm;
    private boolean one = false;
    private int customerID;
    
    public NewOrder(MainWindow previousWindow, Connection database)
    {
        this.previousWindow = previousWindow;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("Coffee Shop - New Order");
        UIManager.put("OptionPane.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("Panel.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", MainWindow.CLR_TEXT);
        UIManager.put("Button.background", MainWindow.CLR_LOGO_BACKGROUND);
        UIManager.put("Button.foreground", MainWindow.CLR_TEXT);
        
        List<String> coffees = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        
        try (Statement getCoffees = database.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet results = getCoffees.executeQuery("SELECT Coffee_ID, "
                        + "Name, Price"
                        + " FROM Coffees"))
        {
            results.first();
            do
            {
                ids.add(results.getInt((1)));
                coffees.add(results.getString(2));
                prices.add(results.getDouble(3));
            }
            while (results.next());
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
        
        cbxCoffees = new JComboBox(coffees.toArray());
        cbxCoffees.setSelectedIndex(0);
        cbxCoffees.setMaximumRowCount(5);
        cbxCoffees.setBackground(MainWindow.CLR_BACKGROUND);
        cbxCoffees.setForeground(MainWindow.CLR_TEXT);
        cbxCoffees.setEditable(false);
        cbxCoffees.setOpaque(true);
        
        pnlTable = new JPanel();
        pnlTable.setBackground(MainWindow.CLR_BACKGROUND);
        
        pnlTable.setBorder(BorderFactory.createLineBorder(Color.black, 5));
        
        lblTop = new JLabel("<html><style>h1 {text-align: center;}</style>"
                + "<h1>New Order Form</h1><br /></html>");
        lblTop.setForeground(MainWindow.CLR_TEXT);
        lblTop.setBackground(MainWindow.CLR_BACKGROUND);
        lblTop.setOpaque(true);
        
        lblEnter = new JLabel("<html><h2>Enter Information</h2></html>");
        lblEnter.setForeground(MainWindow.CLR_TEXT);
        lblEnter.setBackground(MainWindow.CLR_BACKGROUND);
        lblEnter.setOpaque(true);
        
        lblMiddle = new JLabel("<html><h2>Choose Customer</h2></html>");
        lblMiddle.setForeground(MainWindow.CLR_TEXT);
        lblMiddle.setBackground(MainWindow.CLR_BACKGROUND);
        lblMiddle.setOpaque(true);
        
        lblQuantity = new JLabel("Quantity:");
        lblQuantity.setForeground(MainWindow.CLR_TEXT);
        lblQuantity.setBackground(MainWindow.CLR_BACKGROUND);
        lblQuantity.setOpaque(true);
        
        lblType = new JLabel("Coffee Type:");
        lblType.setForeground(MainWindow.CLR_TEXT);
        lblType.setBackground(MainWindow.CLR_BACKGROUND);
        lblType.setOpaque(true);
        
        lblPrice = new JLabel("Price:");
        lblPrice.setForeground(MainWindow.CLR_TEXT);
        lblPrice.setBackground(MainWindow.CLR_BACKGROUND);
        lblPrice.setOpaque(true);
        
        lblFilter = new JLabel("Filter By:");
        lblFilter.setForeground(MainWindow.CLR_TEXT);
        lblFilter.setBackground(MainWindow.CLR_BACKGROUND);
        lblFilter.setOpaque(true);
        
        txtQuantity = new JTextField(2);
        txtQuantity.setForeground(new Color(235, 235, 235));
        txtQuantity.setCaretColor(MainWindow.CLR_TEXT);
        txtQuantity.setBorder(BorderFactory.createLineBorder(
            Color.black, 2));
        txtQuantity.setBackground(MainWindow.CLR_PANEL_BACKGROUND);
        
        txtPrice = new JTextField(4);
        txtPrice.setForeground(new Color(235, 235, 235));
        txtPrice.setCaretColor(MainWindow.CLR_TEXT);
        txtPrice.setBorder(BorderFactory.createLineBorder(
            Color.black, 2));
        txtPrice.setBackground(MainWindow.CLR_PANEL_BACKGROUND);
        
        txtFilter = new JTextField();
        txtFilter.setOpaque(true);
        
        btnCalcDefault = new JButton("Calculate Default Cost");
        btnCalcDefault.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        btnCalcDefault.setForeground(MainWindow.CLR_TEXT);
        btnCalcDefault.addActionListener((ActionEvent) -> 
        {
            if (txtQuantity.getText().isEmpty())
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Quantity is required!", "Error!", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            else
            {
                try
                {
                    int quantity = Integer.parseInt(txtQuantity.getText());
                    txtPrice.setText(String.format("$%.2f", 
                            (prices.get(cbxCoffees.getSelectedIndex()) 
                                    * quantity)));
                }
                catch (NumberFormatException exception)
                {
                    JOptionPane.showConfirmDialog(getContentPane(), 
                            "Quantity must be an integer!", "Error!",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        pnlForm = new JPanel();
        pnlForm.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        pnlForm.add(lblEnter, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(lblType, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlForm.add(cbxCoffees, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(lblQuantity, gbc);
        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlForm.add(txtQuantity, gbc);
        gbc.gridx = 4;
        gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(lblPrice, gbc);
        gbc.gridx = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        pnlForm.add(txtPrice, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        pnlForm.add(btnCalcDefault, gbc);
        gbc.gridy = 3;
        pnlForm.add(lblMiddle, gbc);
        pnlForm.setBackground(MainWindow.CLR_BACKGROUND);
        
        pnlTop = new JPanel();
        pnlTop.add(lblTop);
        pnlTop.setBackground(MainWindow.CLR_BACKGROUND);
        
        radFirst = new JRadioButton("First Name", true);
        radFirst.addItemListener(new ValueChanged());
        radLast = new JRadioButton("Last Name", false);
        radLast.addItemListener(new ValueChanged());
        radPhone = new JRadioButton("Phone Number", false);
        radPhone.addItemListener(new ValueChanged());
        radEmail = new JRadioButton("Email Address", false);
        radEmail.addItemListener(new ValueChanged());
        
        btnAdd = new JButton("Add Order");
        btnAdd.setAlignmentX(CENTER_ALIGNMENT);
        btnAdd.addActionListener((ActionEvent) -> 
        {
            if (!one)
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Please filter results to only one customer.",
                        "Error!", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtQuantity.getText().isEmpty())
            {
                JOptionPane.showConfirmDialog(getContentPane(), 
                        "Quantity is required!", "Error!", 
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                txtQuantity.grabFocus();
                return;
            }
            if (txtPrice.getText().isEmpty())
            {
                JOptionPane.showConfirmDialog(getContentPane(), 
                        "Price is required!", "Error!", 
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                txtQuantity.grabFocus();
                return;
            }
            String quantity = txtQuantity.getText(), price = txtPrice.getText();
            if (price.startsWith("$"))
                price = price.substring(1);
            int quan;
            double pri;
            try 
            {
                quan = Integer.parseInt(quantity);
            }
            catch (NumberFormatException exception)
            {
                JOptionPane.showConfirmDialog(getContentPane(), 
                        "Quantity must be an integer!", "Error!",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                txtQuantity.grabFocus();
                return;
            }
            try
            {
                pri = Double.parseDouble(price);
            }
            catch (NumberFormatException exception)
            {
                JOptionPane.showConfirmDialog(getContentPane(), 
                        "Price must be double!", "Error!",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                txtPrice.grabFocus();
                return;
            }
            int id = ids.get(cbxCoffees.getSelectedIndex());
            try (Statement insert = database.createStatement(ResultSet
                    .TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE))
            {
                insert.execute("INSERT INTO Orders(Customer_ID, Coffee_ID, "
                        + "Quantity, Total) VALUES (" + customerID + ", " 
                        + id + ", " + quan + ", " + pri + ")");
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Successfully added to database!", "Success!",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
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
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(radFirst);
        group.add(radLast);
        group.add(radPhone);
        group.add(radEmail);
        
        pnlSearch = new JPanel();
        pnlSearch.setLayout(new BoxLayout(pnlSearch, BoxLayout.X_AXIS));
        pnlSearch.add(lblFilter);
        pnlSearch.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSearch.add(radFirst);
        pnlSearch.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSearch.add(radLast);
        pnlSearch.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSearch.add(radPhone);
        pnlSearch.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSearch.add(radEmail);
        pnlSearch.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSearch.add(txtFilter);
        pnlSearch.setBackground(MainWindow.CLR_BACKGROUND);
        pnlSearch.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        for (Component component : pnlSearch.getComponents())
        {
            component.setBackground(MainWindow.CLR_BACKGROUND);
            component.setForeground(MainWindow.CLR_TEXT);
            if (component instanceof JRadioButton)
                ((JRadioButton)component).setOpaque(true);
        }
        
        btnAdd.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        
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
                showResults(database, where, what, pnlTable);
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
        add(pnlForm);
        add(pnlTable);
        add(pnlSearch);
        add(btnAdd);
        add(Box.createRigidArea(new Dimension(0, 20)));
        showResults(database, "First_Name", "", pnlTable);
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
            String filter, JPanel thePanel)
    {
        try (Statement displayCustomers = database.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);)
        {
            ResultSet results;
                results = displayCustomers
                    .executeQuery("SELECT Customer_ID, First_Name, Last_Name,"
                            + " Phone_Number, Email_Address FROM Customers "
                        + "WHERE " + where + " LIKE '%" 
                        + filter + "%'");
            thePanel.removeAll();
            thePanel.repaint();
            ResultSetMetaData metaData = results.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            results.last();
            int numberOfRows = results.getRow();
            results.first();
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
                    if (metaData.getColumnName(j).equals("PHONE_NUMBER"))
                        text = "(" + text.substring(0, 3) + ") " 
                                + text.substring(3, 6) + "-" 
                                + text.substring(6);
                    else if (metaData.getColumnName(j).equals("CREDIT_LIMIT") 
                            || metaData.getColumnName(j).equals("Total"))
                    {
                        NumberFormat formatter 
                                = NumberFormat.getCurrencyInstance();
                        text = formatter.format(Double.parseDouble(text));
                    }
                    if (i % 2 == 0)
                        thePanel.add(new Cell(text, true), gbc);
                    else
                        thePanel.add(new Cell(text, false), gbc);
                }
                results.next();
            }
            if (numberOfRows == 1)
            {
                one = true;
                results.first();
                customerID = results.getInt(1);
            }
            else
                one = false;
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
