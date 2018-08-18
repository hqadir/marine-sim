import processing.core.PVector;
import java.util.*;

public class Phytoplankton extends Organism {

    // lambda = survival_threshold for gene selection
    private final double lambda = 0.2;
    // determines how many offspring are likely to be generated during mating, also dependent of organism energy
    private final double mating_proba = 1.7;
    // How much space the organism takes within the HashGrid
    public static final int RADIUS = 10;

    public Phytoplankton(Chromosome plight_chromosome, Chromosome pco2_chromosome, int width, int height) {
        /*
        Constructor for a new Phytoplankton organism. Used for the initial population.

        @param  plight_chromosome - Pco2 is the chromosome holding the CDioxGenes for photosynthesis
        @param  plight_chromosome - Plight is the chromosome holding the TempGenes for photosynthesis
        @param  width - width of the zone (collision detection)
        @param  height - height of the zone (collision detection)
         */

        // assign random ID
        this.id = UUID.randomUUID();

        // add genes to genotype
        this.genotype.put("Plight", plight_chromosome);
        this.genotype.put("Pco2", pco2_chromosome);

        // width and depth of zone to prevent collisions
        this.width = width;
        this.height = height;

        // set location, movement and colour parameters
        this.location = new PVector(random(RADIUS, width-RADIUS), random(RADIUS, height-RADIUS));
        this.movement = new PVector(random(-1,1),  random(-1,1));
        this.colour   = color(86,206,60); // Green for Phytoplankton
    }

    public Phytoplankton(Map<String, Chromosome> genotype, int width, int height) {
        /*
        Constructor for a new Phytoplankton organism.
        Used for the offspring (assuming the genotype has already been assigned based on parents)

        @param  genotype - pre-made Map containing genotype to assign to this new offspring
        @param  width - width of the zone (collision detection)
        @param  height - height of the zone (collision detection)
         */

        // genotype already made, simply assign to super
        super(genotype);

        // assign random ID
        this.id = UUID.randomUUID();

        // width and depth of zone to prevent collisions
        this.width = width;
        this.height = height;

        // set location, movement and colour parameters
        this.location = new PVector(random(RADIUS, width-RADIUS), random(RADIUS, height-RADIUS));
        this.movement = new PVector(random(-1,1),  random(-1,1));
        this.colour   = color(86,206,60); // Green for Phytoplankton
    }

    @Override
    public List<Organism> mate(Organism father) {
        /*
        Will generate offspring based on crossover of parent genotypes (current object and param), used in MATING

        @param  father - the father organisms to be crossed over with
        @return offsprings - a List containing all organisms
         */

        List<Organism> offsprings = new ArrayList<Organism>();

        // generate random number based on energy and mating_proba
        int random_num = (int)this.energy + (int)(Math.random() * mating_proba);

        // generate genotypes based on crossover, make list
        for (int i = 0; i < random_num; i++) {
            Map<String, Chromosome> new_genotype = crossover(getGenotype(), father.getGenotype());
            offsprings.add(new Phytoplankton(new_genotype, this.width, this.height));
        }

        return offsprings;
    }

    @Override
    public boolean survival(Map<String, Double> abiotic_factors) {
        /*
        Determine if the organism will survive by evaluating cumulative fitness, used in SELECTION
        All genes must pass survival threshold (lambda) in order to survive

        @param  abiotic_factors - Map containing all abiotic_factors for the zone occupied in that time step
        @return success - boolean based on success of all genes
         */

        // retrieve factors from map and discover the fitness through the Gene
        // measure fitness for LightGene (Plight)
        double light_intensity = abiotic_factors.get("light_intensity");
        double light_success = this.genotype.get("Plight").getDominant().fitness(light_intensity);

        // measure fitness for CDioxGene (Pco2)
        double co2_intensity = abiotic_factors.get("co2");
        double co2_success = this.genotype.get("Pco2").getDominant().fitness(co2_intensity);

        return (light_success > this.lambda && co2_success > this.lambda);
    }

    public double photosynthesis(double co2_intensity) {
        /*
        Perform photosynthesis based on gene value and Carbon Dioxide level in zone, used in LIVING
        Result of gene function adds to cumulative energy of organism

        @param  co2_intensity - current amount of carbon dioxide in zone
        @return performance - amount of Carbon Dioxide to deplete (also amount of Oxygen to augment) to zone
         */

        double performance = this.genotype.get("Pco2").getDominant().fitness(co2_intensity);
        addEnergy(performance);

        return performance;
    }

}
