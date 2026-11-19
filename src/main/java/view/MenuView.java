package view;

import javax.swing.*;
import java.awt.*;

public class MenuView {

    private JFrame mainFrame;
    private JPanel controlPanel;

    public void displayWindow() {
        mainFrame = new JFrame("Seahorse Chess");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700, 700);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null); // Window goes in the middle of screen

        ImageIcon logo = new ImageIcon("resources/logo.png");
        mainFrame.setIconImage(logo.getImage());

        // Background panel
        Image bgImage = new ImageIcon("resources/menu/bgmn.png").getImage();
        controlPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Menu buttons container
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton playButton = createButton("resources/menu/play.png");
        JButton settingButton = createButton("resources/menu/setting.png");
        JButton rankingButton = createButton("resources/menu/rank.png");

        playButton.addActionListener(e -> {
            System.out.println("Start game");
            // TODO: Open GameView
        });

        settingButton.addActionListener(e -> {
            System.out.println("Open settings");
        });

        rankingButton.addActionListener(e -> {
            System.out.println("Open ranking");
        });

        buttonPanel.add(playButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(settingButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(rankingButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(120, 0, 0, 0);
        controlPanel.add(buttonPanel, gbc);

        mainFrame.setContentPane(controlPanel);
        mainFrame.setVisible(true);
    }

    private JButton createButton(String path) {
        JButton btn = new JButton(new ImageIcon(path));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        MenuView menu = new MenuView();
        menu.displayWindow();
    }
}