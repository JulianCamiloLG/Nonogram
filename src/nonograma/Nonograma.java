/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonograma;

import jade.Boot;

/**
 *
 * @author JulianCamilo
 */
public class Nonograma {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        switch (args[0]){
            case "-h":
                mensajeAyuda();
                break;
            case "-i":
                resolverInverso(args[1]);
                break;
            default:
                resolverNonograma(args[0]);
                break;
        }
    }
    public static void mensajeAyuda(){
        System.out.println("Programa creado por:");
        System.out.println("Julian Camilo Lopez -- Sebastian Marulanda Sanchez");
        System.out.println("---------------------------------------------------");
        System.out.println("Lista de comandos:");
        System.out.println("Ayuda: java -jar <ruta-de-jar> -h");
        System.out.println("Resolver nonograma: java -jar <ruta-de-jar> <ruta-del-archivo.txt>");
        System.out.println("Resolver nonograma inverso: java -jar <ruta-de-jar> -i <ruta-del-archivo.txt>");
    }
    
    /**
     * Método para lanzar el agente normal en modo de
     * nonograma normal
     * param[1]= nombre-agente:clase-agente (parametros-agente separados por comas)
     * @param ruta la ruta o el nombre del archivo a resolver
     */
    public static void resolverNonograma(String ruta){
        String[] param = new String[2];
        param[0] = "-gui";
        param[1] = "AgenteNormal:agentes.AgenteNormal("
                + ruta+")";
        Boot.main(param);
        
    }
    
    /**
     * Método para lanzar el agente inverso en modo de
     * nonograma inverso
     * @param ruta La ruta del nonograma inverso a resolver
     */
    public static void resolverInverso(String ruta){
        String[] param = new String[2];
        param[0] = "-gui";
        param[1] = "AgenteInverso:agentes.AgenteInverso("
                + ruta+")";
        Boot.main(param);
    }
    
}
