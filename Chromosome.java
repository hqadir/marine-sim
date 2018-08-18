import java.util.ArrayList;
import java.util.List;

public class Chromosome {

    // List of size two storing each allele in the Chromosome
    private List<Gene> pair;

    public Chromosome(Gene allele_a, Gene allele_b) {
        /*
        Constructor when first Chromosomes are made, with each allele being added to the global pair
         */

        this.pair = new ArrayList<Gene>();
        this.pair.add(0, allele_a);
        this.pair.add(1, allele_b);
    }

    public Chromosome(List pair) {
        /*
        When mating occurs new offspring will use this constructor (pairs will already be made)
         */

        this.pair = pair;
    }

    public Gene getAllele_a() {
        return this.pair.get(0);
    }

    public Gene getAllele_b() {
        return this.pair.get(1);
    }

    public List<Gene> getPair() { return this.pair; }

    public Gene getDominant() {
        /*
        Assesses dominance of both genes to check for dominance, returns which one is
         */

        if (getAllele_a().getDominance() == true) {
            return getAllele_a();
        } else {
            return getAllele_b();
        }
    }

    public boolean isHomoRecessive() {
        /*
        Check if the chromosome is homozygous recessive (both alleles must be recessive)
         */

        return (!getAllele_a().getDominance() && !getAllele_b().getDominance());
    }

    public boolean isHomoDominant() {
        /*
        Check if the chromosome is homozygous dominant (both alleles must be dominant)
         */

        return (getAllele_a().getDominance() && getAllele_b().getDominance());
    }

    public boolean isHetDominant() {
        /*
        Check if the chromosome is heterozygous dominant (one allele must be dominant)
         */

        return (getAllele_a().getDominance() || getAllele_b().getDominance());
    }

    public void mutate() {
        /*
        Call to mutate a random allele in the chromosome
         */

        int rand_allele = (int)(Math.random() * 1);
        this.pair.get(rand_allele).mutation();
    }

}
