/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JulianCamilo
 */
public class AgenteServidor extends Agent {

    private ComportamientoNonogramaNormal comportamientoNormal;
    private ComportamientoNonogramaInverso comportamientoInverso;

    /**
     * Método sobre carga del agente, que decide el tipo de comportamiento
     * de acuerdo al escogido por el usuario en la ejecución
     */
    @Override
    protected void setup() {

        String archivo = (String) this.getArguments()[0];
        int modo = Integer.parseInt(this.getArguments()[1].toString());
        System.out.println("Bienvenido, estamos preparandolo todo para resolver tu nonograma");
        if (modo == 0) {
            System.out.println("Procesando nonograma: " + archivo + " en modo normal");
            comportamientoNormal = new ComportamientoNonogramaNormal(archivo);
            addBehaviour(comportamientoNormal);
        } else {
            System.out.println("Procesando nonograma: " + archivo + " en modo inverso");
            comportamientoInverso = new ComportamientoNonogramaInverso(archivo);
            addBehaviour(comportamientoInverso);
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Gracias, vuelve pronto");
    }

    /**
     * Clase que define el comportamiento para un nonograma normal
     */
    public class ComportamientoNonogramaNormal extends SimpleBehaviour {

        private final String archivo;
        private LinkedList<String> filas;
        private LinkedList<String> columnas;

        public ComportamientoNonogramaNormal(String archivo) {
            this.archivo = archivo;
        }

        /**
         * Método encargado de abrir y leer el archivo donde esta el nanograma
         * creando los valores en las filas y las columnas
         */
        private void leerArchivo() {
            LinkedList<String> valores = new LinkedList();
            File file = new File("C:\\Users\\JulianCamilo\\Documents\\NetBeansProjects\\Nonograma\\src\\archivos\\" + archivo);
            try {
                Scanner lector = new Scanner(file);
                while (lector.hasNextLine()) {
                    valores.add(lector.nextLine());
                }
            } catch (FileNotFoundException ex) {
                System.out.println("No se encontro el archivo especificado");
            }
            filas = new LinkedList<>(valores.subList(0, valores.size() / 2));
            columnas = new LinkedList<>(valores.subList((valores.size() / 2), valores.size()));
            System.out.println("filas: " + filas);
            System.out.println("columnas: " + columnas);
        }

        @Override
        public void action() {
            leerArchivo();
        }

        @Override
        public boolean done() {
            System.out.println("Termine de procesar");
            return true;
        }
    }
//#############################################################################################################    
//#############################################################################################################    
//#############################################################################################################    
//#############################################################################################################    

    /**
     * Clase encargada del comportamiento para un nanograma inverso
     */
    public class ComportamientoNonogramaInverso extends SimpleBehaviour {

        private final String archivo;
        private char matriz[][];
        private int limiteMatriz;

        public ComportamientoNonogramaInverso(String archivo) {
            this.archivo = archivo;
        }
        
        /**
         * Método encargado de abrir y leer el archivo  del nanograma inverso
         * creando una lista preliminar con las filas
         */
        private void leerArchivo() {
            LinkedList<String> filas = new LinkedList();
            File file = new File("C:\\Users\\JulianCamilo\\Documents\\NetBeansProjects\\Nonograma\\src\\archivos\\" + archivo);
            try {
                Scanner lector = new Scanner(file);
                while (lector.hasNext()) {
                    filas.add(lector.next());
                }
            } catch (FileNotFoundException ex) {
                System.out.println("No se encontro el archivo especificado");
            }
            llenarMatriz(filas);
        }

        /**
         * Inicializa la matriz dividiendo cada valor de fila en la lista de filas
         * en caracteres para las columnas
         * @param filas la lista de todas las filas que sera dividida en caracters individuales
         */
        private void llenarMatriz(LinkedList<String> filas) {
            this.limiteMatriz = filas.size();
            this.matriz = new char[limiteMatriz][limiteMatriz];
            int cont = 0;
            for (String valore : filas) {
                for (int i = 0; i < valore.length(); i++) {
                    matriz[cont][i] = valore.charAt(i);
                }
                cont++;
            }

            imprimirMatriz();
        }

        /**
         * Método encargado de mostrar como queda finalmente la matriz
         */
        public void imprimirMatriz() {
            for (int i = 0; i < limiteMatriz; i++) {
                for (int j = 0; j < limiteMatriz; j++) {
                    System.out.print(matriz[i][j]);
                }
                System.out.println("");
            }
        }

        @Override
        public void action() {
            leerArchivo();

        }

        @Override
        public boolean done() {
            System.out.println("Termine de procesar");
            return true;
        }

    }

}
