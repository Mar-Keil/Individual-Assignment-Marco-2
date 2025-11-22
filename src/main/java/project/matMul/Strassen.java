package project.matMul;

import project.RndWithNull;
import java.util.Arrays;

public class Strassen implements IMatrix {

    private final int size;
    private final double[] a;
    private final double[] b;
    private final double[] c;

    private final double[] W;
    private static final int THRESHOLD = 128;

    public Strassen(RndWithNull rnd, int size, int percentage) {
        this.size = size;
        this.a = rnd.fill(size * size, percentage);
        this.b = rnd.fill(size * size, percentage);
        this.c = new double[size * size];

        this.W = new double[3 * size * size];
    }

    @Override
    public void multiply() {
        strassen(a, 0, size,
                 b, 0, size,
                 c, 0, size,
                 size, 0);
    }

    @Override
    public void clearC(){
        Arrays.fill(c, 0.0);
    }

    @Override
    public double peek() {
        return c[0];
    }

    private void strassen(double[] A, int a0, int strideA,
                          double[] B, int b0, int strideB,
                          double[] C, int c0, int strideC,
                          int n, int w0) {

        if (n <= THRESHOLD || (n & 1) != 0) {
            multiplyClassic(A, a0, strideA,
                            B, b0, strideB,
                            C, c0, strideC,
                            n);
            return;
        }

        int k = n / 2;

        int a11 = a0;
        int a12 = a0 + k;
        int a21 = a0 + k * strideA;
        int a22 = a21 + k;

        int b11 = b0;
        int b12 = b0 + k;
        int b21 = b0 + k * strideB;
        int b22 = b21 + k;

        int c11 = c0;
        int c12 = c0 + k;
        int c21 = c0 + k * strideC;
        int c22 = c21 + k;

        int M1 = w0;
        int M2 = M1 + k * k;
        int M3 = M2 + k * k;
        int M4 = M3 + k * k;
        int M5 = M4 + k * k;
        int M6 = M5 + k * k;
        int M7 = M6 + k * k;

        int T1 = M7 + k * k;
        int T2 = T1 + k * k;

        int nextW = T2 + k * k;

        add(A, a11, strideA, A, a22, strideA,
            W, T1, k,
            k);
        add(B, b11, strideB, B, b22, strideB,
            W, T2, k,
            k);
        strassen(W, T1, k,
                 W, T2, k,
                 W, M1, k,
                 k, nextW);

        add(A, a21, strideA, A, a22, strideA,
            W, T1, k,
            k);
        strassen(W, T1, k,
                 B, b11, strideB,
                 W, M2, k,
                 k, nextW);

        sub(B, b12, strideB, B, b22, strideB,
            W, T2, k,
            k);
        strassen(A, a11, strideA,
                 W, T2, k,
                 W, M3, k,
                 k, nextW);

        sub(B, b21, strideB, B, b11, strideB,
            W, T2, k,
            k);
        strassen(A, a22, strideA,
                 W, T2, k,
                 W, M4, k,
                 k, nextW);

        add(A, a11, strideA, A, a12, strideA,
            W, T1, k,
            k);
        strassen(W, T1, k,
                 B, b22, strideB,
                 W, M5, k,
                 k, nextW);

        sub(A, a21, strideA, A, a11, strideA,
            W, T1, k,
            k);
        add(B, b11, strideB, B, b12, strideB,
            W, T2, k,
            k);
        strassen(W, T1, k,
                 W, T2, k,
                 W, M6, k,
                 k, nextW);

        sub(A, a12, strideA, A, a22, strideA,
            W, T1, k,
            k);
        add(B, b21, strideB, B, b22, strideB,
            W, T2, k,
            k);
        strassen(W, T1, k,
                 W, T2, k,
                 W, M7, k,
                 k, nextW);

        combine(C, c11, c12, c21, c22, strideC,
                W, M1, M2, M3, M4, M5, M6, M7,
                k);
    }

    private void multiplyClassic(double[] A, int a0, int strideA,
                                 double[] B, int b0, int strideB,
                                 double[] C, int c0, int strideC,
                                 int n) {

        for (int i = 0; i < n; i++) {
            int ai = a0 + i * strideA;
            int ci = c0 + i * strideC;

            for (int j = 0; j < n; j++) {
                double sum = 0.0;
                int bjBase = b0 + j;

                for (int k = 0; k < n; k++) {
                    sum += A[ai + k] * B[bjBase + k * strideB];
                }
                C[ci + j] = sum;
            }
        }
    }

    private void add(double[] A, int a0, int strideA,
                     double[] B, int b0, int strideB,
                     double[] R, int r0, int strideR,
                     int n) {

        for (int i = 0; i < n; i++) {
            int ai = a0 + i * strideA;
            int bi = b0 + i * strideB;
            int ri = r0 + i * strideR;

            for (int j = 0; j < n; j++) {
                R[ri + j] = A[ai + j] + B[bi + j];
            }
        }
    }

    private void sub(double[] A, int a0, int strideA,
                     double[] B, int b0, int strideB,
                     double[] R, int r0, int strideR,
                     int n) {

        for (int i = 0; i < n; i++) {
            int ai = a0 + i * strideA;
            int bi = b0 + i * strideB;
            int ri = r0 + i * strideR;

            for (int j = 0; j < n; j++) {
                R[ri + j] = A[ai + j] - B[bi + j];
            }
        }
    }

    private void combine(double[] C, int c11, int c12, int c21, int c22,
                         int strideC,
                         double[] W, int M1, int M2, int M3, int M4,
                         int M5, int M6, int M7,
                         int k) {

        for (int i = 0; i < k; i++) {
            int ci11 = c11 + i * strideC;
            int ci12 = c12 + i * strideC;
            int ci21 = c21 + i * strideC;
            int ci22 = c22 + i * strideC;

            int row = i * k;

            for (int j = 0; j < k; j++) {
                double m1 = W[M1 + row + j];
                double m2 = W[M2 + row + j];
                double m3 = W[M3 + row + j];
                double m4 = W[M4 + row + j];
                double m5 = W[M5 + row + j];
                double m6 = W[M6 + row + j];
                double m7 = W[M7 + row + j];

                C[ci11 + j] = m1 + m4 - m5 + m7;
                C[ci12 + j] = m3 + m5;
                C[ci21 + j] = m2 + m4;
                C[ci22 + j] = m1 - m2 + m3 + m6;
            }
        }
    }
}