package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class SteamClient extends JFrame implements ActionListener {
    private JButton viewCatalogButton;
    private JButton viewLibraryButton;  // Nuevo botón
    private JButton viewProfileButton;
    private JButton logoutButton;
    
    private Steam steam;
    private Steam.Jugador currentUser;
    
    public SteamClient(Steam steam, Steam.Jugador user) {
        super("Panel de Usuario - " + user.username);
        this.steam = steam;
        this.currentUser = user;
        
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(40, 65, 104));
        
        viewCatalogButton = new JButton("1. Ver Catálogo de Juegos");
        viewCatalogButton.setBounds(100, 50, 200, 40);
        viewCatalogButton.addActionListener(this);
        this.add(viewCatalogButton);
        
        viewLibraryButton = new JButton("2. Ver Biblioteca");  // Cambiado
        viewLibraryButton.setBounds(100, 110, 200, 40);
        viewLibraryButton.addActionListener(this);
        this.add(viewLibraryButton);
        
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
        } else if (e.getSource() == viewLibraryButton) {
            Biblioteca biblioteca = new Biblioteca(steam, currentUser);
            biblioteca.setVisible(true);
        } else if (e.getSource() == viewProfileButton) {
            viewProfile();
        } else if (e.getSource() == logoutButton) {
            logout();
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
