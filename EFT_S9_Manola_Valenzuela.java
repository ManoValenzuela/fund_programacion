/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package eft_s9_manola_valenzuela;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EFT_S9_Manola_Valenzuela {

    // ====== ESTADÍSTICAS GLOBALES ======
    public static int totalEntradasVendidas = 0;
    public static int totalDescuentosAplicados = 0;
    public static int ingresosTotales = 0;

    // ====== VENTAS ======
    public static List<Venta> ventas = new ArrayList<>();
    private static int nextId = 1001; // Generador de IDs
    static final Map<Integer, Venta> ventasPorId = new HashMap<>();

    // ====== SALA / ARREGLO DE ASIENTOS ======
    static final int FILAS = 15; // A..O (0..14)
    static final int COLUMNAS = 20; // 1..20 (0..19)
    static final int[][] asientoId = new int[FILAS][COLUMNAS]; // 0 = libre; otro = ID de la venta

    // ====== SCANNER ======
    public static final Scanner sc = new Scanner(System.in);

    // ====== ENUM UBICACIONES ======
    enum Ubicacion {
        VIP(20000), PALCO (18000), PLATEABAJA (15000), PLATEAALTA (13000), GALERIA (10000);
        private final int precioBase;
        Ubicacion(int precioBase) { this.precioBase = precioBase; }
        public int getPrecioBase() { return precioBase; }
    }

    // ====== CLASE VENTA ======
    public static class Venta {
        private int id;
        private int fila;
        private int columna;
        private Ubicacion ubicacion;
        private int precioBase;
        private String tipoDescuento;
        private int porcentajeDescuento;
        private int precioFinal;

        public Venta(
            int id, Ubicacion ubicacion, int precioBase,
            String tipoDescuento, int porcentajeDescuento, int precioFinal,
            int fila, int columna
        ) {
            this.id = id;
            this.ubicacion = ubicacion;
            this.precioBase = precioBase;
            this.tipoDescuento = tipoDescuento;
            this.porcentajeDescuento = porcentajeDescuento;
            this.precioFinal = precioFinal;
            this.fila = fila;
            this.columna = columna;
        }

        public int getId() { return id; }
        public Ubicacion getUbicacion() { return ubicacion; }
        public int getPrecioBase() { return precioBase; }
        public String getTipoDescuento() { return tipoDescuento; }
        public int getPorcentajeDescuento() { return porcentajeDescuento; }
        public int getPrecioFinal() { return precioFinal; }
        public int getFila() { return fila; }
        public int getColumna() { return columna; }
        public int getMontoDescuento() {
            return precioBase - precioFinal;
            }
        }
    
    static final Locale CL = new Locale("es", "CL");
    static final DecimalFormat miles = (DecimalFormat) NumberFormat.getNumberInstance(CL);
    static {
        miles.setGroupingUsed(true);
        miles.setMinimumFractionDigits(0);
        miles.setMaximumFractionDigits(0);
    }
    
    public static void main(String[] args) {
        boolean salir = false;
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero();
            switch (opcion) {
                case 1 -> venderEntrada();
                case 2 -> mostrarResumenVentas();
                case 3 -> generarBoletas();
                case 4 -> mostrarIngresosTotales();
                case 5 -> modificarReserva();
                case 6 -> cancelarReserva();
                case 7 -> buscarReserva();
                case 8 -> mostrarMapa();
                case 9 -> {
                    System.out.println("\nGracias por su compra. ¡Vuelva pronto!");
                    salir = true;
                }
                default -> System.out.println("\nOpción inválida, intenta nuevamente.\n");
            }
        } while (!salir);

        sc.close();
    }
    
    // ====== MENÚ ======
    public static void mostrarMenu() {
        System.out.println("\n==============================");
        System.out.println("      TEATRO MORO - MENÚ");
        System.out.println("==============================");
        System.out.println("1) Venta de entradas");
        System.out.println("2) Visualizar resumen de ventas");
        System.out.println("3) Generar boleta(s)");
        System.out.println("4) Calcular ingresos totales");
        System.out.println("5) Modificar reserva");
        System.out.println("6) Cancelar reserva");
        System.out.println("7) Buscar reservas");
        System.out.println("8) Mostrar mapa de asientos");
        System.out.println("9) Salir");
        System.out.print("Seleccione una opción: ");
    }

    // ====== OPCIONES ======
    
    //Opción 1: Venta de entradas
    static void venderEntrada() {
        System.out.println("\n--- Venta de Entradas ---");

        // 1) Ubicación y precio base
        Ubicacion ubic = seleccionarUbicacion();
        int precioBase = ubic.getPrecioBase();

        // 2) Descuento
        int porcentajeDesc = 0;
        String tipoDesc = "Sin descuento";
        if (preguntarSiNo("¿Es estudiante? (S/N): ")) {
            porcentajeDesc = 25;
            tipoDesc = "Estudiante";
        } else {
            System.out.print("Ingrese edad: ");
            int edad = leerEntero();
            if (edad >= 65) {
                porcentajeDesc = 30;
                tipoDesc = "Tercera edad";
            } else if (preguntarSiNo ("¿Es mujer? (S/N): ")) {
                porcentajeDesc = 7;
                tipoDesc = "Mujer";
            } else if (edad <= 15) {
                porcentajeDesc = 5;
                tipoDesc = "Niño";
            }
        }
        
        // 3) Selección de asiento
        System.out.println("\nSeleccione su asiento:");
        mostrarMapa();
        int f, c;
        while (true) {
        f = leerFila();
        c = leerColumna();
        if (asientoId[f][c] == 0) break;
        System.out.println("Ese asiento está ocupado. Elige otro.\n");
        }

        // 4) Registrar venta
        int precioFinal = aplicarDescuento(precioBase, porcentajeDesc);
        int id = nextId++;
        Venta venta = new Venta(id, ubic, precioBase, tipoDesc, porcentajeDesc, precioFinal, f, c);
        ventas.add(venta);
        ventasPorId.put(id, venta);
        asientoId[f][c] = id;

        // Actualizar estadísticas
        totalEntradasVendidas++;
        ingresosTotales += precioFinal;
        if (porcentajeDesc > 0) totalDescuentosAplicados++;

        System.out.printf("\nVenta registrada correctamente. ID: %d | Asiento: %s%n%n",
                id, etiquetaAsiento(f, c));
    }
    
    //Opción 2: Mostrar resumen de ventas
    static void mostrarResumenVentas() {
        System.out.println("\n--- Resumen de Ventas ---");
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas aún.\n");
            return;
        }
        for (Venta v : ventas) {
            System.out.printf("ID:%d | Asiento:%s | Ubicación:%s | Base:$%s | Desc:%s (%d%%) | Final:$%s%n",
        v.getId(),
        etiquetaAsiento(v.getFila(), v.getColumna()),
        v.getUbicacion(),
        formatoPesos(v.getPrecioBase()),       
        v.getTipoDescuento(),
        v.getPorcentajeDescuento(),
        formatoPesos(v.getPrecioFinal()));
        }
        
        System.out.printf("\nTotal de ventas: %d | Con descuento: %d | Ingresos: $%s%n%n",
            totalEntradasVendidas, totalDescuentosAplicados, formatoPesos(ingresosTotales));
    }
    
    //Opción 3: Generar boletas
    static void generarBoletas() {
    System.out.println("\n--- Generación de Boletas ---");
    if (ventas.isEmpty()) {
        System.out.println("No hay ventas para generar boletas.\n");
        return;
    }
    for (Venta v : ventas) {
        imprimirBoleta(v); // solo la venta
    }
    }
    
    //Opción 4: Mostrar ingresos totales
    static void mostrarIngresosTotales() {
        System.out.println("\n--- Ingresos Totales ---");
        System.out.printf("Entradas vendidas: %d%n", totalEntradasVendidas);
        System.out.printf("Con descuento: %d%n", totalDescuentosAplicados);
        System.out.printf("Ingresos acumulados: $%s%n%n", formatoPesos(ingresosTotales));
    }
    
    //Opción 5: Modificar reserva
    private static void modificarReserva() {
        System.out.println("\n--- Modificar reserva ---");
        if (ventas.isEmpty()) { System.out.println("No hay reservas.\n"); return; }

        System.out.print("Ingrese ID de reserva: ");
        int id = leerEntero();
        int idx = indexOfVentaById(id);
        if (idx == -1) { System.out.println("No se encontró la reserva.\n"); return; }

        Venta v = ventas.get(idx);
        System.out.printf("Actual: Asiento %s | Ubicación %s | Total $%s%n",
            etiquetaAsiento(v.getFila(), v.getColumna()), v.getUbicacion(), formatoPesos(v.getPrecioFinal()));

        // Cambiar asiento
        if (preguntarSiNo("¿Desea cambiar el asiento? (S/N): ")) {
            mostrarMapa();
            int nf = leerFila();
            int nc = leerColumna();
            if (asientoId[nf][nc] != 0) { System.out.println("Asiento ocupado.\n"); return; }
        
        // liberar asiento anterior y ocupar el nuevo
        asientoId[v.getFila()][v.getColumna()] = 0;
        asientoId[nf][nc] = v.getId();
        
        // actualizar objeto
        v.columna = nc; v.fila = nf;
        }

    // Cambiar ubicación (recalcula precios)
    if (preguntarSiNo("¿Desea cambiar la ubicación? (S/N): ")) {
        Ubicacion nueva = seleccionarUbicacion();
        int nuevoBase = nueva.getPrecioBase();
        
        // rehacer descuento manteniendo el tipo original
        int nuevoFinal = aplicarDescuento(nuevoBase, v.getPorcentajeDescuento());
        
        // ajustar estadísticas: restar antiguo y sumar nuevo
        ingresosTotales -= v.getPrecioFinal();
        ingresosTotales += nuevoFinal;
        
        // actualizar venta
        v.ubicacion = nueva;
        v.precioBase = nuevoBase;
        v.precioFinal = nuevoFinal;
    }

    System.out.println("Reserva modificada correctamente.\n");
    }
    
    //Opción 6: cancelar reserva
    private static void cancelarReserva() {
        System.out.println("\n--- Cancelar reserva ---");
        if (ventas.isEmpty()) {
            System.out.println("No hay reservas para cancelar.\n");
            return;
        }
        System.out.print("Ingrese el ID de la reserva a cancelar: ");
        int id = leerEntero();

        int idx = indexOfVentaById(id);
        if (idx == -1) {
            System.out.println("No se encontró una reserva con ese ID.\n");
            return;
        }

        Venta v = ventas.get(idx);
        System.out.printf("Reserva encontrada -> ID:%d | Asiento:%s | %s | Total:$%s%n",
            v.getId(), etiquetaAsiento(v.getFila(), v.getColumna()), v.getUbicacion(), formatoPesos(v.getPrecioFinal()));

        if (!preguntarSiNo("¿Confirmar cancelación? (S/N): ")) {
            System.out.println("Operación cancelada.\n");
            return;
        }

        // Revertir estadísticas
        totalEntradasVendidas--;
        ingresosTotales -= v.getPrecioFinal();
        if (v.getPorcentajeDescuento() > 0) totalDescuentosAplicados--;

        // Liberar asiento
        asientoId[v.getFila()][v.getColumna()] = 0;
        ventas.remove(idx);
        ventasPorId.remove(id);

        System.out.println("Reserva cancelada correctamente.\n");
    }
    
    //Opción 7: Buscar reservas
    private static void buscarReserva() {
        System.out.println("\n--- Buscar reserva ---");
        if (ventas.isEmpty()) {
            System.out.println("No hay reservas registradas.\n");
            return;
        }
        System.out.print("Ingrese el ID de reserva a buscar: ");
        int id = leerEntero();

        Venta v = findVentaById(id);
        if (v == null) {
            System.out.println("No se encontró una reserva con ese ID.\n");
            return;
        }

        System.out.println("\n======= RESERVA ENCONTRADA =======");
        System.out.printf("ID: %d%n", v.getId());
        System.out.printf("Asiento: %s%n", etiquetaAsiento(v.getFila(), v.getColumna()));
        System.out.printf("Ubicación: %s%n", v.getUbicacion());
        System.out.printf("Precio base: $%s%n", formatoPesos(v.getPrecioBase()));
        String descLinea = v.getPorcentajeDescuento() > 0 ?
                v.getTipoDescuento() + " (" + v.getPorcentajeDescuento() + "%)" : "Sin descuento";
        System.out.printf("Descuento: %s%n", descLinea);
        System.out.printf("Total pagado: $%s%n", formatoPesos(v.getPrecioFinal()));
        System.out.println("==================================\n");
    }
     
     //Opción 8: Mostrar Mapa de Asientos
    static void mostrarMapa() {
        System.out.println("\nMapa de asientos (L=libre, X=ocupado):");
        System.out.print("   ");
        for (int c = 1; c <= COLUMNAS; c++) {
            System.out.print(String.format("%02d ", c));
        }
        System.out.println();
        for (int f = 0; f < FILAS; f++) {
            char filaChar = (char) ('A' + f);
            System.out.print(filaChar + "  ");
            for (int c = 0; c < COLUMNAS; c++) {
                System.out.print(asientoId[f][c] == 0 ? "L  " : "X  ");
            }
            System.out.println();
        }
        System.out.println();
    }


    // ====== UTILIDADES ======
    
    //Para verificar opción válida
    private static int leerEntero() {
        while (true) {
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida, intenta nuevamente: ");
            }
        }
    }
    
    static Ubicacion seleccionarUbicacion() {
        while (true) {
            System.out.println("Elige la ubicación:");
            System.out.println("1) VIP ($20.000) Filas A-B-C / Columnas 05 a 16 ");
            System.out.println("2) Palco ($18.000) Filas A-B-C / Columnas 01 a 04 y 17 a 20");
            System.out.println("3) Platea Baja ($15.000) Filas D-E-F-G / Columnas 01 a 20");
            System.out.println("4) Platea Alta ($13.000) Filas H-I-J-K / Columnas 01 a 20");
            System.out.println("5) Galeria ($10.000) Filas L-M-N-O / Columnas 01 a 20");
            int op = leerEntero();
            switch (op) {
                case 1: return Ubicacion.VIP;
                case 2: return Ubicacion.PALCO;
                case 3: return Ubicacion.PLATEABAJA;
                case 4: return Ubicacion.PLATEAALTA;
                case 5: return Ubicacion.GALERIA;
                default: System.out.println("Opción inválida. Intenta nuevamente.\n");
            }
        }
    }
    
    static int aplicarDescuento(int precioBase, int porcentaje) {
        double factor = 1 - (porcentaje / 100.0);
        return (int) Math.round(precioBase * factor);
    }
    
    //Para verificar respuesta sí o no
    static boolean preguntarSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String r = sc.nextLine().trim();
            if (r.equalsIgnoreCase("S")) return true;
            if (r.equalsIgnoreCase("N")) return false;
            System.out.println("Responde S o N, por favor.");
        }
    }
    
    //Para buscar el índice de la venta por ID 
    private static int indexOfVentaById(int id) {
    Venta v = ventasPorId.get(id);
    return (v == null) ? -1 : ventas.indexOf(v); // usa el mapa y evita recorrer todo
    }
    
    //Método para buscar ventas por ID
    private static Venta findVentaById(int id) {
    return ventasPorId.get(id); // O(1)
    }

    // ====== ASIENTOS ======
    static int leerFila() {
        while (true) {
            System.out.print("Fila (A-O): ");
            String s = sc.nextLine().trim().toUpperCase();
            if (s.length() == 1 && s.charAt(0) >= 'A' && s.charAt(0) <= 'O')
                return s.charAt(0) - 'A';
            System.out.println("Fila inválida.");
        }
    }

    static int leerColumna() {
        while (true) {
            System.out.print("Columna (1-20): ");
            try {
                int c = Integer.parseInt(sc.nextLine().trim());
                if (c >= 1 && c <= 20) return c - 1;
            } catch (NumberFormatException ignore) {}
            System.out.println("Columna inválida.");
        }
    }

    static String etiquetaAsiento(int f, int c) {
        char filaChar = (char) ('A' + f);
        return filaChar + String.format("%02d", (c + 1));
    }
    
    //Método para formato de pesos
    static String formatoPesos(int monto) {
    return miles.format(monto);
    }
    
    // ====== IMPRIMIR BOLETA ======
    public static void imprimirBoleta(Venta venta) {
    System.out.println("\n======= BOLETA DE COMPRA =======");
    System.out.println("ID venta: " + venta.getId());
    System.out.println("Asiento: " + etiquetaAsiento(venta.getFila(), venta.getColumna()));
    System.out.println("Ubicación: " + venta.getUbicacion());
    System.out.println("Precio base: $" + formatoPesos(venta.getPrecioBase()));
    System.out.println("Descuento: $" + formatoPesos(venta.getMontoDescuento()));
    System.out.println("Total a pagar: $" + formatoPesos(venta.getPrecioFinal()));
    System.out.println("================================\n");
    }
    
}
