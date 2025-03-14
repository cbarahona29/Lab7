package lab7;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoJuegos extends JFrame {
    private Steam steam;
    private JPanel gamePanel;
    private Color steamBackgroundColor = new Color(27, 40, 56);  // Steam dark blue background
    private Color steamCardColor = new Color(42, 71, 94);        // Steam card background
    private Color steamTextColor = new Color(195, 213, 220);     // Steam text color
    private Color steamHighlightColor = new Color(102, 192, 244); // Steam blue highlight
    
    public CatalogoJuegos(Steam steam) {
        this.steam = steam;
        setTitle("Steam - Catálogo de Juegos");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(steamBackgroundColor);
        
        // Header panel with return button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(23, 26, 33));  // Darker header
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("TIENDA");
        titleLabel.setForeground(steamTextColor);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton returnButton = new JButton("Regresar al Menú");
        returnButton.setBackground(new Color(66, 95, 122));
        returnButton.setForeground(Color.WHITE);
        returnButton.setFocusPainted(false);
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Close this window
            }
        });
        headerPanel.add(returnButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Game panel with grid layout for store-like appearance
        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setBackground(steamBackgroundColor);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setBackground(steamBackgroundColor);
        scrollPane.getViewport().setBackground(steamBackgroundColor);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        loadGames();
    }
    
    private void loadGames() {
        gamePanel.removeAll();
        try {
            List<Steam.Juego> juegos = steam.leerTodosLosJuegos();
            
            // Get most recent games (eliminate duplicates by code)
            Map<Integer, Steam.Juego> juegosMasRecientes = new HashMap<>();
            
            // Process games in reverse to keep most recent
            for (int i = juegos.size() - 1; i >= 0; i--) {
                Steam.Juego juego = juegos.get(i);
                if (!juegosMasRecientes.containsKey(juego.codigo)) {
                    juegosMasRecientes.put(juego.codigo, juego);
                }
            }
            
            List<Steam.Juego> juegosAMostrar = new ArrayList<>(juegosMasRecientes.values());
            
            // Add games to panel
            for (Steam.Juego juego : juegosAMostrar) {
                addGameCard(juego);
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los juegos: " + e.getMessage());
            e.printStackTrace();
        }
        gamePanel.revalidate();
        gamePanel.repaint();
    }
    
    private void addGameCard(Steam.Juego juego) {
        // Create a store-like game card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout(0, 0));
        cardPanel.setBackground(steamCardColor);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        cardPanel.setMaximumSize(new Dimension(800, 120));
        
        // Image panel
        ImageIcon icon = loadGameImage(juego);
        JLabel imageLabel;
        if (icon != null && icon.getIconWidth() > 0) {
            imageLabel = new JLabel(icon);
        } else {
            // Placeholder panel for missing images
            imageLabel = new JLabel("Sin imagen");
            imageLabel.setOpaque(true);
            imageLabel.setBackground(new Color(35, 60, 81));
            imageLabel.setPreferredSize(new Dimension(184, 69));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setForeground(steamTextColor);
        }
        
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        cardPanel.add(imageLabel, BorderLayout.WEST);
        
        // Game info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(steamCardColor);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Game title
        JLabel nameLabel = new JLabel(juego.titulo);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(steamTextColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // System requirements
        String osName = "";
        switch(juego.so) {
            case 'W': osName = "Windows"; break;
            case 'M': osName = "Mac"; break;
            case 'L': osName = "Linux"; break;
            default: osName = "Desconocido";
        }
        
        JLabel osLabel = new JLabel("Sistema: " + osName);
        osLabel.setForeground(new Color(170, 170, 170));
        osLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        osLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(osLabel);
        
        // Age requirement
        JLabel ageLabel = new JLabel("Edad: " + juego.edadMinima + "+");
        ageLabel.setForeground(new Color(170, 170, 170));
        ageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(ageLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        // Game ID (useful for downloads)
        JLabel idLabel = new JLabel("ID: " + juego.codigo);
        idLabel.setForeground(new Color(120, 120, 120));
        idLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(idLabel);
        
        cardPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Price and download panel
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBackground(steamCardColor);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 15));
        
        // Price
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", juego.precio));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(steamHighlightColor);
        priceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        actionPanel.add(priceLabel);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Download button
        JButton downloadButton = new JButton("Descargar");
        downloadButton.setBackground(new Color(59, 133, 224));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setFocusPainted(false);
        downloadButton.setFont(new Font("Arial", Font.BOLD, 12));
        downloadButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadGame(juego.codigo);
            }
        });
        actionPanel.add(downloadButton);
        
        cardPanel.add(actionPanel, BorderLayout.EAST);
        
        // Add the card to the main panel
        gamePanel.add(cardPanel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private ImageIcon loadGameImage(Steam.Juego juego) {
        try {
            // Check if file exists
            File imageFile = new File(juego.rutaImagen);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(juego.rutaImagen);
                // Resize image to standard store size (184x69 - typical Steam store thumbnail size)
                Image img = icon.getImage();
                if (img != null && img.getWidth(null) > 0) {
                    Image scaledImg = img.getScaledInstance(184, 69, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImg);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar imagen para " + juego.titulo + ": " + e.getMessage());
        }
        return null;
    }
    
    private void downloadGame(int gameId) {
        // Create a custom dialog with Steam styling
        JDialog downloadDialog = new JDialog(this, "Descargar Juego", true);
        downloadDialog.setSize(350, 200);
        downloadDialog.setLocationRelativeTo(this);
        downloadDialog.setLayout(new BorderLayout());
        downloadDialog.getContentPane().setBackground(steamBackgroundColor);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        formPanel.setBackground(steamBackgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // User ID field
        JPanel userPanel = new JPanel(new BorderLayout(5, 0));
        userPanel.setBackground(steamBackgroundColor);
        JLabel userLabel = new JLabel("Código de jugador:");
        userLabel.setForeground(steamTextColor);
        JTextField userField = new JTextField();
        userPanel.add(userLabel, BorderLayout.NORTH);
        userPanel.add(userField, BorderLayout.CENTER);
        
        // OS selection
        JPanel osPanel = new JPanel(new BorderLayout(5, 0));
        osPanel.setBackground(steamBackgroundColor);
        JLabel osLabel = new JLabel("Sistema Operativo:");
        osLabel.setForeground(steamTextColor);
        
        String[] systems = {"Windows (W)", "Mac (M)", "Linux (L)"};
        JComboBox<String> osCombo = new JComboBox<>(systems);
        osPanel.add(osLabel, BorderLayout.NORTH);
        osPanel.add(osCombo, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(steamBackgroundColor);
        
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setBackground(new Color(51, 55, 59));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> downloadDialog.dispose());
        
        JButton confirmButton = new JButton("Descargar");
        confirmButton.setBackground(new Color(59, 133, 224));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int playerId = Integer.parseInt(userField.getText().trim());
                    String selectedOS = (String) osCombo.getSelectedItem();
                    char os = selectedOS.charAt(selectedOS.indexOf("(") + 1);
                    
                    downloadDialog.dispose();
                    
                    boolean success = steam.downloadGame(gameId, playerId, os);
                    if (success) {
                        showSuccessDialog();
                    } else {
                        JOptionPane.showMessageDialog(CatalogoJuegos.this, 
                            "No se pudo descargar el juego. Verifique los requisitos.", 
                            "Error de Descarga", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(downloadDialog, 
                        "El código de jugador debe ser un número.", 
                        "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(downloadDialog, 
                        "Error durante la descarga: " + ex.getMessage(), 
                        "Error de Sistema", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        // Add all components
        formPanel.add(userPanel);
        formPanel.add(osPanel);
        formPanel.add(buttonPanel);
        
        downloadDialog.add(formPanel, BorderLayout.CENTER);
        downloadDialog.setVisible(true);
    }
    
    private void showSuccessDialog() {
        JDialog successDialog = new JDialog(this, "Descarga Exitosa", true);
        successDialog.setSize(300, 150);
        successDialog.setLocationRelativeTo(this);
        successDialog.setLayout(new BorderLayout());
        successDialog.getContentPane().setBackground(steamBackgroundColor);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(steamBackgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel messageLabel = new JLabel("¡Juego descargado exitosamente!");
       messageLabel.setForeground(steamTextColor);
messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
contentPanel.add(messageLabel, BorderLayout.CENTER);

JButton okButton = new JButton("OK");
okButton.setBackground(new Color(59, 133, 224));
okButton.setForeground(Color.WHITE);
okButton.setFocusPainted(false);
okButton.addActionListener(e -> successDialog.dispose());

JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
buttonPanel.setBackground(steamBackgroundColor);
buttonPanel.add(okButton);
contentPanel.add(buttonPanel, BorderLayout.SOUTH);

successDialog.add(contentPanel);
successDialog.setVisible(true);
    }
}