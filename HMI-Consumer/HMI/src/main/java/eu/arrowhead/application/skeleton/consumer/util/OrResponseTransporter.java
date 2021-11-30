package eu.arrowhead.application.skeleton.consumer.util;

import org.springframework.stereotype.Component;

import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;

@Component
public class OrResponseTransporter {
	
	private static boolean isAvailable;
	private static OrchestrationResponseDTO orResponse;
	
	public static boolean isAvailable() {
		return isAvailable;
	}
	
	public static void setResponse(OrchestrationResponseDTO resp) {
		orResponse = resp;
		isAvailable = true;
	}
	
	public static OrchestrationResponseDTO getResponse() {
		return orResponse;
	}
	
	

}
