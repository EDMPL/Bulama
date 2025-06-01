import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UserInterface {
    private JPanel ui;
    private JTextField promptField;
    private JTextField response;
    private JButton submitButton;

    public UserInterface() {
        ui = new JPanel();
        ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));
        
        // Create prompt field with fixed size
        promptField = new JTextField();
        promptField.setPreferredSize(new Dimension(400, 25));
        promptField.setMaximumSize(new Dimension(400, 25));
        promptField.setMinimumSize(new Dimension(400, 25));
        promptField.setAlignmentX(Component.CENTER_ALIGNMENT);

        promptField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String prompt = promptField.getText();
            }
        });
        
        // Create title
        JLabel title = new JLabel("\uD83E\uDD16 Bulama");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        // Create prompt label
        JLabel promptLabel = new JLabel("Prompt:");
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create submit button
        submitButton = new JButton("Submit");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String prompt = promptField.getText();
            }
        });

        // Create response label
        JLabel responseLabel = new JLabel("Response:");
        responseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create response field with fixed size
        response = new JTextField();
        response.setPreferredSize(new Dimension(900, 75));
        response.setMaximumSize(new Dimension(900, 75));
        response.setMinimumSize(new Dimension(900, 75));
        response.setAlignmentX(Component.CENTER_ALIGNMENT);
        response.setEditable(false);

        // Add components with spacing
        ui.add(title);
        ui.add(Box.createVerticalStrut(10));
        ui.add(promptLabel);
        ui.add(Box.createVerticalStrut(5));
        ui.add(promptField);
        ui.add(Box.createVerticalStrut(10));
        ui.add(submitButton);
        ui.add(Box.createVerticalStrut(20));
        ui.add(responseLabel);
        ui.add(Box.createVerticalStrut(10));
        ui.add(response);
        ui.add(Box.createVerticalGlue()); // Push everything to the top
    }

    public JPanel getUI() {
        return this.ui;
    }

    public void setPrompt(String prompt) {
        promptField.setText(prompt);
    }

    public String getPrompt() {
        return promptField.getText();
    }

    public void setResponse(String responseText) {
        response.setText(responseText);
    }

    public String getResponse() {
        return response.getText();
    }
    
    public void clear() {
        promptField.setText("");
        response.setText("");
    }
    
    public void addSubmitListener(ActionListener actionListener) {
        submitButton.addActionListener(actionListener);
    }

    public void setEnabled(boolean enabled) {
        promptField.setEnabled(enabled);
        response.setEnabled(enabled);
        for (Component comp : ui.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(enabled);
            }
        }
    }
}