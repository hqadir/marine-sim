import org.gicentre.utils.geom.Locatable;
import processing.core.PApplet;
import processing.core.PVector;
import java.util.*;

public abstract class Organism extends PApplet implements Locatable {

    // ID for identifying the organisms
    public UUID id;
    // Global mutation rate for every organism
    public final double mutation_rate = 0.05;
    // Every organism has an energy based on the fitness of it's genotype
    public double energy;
    // Map containing all chromosomes making up the genotype of an organism, identified by a string
    public Map<String, Chromosome> genotype = new HashMap<String, Chromosome>();
    // HashGrid information; location and movement for spatial organism behaviour
    public PVector location, movement;
    // Colour of ellipse
    public int colour;
    // How much space the organism takes within the HashGrid
    public static int RADIUS;

    public Organism() {
        /*
        Default constructor assigning a random UUID
         */

        this.id = UUID.randomUUID();
    }

    public Organism(Map<String, Chromosome> genotype) {
        /*
        All new offspring will be assigned a genotype generated externally during mating
         */

        this.id = UUID.randomUUID();
        this.genotype = genotype;
    }

    public Map getGenotype() { return this.genotype; }

    public String getGenotypeString() { return this.genotype.toString(); }

    public void addEnergy( double adjustment ) {
        this.energy = adjustment;
    }

    public Map crossover(Map<String, Chromosome> ma_genotype, Map<String, Chromosome> pa_genotype) {
        /*
        Mendelian crossover of genotype
        Parent genotypes extracted for chromosome
        Punnett Square makes all possible permutations of combinations
        Random combination will be selected for assignment into the offspring genotype
        Random mutation may occur on genes before they are assigned to the genotype, mutation_rate determines likelihood

        @param  ma_genotype - maternal genotype as a Map
        @param  pa_genotype - paternal genotype as a Map
        @return return genotype to assign to offspring
         */

        Map<String, Chromosome> new_genotype = new HashMap<String, Chromosome>();

        // iterate through each genotype concurrently
        Iterator<Map.Entry<String, Chromosome>> ma_iter = ma_genotype.entrySet().iterator();
        Iterator<Map.Entry<String, Chromosome>> pa_iter = pa_genotype.entrySet().iterator();

        while ( ma_iter.hasNext() || pa_iter.hasNext() ) {

            // get next gene in both genoypyes
            Map.Entry<String, Chromosome> ma_gene = ma_iter.next();
            Map.Entry<String, Chromosome> pa_gene = pa_iter.next();

            // extract Chromosome for crossover
            Chromosome ma_chromosome = (Chromosome) ma_gene.getValue();
            Chromosome pa_chromosome = (Chromosome) pa_gene.getValue();

            // List of all Gene crosses - make long list first and extract pairs after
            List<Gene> crosses = new ArrayList<Gene>();

            // Loops will grab each new combination pair from a each genotype and pair them up
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    crosses.add(i, ma_chromosome.getPair().get(i));
                    crosses.add(j, pa_chromosome.getPair().get(j));
                }
            }

            // get all cross combinations by dividing up lists
            Chromosome cross_a = new Chromosome(crosses.subList(0, 2));
            Chromosome cross_b = new Chromosome(crosses.subList(2, 4));
            Chromosome cross_c = new Chromosome(crosses.subList(4, 6));
            Chromosome cross_d = new Chromosome(crosses.subList(6, 8));

            List<Chromosome> punnet = new ArrayList<Chromosome>();

            punnet.add(0, cross_a);
            punnet.add(1, cross_b);
            punnet.add(2, cross_c);
            punnet.add(3, cross_d);

            int random_num = (int) (Math.random() * 3);

            // select a random punnett combination as the new chromosome for thes the offspring for that gene
            Chromosome new_chromosome = punnet.get(random_num);

            // MUTATION, allow the new chromosome to undergo a random mutation on any one of the alleles
            if (Math.random() > this.mutation_rate) {
                new_chromosome.mutate();
            }

            // put new combination (for this gene) into the new genotype, iterate to next one to complete
            new_genotype.put(ma_gene.getKey(), new_chromosome);
        }

        return new_genotype;
    }

    public PVector getLocation() {
        return location;
    }

    public int getColour()
    {
        return colour;
    }

    public void move() {
        /*
        Void method for moving the organism in the HashGrid along vector, maintaining collision detection
        This method is called at every time step to make movement look realistic
         */

        // generate expected location based on vector direction and old position
        float newX = location.x + movement.x;
        float newY = location.y + movement.y;

        // if the expected location is to be out of the bounds, reverse the vector direction

        if ((newX < RADIUS) || (newX > this.width-RADIUS))
        {
            movement.x = -movement.x;
            newX = location.x + movement.x;
        }

        if ((newY < RADIUS) || (newY > this.height-RADIUS))
        {
            movement.y = -movement.y;
            newY = location.y + movement.y;
        }

        // update location
        location.x += movement.x;
        location.y += movement.y;
    }

    public int getRADIUS() {
        return this.RADIUS;
    }

    public abstract List<Organism> mate(Organism father);

    public abstract boolean survival(Map<String, Double> abiotic_factors);

}
