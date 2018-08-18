# marine-sim

This is a project I did in final year during my Computer Science BSc originally titled (probably a bit pretentiously) 'An interactive educational simulation of a marine ecosystem consisting of planktonic organisms and their evolution over time'. The software itself is written in Java and uses the Processing library for visualisation.

For a very detailed overview of the project, which involves research and testing, please see the project documentation attached as a pdf. Please note it is formatted for dissertation style submission.

A lot more work can be done to tighten up the simulation, add depth and certainly improve the visuals and the experience. The tighteness of my deadline explains why it can be unfinished in places. Nonetheless, it was a really fun project to do.

## Overview

This was an attempt to model the system dynamics between an evolving plankton population and a marine ecosystem. 

In short, three different distinct species (Phytoplankton, Zooplankton, Crustacean) carry a variety of genes with two different allelic expressions, where genetic fitness models how 'successful' the organism perform within that respective function. However, certain genes will alter the abiotic environment, such as Carbon Dioxide and Oxygen levels, which in effect alters the fitness of the genes themselves. This reinforcing loop models creates an adaptive fitness landscape and models an interesting dynamic equilibrium which is always converged towards, and can be seen in both population densities and abiotic factor levels. Random mutations of the genes contribute to periodic blooms, where the most successful variations of an allele rapidly disperse throughout the gene pool and dominate. Predation and grazing creates further influences on the system which exhibits it's own interesting behaviours on the system; blooms of a predator populations result in direct declines in their prey, especially if the predation genes themselves evolve to be highly fit. 

Research into the ecology of marine ecosystem informs the model, dividing the spatial area into three zones; the Photic, Aphotic and Abyssal zones. Each zone has different levels of light intensity which affects the fitness of Photosythesis genes (carried only by Phytoplankton). This keeps Phytoplankton abudant at the top, in the Photic Zone. Whilst this would presumably would seem to create a grazing ground for predators such as Zooplankton, the high levels of Phytoplankton tend to take up significant amounts of Carbon Dioxide, imposing a limit on the population densities of their predators. They therefore tend to succeed at lower levels of the marine environment, but get limited by the lack of prey, keeping balance throughout.

Research into fitness models inspired the mathematical representation of alleles using logistic curves. I found this to be a useful way of both modelling alleles of the same gene through alteration of curvature, but also as a way of modelling fitness correlated against the abundance of specific abiotic factors and effect of fitness when it reaches a limit.

## User Guide

### Interactivity

The software is interactive and allows you to alter levels of Carbon Dioxide and Oxygen through toggles. The levels toggle between depths of the marine ecoystems, and cycling through the depths will take you to each respective control area. Increasing Oxygen levels in the Photic zone for instance will cause a sudden bloom of Phytoplankton (followed by mass extinction as Carbon Dioxide reaches a point too low for survival - the closed system at play).

Scroll to zoom in and out, and pan to move around. Speed up and down to simulate time.

You will see analytics about genes and population levels, including fitness values of each allele in the zone with respect to the function used to quantify it. These values rapidly change due to random mutations.

### System Requirements
- Java 8
- MacOS

In order to run the software please download **marine-sim.jar** which is already compiled and packaged with Processing. Simply double click to run or for command line:

`java -jar marine_sim.jar`

  
