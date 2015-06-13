# MasterThesisCodeFinal 

Allows for populating a prototype with external data. This code builds on the work of Fredrik Larsen, which can be found by following this link: https://github.com/frel/WireframeToJavaFX

In order to generate and run the example code for the project named "masterexample" do the following:

1. Locate the package named generator, then the file named Generator.xtend, right click and select run as Java application.
2. Refresh the package named generated and verify that the new files generated are visible in Eclipse.
3. Locate the package named application, then the file named AppController.xtend, right click and select run as Java application.

In order to change which Wireframesketcher project to generate a prototype for, locate the package named application, then open the file named Constants.xtend and change the value of SUB_PROJECT_NAME to the name of the Wireframesketcher project to generate code for.

See the projects named "masterexample" and "movieapp" under the "wireframing-tutorial" project for examples on how to populate a prototype with any of the three data sources existing inside the package named datagenerator. To use a data source of your own, make sure it's based on ecore and xmi and store it inside the datagenerator package.


