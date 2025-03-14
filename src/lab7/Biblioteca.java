package lab7;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Biblioteca extends JFrame {
    private Steam steam;
    private JPanel gamePanel;
    private int playerId;
    // Simplified color palette
    private final Color BACKGROUND_COLOR = new Color(27, 40, 56);
    private final Color CARD_COLOR = new Color(42, 71, 94);
    private final Color TEXT_COLOR = new Color(195, 213, 220);
    private final Color BUTTON_COLOR = new Color(59, 133, 224);
    
    public Biblioteca(Steam steam, int playerId) {
        this.steam = steam;
        this.playerId = playerId;
        setupFrame();
        loadPlayerGames();
    }
    
    private void setupFrame() {
        setTitle("Steam - Biblioteca de Juegos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
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
        
        JLabel titleLabel = new JLabel("MI BIBLIOTECA");
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
    
    private void loadPlayerGames() {
        gamePanel.removeAll();
        try {
            List<Steam.Juego> allGames = steam.leerTodosLosJuegos();
            List<Steam.Juego> playerGames = new ArrayList<>();
            
            List<Object> allDownloads = steam.leerTodasLasDescargas();
            if (allDownloads != null) {
                for (Object download : allDownloads) {
                    int downloadPlayerId = getPlayerIdFromDownload(download);
                    int gameId = getGameIdFromDownload(download);
                    
                    if (downloadPlayerId == this.playerId) {
                        for (Steam.Juego game : allGames) {
                            if (game.codigo == gameId) {
                                playerGames.add(game);
                                break;
                            }
                        }
                    }
                }
            }
            
            if (playerGames.isEmpty()) {
                showEmptyLibraryMessage();
            } else {
                for (Steam.Juego game : playerGames) {
                    addGameCard(game);
                }
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la biblioteca: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        gamePanel.revalidate();
        gamePanel.repaint();
    }
    
    private int getPlayerIdFromDownload(Object download) {
 
        try {
            java.lang.reflect.Field field = download.getClass().getDeclaredField("jugadorId");
            field.setAccessible(true);
            return (int) field.get(download);
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Invalid ID
        }
    }
    
    private int getGameIdFromDownload(Object download) {
    
        try {
            java.lang.reflect.Field field = download.getClass().getDeclaredField("codigoJuego");
            field.setAccessible(true);
            return (int) field.get(download);
        } catch (Exception e) {
            e.printStackTrace();
            return -1; 
        }
    }
    
    private void showEmptyLibraryMessage() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(BACKGROUND_COLOR);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        
        JLabel messageLabel = new JLabel("No tienes juegos en tu biblioteca");
        messageLabel.setForeground(TEXT_COLOR);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton storeButton = new JButton("Ir a la Tienda");
        storeButton.setBackground(BUTTON_COLOR);
        storeButton.setForeground(Color.WHITE);
        storeButton.setFocusPainted(false);
        storeButton.addActionListener(e -> {
            dispose();
            new CatalogoJuegos(steam).setVisible(true);
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(storeButton);
        
        messagePanel.add(messageLabel, BorderLayout.NORTH);
        messagePanel.add(buttonPanel, BorderLayout.CENTER);
        gamePanel.add(messagePanel);
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
        
        JPanel actionPanel = createActionPanel(game);
        cardPanel.add(actionPanel, BorderLayout.EAST);
        
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
        
        // System info
        String osName = getOSName(game.so);
        JLabel osLabel = new JLabel("Sistema: " + osName);
        osLabel.setForeground(new Color(170, 170, 170));
        osLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        osLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(osLabel);
        
        return infoPanel;
    }
    
    private String getOSName(char so) {
        switch(so) {
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
        
      
     
        return actionPanel;
    }
    
    private ImageIcon loadGameImage(Steam.Juego game) {
        try {
            if (game.rutaImagen.startsWith("/images/")) {
                ImageIcon icon = new ImageIcon(getClass().getResource(game.rutaImagen));
                Image img = icon.getImage();
                if (img != null && img.getWidth(null) > 0) {
                    Image scaledImg = img.getScaledInstance(184, 69, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImg);
                }
            } else {
                File imageFile = new File(game.rutaImagen);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(game.rutaImagen);
                    Image img = icon.getImage();
                    if (img != null && img.getWidth(null) > 0) {
                        Image scaledImg = img.getScaledInstance(184, 69, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImg);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar imagen para " + game.titulo + ": " + e.getMessage());
        }
        return null;
    }
    
    private void playGame(Steam.Juego game) {
        JDialog gameDialog = new JDialog(this, game.titulo + " - Jugando", true);
        gameDialog.setSize(500, 300);
        gameDialog.setLocationRelativeTo(this);
        gameDialog.setLayout(new BorderLayout());
        gameDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JLabel playingLabel = new JLabel("Jugando a " + game.titulo);
        playingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playingLabel.setForeground(TEXT_COLOR);
        playingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playingLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JButton closeButton = new JButton("Cerrar Juego");
        closeButton.setBackground(BUTTON_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> gameDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(closeButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        gameDialog.add(playingLabel, BorderLayout.CENTER);
        gameDialog.add(buttonPanel, BorderLayout.SOUTH);
        gameDialog.setVisible(true);
    }
}