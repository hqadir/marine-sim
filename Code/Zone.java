import com.sun.org.apache.xpath.internal.operations.Or;
import org.gicentre.utils.geom.HashGrid;

import java.util.*;

public class Zone {

    // HashGrid structure will store all organisms spatially for the simulation
    // Species Map will store every species in a Map entry with a list of all organisms of that species in the Zone
    // When organisms are born and die both data structures MUST be concurrent (each serves a different purpose)
    private HashGrid<Organism> organismHashGrid;
    private Map<String, List<Organism>> species;

    // All abiotic factors limited between -5 and 5, Map of each factor along with value
    private Map<String, Double> abiotic_factors = new HashMap<>();

    public Zone (List<Organism> organisms, double light, double co2, double o2, double temp, int width, int height, int radius) {
        /*
        Constructor for Zone. Will take params of all abiotic values and spatial data relating to the Zone

        @param  light, co2, o2 and temp - initial abiotic factors for this zone, to be stored in a Map
        @param  width, height and radius - variables for height and width of zone (collision boundaries) for HashGrid
         */

        // abiotic variables (initial)
        Double light_intensity = light;
        Double co2_level = co2;
        Double o2_level = o2;
        Double temp_level = temp;

        // Make map of abiotic factors
        // This map will store the raw abiotic factor, but in use within genes it is mapped to abiotic logistic function by getMappedAbiotic()
        // see documentation for more information
        this.abiotic_factors.put("light_intensity", light_intensity);
        this.abiotic_factors.put("co2", co2_level);
        this.abiotic_factors.put("o2", o2_level);
        this.abiotic_factors.put("temp", temp_level);

        // HashGrid for all organisms, given collision boundaries for organisms movement and radius for each organism
        this.organismHashGrid = new HashGrid<Organism>(width, height, radius+1);
        this.organismHashGrid.addAll(organisms);

        // Make species map
        this.species = new HashMap<>();
        this.species.put("Phytoplankton", new ArrayList<Organism>());
        this.species.put("Zooplankton", new ArrayList<Organism>());
        this.species.put("Crustacean", new ArrayList<Organism>());


        // Sort through organisms, assigning them their respective species and making the species map
        for ( Iterator<Organism> iter = organisms.listIterator(); iter.hasNext(); ) {
            Organism organism = iter.next();
            if (organism instanceof Phytoplankton) {
                List<Organism> species_list = this.species.get("Phytoplankton");
                species_list.add(organism);
            } else if (organism instanceof Zooplankton) {
                List<Organism> species_list = this.species.get("Zooplankton");
                species_list.add(organism);
            } else if (organism instanceof Crustacean) {
                List<Organism> species_list = this.species.get("Crustacean");
                species_list.add(organism);
            }
        }
    }


    public void selection() {
        /*
        Selection function
        Iterates through species list, if an organism does not survival remove it
         */

        // iterate through organisms list of every species and check survival
        Iterator species = this.species.entrySet().iterator();
        while (species.hasNext()) {

            Map.Entry<String, List<Organism>> next_species = (Map.Entry) species.next();
            List<Organism> species_population = next_species.getValue();


            double before_pop = species_population.size();

            // iterate though each organism in species list
            for (Iterator<Organism> iter = species_population.listIterator(); iter.hasNext(); ) {
                Organism organism = iter.next();

                // cap the population size to prevent extinction - only species five organisms can be selected
                if ( (!organism.survival(getMappedAbioticAll())) && (species_population.size() > 5) ) {

                    // to avoid ConcurrentModificationException do iter.remove()
                    iter.remove();
                    removeOrganism(next_species.getKey(), organism);
                }
            }
        }
    }

    public void mating() {
        /*
        Mating function
        Iterates through species list, selecting a random mating pool and dividing into maternal and paternal populations
        Each parent population iterated through concurrently, paired up to mate
        All offspring added to global zone population for that species
         */

        // Iterate through species Map
        Iterator species = this.species.entrySet().iterator();
        while (species.hasNext()) {

            // extract every species with list with organisms
            Map.Entry<String, List<Organism>> next_species = (Map.Entry) species.next();
            List<Organism> species_population = next_species.getValue();

            double before_pop = species_population.size();

            // mate on population as long as there is at least two parents
            if (species_population.size() > 1) {

                // select random mating pool up the population size, keep looping until an even number is found
                int mating_size;
                do {
                    mating_size = 2 + (int) (Math.random() * species_population.size() - 1);
                } while (mating_size % 2 != 0);

                // make mating population as sublist of main one and shuffle
                List<Organism> mating_population = new ArrayList<Organism>(species_population.subList(0, mating_size));
                Collections.shuffle(mating_population);

                // make lists of maternal and paternal populations
                List<Organism> fathers = new ArrayList<>(species_population.subList(0, mating_population.size() / 2));
                List<Organism> mothers = new ArrayList<>(species_population.subList(mating_population.size() / 2, mating_population.size()));

                // iterate through both populations
                Iterator<Organism> it1 = fathers.iterator();
                Iterator<Organism> it2 = mothers.iterator();

                while (it1.hasNext() && it2.hasNext()) {

                    // mate using organism function, adding to all
                    Organism father = it1.next();
                    Organism mother = it2.next();
                    List<Organism> offspring = mother.mate(father);

                    // add to species entry in species map and global HashGrid
                    species_population.addAll(offspring);
                    this.organismHashGrid.addAll(offspring);
                }
            }
        }
    }

    public void live() {
        /*
        Living (and Predation) function
        All organisms iterated through and considered for life process based on abiotic factor in zone
        If organism cannot perform function (due to fitness below lambda) perform selection (remove from zone (kill))
        Depending on life process, certain amount of abiotic factor depleted and opposite factor augmented
         */


        // iterate though species map
        Iterator species = this.species.entrySet().iterator();
        while (species.hasNext()) {

            // iterate through organism list
            Map.Entry<String, List<Organism>> next_species = (Map.Entry) species.next();
            List<Organism> species_population = next_species.getValue();


            for (Iterator<Organism> iter = species_population.listIterator(); iter.hasNext(); ) {
                Organism organism = iter.next();

                // perform life processes, each if clause specific to the the species
                // the process is only performed if the organism can survive, else it is selected out
                // selection here will keep a minimum of two species for the sake of preventing extinction
                if (organism instanceof Phytoplankton) {

                    if (organism.survival(getMappedAbioticAll())) {
                        double adjustment = ((Phytoplankton) organism).photosynthesis(getMappedAbiotic("co2"));

                        // photosynthesis occurs - co2 goes down, o2 goes up
                        adjustAbiotic("co2", -adjustment);
                        adjustAbiotic("o2", adjustment);
                    } else {
                        if ( species_population.size() > 2 ) {
                            iter.remove();
                            removeOrganism(next_species.getKey(), organism);
                        }
                    }
                }
                else if (organism instanceof Zooplankton) {

                    if (organism.survival(getMappedAbioticAll())) {
                        double adjustment = ((Zooplankton) organism).respiration(getMappedAbiotic("o2"));

                        // respiration occurs - o2 goes down, co2 goes up
                        adjustAbiotic("o2", -adjustment * 2.3);
                        adjustAbiotic("co2", adjustment);
                    } else {
                        if ( species_population.size() > 2 ) {
                            iter.remove();
                            removeOrganism(next_species.getKey(), organism);
                        }
                    }
                }
                else if (organism instanceof Crustacean) {

                    if (organism.survival(getMappedAbioticAll())) {
                        double adjustment = ((Crustacean) organism).respiration(getMappedAbiotic("o2"));

                        // respiration occurs - o2 goes down, co2 goes up
                        adjustAbiotic("o2", -adjustment * 2.3);
                        adjustAbiotic("co2", adjustment);
                    } else {
                        if ( species_population.size() > 2 ) {
                            iter.remove();
                            removeOrganism(next_species.getKey(), organism);
                        }
                    }
                }
            }
        }
    }

    public void predation() {
        /*
        Predation function
        Zooplankton and Crustacean are considered for predation
        Predation will only occur if the target prey size is large enough to preyed upon
        A list is returned by the organism object containing a list of all organisms to be removed by preying
         */

        // iterate though species map
        Iterator species = this.species.entrySet().iterator();
        while (species.hasNext()) {

            // iterate through organism list
            Map.Entry<String, List<Organism>> next_species = (Map.Entry) species.next();
            List<Organism> species_population = next_species.getValue();


            for (Iterator<Organism> iter = species_population.listIterator(); iter.hasNext(); ) {
                Organism organism = iter.next();

                // consider predation for Zooplankton and Cructacean, only predate if a sizable number of prey still alive
                if (organism instanceof Zooplankton) {

                    // only predate if there are some organisms alive still
                    if (this.species.get("Phytoplankton").size() > 10) {
                        removeOrganisms("Phytoplankton", ((Zooplankton) organism).predation(this.species.get("Phytoplankton")));
                    }

                } else if (organism instanceof Crustacean) {

                    // only predate if there are some organisms alive still
                    if (this.species.get("Zooplankton").size() > 10) {
                        removeOrganisms("Zooplankton", ((Crustacean) organism).predation(this.species.get("Zooplankton")));
                    }

                }
            }
        }
    }

    public void removeOrganisms(String species, List<Organism> organisms) {
        /*
        Will remove a list of organisms from BOTH the species map and the organism HashGrid

        @param  species - string of the species to remove
        @param  organisms - list of organisms to remove from both organism storage mechanisms
         */

        // ensure there is enough of a species of a species to remove still and remove from both storage means
        if ( this.species.get(species).size() > 0 && this.organismHashGrid.size() > 0 ) {
            this.species.get(species).removeAll(organisms);
            this.organismHashGrid.removeAll(organisms);
        }
    }

    public void removeOrganism(String species, Organism organism) {
        /*
        Will remove a single organism from BOTH the species map and the organism HashGrid

        @param  species - string of the species to remove
        @param  organism -  organisms to remove from both organism storage mechanisms
         */

        // ensure there is enough of a species of a species to remove still and remove from both storage means
        if ( this.organismHashGrid.size() > 0 ) {
            this.organismHashGrid.remove(organism);
        }
    }

    public double getOrganismFrequency(String species) {
        /*
        Returns the frequency of organisms for a particular species

        @param  species - string of the species
        @return return the size of the species
         */

        return this.species.get(species).size();
    }

    public double getMappedAbiotic(String factor) {
        /*
        The function will take the raw abiotic factor stored in the Map and put it through the constraining function
        This constrains the abiotic factor to the domain of the gene function

        @param  factor - which factor to extract from the abiotic map
        @return mapped_factor - return the factor mapped through the constraining function
         */

        double theta = -0.2;

        // calculate the new factor by adding it to the existing one
        double mapped_factor = 10 / (1 + Math.exp( theta * this.abiotic_factors.get(factor) ) );
        // translate the curve -5 so it fits the range for the genes

        mapped_factor -= 5;

        return mapped_factor;
    }

    public Map<String, Double> getMappedAbioticAll() {
        /*
        The function will take each raw abiotic factor stored in the Map and put them through the constraining function
        This constrains the abiotic factors to the domain of the gene function

        @return mapped_factor - return the factor mapped through the constraining function
         */

        // make a new map for mapped factors
        Map<String, Double> mapped_factors = new HashMap<>();

        Iterator factor = this.abiotic_factors.entrySet().iterator();
        while (factor.hasNext()) {

            Map.Entry<String, Double> next_factor = (Map.Entry) factor.next();
            mapped_factors.put(next_factor.getKey(), getMappedAbiotic(next_factor.getKey()));
        }

        return mapped_factors;
    }

    public void adjustAbiotic(String factor, double amount) {
        /*
        Adjustment of an abiotic factor takes place here, where it is prevented from going below a bound

        @param  factor - factor to adjust
        @param  amount - amount to adjust
         */

        // adjust factor externally before adding
        double adjustment = this.abiotic_factors.get(factor) + amount;

        // ensure the lower bound is not broken out of
        if (adjustment > -10) {
            this.abiotic_factors.put(factor, adjustment);
        } else {
            this.abiotic_factors.put(factor, -3.0);
        }
    }

    public void addCo2() {
        /*
        This function is called every time step to add some co2 to each zone.
         */
        this.abiotic_factors.put("co2", this.abiotic_factors.get("co2") + 50);
    }

    public double getLightIntensity() {
        return (double) this.abiotic_factors.get("light_intensity");
    }

    public double getCo2() {
        return (double) this.abiotic_factors.get("co2");
    }

    public double geto2() {
        return (double) this.abiotic_factors.get("o2");
    }

    public double getTemp() { return (double) this.abiotic_factors.get("temp"); }

    public Map<String, Map<Gene, Integer>> getGenes() {
        /*
        This function will return a Map containing all the genes in the zone
        Each entry will contain a Map in itself which contains the frequency of each gene
         */

        // make a new map ready to collect information
        Map<String, Map<Gene, Integer>> genes = new HashMap<>();
        genes.put("Plight", new HashMap<Gene, Integer>());
        genes.put("Pco2", new HashMap<Gene, Integer>());
        genes.put("Ro2", new HashMap<Gene, Integer>());
        genes.put("Rtemp", new HashMap<Gene, Integer>());

        // iterate through the species to extract each gene
        Iterator species = this.species.entrySet().iterator();
        while (species.hasNext()) {

            // for each species
            Map.Entry<String, List<Organism>> next_species = (Map.Entry) species.next();
            List<Organism> species_population = next_species.getValue();

            // iterate through the population list
            for (Iterator<Organism> iter = species_population.listIterator(); iter.hasNext(); ) {
                Organism organism = iter.next();

                // iterate through the gene map to look for each gene and add it to the map along with +1 frequency
                Iterator gene_names = genes.entrySet().iterator();
                while (gene_names.hasNext()) {

                    // for each type
                    Map.Entry<String, HashMap<Gene, Integer>> gene_type = (Map.Entry) gene_names.next();
                    Map<Gene, Integer> gene_frequencies = gene_type.getValue();

                    // check if we found the gene by looking at the genotype and checking if the gene is in there
                    if (organism.getGenotype().containsKey(gene_type.getKey())) {

                        // we will have two copies of this gene, so we need to identify each one and add them
                        Chromosome org_chromosome = (Chromosome) organism.getGenotype().get(gene_type.getKey());
                        Gene allele_a = org_chromosome.getAllele_a();
                        Gene allele_b = org_chromosome.getAllele_b();

                        // if we have not added it yet make the new gene in the map, else add
                        if (!gene_frequencies.containsKey(allele_a)) {
                            gene_frequencies.put(allele_a, 0);
                        } else {
                            gene_frequencies.put(allele_a, gene_frequencies.get(allele_a) + 1);
                        }

                        if (!gene_frequencies.containsKey(allele_b)) {
                            gene_frequencies.put(allele_b, 0);
                        } else {
                            gene_frequencies.put(allele_b, gene_frequencies.get(allele_b) + 1);
                        }
                    }
                }
            }
        }

        // return our new map of gene frequencies
        return genes;

    }

    public Map<String, Integer> getAllelicFrequencies() {
        /*
        This function returns the frequency of all the different chromosomal pair types in the zone
         */

        // make a map of all pairs
        // the pairs can be Homozygous Dominant, Heterozygous or Homozygous Recessive
        // each gene will have a feature of the list above (unless the pair is extinct)
        // we iterate through the genes to find them
        Map<String, Integer> pairs = new HashMap<>();
        pairs.put("Plight_hom_dom", 0);
        pairs.put("Plight_het_dom", 0);
        pairs.put("Plight_hom_rec", 0);
        pairs.put("Pco2_hom_dom", 0);
        pairs.put("Pco2_het_dom", 0);
        pairs.put("Pco2_hom_rec", 0);
        pairs.put("Ro2_hom_dom", 0);
        pairs.put("Ro2_het_dom", 0);
        pairs.put("Ro2_hom_rec", 0);
        pairs.put("Rtemp_hom_dom", 0);
        pairs.put("Rtemp_het_dom", 0);
        pairs.put("Rtemp_hom_rec", 0);

        // iterate through all organisms for this collection
        for (Organism organism : organismHashGrid) {

            // the following code will extract all chromosomes and check for their property
            // it will increment the respective value in the return map when found

            // if an organism is Phytoplankton it will only contain the Plight and Pco2 genes, look for them here
            if (organism instanceof Phytoplankton) {
                Chromosome org_chromosome = (Chromosome) organism.getGenotype().get("Plight");
                if (org_chromosome.isHomoDominant()) {
                    pairs.put("Plight_hom_dom", pairs.get("Plight_hom_dom") + 1);
                } else if (org_chromosome.isHomoRecessive()) {
                    pairs.put("Plight_hom_rec", pairs.get("Plight_hom_rec") + 1);
                } else if (org_chromosome.isHetDominant()) {
                    pairs.put("Plight_het_dom", pairs.get("Plight_het_dom") + 1);
                }

                org_chromosome = (Chromosome) organism.getGenotype().get("Pco2");
                if (org_chromosome.isHomoDominant()) {
                    pairs.put("Pco2_hom_dom", pairs.get("Pco2_hom_dom") + 1);
                } else if (org_chromosome.isHomoRecessive()) {
                    pairs.put("Pco2_hom_rec", pairs.get("Pco2_hom_rec") + 1);
                } else if (org_chromosome.isHetDominant()) {
                    pairs.put("Pco2_het_dom", pairs.get("Pco2_het_dom") + 1);
                }
            }

            // if an organism is a consumer it will have the Ro2 and Rtemp genes, so look here
            if (organism instanceof Zooplankton || organism instanceof Crustacean) {
                Chromosome org_chromosome = (Chromosome) organism.getGenotype().get("Ro2");
                if (org_chromosome.isHomoDominant()) {
                    pairs.put("Ro2_hom_dom", pairs.get("Ro2_hom_dom") + 1);
                } else if (org_chromosome.isHomoRecessive()) {
                    pairs.put("Ro2_hom_rec", pairs.get("Ro2_hom_rec") + 1);
                } else if (org_chromosome.isHetDominant()) {
                    pairs.put("Ro2_het_dom", pairs.get("Ro2_het_dom") + 1);
                }

                org_chromosome = (Chromosome) organism.getGenotype().get("Rtemp");
                if (org_chromosome.isHomoDominant()) {
                    pairs.put("Rtemp_hom_dom", pairs.get("Rtemp_hom_dom") + 1);
                } else if (org_chromosome.isHomoRecessive()) {
                    pairs.put("Rtemp_hom_rec", pairs.get("Rtemp_hom_rec") + 1);
                } else if (org_chromosome.isHetDominant()) {
                    pairs.put("Rtemp_het_dom", pairs.get("Rtemp_het_dom") + 1);
                }
            }
        }

        // return our finished map
        return pairs;
    }

    public List<Organism> getOrganisms() {
        /*
        This function will simply return a list of all organisms in the zone by iterating through the species map
         */

        List<Organism> all_organisms = new ArrayList<>();

        // iterate through species map and concatenate all lists to make one central one
        for(Map.Entry<String, List<Organism>> entry : this.species.entrySet()) {
            List<Organism> next_species = entry.getValue();
            for (Iterator<Organism> iter = next_species.listIterator(); iter.hasNext(); ) {
                Organism organism = iter.next();
                all_organisms.add(organism);
            }
        }

        return all_organisms;
    }

    public int getTotalPopulation() {
        /*
        Gets the total population size by iterating through the species map and concatenating the population size
         */

        int total_size = 0;
        Iterator species = this.species.entrySet().iterator();
        while (species.hasNext()) {
            Map.Entry<String, List<Organism>> next_species = (Map.Entry) species.next();
            total_size += next_species.getValue().size();
        }

        return total_size;
    }

    public HashGrid<Organism> getOrganismHashGrid() {
        return this.organismHashGrid;
    }

    public double getAvgGeneValue(String gene) {

        List<Double> values = new ArrayList<>();
        List<Organism> organisms = getOrganisms();

        for (Iterator<Organism> iter = organisms.listIterator(); iter.hasNext(); ) {
            Organism organism = iter.next();

            if (organism instanceof Phytoplankton) {
                Chromosome chromosome = (Chromosome) organism.getGenotype().get(gene);
                Gene geneobj = (Gene) chromosome.getDominant();

                values.add(geneobj.getValue());
            }
        }

        double avg = 0;
        double sum = 0;

        for (int i=0; i<values.size(); i++) {
            sum += values.get(i);
        }

        avg = sum / values.size();

        return avg;
    }

}
