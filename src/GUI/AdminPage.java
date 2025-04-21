package GUI;

import javax.swing.*;
import Classes.Admin;
import GUI.stylesAndComponents.AsidePanel;
import GUI.stylesAndComponents.ClickableLabel;
import GUI.stylesAndComponents.StyledPanel;

import java.awt.*;
import java.awt.event.*;

public class AdminPage extends JPanel {
    Admin admin;
    MainFrame mainFrame;

    CardLayout cardLayout;

    public AdminPage(MainFrame mainframe, Admin admin) {
        this.mainFrame = mainframe;
        this.admin = admin;

        // Set the background color of this panel to a darker shade
        setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout());

        // Sidebar (aside)
        AsidePanel asidePanel = new AsidePanel();
        asidePanel.setLayout(new GridBagLayout());  // Change to GridBagLayout for flexibility

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 20, 10, 10); // Spacing around components
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row counter
        int row = 0;

        // Create ClickableLabels for Admin actions
        ClickableLabel viewUsersLabel = new ClickableLabel("View Users");
        ClickableLabel viewCustomersLabel = new ClickableLabel("View Customers");
        ClickableLabel viewPublishersLabel = new ClickableLabel("View Publishers");
        ClickableLabel viewAdminsLabel = new ClickableLabel("View Admins");
        ClickableLabel manageBooksLabel = new ClickableLabel("Manage Books");
        ClickableLabel editProfileLabel = new ClickableLabel("Edit Profile");

        // Set preferred sizes for all labels
        ClickableLabel[] labels = {
            viewUsersLabel, viewCustomersLabel, viewPublishersLabel, 
            viewAdminsLabel, manageBooksLabel, editProfileLabel
        };

        // Add labels to the sidebar
        for (ClickableLabel label : labels) {
            label.setPreferredSize(new Dimension(200, 60));
            gbc.gridy = row++;
            asidePanel.add(label, gbc);
        }

        // ActionListener for ClickableLabels
        ActionListener labelClickListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClickableLabel clickedLabel = (ClickableLabel) e.getSource();
                String labelText = clickedLabel.getText();

                // Handle actions based on label text
                switch (labelText) 
                {
                    case "View Users":
                        // Handle action for "View Users"
                        System.out.println("View Users clicked");
                        break;

                    case "View Customers":
                        // Handle action for "View Customers"
                        System.out.println("View Customers clicked");
                        break;

                    case "View Publishers":
                        // Handle action for "View Publishers"
                        System.out.println("View Publishers clicked");
                        break;

                    case "View Admins":
                        // Handle action for "View Admins"
                        System.out.println("View Admins clicked");
                        break;

                    case "Manage Books":
                        // Handle action for "Manage Books"
                        System.out.println("Manage Books clicked");
                        break;

                    case "Edit Profile":
                        // Handle action for "Edit Profile"
                        System.out.println("Edit Profile clicked");
                        break;
                        
                    default:
                        break;
                }
            }
        };

        // Attach the listener to each label
        for (ClickableLabel label : labels) {
            label.addActionListener(labelClickListener);
        }

        // Main content with gradient background
        cardLayout = new CardLayout();
        StyledPanel mainPanel = new StyledPanel();
        mainPanel.setLayout(cardLayout);

        // Split Pane (Aside on the left, Main on the right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, asidePanel, mainPanel);
        splitPane.setDividerLocation(200); // Set reasonable width for sidebar
        splitPane.setDividerSize(5);
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER); // ✅ Correct position
    }
}
