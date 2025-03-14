package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class SteamAdmin extends JFrame implements ActionListener {
    private JTextArea outputArea;
    private Steam steam;
    private Steam.Jugador currentUser;
    
    private JButton addGameButton;
    private JButton updatePriceButton;
    private JButton deleteGameButton;
    private JButton clientReportButton;
    private JButton viewGamesButton;
    private JButton logoutButton;
    
    public SteamAdmin(Steam steam, Steam.Jugador admin) {
        super("Panel de Administrador - " + admin.username);
        this.steam = steam;
        this.currentUser = admin;
        
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(40, 65, 104)); // Steam-like dark blue color
        
        // Create buttons with fixed positions
        addGameButton = new JButton("1. Agregar Juego");
        addGameButton.setBounds(100, 50, 200, 40);
        addGameButton.addActionListener(this);
        this.add(addGameButton);
        
        updatePriceButton = new JButton("2. Actualizar Precio");
        updatePriceButton.setBounds(100, 110, 200, 40);
        updatePriceButton.addActionListener(this);
        this.add(updatePriceButton);
        
        deleteGameButton = new JButton("3. Eliminar Juego");
        deleteGameButton.setBounds(100, 170, 200, 40);
        deleteGameButton.addActionListener(this);
        this.add(deleteGameButton);
        
        clientReportButton = new JButton("4. Reporte Cliente");
        clientReportButton.setBounds(100, 230, 200, 40);
        clientReportButton.addActionListener(this);
        this.add(clientReportButton);
        
        viewGamesButton = new JButton("5. Ver Juegos");
        viewGamesButton.setBounds(100, 290, 200, 40);
        viewGamesButton.addActionListener(this);
        this.add(viewGamesButton);
        
        logoutButton = new JButton("6. Cerrar Sesión");
        logoutButton.setBounds(100, 350, 200, 40);
        logoutButton.addActionListener(this);
        this.add(logoutButton);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addGameButton) {
            addGame();
        } else if (e.getSource() == updatePriceButton) {
            updatePrice();
        } else if (e.getSource() == deleteGameButton) {
            deleteGame();
        } else if (e.getSource() == clientReportButton) {
            generateClientReport();
        } else if (e.getSource() == viewGamesButton) {
            viewGames();
        } else if (e.getSource() == logoutButton) {
            logout();
        }
    }
    
    private void addGame() {
        JTextField titleField = new JTextField();
        JTextField osField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField imageField = new JTextField();
        
        Object[] inputs = {
            "Título:", titleField,
            "Sistema Operativo (W/M/L):", osField,
            "Edad Mínima:", ageField,
            "Precio:", priceField,
            "Ruta Imagen:", imageField
        };
        
        int result = JOptionPane.showConfirmDialog(this, inputs, "Agregar Juego", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                char os = osField.getText().trim().charAt(0);
                int minAge = Integer.parseInt(ageField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                String imagePath = imageField.getText().trim();
                
                steam.addGame(title, os, minAge, price, imagePath);
                JOptionPane.showMessageDialog(this, "Juego agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar juego: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updatePrice() {
        JTextField codeField = new JTextField();
        JTextField priceField = new JTextField();
        
        Object[] inputs = {
            "Código del Juego:", codeField,
            "Nuevo Precio:", priceField
        };
        
        int result = JOptionPane.showConfirmDialog(this, inputs, "Actualizar Precio", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int code = Integer.parseInt(codeField.getText().trim());
                double newPrice = Double.parseDouble(priceField.getText().trim());
                
                steam.updatePriceFor(code, newPrice);
                JOptionPane.showMessageDialog(this, "Precio actualizado para el juego " + code + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar precio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteGame() {
        JTextField codeField = new JTextField();
        
        Object[] inputs = {
            "Código del Juego a Eliminar:", codeField
        };
        
        int result = JOptionPane.showConfirmDialog(this, inputs, "Eliminar Juego", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int gameCode = Integer.parseInt(codeField.getText().trim());
                
                boolean deleted = deleteGameFromSteam(gameCode);
                
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Juego con código " + gameCode + " eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el juego. Verifique que el código existe.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El código debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar juego: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean deleteGameFromSteam(int gameCode) {
        try {
            List<Steam.Juego> games = steam.leerTodosLosJuegos();
            
            boolean gameFound = false;
            for (Steam.Juego game : games) {
                if (game.codigo == gameCode) {
                    gameFound = true;
                    break;
                }
            }
            
            if (!gameFound) {
                return false;
            }
            
            steam.deleteGame(gameCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void generateClientReport() {
        JTextField codeField = new JTextField();
        JTextField fileNameField = new JTextField();
        
        Object[] inputs = {
            "Código del Cliente:", codeField,
            "Nombre Archivo (con ruta):", fileNameField
        };
        
        int result = JOptionPane.showConfirmDialog(this, inputs, "Reporte Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int clientCode = Integer.parseInt(codeField.getText().trim());
                String fileName = fileNameField.getText().trim();
                
                String message = steam.reportForClient(clientCode, fileName);
                JOptionPane.showMessageDialog(this, message, "Reporte Cliente", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al generar reporte: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewGames() {
        try {
            List<Steam.Juego> gamesList = steam.leerTodosLosJuegos();
            JTextArea textArea = new JTextArea(20, 40);
            textArea.setEditable(false);
            
            textArea.append("=== Lista de Juegos ===\n");
            for (Steam.Juego game : gamesList) {
                textArea.append(game.toString() + "\n");
            }
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(this, scrollPane, "Lista de Juegos", JOptionPane.PLAIN_MESSAGE);
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer juegos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        this.dispose();
        new LoginFrame(steam).setVisible(true);
    }
}