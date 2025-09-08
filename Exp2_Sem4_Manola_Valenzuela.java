/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package exp2_sem4_manola_valenzuela;

import java.util.Scanner;

/**
 *
 * @author naru
 */
public class Exp2_Sem4_Manola_Valenzuela {

    static boolean A1 = false, A2 = false, A3 = false, A4 = false, A5 = false, A6 = false, A7 = false;
    static boolean B1 = false, B2 = false, B3 = false, B4 = false, B5 = false, B6 = false, B7 = false;
    static boolean C1 = false, C2 = false, C3 = false, C4 = false, C5 = false, C6 = false, C7 = false;
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenido al Teatro Moro");
        for (int i = 0; ; i++) {
            System.out.println("¿Qué desea realizar? (Digite una opción)");
            System.out.println("Opción 1) Comprar entrada");
            System.out.println("Opción 2) Salir");
            int opcion = sc.nextInt();
            
            if (opcion == 1) {
                System.out.println("   ");
                for (int j = 1; j <= 7; j++) {
                    System.out.print("     " + j);
                }
                System.out.println("   ");
                
                System.out.println("A | "
                        + (A1 ? "[X]" : "[  ]")+ " "
                        + (A2 ? "[X]" : "[  ]")+ " "
                        + (A3 ? "[X]" : "[  ]")+ " "
                        + (A4 ? "[X]" : "[  ]")+ " "
                        + (A5 ? "[X]" : "[  ]")+ " "
                        + (A6 ? "[X]" : "[  ]")+ " "
                        + (A7 ? "[X]" : "[  ]")+ "  ($25000)");
                System.out.println("B | "
                        + (B1 ? "[X]" : "[  ]")+ " "
                        + (B2 ? "[X]" : "[  ]")+ " "
                        + (B3 ? "[X]" : "[  ]")+ " "
                        + (B4 ? "[X]" : "[  ]")+ " "
                        + (B5 ? "[X]" : "[  ]")+ " "
                        + (B6 ? "[X]" : "[  ]")+ " "
                        + (B7 ? "[X]" : "[  ]")+ "  ($20000)");
                System.out.println("C | "
                        + (C1 ? "[X]" : "[  ]")+ " "
                        + (C2 ? "[X]" : "[  ]")+ " "
                        + (C3 ? "[X]" : "[  ]")+ " "
                        + (C4 ? "[X]" : "[  ]")+ " "
                        + (C5 ? "[X]" : "[  ]")+ " "
                        + (C6 ? "[X]" : "[  ]")+ " "
                        + (C7 ? "[X]" : "[  ]")+ "  ($15000)");
                
                System.out.println("Seleccione su ubicación (ej.: A1, B3, C4)");
                String asiento = sc.next().trim().toUpperCase(); //para evitar errores: elimino espacios y transformo a mayúsculas
                if (asiento.length()!= 2){ //si el largo del asiento es distinto a 2 lo identifica como inválido
                    System.out.println("Código Inválido");
                    continue;
                }
                
                char fila = asiento.charAt(0); //aquí saca la fila que está en la posición 0 del asiento
                String numTxt = asiento.substring(1); //similar al anterior, pero para sacar el número del asiento, que no es un char
                int nro;
                try { //valida que sea letra y número
                    nro = Integer.parseInt(numTxt);
                    
                }catch(NumberFormatException e){
                    System.out.println("Número del asiento es inválido");
                    continue; //esto es para decirle que si ingresa un asiento mal AA, BB, vuelve a iniciar el código
                }
                
                if (fila < 'A' || fila > 'C' || nro <1 || nro > 7){ //valida que esté dentro de los rangos que le dimos
                    System.out.println("Asientos Fuera de rango (fila A-C, Columnas 1-7)");
                    continue;
                }
                
               boolean ocupado = false;
               
                  if (fila == 'A'){
                    if (nro == 1) ocupado = A1;
                    else if (nro == 2) ocupado = A2;
                    else if (nro == 3) ocupado = A3;
                    else if (nro == 4) ocupado = A4;
                    else if (nro == 5) ocupado = A5;
                    else if (nro == 6) ocupado = A6;
                    else ocupado = A7;
                    
                }
                
                 if (fila == 'B'){
                    if (nro == 1) ocupado = B1;
                    else if (nro == 2) ocupado = B2;
                    else if (nro == 3) ocupado = B3;
                    else if (nro == 4) ocupado = B4;
                    else if (nro == 5) ocupado = B5;
                    else if (nro == 6) ocupado = B6;
                    else ocupado = B7;
                    
                }
                 
                 if (fila == 'C'){
                    if (nro == 1) ocupado = C1;
                    else if (nro == 2) ocupado = C2;
                    else if (nro == 3) ocupado = C3;
                    else if (nro == 4) ocupado = C4;
                    else if (nro == 5) ocupado = C5;
                    else if (nro == 6) ocupado = C6;
                    else ocupado = C7;
                    
                }
                 
                if (ocupado){
                    System.out.println("[X] Ese asiento ya está ocupado, elegir otro");
                    continue;
                }
                
                int precio = 0;
                
                if (fila == 'A') precio = 25000;
                    else if (fila == 'B') precio = 20000;
                    else precio = 15000;
                
                boolean descuentoAplicado = false;
                do {
                    System.out.println("¿Es estudiante? (S/N)");
                    String resp = sc.next().trim();      

                    if (resp.equalsIgnoreCase("S")) { // 10% de descuento a estudiantes
        
                    precio = (int) (precio * 0.9);
                    descuentoAplicado = true;      
                    } else if (resp.equalsIgnoreCase("N")) { // no es estudiante: preguntamos edad y aplicamos 15% solo si > 65
        
                    System.out.println("Ingrese su edad");
                    while (!sc.hasNextInt()) {         // validación por si escriben texto
                    System.out.println("Edad inválida, intente de nuevo:");
                    sc.next();                      
                    }
                    
                    int edad = sc.nextInt();
                    
                    if (edad >= 65) {
                    precio = (int) (precio * 0.85); // descuento 15% adulto mayor
                    }
                    descuentoAplicado = true;
                   
                    } else {
                    System.out.println("Opción inválida. Escriba S o N."); // descuentoAplicado sigue false y repite el do-while
        
                    }
                    } while (!descuentoAplicado);
                
                System.out.println("Asiento "+asiento+ " - Precio: $"+precio);
                System.out.println("¿Confirmar Reserva (S/N?)");
                
                String conf = sc.next().trim().toUpperCase();
                
                if(!conf.equals("S")){
                    System.out.println("Reserva Cancelada");
                    continue;
                }
                
                if(fila == 'A'){
                    if (nro == 1) A1 = true;
                    else if (nro == 2) A2 = true;
                    else if (nro == 3) A3 = true;
                    else if (nro == 4) A4 = true;
                    else if (nro == 5) A5 = true;
                    else if (nro == 6) A6 = true;
                    else A7 = true;
                }
                
                if(fila == 'B'){
                    if (nro == 1) B1 = true;
                    else if (nro == 2) B2 = true;
                    else if (nro == 3) B3 = true;
                    else if (nro == 4) B4 = true;
                    else if (nro == 5) B5 = true;
                    else if (nro == 6) B6 = true;
                    else B7 = true;
                }
                
                if(fila == 'C'){
                    if (nro == 1) C1 = true;
                    else if (nro == 2) C2 = true;
                    else if (nro == 3) C3 = true;
                    else if (nro == 4) C4 = true;
                    else if (nro == 5) C5 = true;
                    else if (nro == 6) C6 = true;
                    else C7 = true;
                }
                
                System.out.println("");
                System.out.println("Asiento: "+ asiento);
                System.out.println("Fila: "+ fila +" | Columna: "+ nro);
                System.out.println("Total a pagar: $"+ precio);
                System.out.println("");
                System.out.println("¿Desea realizar otra compra? (S/N)");
                String otra = sc.next().trim();
                
                if (!otra.equalsIgnoreCase("S")) {
                System.out.println("¡Gracias por su compra!");
                break;
                }
            }
            
            else if (opcion == 2){
                System.out.println("Gracias por visitar el Teatro Moro!");
                break;
            } else {
                System.out.println("Opción no válida");
            }
        }
        
        sc.close();
    }
    
}
