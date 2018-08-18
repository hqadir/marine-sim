import org.gicentre.utils.geom.HashGrid;
import processing.core.PApplet;
import java.util.*;

public class Biome extends PApplet {

    // List of all zones in the Biome (ecosystem), essentially Photic, Aphotic and Abyssal respectively
    // Can theoretically be extended to accommodate more zones
    private List<Zone> zones;

    public Biome(List<Zone> zones) {
        /*
        One Biome per program, simply assign global list with all Zones created
         */

        this.zones = new ArrayList<>(zones);
    }

    public void biomeSelection() {
        /*
        Iterates through each zone and performs SELECTION on each
         */

        for (Iterator<Zone> iter = this.zones.listIterator(); iter.hasNext(); ) {
            Zone zone = iter.next();
            if (zone.getTotalPopulation() > 0) {
                zone.selection();
            }
        }
    }

    public void biomeMating() {
        /*
        Iterates through each zone and performs MATING on each
         */

        for (Iterator<Zone> iter = this.zones.listIterator(); iter.hasNext(); ) {
            Zone zone = iter.next();
            if (zone.getTotalPopulation() > 0) {
                zone.mating();
            }
        }
    }

    public void biomeLiving() {
        /*
        Iterates through each zone and performs LIVING on each
         */

        for (Iterator<Zone> iter = this.zones.listIterator(); iter.hasNext(); ) {
            Zone zone = iter.next();
            if (zone.getTotalPopulation() > 0) {
                zone.live();
            }
        }
    }

    public void biomePredation() {
        /*
        Iterates through each zone and performs PREDATION on each
         */

        for (Iterator<Zone> iter = this.zones.listIterator(); iter.hasNext(); ) {
            Zone zone = iter.next();
            if (zone.getTotalPopulation() > 0) {
                zone.predation();
            }
        }
    }

    public void addRandomCo2() {
        /*
        Each zone gets some random CO2 to maintain Phytoplankton population
         */

        for (Iterator<Zone> iter = this.zones.listIterator(); iter.hasNext(); ) {
            Zone zone = iter.next();
            if (zone.getTotalPopulation() > 0) {
                zone.addCo2();
            }
        }
    }

    public HashGrid getPhoticHashGrid() {
        return this.zones.get(0).getOrganismHashGrid();
    }

    public HashGrid getAPhoticHashGrid() {
        return this.zones.get(1).getOrganismHashGrid();
    }

    public HashGrid getAbyssalHashGrid() { return this.zones.get(2).getOrganismHashGrid(); }

    public Zone getZone( int index ) {
        return this.zones.get(index);
    }

    public String getGeneFrequencyVerbose() {
        /*
        This function will call the getGenes function and generate a string that can be used for analytics
         */

        String to_return = "";

        int i = 0;
        for (Iterator<Zone> iter = this.zones.listIterator(); iter.hasNext(); ) {

            Zone zone = iter.next();
            Map<String, Map<Gene, Integer>> zone_genes = zone.getGenes();

            if (zone_genes.size() == 0) {
                return "EMPTY";
            }

            to_return += "\n" + "--ZONE " + i + "--" + "\n";

            to_return += "co2: " + zone.getCo2() + "\n";
            to_return += "o2: " + zone.geto2() + "\n";

            for (Map.Entry<String, Map<Gene,Integer>> t:zone_genes.entrySet()) {
                String key = t.getKey();
                for (Map.Entry<Gene,Integer> e : t.getValue().entrySet())
                    to_return += "GENE_TYPE: " + key + " GENE: " + e.getKey() + " Dominance: " + e.getKey().getDominance() + " Value: " + e.getKey().getValue() + " FREQ:" + e.getValue() + "\n";
            }
        i++;
        }

        return to_return;
    }

    public String getOrganismsString() {
        String to_return = "";
        for (int i = 0; i < this.zones.size(); i++) {
            List<Organism> zone_organisms = zones.get(i).getOrganisms();
            to_return += "\n" + "ZONE " + i + " : ";
            to_return += zone_organisms.size();

            for (Iterator<Organism> iter = zone_organisms.listIterator(); iter.hasNext(); ) {
                Organism organism = iter.next();
                to_return += organism;
                to_return += ": " + organism.getGenotypeString();

                Chromosome gene = (Chromosome) organism.getGenotype().get("Plight");
                if(gene.isHomoRecessive()) {
                    to_return += " REC: " + gene;
                }

                to_return += "\n";

            }
        }

        return to_return;
    }

    public String getGeneFrequency() {

        String to_return = "";

        int i = 0;
        for (Iterator<Zone> iter = this.zones.listIterator(); iter.hasNext(); ) {

            Zone zone = iter.next();
            Map<String, Map<Gene, Integer>> zone_genes = zone.getGenes();

            if (zone_genes.size() == 0) {
                return "EMPTY";
            }

            to_return += "\n" + "--ZONE " + i + "--" + "\n";

            to_return += "CO2 Level: " + zone.getCo2() + "\n";
            to_return += "O2 Level: " + zone.geto2() + "\n";
            to_return += "Light Intensity: " + zone.getLightIntensity() + "\n";
            to_return += "Temperature: " + zone.getTemp() + "\n";

            for (Map.Entry<String, Map<Gene,Integer>> t:zone_genes.entrySet()) {
                String key = t.getKey();
                for (Map.Entry<Gene,Integer> e : t.getValue().entrySet())
                    to_return += key + ": " + e.getKey() + " " + e.getKey().dominanceString() + " Value: " + e.getKey().getValue() + " Frequency:" + e.getValue() + "\n";
            }
            i++;
        }

        return to_return;

    }

    public void printGeneFrequency(int width, int height) {

        int i = 0;
        for (Iterator<Zone> iter = this.zones.listIterator(); iter.hasNext(); ) {

            Zone zone = iter.next();
            Map<String, Map<Gene, Integer>> zone_genes = zone.getGenes();

            text("--ZONE 0 --", 10, height - 250);

            text("CO2 Level: ", 10, height - 230);
            text(Double.toString(zone.getCo2()), 40, height - 230);

            text("O2 Level: ", 10, height - 210);
            text(Double.toString(zone.geto2()), 40, height - 210);

            text("Light Intensity: ", 10, height - 190);
            text(Double.toString(zone.getLightIntensity()), 40, height - 190);

            text("Temperature: ", 10, height - 170);
            text(Double.toString(zone.getTemp()), 40, height - 170);

            i++;
        }

    }

    public String getAlleleFrequency() {

        String to_return = "";

        int i = 0;
        for (Iterator<Zone> iter = this.zones.listIterator(); iter.hasNext(); ) {
            Zone zone = iter.next();
            to_return += "--ZONE " + i + "--" + "\n";

            to_return += zone.getAllelicFrequencies().toString() + "\n";
            i++;
        }

        return to_return;
    }

}
