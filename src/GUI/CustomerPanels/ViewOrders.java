package GUI.CustomerPanels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import Classes.Books;
import Classes.Customers;
import GUI.MainFrame;
import GUI.stylesAndComponents.Labels;
import GUI.stylesAndComponents.StyledPanel;
import GUI.stylesAndComponents.Buttons;

public class ViewOrders extends JPanel {
    private MainFrame mainFrame;
    private Customers customer;

    public ViewOrders(MainFrame mainFrame, Customers customer) 
    {
        this.mainFrame = mainFrame;
        this.customer = customer;
    }
}
