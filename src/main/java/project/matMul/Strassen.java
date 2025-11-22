package project.matMul;

import project.RndWithNull;

public class Strassen implements IMatrix {

    private final int size;
    private final double[] a;
    private final double[] b;
    private final double[] c;

    private static final int THRESHOLD = 128; // Cutoff für Rekursion

    public Strassen(RndWithNull rnd, int size) {
        this.size = size;
        this.a = rnd.fill(size * size);
        this.b = rnd.fill(size * size);
        this.c = new double[size * size];
    }

    @Override
    public void multiply() {
        strassen(0, 0, 0);
    }

    @Override
    public double peek() {
        return c[0];
    }

    private void strassen(int a0, int b0, int c0) {

        if (size <= THRESHOLD) {
            multiplyClassic(a0, b0, c0);
            return;
        }

        int k = size / 2;

        int a11 = a0;
        int a12 = a0 + k;
        int a21 = a0 + k * size;
        int a22 = a21 + k;

        int b11 = b0;
        int b12 = b0 + k;
        int b21 = b0 + k * size;
        int b22 = b21 + k;

        int c11 = c0;
        int c12 = c0 + k;
        int c21 = c0 + k * size;
        int c22 = c21 + k;

        double[] M1 = new double[k * k];
        double[] M2 = new double[k * k];
        double[] M3 = new double[k * k];
        double[] M4 = new double[k * k];
        double[] M5 = new double[k * k];
        double[] M6 = new double[k * k];
        double[] M7 = new double[k * k];

        double[] T1 = new double[k * k];
        double[] T2 = new double[k * k];

        add(a, a11, a, a22, T1, 0, k);
        add(b, b11, b, b22, T2, 0, k);
        strassen(0, 0, 0);

        add(a, a21, a, a22, T1, 0, k);
        strassen(0, b11, 0);

        sub(b, b12, b, b22, T2, 0, k);
        strassen(a11, 0, 0);

        sub(b, b21, b, b11, T2, 0, k);
        strassen(a22, 0, 0);

        add(a, a11, a, a12, T1, 0, k);
        strassen(0, b22, 0);

        sub(a, a21, a, a11, T1, 0, k);
        add(b, b11, b, b12, T2, 0, k);
        strassen(0, 0, 0);

        sub(a, a12, a, a22, T1, 0, k);
        add(b, b21, b, b22, T2, 0, k);
        strassen(0, 0, 0);

        add(M1, 0, M4, 0, T1, 0, k);
        sub(T1, 0, M5, 0, T2, 0, k);
        add(T2, 0, M7, 0, c, c11, k);

        add(M3, 0, M5, 0, c, c12, k);
        add(M2, 0, M4, 0, c, c21, k);

        sub(M1, 0, M2, 0, T1, 0, k);
        add(T1, 0, M3, 0, T2, 0, k);
        add(T2, 0, M6, 0, c, c22, k);
    }

    private void multiplyClassic(int a0, int b0, int c0) {

        for (int i = 0; i < size; i++) {
            int ai = a0 + i * size;
            int ci = c0 + i * size;

            for (int k = 0; k < size; k++) {
                double aik = a[ai + k];
                int bk = b0 + k * size;

                for (int j = 0; j < size; j++) {
                    c[ci + j] += aik * b[bk + j];
                }
            }
        }
    }

    private void add(double[] A, int a0,
                     double[] B, int b0,
                     double[] R, int r0,
                     int k) {

        for (int i = 0; i < k; i++) {
            int ai = a0 + i * size;
            int bi = b0 + i * size;
            int ri = r0 + i * k;

            for (int j = 0; j < k; j++) {
                R[ri + j] = A[ai + j] + B[bi + j];
            }
        }
    }

    private void sub(double[] A, int a0,
                     double[] B, int b0,
                     double[] R, int r0,
                     int k) {

        for (int i = 0; i < k; i++) {
            int ai = a0 + i * size;
            int bi = b0 + i * size;
            int ri = r0 + i * k;

            for (int j = 0; j < k; j++) {
                R[ri + j] = A[ai + j] - B[bi + j];
            }
        }
    }
}