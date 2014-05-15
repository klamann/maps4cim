package external;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class AddComponentOnJFrameAtRuntime extends JFrame implements ActionListener {

    JPanel panel;

    public AddComponentOnJFrameAtRuntime() {
        super("Add component on JFrame at runtime");
        setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        add(panel, BorderLayout.CENTER);
        JButton button = new JButton("CLICK HERE");
        add(button, BorderLayout.SOUTH);
        button.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        panel.add(new JButton("Button"));
        panel.revalidate();
        validate();
    }

    public static void main(String[] args) {
        AddComponentOnJFrameAtRuntime acojfar = new AddComponentOnJFrameAtRuntime();
    }

}