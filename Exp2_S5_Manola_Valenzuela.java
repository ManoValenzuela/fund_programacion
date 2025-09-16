/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package exp2_s5_manola_valenzuela;

import java.util.Scanner;

/**
 *
 * @author naru
 */

public class Exp2_S5_Manola_Valenzuela {

    // ----- Config & estado general -----
    static String nombreTeatro = "Teatro Moro";
    static int capacidadTotal = 150;

    static int entradasVendidas;       // contador de filas usadas
    static int ingresosPorEntradas;    // suma de precios finales
    static int totalVIP;
    static int totalPlatea;
    static int totalGeneral;

    // ----- "Base de datos" con arreglos paralelos -----
    static int nextTicketId = 1;
    static int[] ids               = new int[capacidadTotal];
    static String[] ubicaciones    = new String[capacidadTotal]; // "VIP" / "Platea" / "General"
    static int[] preciosBase       = new int[capacidadTotal];
    static int[] preciosFinales    = new int[capacidadTotal];
    static boolean[] esEstudiante  = new boolean[capacidadTotal];
    static boolean[] esTercera     = new boolean[capacidadTotal];

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int op;
        do {
            System.out.println("\n=== " + nombreTeatro + " - Sistema de Entradas ===");
            System.out.println("1. Venta de entradas");
            System.out.println("2. Promociones");
            System.out.println("3. Búsqueda de entradas");
            System.out.println("4. Eliminación de entradas");
            System.out.println("5. Resumen");
            System.out.println("0. Salir");
            while (!sc.hasNextInt()) { System.out.println("Opción inválida."); sc.next(); }
            op = sc.nextInt();

            switch (op) {
                case 1 -> venderEntrada();
                case 2 -> mostrarPromociones();
                case 3 -> buscarEntradas();
                case 4 -> eliminarEntrada();
                case 5 -> mostrarResumen();
                case 0 -> System.out.println("Gracias por usar nuestro Sistema de Entradas");
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    public static void venderEntrada() {
        if (entradasVendidas >= capacidadTotal) {
            System.out.println("Sin cupos disponibles.");
            return;
        }

        String ubic, ubicFinal = "";
        int entradaVIP = 25000, entradaPlatea = 20000, entradaGeneral = 15000;
        int precioBase = 0;
        int precioFinal = 0;
        boolean ok = false;

        // Elegir ubicación
        do {
            System.out.println("Ingrese la ubicación que desea comprar:");
            System.out.println("1. VIP: $" + entradaVIP +
                               "\n2. Platea: $" + entradaPlatea +
                               "\n3. General: $" + entradaGeneral);
            ubic = sc.next().trim();

            if (ubic.equals("1") || ubic.equalsIgnoreCase("VIP")) {
                ubicFinal = "VIP"; precioBase = entradaVIP; ok = true;
            } else if (ubic.equals("2") || ubic.equalsIgnoreCase("Platea")) {
                ubicFinal = "Platea"; precioBase = entradaPlatea; ok = true;
            } else if (ubic.equals("3") || ubic.equalsIgnoreCase("General")) {
                ubicFinal = "General"; precioBase = entradaGeneral; ok = true;
            } else {
                System.out.println("Ubicación no válida. Intente nuevamente.");
                ok = false;
            }
        } while (!ok);

        // Descuentos (marca tipo para búsquedas)
        boolean descuentoAplicado = false;
        boolean marcaEst = false;
        boolean marcaTer = false;

        do {
            System.out.println("¿Es estudiante? (S/N)");
            String resp = sc.next().trim();

            if (resp.equalsIgnoreCase("S")) {          // 10% estudiante
                precioFinal = (int) Math.round(precioBase * 0.90);
                descuentoAplicado = true;
                marcaEst = true;

            } else if (resp.equalsIgnoreCase("N")) {   // no estudiante -> pedir edad
                System.out.println("Ingrese su edad");
                while (!sc.hasNextInt()) {
                    System.out.println("Edad inválida, intente de nuevo:");
                    sc.next();
                }
                int edad = sc.nextInt();

                if (edad >= 65) {
                    precioFinal = (int) Math.round(precioBase * 0.85); // 15% tercera edad
                    marcaTer = true;
                } else {
                    precioFinal = precioBase; // sin descuento
                }
                descuentoAplicado = true;

            } else {
                System.out.println("Opción inválida. Escriba S o N.");
            }
        } while (!descuentoAplicado);

        // Guardar ticket en "BD"
        int id = nextTicketId++;
        int i = entradasVendidas; // índice de inserción
        ids[i] = id;
        ubicaciones[i] = ubicFinal;
        preciosBase[i] = precioBase;
        preciosFinales[i] = precioFinal;
        esEstudiante[i] = marcaEst;
        esTercera[i] = marcaTer;
        entradasVendidas++;

        // Acumular métricas
        ingresosPorEntradas += precioFinal;
        if (ubicFinal.equals("VIP")) totalVIP++;
        else if (ubicFinal.equals("Platea")) totalPlatea++;
        else totalGeneral++;

        System.out.println("Ticket → ID #" + id +
                           " | " + ubicFinal +
                           " | Base $" + precioBase +
                           " | Total $" + precioFinal);
    }

    public static void mostrarPromociones() { // solo mostrar
        System.out.println("\n--- Promociones disponibles ---");
        System.out.println("1) Estudiantes: 10% de descuento");
        System.out.println("2) Adultos mayores (>=65): 15% de descuento");
        System.out.println("3) Compras de más de 5 entradas: 10% (no acumulable)");
        System.out.println("**Las promociones no son acumulables entre sí**");
    }

    public static void buscarEntradas() {
        if (entradasVendidas == 0) {
            System.out.println("No hay tickets vendidos aún.");
            return;
        }

        boolean ok = false;
        do {
            System.out.println("\nSeleccione cómo desea buscar su entrada");
            System.out.println("1. Por identificador");
            System.out.println("2. Por ubicación");
            System.out.println("3. Por tipo (estudiante / adulto mayor)");
            String buscar = sc.next().trim();

            // ---- Por ID ----
            if (buscar.equals("1") || buscar.equalsIgnoreCase("Identificador")) {
                System.out.println("Ingrese el identificador (número):");
                while (!sc.hasNextInt()) { System.out.println("ID inválido, número por favor:"); sc.next(); }
                int ID = sc.nextInt();

                boolean found = false;
                for (int j = 0; j < entradasVendidas; j++) {
                    if (ids[j] == ID) {
                        String etiquetaTipo = esEstudiante[j] ? "EST" : (esTercera[j] ? "TER" : "STD");
                        System.out.println("Ticket #" + ids[j] +
                                           " | Ubic: " + ubicaciones[j] +
                                           " | Base $" + preciosBase[j] +
                                           " | Total $" + preciosFinales[j] +
                                           " | " + etiquetaTipo);
                        found = true;
                        break;
                    }
                }
                if (!found) System.out.println("No existe el ID #" + ID + ".");
                ok = true;

            // ---- Por ubicación ----
            } else if (buscar.equals("2") || buscar.equalsIgnoreCase("Ubicación")
                    || buscar.equalsIgnoreCase("Ubicacion")) {

                System.out.println("Ingrese ubicación (1=VIP, 2=Platea, 3=General, o texto):");
                String u = sc.next().trim();
                String objetivo;
                if (u.equals("1") || u.equalsIgnoreCase("VIP")) objetivo = "VIP";
                else if (u.equals("2") || u.equalsIgnoreCase("Platea")) objetivo = "Platea";
                else if (u.equals("3") || u.equalsIgnoreCase("General")) objetivo = "General";
                else { System.out.println("Ubicación inválida."); continue; }

                int count = 0;
                for (int j = 0; j < entradasVendidas; j++) {
                    if (ubicaciones[j].equalsIgnoreCase(objetivo)) {
                        String etiquetaTipo = esEstudiante[j] ? "EST" : (esTercera[j] ? "TER" : "STD");
                        System.out.println("Ticket #" + ids[j] +
                                           " | Ubic: " + ubicaciones[j] +
                                           " | Base $" + preciosBase[j] +
                                           " | Total $" + preciosFinales[j] +
                                           " | " + etiquetaTipo);
                        count++;
                    }
                }
                if (count == 0) System.out.println("Sin resultados para " + objetivo + ".");
                ok = true;

            // ---- Por tipo ----
            } else if (buscar.equals("3") || buscar.equalsIgnoreCase("Tipo")) {
                System.out.println("Elija tipo: 1) Estudiante  2) Adulto mayor");
                String t = sc.next().trim();
                Integer tipo = null;
                if (t.equals("1") || t.equalsIgnoreCase("Estudiante")) tipo = 1;
                else if (t.equals("2") || t.equalsIgnoreCase("Adulto") || t.equalsIgnoreCase("Mayor")) tipo = 2;
                else { System.out.println("Tipo inválido."); continue; }

                int count = 0;
                for (int j = 0; j < entradasVendidas; j++) {
                    boolean match = (tipo == 1 && esEstudiante[j]) || (tipo == 2 && esTercera[j]);
                    if (match) {
                        String etiquetaTipo = esEstudiante[j] ? "EST" : (esTercera[j] ? "TER" : "STD");
                        System.out.println("Ticket #" + ids[j] +
                                           " | Ubic: " + ubicaciones[j] +
                                           " | Base $" + preciosBase[j] +
                                           " | Total $" + preciosFinales[j] +
                                           " | " + etiquetaTipo);
                        count++;
                    }
                }
                if (count == 0) System.out.println("Sin resultados para ese tipo.");
                ok = true;

            } else {
                System.out.println("Parámetro no válido. Intente nuevamente.");
            }

        } while (!ok);
    }

    public static void eliminarEntrada() {
        if (entradasVendidas == 0) {
            System.out.println("No hay tickets para eliminar.");
            return;
        }
        System.out.println("Ingrese el identificador (ID) del ticket a eliminar:");
        while (!sc.hasNextInt()) { System.out.println("ID inválido, número por favor:"); sc.next(); }
        int ID = sc.nextInt();

        int idx = -1;
        for (int j = 0; j < entradasVendidas; j++) {
            if (ids[j] == ID) { idx = j; break; }
        }
        if (idx == -1) {
            System.out.println("No existe el ID #" + ID + ".");
            return;
        }

        // actualizar métricas antes de borrar
        ingresosPorEntradas -= preciosFinales[idx];
        if (ubicaciones[idx].equals("VIP")) totalVIP--;
        else if (ubicaciones[idx].equals("Platea")) totalPlatea--;
        else totalGeneral--;

        // compactar: mover el último registro al hueco
        int last = entradasVendidas - 1;
        if (idx != last) {
            ids[idx] = ids[last];
            ubicaciones[idx] = ubicaciones[last];
            preciosBase[idx] = preciosBase[last];
            preciosFinales[idx] = preciosFinales[last];
            esEstudiante[idx] = esEstudiante[last];
            esTercera[idx] = esTercera[last];
        }
        entradasVendidas--;
        System.out.println("Ticket #" + ID + " eliminado.");
    }

    public static void mostrarResumen() {
        int disponibles = Math.max(capacidadTotal - entradasVendidas, 0);
        System.out.println("\n--- Resumen ---");
        System.out.println("Vendidas: " + entradasVendidas + " | Disponibles: " + disponibles);
        System.out.println("Ingresos: $" + ingresosPorEntradas);
        System.out.println("Por ubicación → VIP: " + totalVIP +
                           " | Platea: " + totalPlatea +
                           " | General: " + totalGeneral);
    }
}

    
