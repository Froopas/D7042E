# E7042E
IoT - Arrowhead scenario

This was made on a linux computer and I am uncertain it would work on a windows computer.

## Installation

In order to get everything to work properly two Arrowhead clouds are required preferably on two devices.

The clouds might need extra database connections, if so is the case run the command

> sudo mysql -u root -p

followed by

> set global max_connections = 2000;

Currently the configuration is that the HMI(HMI-Consumer) is set in the testcloud1 official arrowhead cloud whilst the CMBox(Sensor-Consumer) is in the testcloud2.

To setup the inter cloud connection follow [this](https://github.com/arrowhead-f/core-java-spring/blob/master/documentation/gatekeeper/GatekeeperSetup.md) guide.

The datamanager need to be changed in accordance to the mail in the documentation folder of the repository.

## Run

After the clouds are started, hopefully the application runs smoothly.

To start a system go to the folder where the src folder is located and the run the command
> mvn spring-boot:run

After running both applications the interface can be found on the (ip of HMI system):8888

## Documentation
The documentation for each system is located in their respective sub-folder where there exists a documentation folder.

The System-of-System Description is located in the primary folder documentation folder together with the relevant Datamanager documentation.

## My problems with Arrowhead
Arrowhead works well in concepts but struggle in the execution.

A lot of the core systems lack the documentation of SySD, IDDs, and SDs.

The documentation of how to set up the arrowhead cloud is outdated. Files have the wrong name.
Nowhere is there a mention about a need to add the consumer in the serviceregistry in order for it to be able to orchestrate.

The interfaces which exist are described only with examples and describes only the "outermost" objects in the JSON requests. It would be easier to work with if there were OpenAPI(or similar) documentation describing exactly the requirements for the request. This is mostly a problem when building your own application without using the Java spring skeleton.

The fact that the arrowhead framework was updated to 4.4 with no recent commits is confusing. If you have the source code you should not be dependent on the maven repositories. (I am quite unfamiliar with the way maven works but has encountered trouble with this point).
You should be able to install and run older versions of the arrowhead core systems.

The performance of Arrowhead is not the best. On a raspberry pi it takes about 10-15 minutes for all core systems to start.
One cloud takes about 500+ database connections which should not be needed.