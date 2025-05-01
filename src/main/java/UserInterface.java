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

    public UserInterface() {
        ui = new JPanel();
        promptField = new JTextField();
        promptField.setAlignmentX(Component.CENTER_ALIGNMENT);

        ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));
        //promptField.setMaximumSize(new Dimension(200, 25)); // sets both preferred and maximum size

        promptField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String prompt = promptField.getText();
            }
        });
        JLabel title = new JLabel("\uD83E\uDD16 Bulama");
        title.setFont(new Font("Arial", Font.BOLD, 18)); // larger bold font
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);    // center horizontally


        // Add components
        ui.add(title);
        ui.add(Box.createVerticalStrut(10)); // spacing
        ui.add(new JLabel("Prompt:"));
        ui.add(promptField);
        ui.add(new JButton("Submit") {{
            setAlignmentX(JLabel.CENTER);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String prompt = promptField.getText();
                    System.out.println("Prompt: " + prompt);
                }
            });
        }});
        ui.add(new JLabel("Response:"));
        response = new JTextField();
        response.setPreferredSize(new Dimension(150, 25)); // width: 150px, height: 25px
        response.setEditable(false);
        ui.add(response);
    }

    public JPanel getUI() {
        return this.ui;
    }

    public void setPrompt (String prompt){
        promptField.setText(prompt);
    }
}
