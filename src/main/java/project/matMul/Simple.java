package project.matMul;

import project.RndWithNull;

public class Simple implements IMatrix{

    private final int size;
    private final double[][] a;
    private final double[][] b;
    private final double[][] c;

    public Simple(RndWithNull rnd, int size) {
        this.size = size;
        this.a = new double[size][];
        this.b = new double[size][];
        this.c = new double[size][size];
        for (int r = 0; r < size; r++) {
            this.a[r] = rnd.fill(size);
            this.b[r] = rnd.fill(size);
        }
    }

    @Override
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

    @Override
    public double peek() {
        return c[0][0];
    }
}