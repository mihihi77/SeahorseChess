package view;
import javax.swing.*;
import java.io.*;

public class GameView {
    public static void main(String[] args){

        // CREATE GAME WINDOW
        JFrame frame = new JFrame("Vietnamese Seahorse Chess");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(500,500);

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
