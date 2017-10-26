package com.ratpack.smartapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ratpack.guice.Guice;
import ratpack.handling.Chain;
import ratpack.http.client.ReceivedResponse;
import ratpack.server.BaseDir;
import ratpack.test.embed.EmbeddedApp;

@RunWith(JUnit4.class)
public class IntegrationTest {

	 @Test
	  public void completeTest() throws Exception {
		  EmbeddedApp.of(s-> s
				  .serverConfig(c -> c.baseDir(BaseDir.find()))
			        .registry(Guice.registry(b -> b.module(MyModule.class)))
			        .handlers(chain -> chain
			        	.get("switches",SmartGetHandler.class)
			        	.post("setLevel", SmartAppHandler.class)
			            .prefix("static", nested -> nested.fileSystem("assets/images", Chain::files)) // Bind the /static app path to the src/ratpack/assets/images dir
			            .all(ctx -> ctx.render("root handler!"))
			        )	  
		).test(httpClient -> {
					String [] lightValues = {"25","50","75","99"};
					for (String value : lightValues) {
						ReceivedResponse postResponse = httpClient.requestSpec(s ->
				         s.body(b -> b.type("application/json").text("{\"value\":\""+value+"\"}"))
				        ).post("setLevel");
						
				        assertEquals("Success", postResponse.getBody().getText());
				      
				        ReceivedResponse getResponse = httpClient.get("switches");
				        assertEquals("text/plain;charset=UTF-8", getResponse.getHeaders().get("Content-Type"));
				        String expectedString = "[{\"name\":\"switches\",\"value\":"+value+"}]";
				        System.out.println("Checking the response contains "+value+" or not in switches get request - "+getResponse.getBody().getText());
				        assertEquals(expectedString, getResponse.getBody().getText());
				        
					}
		});
	  }
}
