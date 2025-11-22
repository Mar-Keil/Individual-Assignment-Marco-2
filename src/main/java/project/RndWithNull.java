package project;

import java.util.Random;

public class RndWithNull {

    private final Random rnd;

    public RndWithNull(){
        this.rnd = new Random();
    }

    public double[] fill(int size, int percentage) {
        double[] line = new double[size];
        for (int i = 0; i < size; i++) {
            if (rnd.nextInt(100) < percentage) {
                line[i] = 0.0;
            } else {
                line[i] = rnd.nextDouble();
            }
        }
        return line;
    }
}
