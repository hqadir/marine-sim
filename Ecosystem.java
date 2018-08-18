import org.gicentre.utils.geom.HashGrid;
import processing.core.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import org.gicentre.utils.move.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class Ecosystem extends PApplet {

    public static void main(String[] args) {PApplet.main(new String[] {"Ecosystem"});}

    //	--GLOBAL VARIABLES--

    // Set up Biome and define zones that divide it
    List<Zone> zones = new ArrayList<>();
    Zone photic;
    Zone aphotic;
    Zone abyssal;
    Biome marine;

    // Zoomer object for navigational control
    ZoomPan zoomer;

    // Minimum radius of organism for making the HashGrid
    static final int RADIUS = 10;

    // Intial DELAY to frame rate
    int delay = 100;

    // Colour gradient variables
    int Y_AXIS = 1;
    int X_AXIS = 2;
    int b1, b2;

    // Tracks which zone analytics we are currently looking at
    int info_pointer = 0;
    // Toggle variable to show analytics
    boolean toggle_info = true;

    PrintWriter phyto;
    PrintWriter zoo;
    PrintWriter crust;
    PrintWriter o2;
    PrintWriter co2;
    PrintWriter gene_plight;
    PrintWriter gene_pco2;

    public void settings() {
        /*
        Processing function defining initial settings such as window size and ZoomPan instance
         */

        size(1024, 700, P2D);
        zoomer = new ZoomPan(this);
        smooth(3);
    }

    public void setup() {

        noStroke();

        // Define background colour
        b1 = color(10, 22, 59);
        b2 = color(21, 76, 234);

        int zone_height = 200;

        // Population of all organisms for each zone
        ArrayList<Organism> photic_population = new ArrayList<>();
        ArrayList<Organism> aphotic_population = new ArrayList<>();
        ArrayList<Organism> abyssal_population = new ArrayList<>();

        // Define initial genes with pre-determined values. One dominant and one recessive
        Gene plight_dominant = new LightGene(0.8, true);
        Gene plight_recessive = new LightGene(0.3, false);

        Gene pco2_dominant = new CDioxGene(0.8, true);
        Gene pco2_recessive = new CDioxGene(0.3, false);

        Gene ro2_dominant = new OxygenGene(0.8, true);
        Gene ro2_recessive = new OxygenGene(0.3, false);

        Gene rtemp_dominant = new TempGene(0.7, true);
        Gene rtemp_recessive = new TempGene(0.2, false);


        // Define all possible Chromosomes for population by combining alleles
        // plight (LightGene)
        Chromosome plight_hom_dom = new Chromosome(plight_dominant, plight_dominant);
        Chromosome plight_het_dom = new Chromosome(plight_dominant, plight_recessive);
        Chromosome plight_hom_rec = new Chromosome(plight_recessive, plight_recessive);
        // pco2 (CDioxGene)
        Chromosome pco2_hom_dom = new Chromosome(pco2_dominant, pco2_dominant);
        Chromosome pco2_het_dom = new Chromosome(pco2_dominant, pco2_recessive);
        Chromosome pco2_hom_rec = new Chromosome(pco2_recessive, pco2_recessive);
        // ro2 (OxygenGene)
        Chromosome ro2_hom_dom = new Chromosome(ro2_dominant, ro2_dominant);
        Chromosome ro2_het_dom = new Chromosome(ro2_dominant, ro2_recessive);
        Chromosome ro2_hom_rec = new Chromosome(ro2_recessive, ro2_recessive);
        // rtemp (TempGene)
        Chromosome rtemp_hom_dom = new Chromosome(rtemp_dominant, rtemp_dominant);
        Chromosome rtemp_het_dom = new Chromosome(rtemp_dominant, rtemp_recessive);
        Chromosome rtemp_het_rec = new Chromosome(rtemp_recessive, rtemp_recessive);


        // PHOTIC Phytoplankton Population
        for (int a = 0; a < 5; a++) {
            photic_population.add(new Phytoplankton(plight_hom_dom, pco2_hom_dom, width, zone_height));
        }
        for (int b = 0; b < 5; b++) {
            photic_population.add(new Phytoplankton(plight_het_dom, pco2_het_dom, width, zone_height));
        }
        for (int c = 0; c < 5; c++) {
            photic_population.add(new Phytoplankton(plight_hom_rec, pco2_hom_rec, width, zone_height));
        }
        // PHOTIC Zooplankton Population
        for (int a = 0; a < 1; a++) {
            photic_population.add(new Zooplankton(ro2_hom_dom, rtemp_hom_dom, width, zone_height));
        }
        for (int b = 0; b < 1; b++) {
            photic_population.add(new Zooplankton(ro2_het_dom, rtemp_het_dom, width, zone_height));
        }
        for (int c_ = 0; c_ < 1; c_++) {
            photic_population.add(new Zooplankton(ro2_hom_rec, rtemp_het_rec, width, zone_height));
        }
        // PHOTIC Crustacean Population
        for (int a = 0; a < 1; a++) {
            photic_population.add(new Crustacean(ro2_hom_dom, rtemp_hom_dom, width, zone_height));
        }
        for (int b = 0; b < 1; b++) {
            photic_population.add(new Crustacean(ro2_het_dom, rtemp_het_dom, width, zone_height));
        }
        for (int c = 0; c < 1; c++) {
            photic_population.add(new Crustacean(ro2_hom_rec, rtemp_het_rec, width, zone_height));
        }

        // APHOTIC Phytoplankton Population
        for (int a = 0; a < 5; a++) {
            aphotic_population.add(new Phytoplankton(plight_hom_dom, pco2_hom_dom, width, zone_height));
        }
        for (int b = 0; b < 3; b++) {
            aphotic_population.add(new Phytoplankton(plight_het_dom, pco2_het_dom, width, zone_height));
        }
        for (int c = 0; c < 2; c++) {
            aphotic_population.add(new Phytoplankton(plight_hom_rec, pco2_hom_rec, width, zone_height));
        }
        // APHOTIC Zooplankton Population
        for (int a = 0; a < 4; a++) {
            aphotic_population.add(new Zooplankton(ro2_hom_dom, rtemp_hom_dom, width, zone_height));
        }
        for (int b = 0; b < 4; b++) {
            aphotic_population.add(new Zooplankton(ro2_het_dom, rtemp_het_dom, width, zone_height));
        }
        for (int c = 0; c < 4; c++) {
            aphotic_population.add(new Zooplankton(ro2_hom_rec, rtemp_het_rec, width, zone_height));
        }
        // APHOTIC Crustacean Population
        for (int a = 0; a < 1; a++) {
            aphotic_population.add(new Crustacean(ro2_hom_dom, rtemp_hom_dom, width, zone_height));
        }
        for (int b = 0; b < 1; b++) {
            aphotic_population.add(new Crustacean(ro2_het_dom, rtemp_het_dom, width, zone_height));
        }
        for (int c = 0; c < 1; c++) {
            aphotic_population.add(new Crustacean(ro2_hom_rec, rtemp_het_rec, width, zone_height));
        }

        // ABYSSAL Phytoplankton Population
        for (int a = 0; a < 5; a++) {
            abyssal_population.add(new Phytoplankton(plight_hom_dom, pco2_hom_dom, width, zone_height));
        }
        for (int b = 0; b < 3; b++) {
            abyssal_population.add(new Phytoplankton(plight_het_dom, pco2_het_dom, width, zone_height));
        }
        for (int c = 0; c < 2; c++) {
            abyssal_population.add(new Phytoplankton(plight_hom_rec, pco2_hom_rec, width, zone_height));
        }
        // ABYSSAL Zooplankton Population
        for (int a = 0; a < 4; a++) {
            abyssal_population.add(new Zooplankton(ro2_hom_dom, rtemp_hom_dom, width, zone_height));
        }
        for (int b = 0; b < 4; b++) {
            abyssal_population.add(new Zooplankton(ro2_het_dom, rtemp_het_dom, width, zone_height));
        }
        for (int c = 0; c < 4; c++) {
            abyssal_population.add(new Zooplankton(ro2_hom_rec, rtemp_het_rec, width, zone_height));
        }
        // APHOTIC Crustacean Population
        for (int a = 0; a < 1; a++) {
            abyssal_population.add(new Crustacean(ro2_hom_dom, rtemp_hom_dom, width, zone_height));
        }
        for (int b = 0; b < 1; b++) {
            abyssal_population.add(new Crustacean(ro2_het_dom, rtemp_het_dom, width, zone_height));
        }
        for (int c = 0; c < 1; c++) {
            abyssal_population.add(new Crustacean(ro2_hom_rec, rtemp_het_rec, width, zone_height));
        }

        // Shuffle the genome
        Collections.shuffle(photic_population);
        Collections.shuffle(aphotic_population);
        Collections.shuffle(abyssal_population);

        // Create zones
        photic = new Zone(photic_population, 10.0, 40.0,40.0, 5.5, width, 300, RADIUS);
        aphotic = new Zone(aphotic_population, -3.0, 40.0, 40.0, 5.5, width, 300, RADIUS);
        abyssal = new Zone(abyssal_population, -10, 10,10,-10, width, 300, RADIUS);

        // Add newly populated zones to the Biome
        zones.add(photic);
        zones.add(aphotic);
        zones.add(abyssal);

        // Create Biome
        marine = new Biome(zones);

    }

    public void printGeneFrequency( int zone_index ) {

        Zone zone = marine.getZone( zone_index );
        Map<String, Map<Gene, Integer>> zone_genes = zone.getGenes();

        if ( zone_index == 0 ) {
            text("-- PHOTIC ZONE --", 10, height - 190);
        } else if ( zone_index == 1 ) {
            text("-- APHOTIC ZONE --", 10, height - 190);
        } else {
            text("-- ABYSSAL ZONE --", 10, height - 190);
        }

        text("CO2 Level: ", 10, height - 170);
        text(Double.toString(zone.getCo2()), 100, height - 170);

        text("O2 Level: ", 10, height - 160);
        text(Double.toString(zone.geto2()), 100, height - 160);

        text("Light Intensity: ", 10, height - 150);
        text(Double.toString(zone.getLightIntensity()), 100, height - 150);

        text("Temperature: ", 10, height - 140);
        text(Double.toString(zone.getTemp()), 100, height - 140);

        int base = 120;
        int step = 0;
        for (Map.Entry<String, Map<Gene,Integer>> t:zone_genes.entrySet()) {
            String key = t.getKey();
            for (Map.Entry<Gene, Integer> e : t.getValue().entrySet()) {

                text(key + ": ", 10, height - (base - step));
                text(e.getKey().toString(), 60, height - (base - step));
                text(e.getKey().dominanceString(), 200, height - (base - step));
                text("Value: ", 270, height - (base - step));
                text(Double.toString(e.getKey().getValue()), 310, height - (base - step));
                text("Frequency: ", 450, height - (base - step));
                text(Double.toString(e.getValue()), 530, height - (base - step));

                step += 10;
            }
        }

        text("Phytoplankton Frequency: ", 10, height-30);
        text(Double.toString(zone.getOrganismFrequency("Phytoplankton")), 200, height - 30);

        text("Zooplankton Frequency: ", 10, height-20);
        text(Double.toString(zone.getOrganismFrequency("Zooplankton")), 200, height - 20);

        text("Crustacean Frequency: ", 10, height-10);
        text(Double.toString(zone.getOrganismFrequency("Crustacean")), 200, height - 10);
    }

    public void setGradient(int x, int y, float w, float h, int c1, int c2, int axis) {

        noFill();

        if (axis == Y_AXIS) {  // Top to bottom gradient
            for (int i = y; i <= y+h; i++) {
                float inter = map(i, y, y+h, 0, 1);
                int c = lerpColor(c1, c2, inter);
                stroke(c);
                line(x, i, x+w, i);
            }
        }
        else if (axis == X_AXIS) {  // Left to right gradient
            for (int i = x; i <= x+w; i++) {
                float inter = map(i, x, x+w, 0, 1);
                int c = lerpColor(c1, c2, inter);
                stroke(c);
                line(i, y, i, y+h);
            }
        }
    }

    public void drawPhoticOrganisms() {

        // PHOTIC zone
        // retrieve photic_hash_grid
        HashGrid<Organism> photic_hash_grid = marine.getPhoticHashGrid();

        //
        for (Organism org : photic_hash_grid) {
            fill(org.getColour());

            if (org instanceof Phytoplankton) {
                ellipse(org.getLocation().x, org.getLocation().y, RADIUS, RADIUS);
            } else if (org instanceof Zooplankton){
                ellipse(org.getLocation().x, org.getLocation().y, RADIUS+5, RADIUS+5);
            } else {
                ellipse(org.getLocation().x, org.getLocation().y, RADIUS+8, RADIUS+8);
            }
        }

        for (Organism organism : photic_hash_grid) {
            organism.move();
        }
    }

    public void drawAphoticOrgansisms() {

        // APHOTIC

        HashGrid<Organism> aphotic_hash_grid = marine.getAPhoticHashGrid();

        for (Organism org : aphotic_hash_grid) {
            fill(org.getColour());

            pushMatrix();
            translate(0,200);
            if (org instanceof Phytoplankton) {
                ellipse(org.getLocation().x, org.getLocation().y, RADIUS, RADIUS);
            } else if (org instanceof Zooplankton){
                ellipse(org.getLocation().x, org.getLocation().y, RADIUS+5, RADIUS+5);
            } else {
                ellipse(org.getLocation().x, org.getLocation().y, RADIUS+8, RADIUS+8);
            }
            popMatrix();
        }

        for (Organism organism : aphotic_hash_grid) {
            organism.move();
        }

        aphotic_hash_grid.updateAll();
    }

    public void drawAbyssalOrganisms() {

        HashGrid<Organism> abyssal_hash_grid = marine.getAbyssalHashGrid();

        for (Organism org : abyssal_hash_grid) {
            fill(org.getColour());

            pushMatrix();
            translate(0,400);
            if (org instanceof Phytoplankton) {
                ellipse(org.getLocation().x, org.getLocation().y, RADIUS, RADIUS);
            } else if (org instanceof Zooplankton){
                ellipse(org.getLocation().x, org.getLocation().y, RADIUS+5, RADIUS+5);
            } else {
                ellipse(org.getLocation().x, org.getLocation().y, RADIUS+8, RADIUS+8);
            }
            popMatrix();
        }

        for (Organism organism : abyssal_hash_grid) {
            organism.move();
        }

        abyssal_hash_grid.updateAll();
    }

    public void displayControlInfo() {

        textSize(10);
        fill(255, 255, 255);

        text("i - toggle info", width-180, height-10);
        text("z - switch between zones", width-180, height-20);

        if (toggle_info) {

            text("s - speed up", 10, height - 210);
            text("S - slow down", 130, height - 210);
            text("DELAY: " + delay, 250, height - 210);

            text("c - reduce CO2", 10, height - 220);
            text("C - increase CO2", 130, height - 220);

            text("o - reduce O2", 10, height - 230);
            text("O - increase O2", 130, height - 230);

            if (info_pointer == 0) {
                printGeneFrequency(0);
            } else if (info_pointer == 1) {
                printGeneFrequency(1);
            } else if (info_pointer == 2) {
                printGeneFrequency(2);
            }
        }
    }

    public void draw() {

        // Draw the background gradient
        setGradient(0, 0, width, height, b2, b1, Y_AXIS);

        // Set the zoomer control for the mouse
        zoomer.transform();

        // Check for key presses and set changes based on them
        // s/S increases or decreases speed based on delay to system
        if (keyPressed && key == 'S') {
            if (delay < 5000) {
                delay += 10;
            }
        }
        if (keyPressed && key == 's') {
            if (delay > 0) {
                delay -= 10;
            }
        }

        // z flips through zone analytics
        if (keyPressed && key == 'z') {
            info_pointer++;
            if (info_pointer == 3) {
                info_pointer = 0;
            }
        }
        // i toggles the analytics
        if (keyPressed && key == 'i') {
            if (toggle_info) {
                toggle_info = false;
            } else {
                toggle_info = true;
            }
        }

        // c/C increase/decrease co2
        if (keyPressed && key == 'c') {
            marine.getZone(info_pointer).adjustAbiotic("co2", -1000);
        }
        if (keyPressed && key == 'C') {
            marine.getZone(info_pointer).adjustAbiotic("co2", 1000);
        }
        // o/O increase/decrease oxygen
        if (keyPressed && key == 'o') {
            marine.getZone(info_pointer).adjustAbiotic("o2", -1000);
        }
        if (keyPressed && key == 'O') {
            marine.getZone(info_pointer).adjustAbiotic("o2", 1000);
        }


        // draw organisms to screen with the helper functions
        drawPhoticOrganisms();
        drawAphoticOrgansisms();
        drawAbyssalOrganisms();

        // draw controls onto screen with helper
        displayControlInfo();

        // add delay set by user (controls frame rate)
        delay(delay);

        // SIMULATION CYCLE
        marine.biomeLiving();
        marine.biomePredation();
        marine.biomeSelection();
        marine.biomeMating();
        // adds random co2 to compensate for Phytoplankton uptake
        marine.addRandomCo2();

    }

    public void exit() {
        
        System.out.println("STOPPED");
        super.exit();
    }

}
