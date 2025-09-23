/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package exp2_s6_manola_valenzuela;

import java.util.*;

/**
 *
 * @author naru
 */
public class Exp2_S6_Manola_Valenzuela {
      
 // Sistema de ventas y reservas - Teatro Moro
 // - Menú: reservar, modificar reserva, comprar, imprimir boleta (resumen), resumen y salir

    // ===== Configuración general =====
    static final String NOMBRE_TEATRO = "Teatro Moro";
    static final int FILAS = 10;              // A..J
    static final int COLUMNAS = 15;           // 01..15
    static final long HOLD_MILLIS = 5 * 60 * 1000; // 5 minutos

    // Sectores por fila
    // A-B VIP, C-E Platea Baja, F-H Platea Alta, I-J Palcos
    static final int PRECIO_VIP = 20000;
    static final int PRECIO_PLATEA_BAJA = 15000;
    static final int PRECIO_PLATEA_ALTA = 12000;
    static final int PRECIO_PALCOS = 18000;

    // estado: 0 libre, 1 reservado, 2 vendido
    static int[][] sala = new int[FILAS][COLUMNAS];
    // timestamps de reserva (0 si no reservado)
    static long[][] reservadoEn = new long[FILAS][COLUMNAS];

    // contadores "globales"
    static int totalDisponibles = FILAS * COLUMNAS;
    static int totalReservados = 0;
    static int totalVendidos = 0;

    // métrica simple de ingresos totales por boletas impresas en la última compra
    static int ingresosAcumulados = 0;

    // última boleta (resumen)
    static String[] boletaCodigos = new String[FILAS * COLUMNAS];
    static String[] boletaZonas   = new String[FILAS * COLUMNAS];
    static int[]    boletaPrecios = new int[FILAS * COLUMNAS];
    static int      boletaCount   = 0;

    static final Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {

        inicializarSala();
        loopMenu();
        System.out.println("Gracias por usar el Sistema de Entradas del " + NOMBRE_TEATRO + ".");
    }

    // Inicialización
    static void inicializarSala() {
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                sala[f][c] = 0;           // LIBRE
                reservadoEn[f][c] = 0L;   
            }
        }
    }

    // Menú principal
    static void loopMenu() {
        int op;
        do {
            expirarReservasVencidas(); // mantiene el sistema limpio
            mostrarResumen();
            mostrarMapa();
            System.out.println("=== " + NOMBRE_TEATRO + " - Menú Principal ===");
            System.out.println("1. Reservar asiento");
            System.out.println("2. Modificar reserva");
            System.out.println("3. Comprar entradas");
            System.out.println("4. Imprimir boleta (resumen)");
            System.out.println("5. Resumen general");
            System.out.println("0. Salir");

            while (!sc.hasNextInt()) { System.out.println("Opción inválida."); sc.next(); }
            op = sc.nextInt(); sc.nextLine(); // limpiar fin de línea

            switch (op) {
                case 1 -> reservarFlujo();
                case 2 -> modificarReserva();
                case 3 -> comprarFlujo();
                case 4 -> imprimirBoletaResumen();
                case 5 -> mostrarResumen();
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    // Visualización 
    static void mostrarResumen() {
        System.out.printf("[Resumen] Disponibles: %d | Reservados: %d | Vendidos: %d | Ingresos: $%d",
                totalDisponibles, totalReservados, totalVendidos, ingresosAcumulados);
    }

    static void mostrarMapa() {
        System.out.println("Mapa de sala (.: libre, R: reservado, X: vendido)");
        System.out.print("    ");
        for (int c = 1; c <= COLUMNAS; c++) System.out.printf("%02d ", c);
        System.out.println();
        for (int f = 0; f < FILAS; f++) {
            char filaChar = (char) ('A' + f);
            System.out.printf("%c | ", filaChar);
            for (int c = 0; c < COLUMNAS; c++) {
                char marca = (sala[f][c] == 0) ? '.' : (sala[f][c] == 1 ? 'R' : 'X');
                System.out.print(marca + "  ");
            }
            System.out.println();
        }
    }

    // Reservas
    static void reservarFlujo() {
        do {
            String codigo = leerCodigoAsiento("Ingresa el asiento a reservar (p.ej. A01): ");
            int[] idx = indicesDe(codigo);
            int f = idx[0], c = idx[1];
            if (sala[f][c] == 0) { // LIBRE
                sala[f][c] = 1; // RESERVADO
                reservadoEn[f][c] = System.currentTimeMillis();
                totalDisponibles--; totalReservados++;
                System.out.println("✔ Asiento " + codigo + " reservado por 5 minutos.");
            } else {
                System.out.println("✖ No se puede reservar. Estado actual: " + nombreEstado(sala[f][c]));
            }
        } while (leerSiNo("¿Reservar otro asiento? (S/N): "));
    }

    static void modificarReserva() {
        String origen = leerCodigoAsiento("Ingresa el asiento RESERVADO que quieres cambiar: ");
        int[] o = indicesDe(origen);
        if (sala[o[0]][o[1]] != 1) { // no reservado
            System.out.println("✖ Ese asiento no está reservado (estado: " + nombreEstado(sala[o[0]][o[1]]) + ")");
            return;
        }
        String destino = leerCodigoAsiento("Ingresa el NUEVO asiento (debe estar libre): ");
        int[] d = indicesDe(destino);
        if (sala[d[0]][d[1]] != 0) {
            System.out.println("✖ El nuevo asiento no está libre (estado: " + nombreEstado(sala[d[0]][d[1]]) + ")");
            return;
        }
        // mover reserva
        sala[d[0]][d[1]] = 1; reservadoEn[d[0]][d[1]] = System.currentTimeMillis();
        sala[o[0]][o[1]] = 0; reservadoEn[o[0]][o[1]] = 0L;
        // contadores no cambian (reservado -> reservado)
        System.out.println("✔ Reserva movida de " + origen + " a " + destino);
    }

    // Compras
    static void comprarFlujo() {
        expirarReservasVencidas();
        System.out.println("¿Deseas comprar asientos reservados (R) o seleccionar libres (L)?");
        char modo = leerOpcion("Elige R/L: ", new char[]{'R','L'});
        int cantidad = leerEnteroRango("¿Cuántas entradas comprarás?: ", 1, 30);

        // limpiar última boleta
        boletaCount = 0;

        for (int i = 0; i < cantidad; i++) {
            String codigo = leerCodigoAsiento("Asiento a comprar (#" + (i+1) + "): ");
            int[] idx = indicesDe(codigo);
            int f = idx[0], c = idx[1];

            if (modo == 'R') {
                if (sala[f][c] != 1) { // debe estar reservado
                    System.out.println("✖ No está RESERVADO. Prueba otro.");
                    i--; continue; // repetir
                }
            } else { // L -> libres
                if (sala[f][c] != 0) {
                    System.out.println("✖ No está LIBRE. Prueba otro.");
                    i--; continue; // repetir
                }
            }

            // aplicar descuento por entrada (opcional)
            int precioBase = precioPorFila(f);
            int precioFinal = aplicarDescuentoInteractivo(precioBase);

            // vender
            if (sala[f][c] == 1) { totalReservados--; }
            else if (sala[f][c] == 0) { totalDisponibles--; }
            sala[f][c] = 2; reservadoEn[f][c] = 0L;
            totalVendidos++; //revisar contadores globales tras compra*

            // guardar en boleta (resumen)
            boletaCodigos[boletaCount] = codigo;
            boletaZonas[boletaCount]   = nombreZona(f);
            boletaPrecios[boletaCount] = precioFinal;
            boletaCount++;

            System.out.println("✔ Comprado " + codigo + " (" + nombreZona(f) + ")");
        }

        // revisar boletaCount y los tres arreglos boleta*
        System.out.println("Subtotal comprado: $" + totalBoleta());
    }

    static void imprimirBoletaResumen() {
        if (boletaCount == 0) {
            System.out.println("No hay compras registradas para imprimir.");
            return;
        }
        System.out.println("================= BOLETA (RESUMEN) =================");
        System.out.println("Teatro : " + NOMBRE_TEATRO);
        System.out.println("Fecha  : " + new Date());
        System.out.println("---------------------------------------------------");
        System.out.printf("%-8s %-14s %10s", "Asiento", "Zona", "Precio");
        for (int i = 0; i < boletaCount; i++) {
            System.out.printf("%-8s %-14s $%10d", boletaCodigos[i], boletaZonas[i], boletaPrecios[i]);
            }
        System.out.println("---------------------------------------------------");
        
        int total = totalBoleta(); //inspeccionar boleta*
        
        System.out.printf("TOTAL: $%d", total);
        System.out.println("===================================================");
        ingresosAcumulados += total; // acumulamos al imprimir boleta
        
        
    }

    // Zonas y precios
    static String nombreZona(int fila) {
        if (fila <= 1) return "VIP";            // A-B
        if (fila <= 4) return "Platea Baja";    // C-E
        if (fila <= 7) return "Platea Alta";    // F-H
        return "Palcos";                         // I-J
    }

    static int precioPorFila(int fila) {
        if (fila <= 1) return PRECIO_VIP;
        if (fila <= 4) return PRECIO_PLATEA_BAJA;
        if (fila <= 7) return PRECIO_PLATEA_ALTA;
        return PRECIO_PALCOS;
    }

    static int totalBoleta() {
        int suma = 0;
        for (int i = 0; i < boletaCount; i++) suma += boletaPrecios[i];
        return suma;
    }

    // Caducidad de reservas
    static void expirarReservasVencidas() {
        long ahora = System.currentTimeMillis();
        int expiradas = 0;
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                if (sala[f][c] == 1 && (ahora - reservadoEn[f][c]) > HOLD_MILLIS) {
                    sala[f][c] = 0;
                    reservadoEn[f][c] = 0L;
                    totalReservados--; totalDisponibles++;
                    expiradas++;
                }
            }
        }
        if (expiradas > 0) System.out.println("(i) Reservas expiradas automáticamente: " + expiradas);
    }

    // Descuentos por entrada: Estudiante (-10%) y Adulto Mayor >=65 (-15%)
    static int aplicarDescuentoInteractivo(int precioBase) {
        // Similar al estilo de S5: preguntar estudiante, si no, edad
        System.out.println("¿Es estudiante? (S/N)");
        String resp = sc.nextLine().trim();
        if (resp.equalsIgnoreCase("S")) {
            return (int)Math.round(precioBase * 0.90); // 10%
        } else if (resp.equalsIgnoreCase("N")) {
            System.out.println("Ingrese su edad");
            while (!sc.hasNextInt()) { System.out.println("Edad inválida, intente de nuevo:"); sc.next(); }
            int edad = sc.nextInt(); sc.nextLine();
            if (edad >= 65) return (int)Math.round(precioBase * 0.85); // 15%
            return precioBase; // sin descuento
        } else {
            System.out.println("Entrada inválida, se deja sin descuento.");
            return precioBase;
        }
    }

    // Utilidades de lectura
    static int leerEnteroRango(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            while (!sc.hasNextInt()) { System.out.println("Valor inválido. Número por favor."); sc.next(); }
            int v = sc.nextInt(); sc.nextLine();
            if (v < min || v > max) {
                System.out.println("Debe ser un entero entre " + min + " y " + max + ".");
            } else {
                return v;
            }
        }
    }

    static boolean leerSiNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (s.equalsIgnoreCase("S")) return true;
            if (s.equalsIgnoreCase("N")) return false;
            System.out.println("Responde 'S' o 'N'.");
        }
    }

    static char leerOpcion(String prompt, char[] validas) {
        Set<Character> set = new HashSet<>();
        for (char ch : validas) set.add(Character.toUpperCase(ch));
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (s.length() == 1) {
                char ch = Character.toUpperCase(s.charAt(0));
                if (set.contains(ch)) return ch;
            }
            System.out.println("Opción inválida.");
        }
    }

    static String leerCodigoAsiento(String prompt) {
        while (true) {
            System.out.print(prompt);
            String code = sc.nextLine().trim().toUpperCase();
            if (esCodigoValido(code)) return code;
            System.out.println("Código inválido. Usa formato FILA+COLUMNA, ej: A01..J15");
        }
    }

    static boolean esCodigoValido(String code) {
        if (code.length() < 2 || code.length() > 3) return false;
        char fila = code.charAt(0);
        if (fila < 'A' || fila >= ('A' + FILAS)) return false;
        try {
            int col = Integer.parseInt(code.substring(1));
            return 1 <= col && col <= COLUMNAS;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static int[] indicesDe(String code) {
        char fila = code.charAt(0);
        int f = fila - 'A';
        int c = Integer.parseInt(code.substring(1)) - 1;
        return new int[]{f, c};
    }

    static String nombreEstado(int estado) {
        if (estado == 0) return "LIBRE";
        if (estado == 1) return "RESERVADO";
        return "VENDIDO";
    }
}