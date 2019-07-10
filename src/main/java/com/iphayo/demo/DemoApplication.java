package com.iphayo.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Optional;


public class DemoApplication extends AbstractVerticle {

  private CustomerRepository repository = new CustomerRepository();

  @Override
  public void start() {

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.get("/customers").handler(this::handleListCustomer);
    router.get("/customers/:id").handler(this::handleGetCustomer);
    router.post("/customers").handler(this::handleAddCustomer);
    router.put("/customers/:id").handler(this::handleEditCustomer);
    router.delete("/customers/:id").handler(this::handleDeleteCustomer);

    vertx.createHttpServer().requestHandler(router).listen(8080);

  }

  private void handleListCustomer(RoutingContext routingContext) {
    JsonArray arr = new JsonArray();
    repository.findAll().forEach(c -> {
      arr.add(JsonObject.mapFrom(c));
    });

    routingContext.response()
      .putHeader("content-type", "application/json")
      .end(arr.encodePrettily());
  }

  private void handleAddCustomer(RoutingContext routingContext) {
    JsonObject body = routingContext.getBodyAsJson();
    HttpServerResponse response = routingContext.response();

    if(body == null) {
      response.setStatusCode(400).end();
	} else {
	  Customer c = body.mapTo(Customer.class);
	  Optional<Customer> customer = repository.findById(Integer.valueOf(c.getId()));

	  if (!customer.isPresent()) {
	    repository.save(c);
	    response.end();
	  } else {
	    response.setStatusCode(400).end();
	  }
	}
  }

  private void handleGetCustomer(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    HttpServerResponse response = routingContext.response();

    if(id == null) {
      response.setStatusCode(400).end();
    } else {
      Optional<Customer> customer = repository.findById(Integer.valueOf(id));
      if(!customer.isPresent()) {
        response.setStatusCode(404).end();
      } else {
        response.putHeader("content-type", "application/json")
          .end(JsonObject.mapFrom(customer.get()).encodePrettily());
      }
    }
  }

  private void handleEditCustomer(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    JsonObject body = routingContext.getBodyAsJson();
    HttpServerResponse response = routingContext.response();

    if(id == null) {
      response.setStatusCode(400).end();
    } else {
      Optional<Customer> customer = repository.findById(Integer.valueOf(id));
      if(!customer.isPresent()) {
        response.setStatusCode(404).end();
      } else {
        Customer c = body.mapTo(Customer.class);
        c.setId(customer.get().getId());

        repository.save(c);
        response.end();
      }
    }
  }


  private void handleDeleteCustomer(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    HttpServerResponse response = routingContext.response();

    if(id == null) {
      response.setStatusCode(400).end();
    } else {
      Optional<Customer> customer = repository.findById(Integer.valueOf(id));
      if(!customer.isPresent()) {
        response.setStatusCode(404).end();
      } else {
        repository.delete(Integer.valueOf(id));
        response.end();
      }
    }
  }

}
