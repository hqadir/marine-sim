import java.util.UUID;

public abstract class Gene {

    // The value is the theta term in the logistic gene equation
    public double value;
    // If this is true, this gene is dominant. Else, it is recessive
    public boolean dominance;
    // Give each gene a unique ID
    public UUID id;


    public Gene(double init_value, boolean dominance) {
        /*
        Constructor common to all genes.

        @param  init_value - the initial unmutated value of the gene
        @param  dominance - the initial dominance of the gene (dominant or recessive based on boolean)
         */
        this.value = init_value;
        this.dominance = dominance;
        this.id = UUID.randomUUID();
    }

    public double getValue() {
        return this.value;
    }

    public boolean getDominance() {
        return this.dominance;
    }

    public String dominanceString() {
        /*
        Returns string for Dominant or Recessive
         */

        if (getDominance()) {
            return "Dominant";
        } else {
            return "Recessive";
        }
    }

    public abstract void mutation();

    public abstract double fitness( double abiotic_factor );

}

