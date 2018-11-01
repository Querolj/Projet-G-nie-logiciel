package com.example.jetty_jersey.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/example")
public class ExampleResource {

	public static class ExampleClass {
		public String field;
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/get")
	public ExampleClass getTest() {
		System.out.println("get");
		ExampleClass instance = new ExampleClass();
		instance.field = "get";
		return instance;
	}
	
	//post = update
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/post")
	@Produces(MediaType.TEXT_HTML)
	public String postTest(@FormParam("name") String name, @FormParam("msg") String msg) {
		System.out.println("post");
		
		return "<h2>Hello, " + name + ", msg : "+msg+ "</h2>";
	}
	
	//put = add
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/put")
	public String putTest(ExampleClass instance) {
		System.out.println("put");
		return "put";
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/delete")
	public void deleteTest(ExampleClass instance) {
		System.out.println("delete");
	}
}











