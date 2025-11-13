package project;

import java.util.Random;

public class Matrix {

    private final int size;
    private final double[][] a;
    private final double[][] b;
    private final double[][] c;

    public Matrix (Random rnd, int size) {
        this.size = size;
        this.a = new double[size][size];
        this.b = new double[size][size];
        this.c = new double[size][size];
        for (int r = 0; r < size; r++) {
            for (int j = 0; j < size; j++) {
                this.a[r][j] = rnd.nextDouble();
                this.b[r][j] = rnd.nextDouble();
            }
        }
    }

    public void multiply() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double s = 0;
                for (int k = 0; k < size; k++) {
                    s += a[i][k] * b[k][j];
                }
                c[i][j] = s;
            }
        }
    }

    public double peek() {
        return c[0][0];
    }
}