# marine-sim

This is a project I did for in final year during my Computer Science BSc originally titled (probably a bit pretentiously) 'An interactive educational simulation of a marine ecosystem consisting of planktonic organisms and their evolution over time'.

It was an attempt to model the system dynamics between an evolving plankton population and a marine ecosystem. In short, three different distinct species carry a variety of genes with two different allelic expressions, where genetic fitness models how 'successful' the organism perform within that respective function. However, certain genes will alter the abiotic environment, such as Carbon Dioxide and Oxygen levels, which in effect alters the fitness of the genes themselves. This reinforcing loop models creates an adaptive fitness landscape and models an interesting dynamic equilibrium which is always converged towards, and can be seen in both population densities and abiotic factor levels. Random mutations of the genes contribute to periodic blooms, where the most successful variations of an allele rapidly disperse throughout the gene pool and dominate. Predation and grazing creates further influences on the system which exhibits it's own interesting behaviours on the system; blooms of a predator populations result in direct declines in their prey, especially if the predation genes themselves evolve to be highly fit. 

Substantial research into the ecology of marine ecosystem informs the model, dividing the spatial area into three zones; the Photic, Aphotic and Abyssal zones. Each zone has different levels of light intensity which affects the fitness of Photosythesis genes. This keeps Phytoplankton abudant at the top. Whilst this would presumably would seem to create a grazing ground for predators such as Zooplankton, the high levels of Phytoplankton tend to take up significant amounts of Carbon Dioxide, imposing a limit on the population densities of their predators. They therefore tend to succeed at lower levels of the marine environment, but get limited by the lack of prey, keeping balance throughout.

Research into models of genetics in academia inspiring a mathematical representation of alleles through logistic curves. I found this a useful way of both modelling alleles of the same gene through alteration of 

The system itself is relatively interactive 

For a very detailed overview of the project, which mainly involved research, please see the project documentation attached as a pdf.  

The software is interactive and allows you to alter levels of Carbon Dioxide and Oxygen through toggles to influence the system,

## User Guide

### System Requirements
- Java 8
- MacOS

In order to run the software please download **marine-sim.jar** which is already compiled and packaged with Processing. Simply double click to run or for command line:

`java -jar marine_sim.jar`

  
