package com.example;

import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
@Push(PushMode.AUTOMATIC)
@StyleSheet("styles.css")
public class Application implements AppShellConfigurator {
    private static Thread serverThread;
    private static volatile boolean isServerRunning = true;
    public static void main(String[] args) {
        startServerInBackground(args);
        createAndShowTrayWindow();
    }

    private static void startServerInBackground(String[] args) {
        serverThread = new Thread(() -> {
            try {

                SpringApplication.run(Application.class, args);

                if (!isServerRunning) {
                    System.out.println("Vaadin server has stopped.");
                    System.exit(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start server: " + e.getMessage(), "Critical Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
        serverThread.setDaemon(false);
        serverThread.start();
    }

    private static void createAndShowTrayWindow() {
        JFrame controlFrame = new JFrame("Kamisado Game");
        controlFrame.setSize(300, 120);
        controlFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        controlFrame.setAlwaysOnTop(false);
        controlFrame.setLocationRelativeTo(null); // По центру экрана

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("<html><center>Server is running at <b>localhost:8080</b><br>Close this window to stop the server.</center></html>", SwingConstants.CENTER);

        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:8080"));
                System.out.println("Browser opened with button.");
            } catch (Exception ex) {
                System.err.println("Failed to open browser automatically: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Open http://localhost:8080 manually", "Browser Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(label, BorderLayout.CENTER);
        panel.add(openButton, BorderLayout.SOUTH);
        controlFrame.add(panel);

        // Крестик на окне
        controlFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutDownApplication();
            }
        });

        controlFrame.setVisible(true);
    }

    private static void shutDownApplication() {
        System.out.println("Shutting down the application...");
        isServerRunning = false;
        System.exit(0);
    }
}
