/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

/**
 *
 * @author JulianCamilo
 */
public class AgenteInverso extends Agent {

    private ComportamientoNonogramaInverso comportamientoInverso;

    @Override
    protected void setup() {
        String archivo = (String) this.getArguments()[0];
        System.out.println("Bienvenido, estamos preparandolo todo para resolver tu nonograma");
        System.out.println("Procesando nonograma: " + archivo + " en modo inverso");
        comportamientoInverso = new ComportamientoNonogramaInverso(archivo);
        addBehaviour(comportamientoInverso);
    }

    @Override
    protected void takeDown() {
        System.out.println("Gracias, vuelve pronto");
    }

    /**
     * Clase que define el comportamiento para un nonograma inverso
     */
    public class ComportamientoNonogramaInverso extends SimpleBehaviour{
        
        private final String archivo;

        public ComportamientoNonogramaInverso(String archivo) {
            this.archivo = archivo;
        }
        
        public void leerArchivo(){
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
