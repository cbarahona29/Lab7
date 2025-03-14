package lab7;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Steam {
    private RandomAccessFile rafCodes;
    private RandomAccessFile rafGames;
    private RandomAccessFile rafPlayers;
    
    public Steam() {
        try {
            File folderSteam = new File("steam");
            if (!folderSteam.exists()) {
                folderSteam.mkdirs();
            }
            File folderDownloads = new File("steam/downloads");
            if (!folderDownloads.exists()) {
                folderDownloads.mkdirs();
            }
            File fileCodes = new File("steam/codes.stm");
            if (!fileCodes.exists()) {
                rafCodes = new RandomAccessFile(fileCodes, "rw");
                rafCodes.writeInt(1);
                rafCodes.writeInt(1);
                rafCodes.writeInt(1);
            } else {
                rafCodes = new RandomAccessFile(fileCodes, "rw");
            }
            rafGames = new RandomAccessFile("steam/games.stm", "rw");
            rafPlayers = new RandomAccessFile("steam/player.stm", "rw");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void crearCarpetasUsuario(String username) {
    File userFolder = new File("steam/" + username);
    if (!userFolder.exists()) {
        boolean creado = userFolder.mkdirs();
        System.out.println("Carpeta creada: " + creado + " - " + userFolder.getAbsolutePath());
    }
}

    public int obtenerSiguienteCodigo(String tipo) throws IOException {
        int codigo = 0;
        if (tipo.equalsIgnoreCase("game")) {
            rafCodes.seek(0);
            codigo = rafCodes.readInt();
            rafCodes.seek(0);
            rafCodes.writeInt(codigo + 1);
        } else if (tipo.equalsIgnoreCase("player")) {
            rafCodes.seek(4);
            codigo = rafCodes.readInt();
            rafCodes.seek(4);
            rafCodes.writeInt(codigo + 1);
        } else if (tipo.equalsIgnoreCase("download")) {
            rafCodes.seek(8);
            codigo = rafCodes.readInt();
            rafCodes.seek(8);
            rafCodes.writeInt(codigo + 1);
        }
        return codigo;
    }
    
    public static class Juego {
        int codigo;
        String titulo;
        char so;
        int edadMinima;
        double precio;
        int contadorDownloads;
        String rutaImagen;
        
        public Juego() {}
        
        public Juego(int codigo, String titulo, char so, int edadMinima, double precio, int contadorDownloads, String rutaImagen) {
            this.codigo = codigo;
            this.titulo = titulo;
            this.so = so;
            this.edadMinima = edadMinima;
            this.precio = precio;
            this.contadorDownloads = contadorDownloads;
            this.rutaImagen = rutaImagen;
        }
        
        public void escribir(RandomAccessFile raf) throws IOException {
            raf.writeInt(codigo);
            raf.writeUTF(titulo);
            raf.writeChar(so);
            raf.writeInt(edadMinima);
            raf.writeDouble(precio);
            raf.writeInt(contadorDownloads);
            raf.writeUTF(rutaImagen);
        }
        
        public static Juego leer(RandomAccessFile raf) throws IOException {
            Juego j = new Juego();
            j.codigo = raf.readInt();
            j.titulo = raf.readUTF();
            j.so = raf.readChar();
            j.edadMinima = raf.readInt();
            j.precio = raf.readDouble();
            j.contadorDownloads = raf.readInt();
            j.rutaImagen = raf.readUTF();
            return j;
        }
        
        @Override
        public String toString() {
            return "Código: " + codigo + ", Título: " + titulo + ", SO: " + so + ", Edad Mínima: " + edadMinima + ", Precio: " + precio + ", Descargas: " + contadorDownloads + ", Imagen: " + rutaImagen;
        }
    }
    
    public static class Jugador {
        int codigo;
        String username;
        String password;
        String nombre;
        long nacimiento;
        int contadorDownloads;
        String rutaImagen;
        String tipoUsuario;
        
        public Jugador() {}
        
        public Jugador(int codigo, String username, String password, String nombre, long nacimiento, int contadorDownloads, String rutaImagen, String tipoUsuario) {
            this.codigo = codigo;
            this.username = username;
            this.password = password;
            this.nombre = nombre;
            this.nacimiento = nacimiento;
            this.contadorDownloads = contadorDownloads;
            this.rutaImagen = rutaImagen;
            this.tipoUsuario = tipoUsuario;
        }
        
        public void escribir(RandomAccessFile raf) throws IOException {
            raf.writeInt(codigo);
            raf.writeUTF(username);
            raf.writeUTF(password);
            raf.writeUTF(nombre);
            raf.writeLong(nacimiento);
            raf.writeInt(contadorDownloads);
            raf.writeUTF(rutaImagen);
            raf.writeUTF(tipoUsuario);
        }
        
        public static Jugador leer(RandomAccessFile raf) throws IOException {
            Jugador p = new Jugador();
            p.codigo = raf.readInt();
            p.username = raf.readUTF();
            p.password = raf.readUTF();
            p.nombre = raf.readUTF();
            p.nacimiento = raf.readLong();
            p.contadorDownloads = raf.readInt();
            p.rutaImagen = raf.readUTF();
            p.tipoUsuario = raf.readUTF();
            return p;
        }
        
        @Override
        public String toString() {
            Date fecha = new Date(nacimiento);
            return "Código: " + codigo + ", Username: " + username + ", Nombre: " + nombre + ", Nacimiento: " + fecha + ", Descargas: " + contadorDownloads + ", Imagen: " + rutaImagen + ", Tipo: " + tipoUsuario;
        }
        
        // Método para obtener la ruta de la carpeta del usuario
        public String getUserFolderPath() {
            return "steam/" + username;
        }
    }
    
    public List<Juego> leerTodosLosJuegos() throws IOException {
        List<Juego> lista = new ArrayList<>();
        rafGames.seek(0);
        while (rafGames.getFilePointer() < rafGames.length()) {
            try {
                Juego j = Juego.leer(rafGames);
                lista.add(j);
            } catch (EOFException e) {
                break;
            }
        }
        return lista;
    }
    
    public void escribirTodosLosJuegos(List<Juego> lista) throws IOException {
        rafGames.setLength(0);
        for (Juego j : lista) {
            j.escribir(rafGames);
        }
    }
    
    public List<Jugador> leerTodosLosJugadores() throws IOException {
        List<Jugador> lista = new ArrayList<>();
        rafPlayers.seek(0);
        while (rafPlayers.getFilePointer() < rafPlayers.length()) {
            try {
                Jugador p = Jugador.leer(rafPlayers);
                lista.add(p);
            } catch (EOFException e) {
                break;
            }
        }
        return lista;
    }
    
    public void escribirTodosLosJugadores(List<Jugador> lista) throws IOException {
        rafPlayers.setLength(0);
        for (Jugador p : lista) {
            p.escribir(rafPlayers);
        }
    }
    
    private void guardarInfoUsuario(Jugador player) throws IOException {
        String userFolder = "steam/" + player.username;
        
        FileWriter fw = new FileWriter(userFolder + "/profile.txt", false);
        PrintWriter pw = new PrintWriter(fw);
        pw.println("Código: " + player.codigo);
        pw.println("Username: " + player.username);
        pw.println("Password: " + player.password);
        pw.println("Nombre: " + player.nombre);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaNacimiento = sdf.format(new Date(player.nacimiento));
        pw.println("Fecha de Nacimiento: " + fechaNacimiento);
        pw.println("Descargas Totales: " + player.contadorDownloads);
        pw.println("Tipo de Usuario: " + player.tipoUsuario);
        pw.close();
    }
    
    public void addGame(String titulo, char so, int edadMinima, double precio, String rutaImagen) throws IOException {
        int codigo = obtenerSiguienteCodigo("game");
        Juego nuevoJuego = new Juego(codigo, titulo, so, edadMinima, precio, 0, rutaImagen);
        rafGames.seek(rafGames.length());
        nuevoJuego.escribir(rafGames);
    }
    
    public void addPlayer(String username, String password, String nombre, Calendar nacimiento, String rutaImagen, String tipoUsuario) throws IOException {
        List<Jugador> lista = leerTodosLosJugadores();
        int codigo = obtenerSiguienteCodigo("player");
        long fechaNacimiento = nacimiento.getTimeInMillis();
        Jugador nuevoJugador = new Jugador(codigo, username, password, nombre, fechaNacimiento, 0, rutaImagen, tipoUsuario);
        lista.add(nuevoJugador);
        escribirTodosLosJugadores(lista);
        
        // Crear carpeta del usuario y guardar su información
        crearCarpetasUsuario(username);
        guardarInfoUsuario(nuevoJugador);
    }
    
    public boolean downloadGame(int codigoJuego, int codigoCliente, char soCliente) throws IOException {
        List<Juego> listaJuegos = leerTodosLosJuegos();
        Juego juegoSeleccionado = null;
        for (Juego j : listaJuegos) {
            if (j.codigo == codigoJuego) {
                juegoSeleccionado = j;
                break;
            }
        }
        if (juegoSeleccionado == null) {
            return false;
        }
        
        List<Jugador> listaJugadores = leerTodosLosJugadores();
        Jugador jugadorSeleccionado = null;
        for (Jugador p : listaJugadores) {
            if (p.codigo == codigoCliente) {
                jugadorSeleccionado = p;
                break;
            }
        }
        if (jugadorSeleccionado == null) {
            return false;
        }
        
        if (Character.toUpperCase(juegoSeleccionado.so) != Character.toUpperCase(soCliente)) {
            return false;
        }
        
        long hoy = System.currentTimeMillis();
        long diferencia = hoy - jugadorSeleccionado.nacimiento;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(diferencia);
        int edad = cal.get(Calendar.YEAR) - 1970;
        if (edad < juegoSeleccionado.edadMinima) {
            return false;
        }
        
        int codigoDownload = obtenerSiguienteCodigo("download");
        
        // Guardar en la carpeta global de descargas
        String nombreArchivoGlobal = "steam/downloads/download_" + codigoDownload + ".stm";
        FileWriter fwGlobal = new FileWriter(nombreArchivoGlobal, false);
        PrintWriter pwGlobal = new PrintWriter(fwGlobal);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fechaDescarga = sdf.format(new Date());
        pwGlobal.println(fechaDescarga);
        pwGlobal.println("IMAGE GAME: " + juegoSeleccionado.rutaImagen);
        pwGlobal.println();
        pwGlobal.println("Download #" + codigoDownload);
        pwGlobal.println(jugadorSeleccionado.nombre + " ha bajado " + juegoSeleccionado.titulo + " a un precio de $ " + juegoSeleccionado.precio);
        pwGlobal.close();
        
        // Guardar en la carpeta personal del usuario
        String userDownloadPath = "steam/" + jugadorSeleccionado.username + "/downloads";
        String nombreArchivoUsuario = userDownloadPath + "/download_" + codigoDownload + ".stm";
        FileWriter fwUsuario = new FileWriter(nombreArchivoUsuario, false);
        PrintWriter pwUsuario = new PrintWriter(fwUsuario);
        pwUsuario.println(fechaDescarga);
        pwUsuario.println("IMAGE GAME: " + juegoSeleccionado.rutaImagen);
        pwUsuario.println();
        pwUsuario.println("Download #" + codigoDownload);
        pwUsuario.println("Has descargado " + juegoSeleccionado.titulo + " a un precio de $ " + juegoSeleccionado.precio);
        pwUsuario.close();
        
        // Registrar el juego en la biblioteca del usuario
        String userGamesPath = "steam/" + jugadorSeleccionado.username + "/games";
        String gameFilePath = userGamesPath + "/game_" + juegoSeleccionado.codigo + ".txt";
        FileWriter fwGame = new FileWriter(gameFilePath, false);
        PrintWriter pwGame = new PrintWriter(fwGame);
        pwGame.println("Código: " + juegoSeleccionado.codigo);
        pwGame.println("Título: " + juegoSeleccionado.titulo);
        pwGame.println("Sistema Operativo: " + juegoSeleccionado.so);
        pwGame.println("Edad Mínima: " + juegoSeleccionado.edadMinima);
       pwGame.println("Precio pagado: $" + juegoSeleccionado.precio);
        pwGame.println("Fecha de compra: " + fechaDescarga);
        pwGame.println("Ruta imagen: " + juegoSeleccionado.rutaImagen);
        pwGame.close();
        
        // Actualizar contadores
        juegoSeleccionado.contadorDownloads++;
        jugadorSeleccionado.contadorDownloads++;
        
        // Actualizar en las listas
        for (int i = 0; i < listaJuegos.size(); i++) {
            if (listaJuegos.get(i).codigo == juegoSeleccionado.codigo) {
                listaJuegos.set(i, juegoSeleccionado);
                break;
            }
        }
        escribirTodosLosJuegos(listaJuegos);
        
        for (int i = 0; i < listaJugadores.size(); i++) {
            if (listaJugadores.get(i).codigo == jugadorSeleccionado.codigo) {
                listaJugadores.set(i, jugadorSeleccionado);
                break;
            }
        }
        escribirTodosLosJugadores(listaJugadores);
        
        // Actualizar el archivo de perfil del usuario
        guardarInfoUsuario(jugadorSeleccionado);
        
        return true;
    }
    
    public void updatePriceFor(int codigoJuego, double nuevoPrecio) throws IOException {
        List<Juego> listaJuegos = leerTodosLosJuegos();
        boolean encontrado = false;
        for (Juego j : listaJuegos) {
            if (j.codigo == codigoJuego) {
                j.precio = nuevoPrecio;
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            escribirTodosLosJuegos(listaJuegos);
        }
    }
    
    public String reportForClient(int codigoCliente, String nombreArchivoTxt) throws IOException {
        List<Jugador> listaJugadores = leerTodosLosJugadores();
        Jugador jugadorReporte = null;
        for (Jugador p : listaJugadores) {
            if (p.codigo == codigoCliente) {
                jugadorReporte = p;
                break;
            }
        }
        if (jugadorReporte == null) {
            return "NO SE PUEDE CREAR REPORTE";
        }
        
        FileWriter fw = new FileWriter(nombreArchivoTxt, false);
        PrintWriter pw = new PrintWriter(fw);
        pw.println("----- REPORTE DEL CLIENTE -----");
        pw.println("Código: " + jugadorReporte.codigo);
        pw.println("Username: " + jugadorReporte.username);
        pw.println("Password: " + jugadorReporte.password);
        pw.println("Nombre: " + jugadorReporte.nombre);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaNacimiento = sdf.format(new Date(jugadorReporte.nacimiento));
        pw.println("Fecha de Nacimiento: " + fechaNacimiento);
        pw.println("Contador de Downloads: " + jugadorReporte.contadorDownloads);
        pw.println("Ruta de Imagen: " + jugadorReporte.rutaImagen);
        pw.println("Tipo de Usuario: " + jugadorReporte.tipoUsuario);
        
        // Listar juegos del usuario
        File userGamesFolder = new File("steam/" + jugadorReporte.username + "/games");
        if (userGamesFolder.exists()) {
            pw.println("\n----- JUEGOS DEL USUARIO -----");
            File[] gameFiles = userGamesFolder.listFiles();
            if (gameFiles != null && gameFiles.length > 0) {
                for (File gameFile : gameFiles) {
                    BufferedReader br = new BufferedReader(new FileReader(gameFile));
                    String line;
                    pw.println("\nJuego: " + gameFile.getName());
                    while ((line = br.readLine()) != null) {
                        pw.println("  " + line);
                    }
                    br.close();
                }
            } else {
                pw.println("El usuario no tiene juegos.");
            }
        }
        
        pw.close();
        return "REPORTE CREADO";
    }
    
    public void printGames() throws IOException {
        List<Juego> listaJuegos = leerTodosLosJuegos();
        for (Juego j : listaJuegos) {
            System.out.println(j.toString());
        }
    }
    
    public boolean deleteGame(int codigoJuego) throws IOException {
        List<Juego> listaJuegos = leerTodosLosJuegos();
        boolean gameFound = false;
        
        // Create a new list excluding the game to delete
        List<Juego> nuevaLista = new ArrayList<>();
        for (Juego j : listaJuegos) {
            if (j.codigo != codigoJuego) {
                nuevaLista.add(j);
            } else {
                gameFound = true;
            }
        }
        
        if (gameFound) {
            escribirTodosLosJuegos(nuevaLista);
            return true;
        }
        
        return false;
    }
    public List<Object> leerTodasLasDescargas() throws IOException {
    List<Object> lista = new ArrayList<>();
    File downloadsFolder = new File("steam/downloads");
    
    if (downloadsFolder.exists() && downloadsFolder.isDirectory()) {
        File[] downloadFiles = downloadsFolder.listFiles((dir, name) -> name.startsWith("download_") && name.endsWith(".stm"));
        
        if (downloadFiles != null) {
            for (File file : downloadFiles) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    // Extract download code from filename (download_X.stm)
                    String filename = file.getName();
                    int codeStart = filename.indexOf("_") + 1;
                    int codeEnd = filename.indexOf(".stm");
                    int downloadCode = Integer.parseInt(filename.substring(codeStart, codeEnd));
                    
                    // Read download details
                    String fechaDescarga = br.readLine(); // First line is the date
                    String imagenLinea = br.readLine(); // Second line has image info
                    
                    br.readLine(); // Empty line
                    String downloadLine = br.readLine(); // "Download #X"
                    String detailsLine = br.readLine(); // Details line
                    
                    // Parse details line to extract player and game info
                    // Format: "{playerName} ha bajado {gameTitle} a un precio de $ {price}"
                    if (detailsLine.contains(" ha bajado ")) {
                        String[] parts = detailsLine.split(" ha bajado ");
                        String playerName = parts[0];
                        
                        // Find player by name
                        List<Jugador> jugadores = leerTodosLosJugadores();
                        int jugadorId = -1;
                        for (Jugador j : jugadores) {
                            if (j.nombre.equals(playerName)) {
                                jugadorId = j.codigo;
                                break;
                            }
                        }
                        
                        // Extract game title and find game by title
                        String[] gameParts = parts[1].split(" a un precio de \\$ ");
                        String gameTitle = gameParts[0];
                        
                        List<Juego> juegos = leerTodosLosJuegos();
                        int codigoJuego = -1;
                        for (Juego j : juegos) {
                            if (j.titulo.equals(gameTitle)) {
                                codigoJuego = j.codigo;
                                break;
                            }
                        }
                        
                        // Create an anonymous class instance with the required fields
                        final int finalJugadorId = jugadorId;
                        final int finalCodigoJuego = codigoJuego;
                        final int finalDownloadCode = downloadCode;
                        final String finalFechaDescarga = fechaDescarga;
                        
                        Object downloadObj = new Object() {
                            public final int codigo = finalDownloadCode;
                            public final int jugadorId = finalJugadorId;
                            public final int codigoJuego = finalCodigoJuego;
                            public final String fecha = finalFechaDescarga;
                        };
                        
                        // Add to list
                        lista.add(downloadObj);
                    }
                } catch (Exception e) {
                    System.out.println("Error reading download file " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    return lista;
}
    
}