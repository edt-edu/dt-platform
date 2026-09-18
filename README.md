# DT Platform

This repository contains reusable software components and experimental implementations used to build and operate **digital twin platforms**.

It brings together services for connecting physical systems to digital-twin models, data mediation and gateway components, visualization tools, and experimental applications developed for different cyber-physical systems.

This repository focuses on the **digital-twin software platform and reusable DT components**. The broader experimental environment is organized across complementary repositories:

* [`cps-fischertechnik`](https://github.com/edt-edu/cps-fischertechnik) contains the physical Fischertechnik cyber-physical production system, its controllers, SCADA software, and related hardware assets.
* [`dt-setups`](https://github.com/edt-edu/dt-setups) contains concrete experimental setups that assemble and configure components from this repository together with physical systems and other infrastructure into reproducible digital-twin experiments.

Together, these repositories provide the building blocks for experimenting with different digital-twin architectures, configurations, and cyber-physical scenarios.

## Project structure

### Components

The `components/` directory contains reusable components intended to be shared across different digital-twin setups.

* `components/gateways/data-gateway-service/` — Gateway service for adapting physical-system data to digital-twin models, including MQTT topic mapping.
* `components/visualization/machines-svg-map/` — TypeScript tool for generating SVG layouts of Fischertechnik machines, including machine positions, static information, and access zones.

### Experimental components

The `experimental-components/` directory contains prototypes, research implementations, and complete example systems under active development by project partners.

It currently includes, among others:

* Factory digital-twin components and related documentation.
* TurtleBot digital-twin systems, robot implementations, and frontend applications.
* Fischertechnik gateway, visualization, and vacuum-gripper digital-twin projects.

Experimental components may evolve independently and may have their own build instructions, dependencies, or maturity level. Refer to the README in each component directory for details.

## License

This project is licensed under the Apache License, Version 2.0.

See the [LICENSE](LICENSE) file for details.
