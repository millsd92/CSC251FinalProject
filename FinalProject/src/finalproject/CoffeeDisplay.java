package finalproject;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author David Mills
 */
public final class CoffeeDisplay extends JFrame implements WindowListener
{
    private final MainWindow previousWindow;
    private final JLabel lblTop;
    private final JPanel pnlTop;
    private final JPanel pnlTable;
    
    public CoffeeDisplay(MainWindow previousWindow, Connection database)
    {
        this.previousWindow = previousWindow;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("Coffee Shop - Display Coffees");
        UIManager.put("OptionPane.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("Panel.background", MainWindow.CLR_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", MainWindow.CLR_TEXT);
        UIManager.put("Button.background", MainWindow.CLR_LOGO_BACKGROUND);
        UIManager.put("Button.foreground", MainWindow.CLR_TEXT);
        
        pnlTable = new JPanel();
        pnlTable.setBackground(MainWindow.CLR_BACKGROUND);
        try (Statement displayCustomers = database.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet results = 
                        displayCustomers
                                .executeQuery("SELECT Name, Description, Price,"
                                        + " Number_In_Stock FROM Coffees");)
        {
            ResultSetMetaData metaData = results.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            results.last();
            int numberOfRows = results.getRow();
            results.first();
            pnlTable.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.ipadx = 30;
            gbc.insets = new Insets(2, 2, 0, 0);
            for (int i = 1; i <= numberOfColumns; i++)
            {
                gbc.gridx = i - 1;
                pnlTable.add(new Header(metaData.getColumnName(i)), gbc);
            }
            gbc.ipady = 30;
            for (int i = 1; i <= numberOfRows; i++)
            {
                gbc.gridy = i + 1;
                for (int j = 1; j <= numberOfColumns; j++)
                {
                    gbc.gridx = j - 1;
                    String text = results.getObject(j).toString();
                    if (metaData.getColumnName(j).equals("PRICE"))
                    {
                        NumberFormat formatter 
                                = NumberFormat.getCurrencyInstance();
                        text = formatter.format(Double.parseDouble(text));
                    }
                    if (i % 2 == 0)
                        pnlTable.add(new Cell(text, true), gbc);
                    else
                        pnlTable.add(new Cell(text, false), gbc);
                }
                results.next();
            }
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
        
        pnlTable.setBorder(BorderFactory.createLineBorder(Color.black, 5));
        
        lblTop = new JLabel("<html><h1>Available Coffees</h1></html>");
        lblTop.setForeground(MainWindow.CLR_TEXT);
        lblTop.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        lblTop.setOpaque(true);
        pnlTop = new JPanel();
        pnlTop.add(lblTop);
        pnlTop.setBackground(MainWindow.CLR_LOGO_BACKGROUND);
        
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(pnlTop);
        add(pnlTable);
        setBackground(MainWindow.CLR_BACKGROUND);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(getSize());
        setMaximumSize(getSize());
        setResizable(false);
    }
    
    private final class Header extends JPanel
    {
        private final JLabel inside;
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
