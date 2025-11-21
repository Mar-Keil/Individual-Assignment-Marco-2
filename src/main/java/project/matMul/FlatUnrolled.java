package project.matMul;

import java.util.Random;

public class FlatUnrolled {

    private final int size;
    private final double[] a;
    private final double[] b;
    private final double[] c;

    public FlatUnrolled(Random rnd, int size) {
        this.size = size;
        this.a = new double[size * size];
        this.b = new double[size * size];
        this.c = new double[size * size];

        for (int i = 0; i < size * size; i++) {
            a[i] = rnd.nextDouble();
            b[i] = rnd.nextDouble();
        }
    }

    private int idx(int row, int col) {
        return row * size + col;
    }

    public void multiply() {
        
        // Wir benutzen i k j Reihenfolge
        for (int i = 0; i < size; i++) {
            int ci = i * size;

            for (int k = 0; k < size; k++) {
                double aik = a[i * size + k];
                int bk = k * size;

                int j = 0;

                for (; j <= size - 4; j += 4) {
                    c[ci + j]     += aik * b[bk + j];
                    c[ci + j + 1] += aik * b[bk + j + 1];
                    c[ci + j + 2] += aik * b[bk + j + 2];
                    c[ci + j + 3] += aik * b[bk + j + 3];
                }

                for (; j < size; j++) {
                    c[ci + j] += aik * b[bk + j];
                }
            }
        }
    }

    public double peek() {
        return c[0];
    }
}