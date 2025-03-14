/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Date;

public class Biblioteca extends JFrame {
    private Steam steam;
    private Steam.Jugador currentUser;
    private JTextArea textArea;
    
    public Biblioteca(Steam steam, Steam.Jugador user) {
        super("Biblioteca - " + user.username);
        this.steam = steam;
        this.currentUser = user;
        
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        
        loadLibrary();
    }
    
    private void loadLibrary() {
        File downloadsFolder = new File("steam/downloads");
        File[] files = downloadsFolder.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    String content = sb.toString();
                    // Se asume que el archivo contiene el nombre del cliente
                    if (content.contains(currentUser.nombre)) {
                        textArea.append("Archivo: " + file.getName() + "\n");
                        textArea.append(content + "\n");
                        textArea.append("-------------------------------\n");
                    }
                } catch (IOException e) {
                    textArea.append("Error al leer " + file.getName() + ": " + e.getMessage() + "\n");
                }
            }
        } else {
            textArea.append("No se encontraron descargas en la biblioteca.\n");
        }
    }
}
