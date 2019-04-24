/*
Basado de : https://www.reddit.com/r/dailyprogrammer/comments/am1x6o/20190201_challenge_374_hard_nonogram_solver/
https://rosettacode.org/wiki/Nonogram_solver
Algoritmo:
*Iniciar con una matriz de ceros, donde 0=sin respuesta, 1=blanco, 2=negro
*Para cada fila, con sus respectivas pistas, generar todas las posibles permutaciones de esa fila, sin tomar en cuenta todas las permutaciones donde un posible blanco se cruza con un posible negro(constraint);
*Si una columna tiene un blanco para cada permutación, esa celda (fila, columna) será blanca, exactamente lo mismo con las negras, si no cumple con estas condiciones la celda continua sin respuesta;
* Hacer lo mismo para cada columna, con sus respectivas pistas.
* Repetir hasta que no existan más cambios
 */
package solucionador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author CD006LA
 */
public class solver {

    static int F, C;
    //tamaño de cada bloque en las pistas, 
    //un ejemplo fila[1][0],[1][1] datos de la fila dos, pista 1 y pista 2
    static int[][] filas, columnas;
    static long[] tabla; //representación del tablero 
    static long[][] permutacionesFila; // todas las posibles permutaciones por fila
    static final char BLANCO = '.', NEGRO = '#';

    public static void main(String[] args) {
        // Lectura, aquí entra juli
        //final InputReader in = new InputReader(System.in);
        final OutputWriter out = new OutputWriter(System.out);
        // filas y columnas se deberian contar del archivo
        //F = in.readInt();
        //C = in.readInt();
        long tiempoInicial = System.currentTimeMillis();
        {
            LinkedList<String> valores = new LinkedList();
            File file = new File("./archivos/"+"copa.txt");
            try {
                Scanner lector = new Scanner(file);
                while (lector.hasNextLine()) {
                    valores.add(lector.nextLine());
                }
            } catch (FileNotFoundException ex) {
                System.out.println("No se encontro el archivo especificado");
            }
            F=valores.size()/2;
            C=valores.size()/2;
            // lectura filas
            filas = new int[F][];
            for (int fila = 0; fila < F; fila++) {
            //    String[] linea = in.readLine().split(" ");
                filas[fila] = new int[valores.get(fila).length()];
                for (int i = 0; i < valores.get(fila).length(); i++) {
                    filas[fila][i] = Integer.parseInt(valores.get(i));
                }
            }
            // lectura columnas
            columnas = new int[C][];
            for (int columna = 0; columna < C; columna++) {
            //    String[] linea = in.readLine().split(" ");
                columnas[columna] = new int[valores.get(columna).length()];
                for (int i = 0; i < valores.get(columna).length(); i++) {
                    columnas[columna][i] = Integer.parseInt(valores.get(i));
                }
            }
            //Representación inicial tablero
            tabla = new long[F];
            for (int fila = 0; fila < F; fila++) {
            //    String s = in.readLine();
                for (int columna = 0; columna < C; columna++) {
                    if (valores.get(fila).charAt(columna) == BLANCO) {
                        continue;
                    }
                    //operacion a nivel de bits
                    // |= OR es igual a +=
                    // << shit a nivel de bits a la izquierda, a tabla en la posicion fila sume lo ue viene en columna dentro del long
                    tabla[fila] |= 1L << columna;
                }
            }
        }
        // Calculo inicial, con este calculo hallamos colisiones 1-0 por cada pasada
        // las permutaciones tambíen se almacenan en bits, donde el primer bit e convierte en 1 si
        // concuerda con el valor de la columna
        permutacionesFila = new long[F][];
        for (int fila = 0; fila < F; fila++) {
            LinkedList<Long> respuesta = new LinkedList<Long>();
            //contar cantidad de espacios que se agregan
            // formula: cantidad de columnas - (longitudpista - 1)
            int espacios = C - (filas[fila].length - 1);
            for (int i = 0; i < filas[fila].length; i++) {
                //para cabla bloque, la cantidad de espacios disminuye
                //por cada bloque existente
                espacios -= filas[fila][i];
            }
            permutaciones(fila, 0, espacios, 0, 0, respuesta);
            //si no hallo una respuesta para una fila determinada
            if (respuesta.isEmpty()) {
                throw new RuntimeException("No se pudo hallar una solución para la fila " + fila);
            }
            permutacionesFila[fila] = new long[respuesta.size()];
            // si la respuesta existem se evalua una nueva fila y sus nuevas permutaciones
            while (!respuesta.isEmpty()) {
                permutacionesFila[fila][respuesta.size() - 1] = respuesta.pollLast();
            }
        }
        // Calculo Completo permutaciones
        valorColumna = new int[F][C];
        indiceColumna = new int[F][C];
        //a nivel de bits, es un &
        bitmask = new long[F];
        valor = new long[F];
        if (dfs(0)) {
            for (int fila = 0; fila < F; fila++) {
                for (int columna = 0; columna < C; columna++) {
                    out.print((tabla[fila] & (1L << columna)) == 0 ? BLANCO : NEGRO);
                }
                out.printLine();
            }
        } else {
            out.printLine("No existe una solución");
        }
        System.err.println("Time: " + (System.currentTimeMillis() - tiempoInicial) + "ms");
        out.close();
    }

    static int[][] valorColumna, indiceColumna;
    static long[] bitmask, valor;

    static boolean dfs(int fila) {
        if (fila == F) {
            return true;
        }
        enmascararFila(fila); // calcular que la máscara sea válida
        for (int i = 0; i < permutacionesFila[fila].length; i++) {
            if ((permutacionesFila[fila][i] & bitmask[fila]) != valor[fila]) {
                continue;
            }
            tabla[fila] = permutacionesFila[fila][i];
            actualizarColumnas(fila);
            if (dfs(fila + 1)) {
                return true;
            }
        }
        return false;
    }

    static void enmascararFila(int fila) {
        bitmask[fila] = valor[fila] = 0;
        if (fila == 0) {
            return;
        }
        long indexCol = 1L;
        for (int columna = 0; columna < C; columna++, indexCol <<= 1) {
            if (valorColumna[fila - 1][columna] > 0) {
                // when column at previous row is set, we know for sure what has to be the next bit according to the current size and the expected size
                bitmask[fila] |= indexCol;
                if (columnas[columna][indiceColumna[fila - 1][columna]] > valorColumna[fila - 1][columna]) {
                    valor[fila] |= indexCol; // must set
                }
            } else if (valorColumna[fila - 1][columna] == 0 && indiceColumna[fila - 1][columna] == columnas[columna].length) {
                // can not add anymore since out of indices
                bitmask[fila] |= indexCol;
            }
        }
    }

    static void actualizarColumnas(int row) {
        long indexColumna = 1L;
        for (int c = 0; c < C; c++, indexColumna <<= 1) {
            // copiar antiguos
            valorColumna[row][c] = row == 0 ? 0 : valorColumna[row - 1][c];
            indiceColumna[row][c] = row == 0 ? 0 : indiceColumna[row - 1][c];
            if ((tabla[row] & indexColumna) == 0) {
                if (row > 0 && valorColumna[row - 1][c] > 0) {
                    // bit sin setear y columna no vacia en la fila anterior => setea a 0 e incrementa en uno
                    valorColumna[row][c] = 0;
                    indiceColumna[row][c]++;
                }
            } else {
                valorColumna[row][c]++; // incrementa en uno
            }
        }
    }

    static void permutaciones(int fila, int pistaActual, int espacios, long permutacion, int shift, LinkedList<Long> result) {
        if (pistaActual == filas[fila].length) {
            if ((tabla[fila] & permutacion) == tabla[fila]) {
                result.add(permutacion);
            }
            return;
        }
        while (espacios >= 0) {
            permutaciones(fila, pistaActual + 1, espacios, permutacion | (bits(filas[fila][pistaActual]) << shift), shift + filas[fila][pistaActual] + 1, result);
            shift++;
            espacios--;
        }
    }

    static long bits(int b) {
        return (1L << b) - 1; // 1 => 1, 2 => 11, 3 => 111, ...
    }

    static void imprimirBit(long n) {
        while (n > 0) {
            System.err.print((n & 1));
            n >>= 1;
        }
    }

    static class InputReader {

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

    static class OutputWriter {

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
