package com.streamviewer.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.streamviewer.rest.database.UserDatabase;
import com.streamviewer.rest.model.User;
import com.streamviewer.rest.util.Injection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *  REST API RESOURCE FOR THE USER INFORMATION
 *  The base url http://localhost:8080/api/v1/user
 */

@Path("user")
public class UserResource {
  private UserDatabase userDb = Injection.provideUserDatabase();
  private Gson gson = Injection.provideGSONObject();

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUsers() {
    List<User> users = userDb.getUsers();
    return Response.ok(gson.toJson(users)).build();
  }

  /***
   * Insert New User to the db
   * @param jsonObject
   * @return
   */

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addUser(String jsonObject) {
    User user = gson.fromJson(jsonObject, User.class);
    userDb.addUser(user);
    JsonObject obj = new JsonObject();
    //obj.addProperty("userId", );
    return Response.ok(obj.toString()).build();
  }

}
