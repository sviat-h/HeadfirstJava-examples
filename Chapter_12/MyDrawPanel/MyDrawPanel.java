package MyDrawPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.String;

public class MyDrawPanel implements ActionListener {
    JFrame frame;

    public static void main(String[] args) {
        MyDrawPanel draw = new MyDrawPanel();
        draw.go();
    }
    public void go () {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton button = new JButton("Change colors!");
        button.addActionListener(this);

        DrawPanel drawPanel = new DrawPanel();

        frame.getContentPane().add(BorderLayout.SOUTH, button);
        frame.getContentPane().add(BorderLayout.CENTER, drawPanel);
        frame.setSize(400,300);
        frame.setVisible(true);
    }
    public void actionPerformed (ActionEvent event){
        frame.repaint();
    }
}
