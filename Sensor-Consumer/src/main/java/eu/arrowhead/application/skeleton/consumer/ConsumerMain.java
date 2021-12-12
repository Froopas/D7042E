package eu.arrowhead.application.skeleton.consumer;

import java.io.File;
import java.util.Scanner;
import java.util.Random;

import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.dto.shared.SenML;
import eu.arrowhead.common.exception.ArrowheadException;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, "ai.aitia"}) //TODO: add custom packages if any
public class ConsumerMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
	private final Logger logger = LogManager.getLogger( ConsumerMain.class );
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(ConsumerMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
    @Override
	public void run(final ApplicationArguments args) throws Exception {
		//SIMPLE EXAMPLE OF INITIATING AN ORCHESTRATION
    	System.out.println(logger.isDebugEnabled());
    	final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
    	
    	final ServiceQueryFormDTO requestedService = new ServiceQueryFormDTO();
    	requestedService.setServiceDefinitionRequirement("proxy");
    	
    	orchestrationFormBuilder.requestedService(requestedService)
    							.flag(Flag.MATCHMAKING, false) //When this flag is false or not specified, then the orchestration response cloud contain more proper provider. Otherwise only one will be chosen if there is any proper.
    							.flag(Flag.OVERRIDE_STORE, true) //When this flag is false or not specified, then a Store Orchestration will be proceeded. Otherwise a Dynamic Orchestration will be proceeded.
    							.flag(Flag.TRIGGER_INTER_CLOUD, false); //When this flag is false or not specified, then orchestration will not look for providers in the neighbor clouds, when there is no proper provider in the local cloud. Otherwise it will. 
    	
    	final OrchestrationFormRequestDTO orchestrationRequest = orchestrationFormBuilder.build();
    	
    	OrchestrationResponseDTO response = null;
    	try {
    		response = arrowheadService.proceedOrchestration(orchestrationRequest);
			printOut(response);
		} catch (final ArrowheadException ex) {
			//Handle the unsuccessful request as you wish!
		}
    	
    	//EXAMPLE OF CONSUMING THE SERVICE FROM A CHOSEN PROVIDER
    	
    	if (response == null || response.getResponse().isEmpty()) {
    		//If no proper providers found during the orchestration process, then the response list will be empty. Handle the case as you wish!
    		logger.debug("Orchestration response is empty");
    		return;
    	}
		logger.debug("Test");
    	
    	final OrchestrationResultDTO result = response.getResponse().get(0); //Simplest way of choosing a provider.
    	
    	final HttpMethod httpMethod = HttpMethod.PUT;//Http method should be specified in the description of the service.
    	final String address = result.getProvider().getAddress();
    	final int port = result.getProvider().getPort();
    	String serviceUri = result.getServiceUri();
    	final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
    	String token = null;
    	if (result.getAuthorizationTokens() != null) {
    	
			token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
		}
		serviceUri += "/cmbox1/sensor1";
		System.out.println(address);
		System.out.println(port);
		System.out.println(serviceUri);

		String datastr = "";
		String currentPath = new java.io.File(".").getCanonicalPath();
		System.out.println("Current dir:" + currentPath);
		try{
			File fh = new File("./DatasetTruncated.csv");
			Scanner scan = new Scanner(fh);
			while (scan.hasNextLine()) {
				datastr += (scan.nextLine()) + "\n";
				//System.out.println(datastr);
			}
			scan.close();
			System.out.println("Done read");
		} catch (Exception e) {
			System.out.println("Error in file");
		}

		Random rand = new Random();

		String[] entries = datastr.split("\n");
		System.out.println(datastr);
		int i = 0;
		while(true) {
			String entry = entries[i];
			String[] fields = entry.split(",");
			System.out.println(entry);

			final Object payload = "[{ \"bn\": \"CMBox1\", \"bt\":"+ fields[0] + ", \"bu\": \"V/g\", \"bver\": 1.0, \"n\": \"senor1\", \"u\": \"V/g\", \"v\":"+ fields[3] +"}]"; //Can be null if not specified in the description of the service.
    	
			final String consumedService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri, interfaceName, token, payload);

			System.out.println(i);

			i = (i + 1) % entries.length;

			Thread.sleep(1000);
		}
		

	}

	private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
