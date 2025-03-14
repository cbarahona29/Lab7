package lab7;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoJuegos extends JFrame {
    private Steam steam;
    private JPanel gamePanel;
    // Simplified color palette
    private final Color BACKGROUND_COLOR = new Color(27, 40, 56);
    private final Color CARD_COLOR = new Color(42, 71, 94);
    private final Color TEXT_COLOR = new Color(195, 213, 220);
    private final Color BUTTON_COLOR = new Color(59, 133, 224);
    
    public CatalogoJuegos(Steam steam) {
        this.steam = steam;
        setupFrame();
        loadGames();
    }
    
    private void setupFrame() {
        setTitle("Steam - Catálogo de Juegos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header with return button
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Game panel with scroll
        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setBackground(BACKGROUND_COLOR);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(23, 26, 33));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("TIENDA");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton returnButton = new JButton("Regresar al Menú");
        returnButton.setBackground(CARD_COLOR);
        returnButton.setForeground(Color.WHITE);
        returnButton.setFocusPainted(false);
        returnButton.addActionListener(e -> dispose());
        headerPanel.add(returnButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private void loadGames() {
        gamePanel.removeAll();
        try {
            // Get all games and keep only the most recent version
            List<Steam.Juego> allGames = steam.leerTodosLosJuegos();
            Map<Integer, Steam.Juego> uniqueGames = new HashMap<>();
            
            // Process games in reverse to keep most recent
            for (int i = allGames.size() - 1; i >= 0; i--) {
                Steam.Juego game = allGames.get(i);
                if (!uniqueGames.containsKey(game.codigo)) {
                    uniqueGames.put(game.codigo, game);
                }
            }
            
            // Display each unique game
            for (Steam.Juego game : uniqueGames.values()) {
                addGameCard(game);
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los juegos: " + e.getMessage());
        }
        gamePanel.revalidate();
        gamePanel.repaint();
    }
    
    private void addGameCard(Steam.Juego game) {
        JPanel cardPanel = new JPanel(new BorderLayout(5, 0));
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        cardPanel.setMaximumSize(new Dimension(750, 100));
        
        // Game image
        JLabel imageLabel = createImageLabel(game);
        cardPanel.add(imageLabel, BorderLayout.WEST);
        
        // Game info
        JPanel infoPanel = createInfoPanel(game);
        cardPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Price and download
        JPanel actionPanel = createActionPanel(game);
        cardPanel.add(actionPanel, BorderLayout.EAST);
        
        // Add card to game panel with spacing
        gamePanel.add(cardPanel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 8)));
    }
    
    private JLabel createImageLabel(Steam.Juego game) {
        ImageIcon icon = loadGameImage(game);
        JLabel imageLabel;
        
        if (icon != null) {
            imageLabel = new JLabel(icon);
        } else {
            imageLabel = new JLabel("Sin imagen");
            imageLabel.setOpaque(true);
            imageLabel.setBackground(new Color(35, 60, 81));
            imageLabel.setPreferredSize(new Dimension(184, 69));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setForeground(TEXT_COLOR);
        }
        
        return imageLabel;
    }
    
    private JPanel createInfoPanel(Steam.Juego game) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Game title
        JLabel nameLabel = new JLabel(game.titulo);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // System & age info
        String osName = getOSName(game.so);
        JLabel osLabel = new JLabel("Sistema: " + osName + " | Edad: " + game.edadMinima + "+");
        osLabel.setForeground(new Color(170, 170, 170));
        osLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        osLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(osLabel);
        
        // Game ID
        JLabel idLabel = new JLabel("ID: " + game.codigo);
        idLabel.setForeground(new Color(120, 120, 120));
        idLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(idLabel);
        
        return infoPanel;
    }
    
    private String getOSName(char os) {
        switch(os) {
            case 'W': return "Windows";
            case 'M': return "Mac";
            case 'L': return "Linux";
            default: return "Desconocido";
        }
    }
    
    private JPanel createActionPanel(Steam.Juego game) {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBackground(CARD_COLOR);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        
        // Price
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", game.precio));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        actionPanel.add(priceLabel);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        // Download button
        JButton downloadButton = new JButton("Descargar");
        downloadButton.setBackground(BUTTON_COLOR);
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setFocusPainted(false);
        downloadButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        downloadButton.addActionListener(e -> downloadGame(game.codigo));
        actionPanel.add(downloadButton);
        
        return actionPanel;
    }
    
 private ImageIcon loadGameImage(Steam.Juego game) {
    System.out.println("\n---------------------------------------");
    System.out.println("DEBUG - Cargando imagen para: " + game.titulo);
    System.out.println("Ruta de imagen: " + game.rutaImagen);
    
    try {
        // Check if path is null or empty
        if (game.rutaImagen == null || game.rutaImagen.trim().isEmpty()) {
            System.out.println("ERROR: Ruta de imagen vacía");
            return null;
        }
        
        // APPROACH 1: Try loading as a resource from classpath
        if (game.rutaImagen.startsWith("/") || game.rutaImagen.startsWith("images/")) {
            String resourcePath = game.rutaImagen;
            if (!resourcePath.startsWith("/")) {
                resourcePath = "/" + resourcePath;
            }
            
            System.out.println("Intentando cargar como recurso: " + resourcePath);
            java.net.URL imgURL = getClass().getResource(resourcePath);
            
            if (imgURL != null) {
                System.out.println("URL del recurso encontrada: " + imgURL.toString());
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage();
                
                if (img != null && img.getWidth(null) > 0) {
                    System.out.println("Imagen cargada como recurso exitosamente");
                    Image scaledImg = img.getScaledInstance(184, 69, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImg);
                } else {
                    System.out.println("ERROR: Recurso encontrado pero imagen inválida");
                }
            } else {
                System.out.println("ERROR: URL del recurso no encontrada");
                
                // Try without leading slash as fallback
                if (resourcePath.startsWith("/")) {
                    String altPath = resourcePath.substring(1);
                    System.out.println("Intentando alternativa sin barra inicial: " + altPath);
                    imgURL = getClass().getResource(altPath);
                    
                    if (imgURL != null) {
                        ImageIcon icon = new ImageIcon(imgURL);
                        Image img = icon.getImage();
                        if (img != null && img.getWidth(null) > 0) {
                            System.out.println("Imagen cargada con ruta alternativa exitosamente");
                            Image scaledImg = img.getScaledInstance(184, 69, Image.SCALE_SMOOTH);
                            return new ImageIcon(scaledImg);
                        }
                    } else {
                        System.out.println("ERROR: URL alternativa tampoco encontrada");
                    }
                }
            }
        }
        
        // APPROACH 2: Try loading as absolute file path
        System.out.println("Intentando cargar como archivo absoluto: " + game.rutaImagen);
        File imageFile = new File(game.rutaImagen);
        if (imageFile.exists() && imageFile.isFile()) {
            System.out.println("Archivo encontrado: " + imageFile.getAbsolutePath());
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            Image img = icon.getImage();
            
            if (img != null && img.getWidth(null) > 0) {
                System.out.println("Imagen cargada desde archivo exitosamente");
                Image scaledImg = img.getScaledInstance(184, 69, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            } else {
                System.out.println("ERROR: Archivo existe pero no se pudo cargar como imagen");
            }
        } else {
            System.out.println("ERROR: Archivo no existe como ruta absoluta");
        }
        
        // APPROACH 3: Try loading from current directory or project root
        System.out.println("Intentando cargar como ruta relativa...");
        
        // Try multiple base directories
        File[] baseDirs = {
            new File("."),                         // Current directory
            new File("src"),                       // src folder
            new File("src/lab7"),                  // package directory
            new File("src/main/resources"),        // Maven resources
            new File("resources")                  // Common resources folder
        };
        
        String relativePath = game.rutaImagen;
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        
        for (File baseDir : baseDirs) {
            if (baseDir.exists() && baseDir.isDirectory()) {
                File relativeFile = new File(baseDir, relativePath);
                System.out.println("Intentando: " + relativeFile.getAbsolutePath());
                
                if (relativeFile.exists() && relativeFile.isFile()) {
                    System.out.println("Archivo encontrado en: " + relativeFile.getAbsolutePath());
                    ImageIcon icon = new ImageIcon(relativeFile.getAbsolutePath());
                    Image img = icon.getImage();
                    
                    if (img != null && img.getWidth(null) > 0) {
                        System.out.println("Imagen cargada desde ruta relativa exitosamente");
                        Image scaledImg = img.getScaledInstance(184, 69, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImg);
                    } else {
                        System.out.println("ERROR: Archivo existe pero no es una imagen válida");
                    }
                }
            }
        }
        
        // APPROACH 4: Last resort, try using ClassLoader directly
        System.out.println("Intentando cargar con ClassLoader...");
        ClassLoader classLoader = getClass().getClassLoader();
        java.net.URL resourceUrl = classLoader.getResource(relativePath);
        
        if (resourceUrl != null) {
            System.out.println("Recurso encontrado con ClassLoader: " + resourceUrl);
            ImageIcon icon = new ImageIcon(resourceUrl);
            Image img = icon.getImage();
            if (img != null && img.getWidth(null) > 0) {
                System.out.println("Imagen cargada con ClassLoader exitosamente");
                Image scaledImg = img.getScaledInstance(184, 69, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } else {
            System.out.println("ERROR: No se encontró la imagen con ClassLoader");
        }
        
    } catch (Exception e) {
        System.out.println("EXCEPCIÓN al cargar imagen: " + e.getClass().getName());
        System.out.println("Mensaje: " + e.getMessage());
        e.printStackTrace();
    }
    
    System.out.println("ERROR FINAL: No se pudo cargar la imagen usando ningún método");
    System.out.println("---------------------------------------");
    return null;
}
    
    private void downloadGame(int gameId) {
        JDialog dialog = new JDialog(this, "Descargar Juego", true);
        dialog.setSize(300, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 1, 8, 8));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        // Player ID field
        JTextField playerIdField = createFormField(formPanel, "Código de jugador:");
        
        // OS selection
        String[] systems = {"Windows (W)", "Mac (M)", "Linux (L)"};
        JComboBox<String> osCombo = new JComboBox<>(systems);
        JPanel osPanel = new JPanel(new BorderLayout(5, 0));
        osPanel.setBackground(BACKGROUND_COLOR);
        JLabel osLabel = new JLabel("Sistema Operativo:");
        osLabel.setForeground(TEXT_COLOR);
        osPanel.add(osLabel, BorderLayout.NORTH);
        osPanel.add(osCombo, BorderLayout.CENTER);
        formPanel.add(osPanel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setBackground(new Color(51, 55, 59));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton confirmButton = new JButton("Descargar");
        confirmButton.setBackground(BUTTON_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.addActionListener(e -> {
            try {
                int playerId = Integer.parseInt(playerIdField.getText().trim());
                String selectedOS = (String) osCombo.getSelectedItem();
                char os = selectedOS.charAt(selectedOS.indexOf("(") + 1);
                
                dialog.dispose();
                processDownload(gameId, playerId, os);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "El código de jugador debe ser un número.",
                    "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        formPanel.add(buttonPanel);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private JTextField createFormField(JPanel parent, String labelText) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(BACKGROUND_COLOR);
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        JTextField field = new JTextField();
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        parent.add(panel);
        return field;
    }
    
    private void processDownload(int gameId, int playerId, char os) {
        try {
            boolean success = steam.downloadGame(gameId, playerId, os);
            if (success) {
                showMessage("¡Juego descargado exitosamente!", "Descarga Exitosa");
            } else {
                showMessage("No se pudo descargar el juego. Verifique los requisitos.", "Error de Descarga");
            }
        } catch (IOException ex) {
            showMessage("Error durante la descarga: " + ex.getMessage(), "Error de Sistema");
        }
    }
    
    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, 
            title.contains("Error") ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
}