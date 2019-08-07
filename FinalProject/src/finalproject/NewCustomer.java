package finalproject;

// Necessary imports
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *  This is the form to validate and add a new customer to the database.
 * @author David Mills
 */
public final class NewCustomer extends JFrame implements WindowListener
{
    private final MainWindow previousWindow;
    private final JPanel pnlAll;
    private final JLabel lblWelcome;
    private final JLabel lblFirst;
    private final JLabel lblLast;
    private final JLabel lblStreet;
    private final JLabel lblCity;
    private final JLabel lblState;
    private final JLabel lblZip;
    private final JLabel lblPhone;
    private final JLabel lblEmail;
    private final JTextField txtFirst;
    private final JTextField txtLast;
    private final JTextField txtStreet;
    private final JTextField txtCity;
    private final JTextField txtState;
    private final JTextField txtZip;
    private final JTextField txtPhone;
    private final JTextField txtEmail;
    private final JButton btnAdd;
    
    public NewCustomer(MainWindow previousWindow, Connection database)
    {
        this.previousWindow = previousWindow;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("Coffee Shop - Add Customer");
        UIManager.put("OptionPane.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("Panel.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", MainWindow.CLR_TEXT);
        UIManager.put("Button.background", MainWindow.CLR_LOGO_BACKGROUND);
        UIManager.put("Button.foreground", MainWindow.CLR_TEXT);
        
        lblWelcome = new JLabel("<html><h1>Add Customer Form</h1></html>");
        lblFirst = new JLabel("First Name:");
        lblLast = new JLabel("Last Name:");
        lblStreet = new JLabel("Street:");
        lblCity = new JLabel("City:");
        lblState = new JLabel("State:");
        lblZip = new JLabel("Zip Code:");
        lblPhone = new JLabel("Phone Number:");
        lblEmail = new JLabel("Email Address:");
        
        txtFirst = new JTextField(15);
        txtLast = new JTextField(10);
        txtStreet = new JTextField(20);
        txtCity = new JTextField(15);
        txtState = new JTextField(2);
        txtZip = new JTextField(7);
        txtPhone = new JTextField(10);
        txtEmail = new JTextField(20);
        
        btnAdd = new JButton("Verify and Add");
        
        pnlAll = new JPanel();
        pnlAll.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        pnlAll.add(lblWelcome, gbc);
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlAll.add(lblFirst, gbc);
        gbc.gridx = 1;
        pnlAll.add(txtFirst, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        pnlAll.add(lblLast, gbc);
        gbc.gridx = 4;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        pnlAll.add(txtLast, gbc);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        pnlAll.add(lblStreet, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        pnlAll.add(txtStreet, gbc);
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        pnlAll.add(lblCity, gbc);
        gbc.gridx = 1;
        pnlAll.add(txtCity, gbc);
        gbc.gridx = 2;
        pnlAll.add(lblState, gbc);
        gbc.gridx = 3;
        pnlAll.add(txtState, gbc);
        gbc.gridx = 4;
        pnlAll.add(lblZip, gbc);
        gbc.gridx = 5;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        pnlAll.add(txtZip, gbc);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        pnlAll.add(lblPhone, gbc);
        gbc.gridx = 1;
        pnlAll.add(txtPhone, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        pnlAll.add(lblEmail, gbc);
        gbc.gridx = 4;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        pnlAll.add(txtEmail, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 5;
        pnlAll.add(btnAdd, gbc);
        
        btnAdd.addActionListener((ActionEvent) -> 
        {
            for (Component component : pnlAll.getComponents())
                if (component instanceof JTextField)
                    if (((JTextField)component).getText().isEmpty())
                    {
                        JOptionPane.showConfirmDialog(getContentPane(),
                                "All fields are required!", "Error!",
                                JOptionPane.DEFAULT_OPTION, 
                                JOptionPane.ERROR_MESSAGE);
                        ((JTextField) component).grabFocus();
                        return;
                    }
            if (!txtFirst.getText()
                    .matches("[A-Z][a-zA-Z][^#&<>\"~;$^%{}`?']{1,24}$"))
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "First name in invalid format! (Must be capitalized or"
                                + " too long)",
                        "Error!", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.ERROR_MESSAGE);
                txtFirst.grabFocus();
                return;
            }
            if (!txtLast.getText()
                    .matches("[A-Z][a-zA-Z][^#&<>\"~;$^%{}`?']{1,24}$"))
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Last name in invalid format! (Must be capitalized or"
                                + " too long)",
                        "Error!", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.ERROR_MESSAGE);
                txtLast.grabFocus();
                return;
            }
            if (!txtStreet.getText()
                    .matches("[^&<>\"~;$^%{}`?']{1,50}$"))
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Street name contains illegal characters or too long!"
                                + " Illegal"
                                + " characters include: (^$<>\"~;$^{}?`)",
                        "Error!", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.ERROR_MESSAGE);
                txtStreet.grabFocus();
                return;
            }
            if (!txtCity.getText()
                    .matches("[A-Z][a-zA-Z][^#&<>\"~;$^%{}`?']{1,24}$"))
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "City in invalid format! (Must be capitalized or too"
                                + " long)",
                        "Error!", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.ERROR_MESSAGE);
                txtCity.grabFocus();
                return;
            }
            if (!txtState.getText().matches("[A-Za-z][A-Za-z]$"))
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "State must be in valid format! (e.g. AZ)",
                        "Error!", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.ERROR_MESSAGE);
                txtState.grabFocus();
                return;
            }
            if (!txtZip.getText().matches("\\d{5}$"))
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Zip Code must be 5 digits only!",
                        "Error!", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.ERROR_MESSAGE);
                txtState.grabFocus();
                return;
            }
            if (!(txtPhone.getText().matches("^\\(\\d{3}\\)\\s\\d{3}-\\d{4}$") 
                    || txtPhone.getText()
                            .matches("^\\(\\d{3}\\)-\\d{3}-\\d{4}$") || 
                    txtPhone.getText().matches("^\\d{3}\\s\\d{3}\\s\\d{4}$") 
                    || txtPhone.getText().matches("^\\d{3}-\\d{3}-\\d{4}$") || 
                    txtPhone.getText().matches("(?<!\\d)\\d{10}(?!\\d)")))
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "<html>Phone number is in invalid format! "
                                + "Try one of the following formats:<br />"
                                + "(xxx) xxx-xxxx, (xxx)-xxx-xxxx,"
                                + " xxx xxx xxxx, xxx-xxx-xxxx, or xxxxxxxxxx"
                                + "</html>",
                        "Error!", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.ERROR_MESSAGE);
                txtPhone.grabFocus();
                return;
            }
            if (!txtEmail.getText().matches(".+@.+\\..+") 
                    || txtEmail.getText().length() > 50)
            {
                JOptionPane.showConfirmDialog(getContentPane(),
                        "Email in invalid format or too long!",
                        "Error!", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.ERROR_MESSAGE);
                txtEmail.grabFocus();
                return;
            }
            String first, last, street, city, state, zip, phone, email;
            first = txtFirst.getText();
            last = txtLast.getText();
            street = txtStreet.getText();
            city = txtCity.getText();
            state = txtState.getText().toUpperCase();
            zip = txtZip.getText();
            phone = txtPhone.getText().replace(" ", "").replace("-", "")
                    .replace("(", "").replace(")", "");
            email = txtEmail.getText();
            try (Statement insert = database
                .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_UPDATABLE))
            {
                insert.execute("INSERT INTO Customers(First_Name, Last_Name,"
                        + " Street, City, State, Zip_Code, Phone_Number, "
                        + "Email_Address)" + " VALUES ('" + first + "', '" 
                        + last + "', '" + street + "', '" + city + "', '" 
                        + state + "', " + Integer.parseInt(zip) + ", " 
                        + Long.parseLong(phone) + ", '" + email + "')");
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
        
        for (Component component : pnlAll.getComponents())
        {
            if (component instanceof JLabel)
            {
                component.setForeground(MainWindow.CLR_TEXT);
                component.setBackground(MainWindow.CLR_BACKGROUND);
                ((JLabel)component).setOpaque(true);
            }
            else if (component instanceof JTextField)
            {
                component.setForeground(new Color(235, 235, 235));
                component.setBackground(MainWindow.CLR_PANEL_BACKGROUND);
                ((JTextField)component).setCaretColor(MainWindow.CLR_TEXT);
                ((JTextField)component).setBorder(BorderFactory
                        .createLineBorder(MainWindow.CLR_LOGO_BACKGROUND, 2));
            }
            else if (component instanceof JButton)
            {
                component.setForeground(MainWindow.CLR_TEXT);
                component.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
            }
        }
        
        pnlAll.setBackground(MainWindow.CLR_BACKGROUND);
        pnlAll.setBorder(BorderFactory.createLineBorder(
                MainWindow.CLR_LOGO_BACKGROUND, 5));
        add(pnlAll);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
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
