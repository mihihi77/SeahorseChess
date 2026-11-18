package view;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MenuView {

    private JFrame mainFrame;
    private JPanel controlPanel;

    public void displayWindow(){
        mainFrame = new JFrame("Seahorse Chess");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700,700);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null); // Window goes in the middle of screen
        mainFrame.setVisible(true);

        ImageIcon logo = new ImageIcon("resources/logo.png");

        mainFrame.setIconImage(logo.getImage());

        controlPanel = new JPanel(new BorderLayout());
    }
    public void main(String[] args) {
        // CREATE GAME WINDOW
        displayWindow();

        JButton startButton = new JButton("Start Game");
        // startButton.setPreferredSize(new Dimension(100, 30));
        startButton.setSize(200, 100);
        startButton.setBackground(Color.green);
        mainFrame.add(startButton);
    }
}
