/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Calendar;
import java.util.List;

public class Main {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel panelPrincipal;
    private JPanel panelLogin;
    private JPanel panelAdmin;
    private JPanel panelUsuario;
    
    private Steam steam;
    private Steam.Jugador usuarioActual;
    
    public Main() {
        steam = new Steam();
        try {
            List<Steam.Jugador> jugadores = steam.leerTodosLosJugadores();
            boolean adminExiste = false;
            for (Steam.Jugador p : jugadores) {
                if (p.tipoUsuario.equalsIgnoreCase("admin")) {
                    adminExiste = true;
                    break;
                }
            }
            if (!adminExiste) {
                Calendar cal = Calendar.getInstance();
                cal.set(1990, Calendar.JANUARY, 1);
                steam.addPlayer("admin", "admin", "Administrador", cal, "", "admin");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame = new JFrame("Catálogo de Steam");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelLogin = crearPanelLogin();
        panelAdmin = crearPanelAdmin();
        panelUsuario = crearPanelUsuario();
        panelPrincipal.add(panelLogin, "login");
        panelPrincipal.add(panelAdmin, "admin");
        panelPrincipal.add(panelUsuario, "usuario");
        frame.add(panelPrincipal);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel lblUser = new JLabel("Username:");
        JLabel lblPass = new JLabel("Password:");
        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Iniciar Sesión");
        JLabel lblMensaje = new JLabel("");
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblUser, gbc);
        gbc.gridx = 1;
        panel.add(txtUser, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblPass, gbc);
        gbc.gridx = 1;
        panel.add(txtPass, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);
        gbc.gridy = 3;
        panel.add(lblMensaje, gbc);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUser.getText().trim();
                String password = new String(txtPass.getPassword()).trim();
                try {
                    List<Steam.Jugador> listaJugadores = steam.leerTodosLosJugadores();
                    boolean encontrado = false;
                    for (Steam.Jugador p : listaJugadores) {
                        if (p.username.equals(username) && p.password.equals(password)) {
                            usuarioActual = p;
                            encontrado = true;
                            break;
                        }
                    }
                    if (encontrado) {
                        lblMensaje.setText("Bienvenido " + usuarioActual.nombre);
                        if (usuarioActual.tipoUsuario.equalsIgnoreCase("admin")) {
                            cardLayout.show(panelPrincipal, "admin");
                        } else {
                            cardLayout.show(panelPrincipal, "usuario");
                        }
                    } else {
                        lblMensaje.setText("Credenciales incorrectas");
                    }
                } catch (IOException ex) {
                    lblMensaje.setText("Error en lectura de datos");
                    ex.printStackTrace();
                }
            }
        });
        return panel;
    }
    
    private JPanel crearPanelAdmin() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel opcionesPanel = new JPanel(new FlowLayout());
        JButton btnAgregarJuego = new JButton("Agregar Juego");
        JButton btnActualizarPrecio = new JButton("Actualizar Precio");
        JButton btnReporteCliente = new JButton("Reporte Cliente");
        JButton btnVerJuegos = new JButton("Ver Juegos");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        opcionesPanel.add(btnAgregarJuego);
        opcionesPanel.add(btnActualizarPrecio);
        opcionesPanel.add(btnReporteCliente);
        opcionesPanel.add(btnVerJuegos);
        opcionesPanel.add(btnCerrarSesion);
        JTextArea areaSalida = new JTextArea();
        JScrollPane scroll = new JScrollPane(areaSalida);
        panel.add(opcionesPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        btnAgregarJuego.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField txtTitulo = new JTextField();
                JTextField txtSO = new JTextField();
                JTextField txtEdad = new JTextField();
                JTextField txtPrecio = new JTextField();
                JTextField txtImagen = new JTextField();
                Object[] inputs = {
                    "Título:", txtTitulo,
                    "Sistema Operativo (W/M/L):", txtSO,
                    "Edad Mínima:", txtEdad,
                    "Precio:", txtPrecio,
                    "Ruta Imagen:", txtImagen
                };
                int result = JOptionPane.showConfirmDialog(frame, inputs, "Agregar Juego", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String titulo = txtTitulo.getText().trim();
                        char so = txtSO.getText().trim().charAt(0);
                        int edadMinima = Integer.parseInt(txtEdad.getText().trim());
                        double precio = Double.parseDouble(txtPrecio.getText().trim());
                        String rutaImagen = txtImagen.getText().trim();
                        steam.addGame(titulo, so, edadMinima, precio, rutaImagen);
                        areaSalida.append("Juego agregado exitosamente.\n");
                    } catch (Exception ex) {
                        areaSalida.append("Error al agregar juego: " + ex.getMessage() + "\n");
                    }
                }
            }
        });
        btnActualizarPrecio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField txtCodigo = new JTextField();
                JTextField txtNuevoPrecio = new JTextField();
                Object[] inputs = {
                    "Código del Juego:", txtCodigo,
                    "Nuevo Precio:", txtNuevoPrecio
                };
                int result = JOptionPane.showConfirmDialog(frame, inputs, "Actualizar Precio", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int codigo = Integer.parseInt(txtCodigo.getText().trim());
                        double nuevoPrecio = Double.parseDouble(txtNuevoPrecio.getText().trim());
                        steam.updatePriceFor(codigo, nuevoPrecio);
                        areaSalida.append("Precio actualizado para el juego " + codigo + ".\n");
                    } catch (Exception ex) {
                        areaSalida.append("Error al actualizar precio: " + ex.getMessage() + "\n");
                    }
                }
            }
        });
        btnReporteCliente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField txtCodigo = new JTextField();
                JTextField txtNombreArchivo = new JTextField();
                Object[] inputs = {
                    "Código del Cliente:", txtCodigo,
                    "Nombre Archivo (con ruta):", txtNombreArchivo
                };
                int result = JOptionPane.showConfirmDialog(frame, inputs, "Reporte Cliente", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int codigoCliente = Integer.parseInt(txtCodigo.getText().trim());
                        String nombreArchivo = txtNombreArchivo.getText().trim();
                        String mensaje = steam.reportForClient(codigoCliente, nombreArchivo);
                        areaSalida.append(mensaje + "\n");
                    } catch (Exception ex) {
                        areaSalida.append("Error al generar reporte: " + ex.getMessage() + "\n");
                    }
                }
            }
        });
        btnVerJuegos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Steam.Juego> listaJuegos = steam.leerTodosLosJuegos();
                    areaSalida.append("=== Lista de Juegos ===\n");
                    for (Steam.Juego j : listaJuegos) {
                        areaSalida.append(j.toString() + "\n");
                    }
                } catch (IOException ex) {
                    areaSalida.append("Error al leer juegos: " + ex.getMessage() + "\n");
                }
            }
        });
        btnCerrarSesion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                usuarioActual = null;
                cardLayout.show(panelPrincipal, "login");
            }
        });
        return panel;
    }
    
    private JPanel crearPanelUsuario() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel opcionesPanel = new JPanel(new FlowLayout());
        JButton btnVerCatalogo = new JButton("Ver Catálogo de Juegos");
        JButton btnDescargarJuego = new JButton("Descargar Juego");
        JButton btnVerPerfil = new JButton("Ver Perfil");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        opcionesPanel.add(btnVerCatalogo);
        opcionesPanel.add(btnDescargarJuego);
        opcionesPanel.add(btnVerPerfil);
        opcionesPanel.add(btnCerrarSesion);
        JTextArea areaSalida = new JTextArea();
        JScrollPane scroll = new JScrollPane(areaSalida);
        panel.add(opcionesPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        btnVerCatalogo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Steam.Juego> listaJuegos = steam.leerTodosLosJuegos();
                    areaSalida.append("=== Catálogo de Juegos ===\n");
                    for (Steam.Juego j : listaJuegos) {
                        areaSalida.append(j.toString() + "\n");
                    }
                } catch (IOException ex) {
                    areaSalida.append("Error al leer catálogo: " + ex.getMessage() + "\n");
                }
            }
        });
        btnDescargarJuego.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField txtCodigoJuego = new JTextField();
                JTextField txtSO = new JTextField();
                Object[] inputs = {
                    "Código del Juego:", txtCodigoJuego,
                    "Sistema Operativo (W/M/L):", txtSO
                };
                int result = JOptionPane.showConfirmDialog(frame, inputs, "Descargar Juego", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int codigoJuego = Integer.parseInt(txtCodigoJuego.getText().trim());
                        char so = txtSO.getText().trim().charAt(0);
                        boolean exito = steam.downloadGame(codigoJuego, usuarioActual.codigo, so);
                        if (exito) {
                            areaSalida.append("Descarga exitosa.\n");
                        } else {
                            areaSalida.append("Descarga fallida. Verifique requisitos.\n");
                        }
                    } catch (Exception ex) {
                        areaSalida.append("Error en descarga: " + ex.getMessage() + "\n");
                    }
                }
            }
        });
        btnVerPerfil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaSalida.append("=== Perfil del Usuario ===\n");
                areaSalida.append(usuarioActual.toString() + "\n");
            }
        });
        btnCerrarSesion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                usuarioActual = null;
                cardLayout.show(panelPrincipal, "login");
            }
        });
        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}
