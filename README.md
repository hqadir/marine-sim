# marine-sim

This is a project I did for my final year during my Computer Science BSc originally titled (a bit pretentiously) 'An interactive educational simulation of a marine ecosystem consisting of planktonic organisms and their evolution over time'.

It was an attempt to model the system dynamics between an evolving plankton population and a marine ecosystem. In short, three different distinct species carry a variety of genes with two different allelic expressions, where genetic fitness models how 'successful' the organism perform within that respective function. However, certain genes will alter the abiotic environment, such as CO2 and Oxygen levels, which in effect alters the fitness of the genes themselves. This reinforcing loop models an interesting dynamic equilibrium which is always reached and can be seen in both population densities and abiotic factor levels. Random mutations of the genes contribute to periodic blooms, but the system tends to balance out. Predation and grazing creates further influences on the system with it's own interesting behaviours.

For a very detailed overview of the project, which mainly involved research, please see the project documentation attached as a pdf.  

## User Guide

### System Requirements
- Java 8
- MacOS

In order to run the software please download **marine-sim.jar** which is already compiled and packaged with Processing. Simply double click to run or for command line:

`java -jar marine_sim.jar`
  
