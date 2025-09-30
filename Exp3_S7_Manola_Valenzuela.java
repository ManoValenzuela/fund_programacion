/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package exp3_s7_manola_valenzuela;

import java.util.*;

public class Exp3_S7_Manola_Valenzuela {

    // Variables ESTÁTICAS (estadísticas globales)
    static int totalEntradasVendidas = 0;             // 1) contador global
    static int totalDescuentosAplicados = 0;          // 2) cuántas ventas tuvieron algún descuento
    static int ingresosTotales = 0;                   // 3) acumulador de ingresos ($)

    // Lista para almacenar ventas
    static List<Venta> ventas = new ArrayList<>();

    // Scanner global para entrada por consola
    static final Scanner sc = new Scanner(System.in);

    // Enum para Ubicaciones y sus precios base
    enum Ubicacion {
        VIP(20000), PLATEA(15000), BALCON(10000);
        private final int precioBase;
        Ubicacion(int precioBase) { this.precioBase = precioBase; }
        public int getPrecioBase() { return precioBase; }
    }

    // Clase de instancia que representa una venta
    // Variables de INSTANCIA (persisten por objeto Venta):
    // - ubicacion, precioBase, tipoDescuento, porcentajeDescuento, precioFinal
    static class Venta {
        private Ubicacion ubicacion;          // 1)
        private int precioBase;               // 2)
        private String tipoDescuento;         // 3) "Estudiante", "Tercera edad" o "Sin descuento"
        private int porcentajeDescuento;      // 4) 0, 10 o 15
        private int precioFinal;              // 5)

        public Venta(Ubicacion ubicacion, int precioBase, String tipoDescuento, int porcentajeDescuento, int precioFinal) {
            this.ubicacion = ubicacion;
            this.precioBase = precioBase;
            this.tipoDescuento = tipoDescuento;
            this.porcentajeDescuento = porcentajeDescuento;
            this.precioFinal = precioFinal;
        }

        public Ubicacion getUbicacion() { return ubicacion; }
        public int getPrecioBase() { return precioBase; }
        public String getTipoDescuento() { return tipoDescuento; }
        public int getPorcentajeDescuento() { return porcentajeDescuento; }
        public int getPrecioFinal() { return precioFinal; }
    }

    public static void main(String[] args) {
        // Variables LOCALES de apoyo (temporales)
        boolean salir = false;                     // 1) control del bucle principal
        int opcion;                                // 2) opción del menú
        String entradaUsuario;                     // 3) capturas de texto
        Ubicacion ubicacionSeleccionada = null;    // 4) selección temporal de ubicación

        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");

            switch (opcion) {
                case 1 -> venderEntrada();
                case 2 -> mostrarResumenVentas();
                case 3 -> generarBoletas();
                case 4 -> mostrarIngresosTotales();
                case 5 -> {
                    System.out.println("\nGracias por su compra. ¡Vuelva pronto!");
                    salir = true;
                }
                default -> System.out.println("\nOpción inválida, intenta nuevamente.\n");
            }
        } while (!salir);

        sc.close();
    }

    // Menú
    static void mostrarMenu() {
        System.out.println("\n==============================");
        System.out.println("      TEATRO MORO - MENÚ");
        System.out.println("==============================");
        System.out.println("1) Venta de entradas");
        System.out.println("2) Visualizar resumen de ventas");
        System.out.println("3) Generar boleta(s)");
        System.out.println("4) Calcular ingresos totales");
        System.out.println("5) Salir");
    }

    // Flujo de venta
    static void venderEntrada() {
        System.out.println("\n--- Venta de Entradas ---");
        Ubicacion ubic = seleccionarUbicacion();
        int precioBase = ubic.getPrecioBase();

        // Descuento: 10% estudiante, 15% tercera edad (>=65)
        int porcentajeDesc = 0;
        String tipoDesc = "Sin descuento";

        if (preguntarSiNo("¿Es estudiante? (S/N): ")) {
            porcentajeDesc = 10;
            tipoDesc = "Estudiante";
        } else {
            if (preguntarSiNo("¿Es persona de la tercera edad (>=65)? (S/N): ")) {
                porcentajeDesc = 15;
                tipoDesc = "Tercera edad";
            }
        }

        int precioFinal = aplicarDescuento(precioBase, porcentajeDesc);

        // Crear y guardar venta
        Venta venta = new Venta(ubic, precioBase, tipoDesc, porcentajeDesc, precioFinal);
        ventas.add(venta);

        // Actualizar estadísticas estáticas
        totalEntradasVendidas++;
        ingresosTotales += precioFinal;
        if (porcentajeDesc > 0) totalDescuentosAplicados++;

        System.out.println("\nVenta registrada correctamente.\n");
    }

    //  Resumen (lista)
    static void mostrarResumenVentas() {
        System.out.println("\n--- Resumen de Ventas ---");
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas aún.\n");
            return;
        }
        int i = 1;
        for (Venta v : ventas) {
            System.out.printf(Locale.US,
                "%d) Ubicación: %s | Base: $%,d | Descuento: %s (%d%%) | Final: $%,d%n",
                i++, v.getUbicacion(), v.getPrecioBase(), v.getTipoDescuento(), v.getPorcentajeDescuento(), v.getPrecioFinal());
        }
        System.out.printf(Locale.US, "\nTotal de ventas: %d | Con descuento: %d | Ingresos: $%,d%n\n",
                totalEntradasVendidas, totalDescuentosAplicados, ingresosTotales);
    }

    // Boleta(s)
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

    static void imprimirBoleta(int numero, Venta v) {
        System.out.println("================ BOLETA ================");
        System.out.printf("N°: %03d   | Teatro: %s%n", numero, "Teatro Moro");
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

    // Ingresos totales
    static void mostrarIngresosTotales() {
        System.out.println("\n--- Ingresos Totales ---");
        System.out.printf(Locale.US, "Entradas vendidas: %d%n", totalEntradasVendidas);
        System.out.printf(Locale.US, "Con descuento: %d%n", totalDescuentosAplicados);
        System.out.printf(Locale.US, "Ingresos acumulados: $%,d%n\n", ingresosTotales);
    }

    // Utilidades
    static Ubicacion seleccionarUbicacion() {
        while (true) {
            System.out.println("Elige la ubicación:");
            System.out.println("1) VIP ($20.000)");
            System.out.println("2) Platea ($15.000)");
            System.out.println("3) Balcón ($10.000)");
            int op = leerEntero("> ");
            switch (op) {
                case 1: return Ubicacion.VIP;
                case 2: return Ubicacion.PLATEA;
                case 3: return Ubicacion.BALCON;
                default: System.out.println("Opción inválida. Intenta nuevamente.\n");
            }
        }
    }

    static int aplicarDescuento(int precioBase, int porcentaje) {
        // redondeo a entero
        double factor = 1 - (porcentaje / 100.0);
        return (int)Math.round(precioBase * factor);
    }

    static int leerEntero(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Valor no válido. Intenta de nuevo.");
            }
        }
    }

    static boolean preguntarSiNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String r = sc.nextLine().trim();
            if (r.equalsIgnoreCase("S")) return true;
            if (r.equalsIgnoreCase("N")) return false;
            System.out.println("Responde S o N, por favor.");
        }
    }
}
