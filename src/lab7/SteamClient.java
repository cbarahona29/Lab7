package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class SteamClient extends JFrame implements ActionListener {
    private JPanel mainPanel;
    private JTextArea outputArea;
    private Steam steam;
    private Steam.Jugador currentUser;
    
    private JButton viewCatalogButton;
    private JButton downloadGameButton;
    private JButton viewProfileButton;
    private JButton logoutButton;
    
    public SteamClient(Steam steam, Steam.Jugador user) {
        super("Panel de Usuario - " + user.username);
        this.steam = steam;
        this.currentUser = user;
        
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(40, 65, 104)); // Steam-like dark blue color
        
        // Create buttons with fixed positions
        viewCatalogButton = new JButton("1. Ver Catálogo de Juegos");
        viewCatalogButton.setBounds(100, 50, 200, 40);
        viewCatalogButton.addActionListener(this);
        this.add(viewCatalogButton);
        
        downloadGameButton = new JButton("2. Descargar Juego");
        downloadGameButton.setBounds(100, 110, 200, 40);
        downloadGameButton.addActionListener(this);
        this.add(downloadGameButton);
        
        viewProfileButton = new JButton("3. Ver Perfil");
        viewProfileButton.setBounds(100, 170, 200, 40);
        viewProfileButton.addActionListener(this);
        this.add(viewProfileButton);
        
        logoutButton = new JButton("4. Cerrar Sesión");
        logoutButton.setBounds(100, 230, 200, 40);
        logoutButton.addActionListener(this);
        this.add(logoutButton);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewCatalogButton) {
            CatalogoJuegos catalogo = new CatalogoJuegos(steam);
            catalogo.setVisible(true);
        } else if (e.getSource() == downloadGameButton) {
            downloadGame();
        } else if (e.getSource() == viewProfileButton) {
            viewProfile();
        } else if (e.getSource() == logoutButton) {
            logout();
        }
    }
    
    private void viewCatalog() {
        try {
            List<Steam.Juego> gamesList = steam.leerTodosLosJuegos();
            JTextArea textArea = new JTextArea(20, 40);
            textArea.setEditable(false);
            
            textArea.append("=== Catálogo de Juegos ===\n");
            for (Steam.Juego game : gamesList) {
                textArea.append(game.toString() + "\n");
            }
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(this, scrollPane, "Catálogo de Juegos", JOptionPane.PLAIN_MESSAGE);
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer catálogo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void downloadGame() {
        JTextField gameCodeField = new JTextField();
        JTextField osField = new JTextField();
        
        Object[] inputs = {
            "Código del Juego:", gameCodeField,
            "Sistema Operativo (W/M/L):", osField
        };
        
        int result = JOptionPane.showConfirmDialog(this, inputs, "Descargar Juego", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int gameCode = Integer.parseInt(gameCodeField.getText().trim());
                char os = osField.getText().trim().charAt(0);
                
                boolean success = steam.downloadGame(gameCode, currentUser.codigo, os);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Descarga exitosa.", "Descarga Completada", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Descarga fallida. Verifique requisitos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en descarga: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewProfile() {
        JTextArea profileText = new JTextArea(10, 30);
        profileText.setEditable(false);
        profileText.append("=== Perfil del Usuario ===\n");
        profileText.append("Código: " + currentUser.codigo + "\n");
        profileText.append("Username: " + currentUser.username + "\n");
        profileText.append("Nombre: " + currentUser.nombre + "\n");
        profileText.append("Tipo: " + currentUser.tipoUsuario + "\n");
        profileText.append("Descargas: " + currentUser.contadorDownloads + "\n");
        
        JScrollPane scrollPane = new JScrollPane(profileText);
        JOptionPane.showMessageDialog(this, scrollPane, "Perfil de Usuario", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        this.dispose();
        new LoginFrame(steam).setVisible(true);
    }
}