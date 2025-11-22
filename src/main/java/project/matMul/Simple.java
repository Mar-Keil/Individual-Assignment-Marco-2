package project.matMul;

import project.RndWithNull;

import java.util.Arrays;

public class Simple implements IMatrix{

    private final int size;
    private final double[][] a;
    private final double[][] b;
    private final double[][] c;

    public Simple(RndWithNull rnd, int size, int percentage) {
        this.size = size;
        this.a = new double[size][];
        this.b = new double[size][];
        this.c = new double[size][size];
        for (int r = 0; r < size; r++) {
            this.a[r] = rnd.fill(size, percentage);
            this.b[r] = rnd.fill(size, percentage);
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
    public void clearC(){
        for(int i = 0; i < size; i++){
            Arrays.fill(c[i], 0.0);
        }
    }

    @Override
    public double peek() {
        return c[0][0];
    }
}