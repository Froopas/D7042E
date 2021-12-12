package eu.arrowhead.application.skeleton.consumer.controllers;

import java.lang.Double;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID; 

import org.json.JSONObject;
import org.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import eu.arrowhead.application.skeleton.consumer.util.OrResponseTransporter;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.ArrowheadException;


@RestController
public class TestController {

	@Autowired
	private ArrowheadService arrowheadService;

	@Autowired
	private OrResponseTransporter transporter;
    
    @GetMapping("/test")
    @ResponseBody 
	public String getCars() {
		System.out.println(transporter.isAvailable());
		System.out.println(transporter.getResponse().getResponse().get(0).getServiceUri());
		return "Done";
	}
	@GetMapping("/user")
    @ResponseBody
    public Principal user(Principal user) {
		//System.out.println("Login reached");
		//System.out.println(transporter);
        return user;
    }

    @GetMapping("/resource")
    @ResponseBody
    public Map<String, Object> res() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }

    @GetMapping("/systems")
    @ResponseBody
    public ArrayList<Object> home() {
        System.out.println("Systems reached");
        OrchestrationResponseDTO response = transporter.getResponse();
        final OrchestrationResultDTO result = response.getResponse().get(0); //Simplest way of choosing a provider.
    	
    	final HttpMethod httpMethod = HttpMethod.GET;//Http method should be specified in the description of the service.
    	final String address = result.getProvider().getAddress();
    	final int port = result.getProvider().getPort();
    	final String serviceUri = result.getServiceUri();
    	final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
    	String token = null;
    	if (result.getAuthorizationTokens() != null) {
    		token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
		}
    	final Object payload = null; //Can be null if not specified in the description of the service.
    	String consumedService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri, interfaceName, token, payload, "testkey", "testvalue");
        System.out.println(consumedService);

        JSONObject obj = new JSONObject(consumedService);

        JSONArray jsonSystems = obj.getJSONArray("systems");
        ArrayList<Object> arr = new ArrayList<Object>();
        for(int i = 0; i < jsonSystems.length(); i++) {
            Map<String, Object> system = new HashMap<String, Object>();
            String sysServiceUri = serviceUri + "/" + jsonSystems.getString(i);
            consumedService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, sysServiceUri, interfaceName, token, payload);
            JSONObject obj2 = new JSONObject(consumedService);
            JSONArray jsonServices = obj2.getJSONArray("services");

            ArrayList<Object> services = new ArrayList<Object>();
            for(int j = 0; j < jsonServices.length(); j++) {
                
                Map<String, Object> senML = new HashMap<String, Object>();
                String sysSerServiceUri = sysServiceUri + "/" + jsonServices.getString(j);
                consumedService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, sysSerServiceUri, interfaceName, token, payload);
                System.out.println(consumedService);
                JSONArray arrayJson = new JSONArray(consumedService);
                JSONObject obj3 = arrayJson.getJSONObject(0);

                senML.put("n", obj3.getString("n"));
                senML.put("u", obj3.getString("u"));
                senML.put("v", new Double(obj3.getDouble("v")));

                Map<String, Object> service = new HashMap<String, Object>();
                service.put("name", jsonServices.getString(j));
                service.put("data", senML);
                services.add(service);
            }
            system.put("name", jsonSystems.getString(i));
            system.put("services", services);
            arr.add(system);
        }

        return arr;
    }

    @GetMapping("/systemdata")
    @ResponseBody
    public Map<String, Object> data() {
        System.out.println("Systems reached");
        OrchestrationResponseDTO response = transporter.getResponse();
        final OrchestrationResultDTO result = response.getResponse().get(0); //Simplest way of choosing a provider.
    	
    	final HttpMethod httpMethod = HttpMethod.GET;//Http method should be specified in the description of the service.
    	final String address = result.getProvider().getAddress();
    	final int port = result.getProvider().getPort();
    	String serviceUri = result.getServiceUri();
    	final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
    	String token = null;
    	if (result.getAuthorizationTokens() != null) {
    		token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
		}
        serviceUri += "/cmbox1/sensor1";
    	final Object payload = null; //Can be null if not specified in the description of the service.
    	final String consumedService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri, interfaceName, token, payload, "testkey", "testvalue");
        System.out.println(consumedService);
        Map<String, Object> model = new HashMap<String, Object>();

        JSONArray arr = new JSONArray(consumedService);
        JSONObject obj = arr.getJSONObject(1);
        String nVal = obj.getString("n");
        String uVal = obj.getString("u");
        int vVal = obj.getInt("v");
        model.put("n", nVal);
        model.put("u", uVal);
        model.put("v", vVal);
        
        

        return model;
    }


    @GetMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/";
    }

    // @Configuration
    // protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    //     @Override
    //     protected void configure(HttpSecurity http) throws Exception {
    //         // @formatter:off
    //         http
    //             .httpBasic().and()
    //             .authorizeRequests()
    //                 .antMatchers("/index.html", "/", "/home", "/login").permitAll()
    //                 .anyRequest().authenticated()
    //                 .and()
    //             .csrf()
    //                 .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    //         // @formatter:on
    //     }
    // }

}