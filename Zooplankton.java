import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Zooplankton extends Organism {

    // lambda = survival_threshold for gene selection
    private final double lambda = 0.2;
    // determines how many offspring are likely to be generated during mating, also dependent of organism energy
    private final double mating_proba = 1.1;
    // determines how many prey likely to be predated by Zooplankton during predation, also dependent of organism energy
    private final double predation_proba = 1.1;
    // How much space the organism takes within the HashGrid
    public static final int RADIUS = 15;

    public Zooplankton(Chromosome ro2_chromosome, Chromosome rtemp_chromosome, int width, int height) {
        /*
        Constructor for a new Zooplankton organism. Used for the initial population.

        @param  ro2_chromosome - Ro2 is the chromosome holding the OxygenGenes for respiration
        @param  ro2_chromosome - Rtemp is the chromosome holding the TempGenes for respiration
        @param  width - width of the zone (collision detection)
        @param  height - height of the zone (collision detection)
         */

        // assign random ID
        this.id = UUID.randomUUID();

        // add genes to genotype
        this.genotype.put("Ro2", ro2_chromosome);
        this.genotype.put("Rtemp", rtemp_chromosome);

        // width and depth of zone to prevent collisions
        this.width = width;
        this.height = height;

        // set location, movement and colour parameters
        this.location = new PVector(random(RADIUS, width-RADIUS), random(RADIUS, height-RADIUS));
        this.movement = new PVector(random(-1,1),  random(-1,1));
        colour   = color(180, 37, 28); // light red for Zooplankton
    }

    public Zooplankton(Map<String, Chromosome> genotype, int width, int height) {
        /*
        Constructor for a new Zooplankton organism.
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
        colour   = color(180, 37, 28); // light red for Zooplankton
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
            offsprings.add(new Zooplankton(new_genotype, this.width, this.height));
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
        // measure fitness for OxygenGene (Ro2)
        double o2_intensity = abiotic_factors.get("o2");
        double o2_success = this.genotype.get("Ro2").getDominant().fitness(o2_intensity);

        // measure fitness for TempGene (Rtemp)
        double temp_level = abiotic_factors.get("temp");
        double temp_success = this.genotype.get("Rtemp").getDominant().fitness(temp_level);

        return (o2_success > this.lambda && temp_success > this.lambda);
    }

    public double respiration(double o2_intensity) {
        /*
        Perform respiration based on gene value and Oxygen level in zone, used in LIVING
        Result of gene function adds to cumulative energy of organism

        @param  o2_intensity - current amount of oxygen in zone
        @return performance - amount of Oxygen to deplete (also amount of Carbon Dioxide to augment) to zone
         */

        double performance = this.genotype.get("Ro2").getDominant().fitness(o2_intensity);
        addEnergy(performance);

        return performance;
    }

    public List<Organism> predation( List<Organism> prey ) {
        /*
        Given the list off potential prey (Phytoplankton for Zooplankton) determine how many and which to kill
        Used in PREDATION

        @param  prey - List containing all potential prey
        @return to_kill - List containing all prey determined to kill, based on random selection
         */

        List<Organism> to_kill = new ArrayList<>();

        // random number of how many to prey to kill, based on health and predation_proba (upper bound)
        int likelihood = (int)this.energy + (int)(Math.random() * predation_proba);

        // based on likelihood, select random prey and add to list
        for (int i = 0; i < likelihood; i++ ) {
            int random_org = (int)(Math.random() * prey.size());
            to_kill.add(prey.get(random_org));
        }

        return to_kill;
    }

}
