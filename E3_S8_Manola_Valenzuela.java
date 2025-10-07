/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package e3_s8_manola_valenzuela;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class E3_S8_Manola_Valenzuela {

    // ====== ESTADÍSTICAS GLOBALES ======
    public static int totalEntradasVendidas = 0;
    public static int totalDescuentosAplicados = 0;
    public static int ingresosTotales = 0;

    // ====== VENTAS ======
    public static List<Venta> ventas = new ArrayList<>();
    private static int nextId = 1001; // Generador de IDs

    // ====== SALA / ARREGLO DE ASIENTOS ======
    static final int FILAS = 10; // A..J (0..9)
    static final int COLUMNAS = 15; // 1..15 (0..14)
    static int[][] asientoId = new int[FILAS][COLUMNAS]; // 0 = libre; otro = ID de la venta

    // ====== SCANNER ======
    public static final Scanner sc = new Scanner(System.in);

    // ====== ENUM UBICACIONES ======
    enum Ubicacion {
        VIP(20000), PLATEA(15000), BALCON(10000);
        private final int precioBase;
        Ubicacion(int precioBase) { this.precioBase = precioBase; }
        public int getPrecioBase() { return precioBase; }
    }

    // ====== CLASE VENTA ======
    public static class Venta {
        private final int id;
        private final int fila;
        private final int columna;
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
    }

    // ====== MAIN ======
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
                case 5 -> cancelarReserva();
                case 6 -> buscarReserva();
                case 7 -> mostrarMapa();
                case 8 -> {
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
        System.out.println("5) Cancelar reserva");
        System.out.println("6) Buscar reservas");
        System.out.println("7) Mostrar mapa de asientos");
        System.out.println("8) Salir");
        System.out.print("Seleccione una opción: ");
    }

    // ====== OPCIONES ======
    static void venderEntrada() {
        System.out.println("\n--- Venta de Entradas ---");

        // 1) Ubicación y precio base
        Ubicacion ubic = seleccionarUbicacion();
        int precioBase = ubic.getPrecioBase();

        // 2) Descuento
        int porcentajeDesc = 0;
        String tipoDesc = "Sin descuento";
        if (preguntarSiNo("¿Es estudiante? (S/N): ")) {
            porcentajeDesc = 10;
            tipoDesc = "Estudiante";
        } else {
            System.out.print("Ingrese edad: ");
            int edad = leerEntero();
            if (edad >= 65) {
                porcentajeDesc = 15;
                tipoDesc = "Tercera edad";
            }
        }

        // 3) Selección de asiento
        System.out.println("\nSeleccione su asiento:");
        mostrarMapa();
        int f = leerFila();
        int c = leerColumna();
        if (asientoId[f][c] != 0) {
            System.out.println("Ese asiento está ocupado. Intenta con otro.\n");
            return;
        }

        // 4) Registrar venta
        int precioFinal = aplicarDescuento(precioBase, porcentajeDesc);
        int id = nextId++;
        Venta venta = new Venta(id, ubic, precioBase, tipoDesc, porcentajeDesc, precioFinal, f, c);
        ventas.add(venta);
        asientoId[f][c] = id;

        // Actualizar estadísticas
        totalEntradasVendidas++;
        ingresosTotales += precioFinal;
        if (porcentajeDesc > 0) totalDescuentosAplicados++;

        System.out.printf(Locale.US, "\nVenta registrada correctamente. ID: %d | Asiento: %s%n%n",
                id, etiquetaAsiento(f, c));
    }

    static void mostrarResumenVentas() {
        System.out.println("\n--- Resumen de Ventas ---");
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas aún.\n");
            return;
        }
        for (Venta v : ventas) {
            System.out.printf(Locale.US,
                    "ID:%d | Asiento:%s | Ubicación:%s | Base:$%,d | Desc:%s (%d%%) | Final:$%,d%n",
                    v.getId(), etiquetaAsiento(v.getFila(), v.getColumna()), v.getUbicacion(),
                    v.getPrecioBase(), v.getTipoDescuento(), v.getPorcentajeDescuento(), v.getPrecioFinal());
        }
        System.out.printf(Locale.US,
                "\nTotal de ventas: %d | Con descuento: %d | Ingresos: $%,d%n%n",
                totalEntradasVendidas, totalDescuentosAplicados, ingresosTotales);
    }

    static void generarBoletas() {
        System.out.println("\n--- Generación de Boletas ---");
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas para generar boletas.\n");
            return;
        }
        int i = 1;
        for (Venta v : ventas) {
            imprimirBoleta(i++, v);
        }
    }

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
        System.out.printf(Locale.US, "Reserva encontrada -> ID:%d | Asiento:%s | %s | Total:$%,d%n",
                v.getId(), etiquetaAsiento(v.getFila(), v.getColumna()), v.getUbicacion(), v.getPrecioFinal());

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

        System.out.println("Reserva cancelada correctamente.\n");
    }

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
        System.out.printf(Locale.US, "Precio base: $%,d%n", v.getPrecioBase());
        String descLinea = v.getPorcentajeDescuento() > 0 ?
                v.getTipoDescuento() + " (" + v.getPorcentajeDescuento() + "%)" : "Sin descuento";
        System.out.printf("Descuento: %s%n", descLinea);
        System.out.printf(Locale.US, "Total pagado: $%,d%n", v.getPrecioFinal());
        System.out.println("==================================\n");
    }

    static void imprimirBoleta(int numero, Venta v) {
        System.out.println("================ BOLETA ================");
        System.out.printf(Locale.US, "N°: %03d | Teatro: %s | ID: %d | Asiento: %s%n",
                numero, "Teatro Moro", v.getId(), etiquetaAsiento(v.getFila(), v.getColumna()));
        System.out.println("----------------------------------------");
        System.out.printf(Locale.US, "Ubicación: %-8s     Base: $%,d%n", v.getUbicacion(), v.getPrecioBase());
        String descLinea = v.getPorcentajeDescuento() > 0 ?
                v.getTipoDescuento() + " (" + v.getPorcentajeDescuento() + "%)" : "Sin descuento";
        System.out.printf("Descuento: %s%n", descLinea);
        System.out.printf(Locale.US, "TOTAL A PAGAR: $%,d%n", v.getPrecioFinal());
        System.out.println("----------------------------------------");
        System.out.println("¡Gracias por su compra y preferencia!");
        System.out.println("========================================\n");
    }

    static void mostrarIngresosTotales() {
        System.out.println("\n--- Ingresos Totales ---");
        System.out.printf("Entradas vendidas: %d%n", totalEntradasVendidas);
        System.out.printf("Con descuento: %d%n", totalDescuentosAplicados);
        System.out.printf(Locale.US, "Ingresos acumulados: $%,d%n%n", ingresosTotales);
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
            System.out.println("1) VIP ($20.000)");
            System.out.println("2) Platea ($15.000)");
            System.out.println("3) Balcón ($10.000)");
            int op = leerEntero();
            switch (op) {
                case 1: return Ubicacion.VIP;
                case 2: return Ubicacion.PLATEA;
                case 3: return Ubicacion.BALCON;
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

    private static int indexOfVentaById(int id) {
        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getId() == id) return i;
        }
        return -1;
    }

    private static Venta findVentaById(int id) {
        int idx = indexOfVentaById(id);
        return (idx == -1) ? null : ventas.get(idx);
    }

    // ====== ASIENTOS ======
    static int leerFila() {
        while (true) {
            System.out.print("Fila (A-J): ");
            String s = sc.nextLine().trim().toUpperCase();
            if (s.length() == 1 && s.charAt(0) >= 'A' && s.charAt(0) <= 'J')
                return s.charAt(0) - 'A';
            System.out.println("Fila inválida.");
        }
    }

    static int leerColumna() {
        while (true) {
            System.out.print("Columna (1-15): ");
            try {
                int c = Integer.parseInt(sc.nextLine().trim());
                if (c >= 1 && c <= 15) return c - 1;
            } catch (NumberFormatException ignore) {}
            System.out.println("Columna inválida.");
        }
    }

    static String etiquetaAsiento(int f, int c) {
        char filaChar = (char) ('A' + f);
        return filaChar + String.format("%02d", (c + 1));
    }

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
}

