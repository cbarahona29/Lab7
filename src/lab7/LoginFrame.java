package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoginFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel registerPanel;
    
    private Steam steam;
    
    public LoginFrame(Steam steam) {
        super("Steam Login");
        this.steam = steam;
        
        // Initialize admin if not exists
        try {
            List<Steam.Jugador> players = steam.leerTodosLosJugadores();
            boolean adminExists = false;
            for (Steam.Jugador p : players) {
                if (p.tipoUsuario.equalsIgnoreCase("admin")) {
                    adminExists = true;
                    break;
                }
            }
            if (!adminExists) {
                Calendar cal = Calendar.getInstance();
                cal.set(1990, Calendar.JANUARY, 1);
                steam.addPlayer("admin", "admin", "Administrador", cal, "", "admin");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();
        
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        
        add(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginButton = new JButton("Iniciar Sesión");
        JButton createAccountButton = new JButton("Crear Cuenta");
        JLabel messageLabel = new JLabel("");
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(userField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(passField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);
        
        gbc.gridy = 3;
        panel.add(createAccountButton, gbc);
        
        gbc.gridy = 4;
        panel.add(messageLabel, gbc);
        
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();
                
                try {
                    List<Steam.Jugador> playersList = steam.leerTodosLosJugadores();
                    boolean found = false;
                    Steam.Jugador currentUser = null;
                    
                    for (Steam.Jugador p : playersList) {
                        if (p.username.equals(username) && p.password.equals(password)) {
                            currentUser = p;
                            found = true;
                            break;
                        }
                    }
                    
                    if (found) {
                        messageLabel.setText("Bienvenido " + currentUser.nombre);
                        dispose();
                        
                        if (currentUser.tipoUsuario.equalsIgnoreCase("admin")) {
                            new SteamAdmin(steam, currentUser).setVisible(true);
                        } else {
                            new SteamClient(steam, currentUser).setVisible(true);
                        }
                    } else {
                        messageLabel.setText("Credenciales incorrectas");
                    }
                } catch (IOException ex) {
                    messageLabel.setText("Error en lectura de datos");
                    ex.printStackTrace();
                }
            }
        });
        
        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "register");
            }
        });
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JLabel nameLabel = new JLabel("Nombre:");
        JLabel dateLabel = new JLabel("Fecha de Nacimiento (dd/MM/yyyy):");
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JTextField nameField = new JTextField(15);
        JTextField dateField = new JTextField(15);
        JButton registerButton = new JButton("Registrarse");
        JButton backButton = new JButton("Volver");
        JLabel messageLabel = new JLabel("");
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(userField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(passField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(dateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(registerButton, gbc);
        
        gbc.gridy = 5;
        panel.add(backButton, gbc);
        
        gbc.gridy = 6;
        panel.add(messageLabel, gbc);
        
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();
                String name = nameField.getText().trim();
                String dateStr = dateField.getText().trim();
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date birthDate = sdf.parse(dateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(birthDate);
                    
                    steam.addPlayer(username, password, name, cal, "", "normal");
                    messageLabel.setText("Cuenta creada exitosamente");
                    
                    // Clear fields
                    userField.setText("");
                    passField.setText("");
                    nameField.setText("");
                    dateField.setText("");
                    
                    // Return to login panel after short delay
                    Timer timer = new Timer(2000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cardLayout.show(mainPanel, "login");
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                    
                } catch (ParseException pe) {
                    messageLabel.setText("Formato de fecha incorrecto");
                } catch (IOException ex) {
                    messageLabel.setText("Error al crear cuenta");
                    ex.printStackTrace();
                }
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "login");
            }
        });
        
        return panel;
    } 
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Steam steam = new Steam();
                new LoginFrame(steam).setVisible(true);
            }
        });
    }
}