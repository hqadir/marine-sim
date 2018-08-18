import java.util.Random;

public class OxygenGene extends Gene {

    // Max range [-x,x] with which to generate random mutation amount
    public final double mutation_amount = 0.07;

    public OxygenGene(double init_value, boolean dominance) {
        super(init_value, dominance);
    }

    @Override
    public void mutation() {
        /*
        Mutation function for the gene, which will add a random displacement from the original value below or above it
        Will change the field variable value with new value
         */

        // generation new Random object
        Random r = new Random();

        // keep generating new values while the new values are out of the range of the value for the function to work
        double new_value = 0;
        do {
            double degree = -mutation_amount + (mutation_amount - (-mutation_amount)) * r.nextDouble();
            new_value = this.value + degree;
        } while(new_value < 0.1 || new_value > 1.0);

        this.value = new_value;
    }

    @Override
    public double fitness(double abiotic_factor) {
        /*
        The logistic function gene function is calculated here based on the abiotic factor in question and the value

        @param  abiotic_factor - the x value for the function fetched from the ecosystem
         */

        double theta = this.value;

        // logistic function (see documentation)
        double fitness = 1/(1 + Math.exp(-theta*abiotic_factor));

        return fitness;
    }
}
