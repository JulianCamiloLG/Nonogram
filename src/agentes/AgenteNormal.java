/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.InputMismatchException;
import java.util.LinkedList;

/**
 *
 * @author JulianCamilo
 */
public class AgenteNormal extends Agent {

    private ComportamientoNonogramaNormal comportamientoNormal;

    /**
     * Método sobre carga del agente, que decide el tipo de comportamiento de
     * acuerdo al escogido por el usuario en la ejecución
     */
    @Override
    protected void setup() {

        String archivo = (String) this.getArguments()[0];
        System.out.println("Bienvenido, estamos preparandolo todo para resolver tu nonograma");
        System.out.println("Procesando nonograma: " + archivo + " en modo normal");
        comportamientoNormal = new ComportamientoNonogramaNormal(archivo);
        addBehaviour(comportamientoNormal);
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
        int TAMAÑO_F, TAMAÑO_C;
        int[][] filas, columnas;
        long[] tablero;
        long[][] matrizPermutaciones;
        int[][] valorColumna, columnaIx;
        static final char VACIO = '0', LLENO = 'X';

        public ComportamientoNonogramaNormal(String archivo) {
            this.archivo = archivo;
        }

        /**
         * Método encargado de abrir y leer el archivo donde esta el nanograma
         * creando los valores en las filas y las columnas
         */
        private void leerArchivo() {
            File file = new File("./archivos/" + archivo);
            ComportamientoNonogramaNormal.InputReader in = new ComportamientoNonogramaNormal.InputReader(System.in);
            try {
                in = new ComportamientoNonogramaNormal.InputReader(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
                System.out.println("No se encontro el archivo");
            }
            final ComportamientoNonogramaNormal.OutputWriter out = new ComportamientoNonogramaNormal.OutputWriter(System.out);
            //Tamaños iniciales
            TAMAÑO_F = in.readInt();
            TAMAÑO_C = in.readInt();
            long iniciarTiempo = System.currentTimeMillis();
            {
                //leer las columnas
                columnas = new int[TAMAÑO_C][];
                for (int c = 0; c < TAMAÑO_C; c++) {
                    String[] l = in.readLine().split(" ");
                    columnas[c] = new int[l.length];
                    for (int i = 0; i < l.length; i++) {
                        columnas[c][i] = Integer.parseInt(l[i]);
                    }
                }
                //Leer las filas
                filas = new int[TAMAÑO_F][];
                for (int r = 0; r < TAMAÑO_F; r++) {
                    String[] l = in.readLine().split(" ");
                    filas[r] = new int[l.length];
                    for (int i = 0; i < l.length; i++) {
                        filas[r][i] = Integer.parseInt(l[i]);
                    }
                }

                //leer tableroinicial
                tablero = new long[TAMAÑO_F];
                for (int r = 0; r < TAMAÑO_F; r++) {
                    String s = in.readLine();
                    for (int c = 0; c < TAMAÑO_C; c++) {
                        if (s.charAt(c) == VACIO) {
                            continue;
                        }
                        tablero[r] |= 1L << c;
                    }
                }
            }
            //Valores iniciales
            matrizPermutaciones = new long[TAMAÑO_F][];
            for (int r = 0; r < TAMAÑO_F; r++) {
                LinkedList<Long> res = new LinkedList<>();
                int espacios = TAMAÑO_C - (filas[r].length - 1);
                for (int i = 0; i < filas[r].length; i++) {
                    espacios -= filas[r][i];
                }
                calcPerms(r, 0, espacios, 0, 0, res);
                if (res.isEmpty()) {
                    throw new RuntimeException("No se pudo hallar la solución en esta fila " + r);
                }
                matrizPermutaciones[r] = new long[res.size()];
                while (!res.isEmpty()) {
                    matrizPermutaciones[r][res.size() - 1] = res.pollLast();
                }
            }
            // Calcular valores
            valorColumna = new int[TAMAÑO_F][TAMAÑO_C];
            columnaIx = new int[TAMAÑO_F][TAMAÑO_C];
            if (dfs(0)) {
                // Print
                for (int r = 0; r < TAMAÑO_F; r++) {
                    for (int c = 0; c < TAMAÑO_C; c++) {
                        out.print((tablero[r] & (1L << c)) == 0 ? VACIO : LLENO);
                    }
                    out.printLine();
                }
            } else {
                out.printLine("No se pudo hallar una solución");
            }
            System.err.println("Tiempo total: " + (System.currentTimeMillis() - iniciarTiempo) + "ms");
            out.close();
        }

        boolean dfs(int row) {
            if (row == TAMAÑO_F) {
                // last check for the rows
                for (int c = 0; c < TAMAÑO_C; c++) {
                    if (columnaIx[TAMAÑO_F - 1][c] == columnas[c].length
                            || (columnaIx[TAMAÑO_F - 1][c] == columnas[c].length - 1
                            && valorColumna[TAMAÑO_F - 1][c] == columnas[c][columnaIx[TAMAÑO_F - 1][c]])) {
                        continue;
                    }
                    return false;
                }
                return true;
            }
            for (int i = 0; i < matrizPermutaciones[row].length; i++) {
                tablero[row] = matrizPermutaciones[row][i];
                if (updateCols(row)) {
                    if (dfs(row + 1)) {
                        return true;
                    }
                }
            }
            return false;
        }

        boolean updateCols(int row) {
            if (row == 0) {
                for (int c = 0, ixc = 1; c < TAMAÑO_C; c++, ixc <<= 1) {
                    if ((tablero[0] & ixc) == 0) { // bit not set
                        valorColumna[0][c] = 0;
                    } else {
                        valorColumna[0][c] = 1;
                    }
                }
                return true;
            }
            for (int c = 0, ixc = 1; c < TAMAÑO_C; c++, ixc <<= 1) {
                // copy from previous
                valorColumna[row][c] = valorColumna[row - 1][c];
                columnaIx[row][c] = columnaIx[row - 1][c];
                if ((tablero[row] & ixc) == 0) { // bit not set
                    if (valorColumna[row - 1][c] > 0) {
                        if (columnas[c][columnaIx[row - 1][c]] != valorColumna[row - 1][c]) {
                            return false; // higher number expected at this position
                        }
                        valorColumna[row][c] = 0;
                        columnaIx[row][c]++;
                    }
                } else {
                    if (valorColumna[row - 1][c] == 0 && columnaIx[row - 1][c] == columnas[c].length) {
                        return false; // no numbers left
                    }
                    if (columnas[c][columnaIx[row - 1][c]] == valorColumna[row - 1][c]) {
                        return false; // low number expected at this position
                    }
                    valorColumna[row][c]++; // increase value
                }
            }
            return true;
        }

        //Permitar en las filas y el tablero
        void calcPerms(int r, int cur, int spaces, long perm, int shift, LinkedList<Long> res) {
            if (cur == filas[r].length) {
                if ((tablero[r] & perm) == tablero[r]) {
                    res.add(perm);
                }
                return;
            }
            while (spaces >= 0) {
                calcPerms(r, cur + 1, spaces, perm | (bits(filas[r][cur]) << shift), shift + filas[r][cur] + 1, res);
                shift++;
                spaces--;
            }
        }

        //Corrimiento de bits
        long bits(int b) {
            return (1L << b) - 1; // 1 => 1, 2 => 11, 3 => 111, ...
        }

        void printBit(long n) {
            while (n > 0) {
                System.err.print((n & 1));
                n >>= 1;
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

        class InputReader {

            private InputStream stream;
            private byte[] buf = new byte[1024];
            private int curChar;
            private int numChars;

            public InputReader(InputStream stream) {
                this.stream = stream;
            }

            public int read() {
                if (numChars == -1) {
                    throw new InputMismatchException();
                }
                if (curChar >= numChars) {
                    curChar = 0;
                    try {
                        numChars = stream.read(buf);
                    } catch (IOException e) {
                        throw new InputMismatchException();
                    }
                    if (numChars <= 0) {
                        return -1;
                    }
                }
                return buf[curChar++];
            }

            public String readLine() {
                int c = read();
                while (isSpaceChar(c)) {
                    c = read();
                }
                StringBuilder res = new StringBuilder();
                do {
                    res.appendCodePoint(c);
                    c = read();
                } while (!isEndOfLine(c));
                return res.toString();
            }

            public String readString() {
                int c = read();
                while (isSpaceChar(c)) {
                    c = read();
                }
                StringBuilder res = new StringBuilder();
                do {
                    res.appendCodePoint(c);
                    c = read();
                } while (!isSpaceChar(c));
                return res.toString();
            }

            public long readLong() {
                int c = read();
                while (isSpaceChar(c)) {
                    c = read();
                }
                int sgn = 1;
                if (c == '-') {
                    sgn = -1;
                    c = read();
                }
                long res = 0;
                do {
                    if (c < '0' || c > '9') {
                        throw new InputMismatchException();
                    }
                    res *= 10;
                    res += c - '0';
                    c = read();
                } while (!isSpaceChar(c));
                return res * sgn;
            }

            public int readInt() {
                int c = read();
                while (isSpaceChar(c)) {
                    c = read();
                }
                int sgn = 1;
                if (c == '-') {
                    sgn = -1;
                    c = read();
                }
                int res = 0;
                do {
                    if (c < '0' || c > '9') {
                        throw new InputMismatchException();
                    }
                    res *= 10;
                    res += c - '0';
                    c = read();
                } while (!isSpaceChar(c));
                return res * sgn;
            }

            public boolean isSpaceChar(int c) {
                return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
            }

            public boolean isEndOfLine(int c) {
                return c == '\n' || c == '\r' || c == -1;
            }
        }

        class OutputWriter {

            private final PrintWriter writer;

            public OutputWriter(OutputStream outputStream) {
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        outputStream)));
            }

            public OutputWriter(Writer writer) {
                this.writer = new PrintWriter(writer);
            }

            public void print(Object... objects) {
                for (int i = 0; i < objects.length; i++) {
                    if (i != 0) {
                        writer.print(' ');
                    }
                    writer.print(objects[i]);
                }
            }

            public void printLine(Object... objects) {
                print(objects);
                writer.println();
            }

            public void close() {
                writer.close();
            }
        }
    }
//#############################################################################################################    
//#############################################################################################################    
//#############################################################################################################    
//#############################################################################################################    
}
