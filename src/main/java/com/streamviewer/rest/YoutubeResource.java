package com.streamviewer.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.streamviewer.rest.database.ChatDatabase;
import com.streamviewer.rest.model.ChatMessage;
import com.streamviewer.rest.util.Injection;
import com.streamviewer.rest.youtube.live.GetLiveChatId;
import com.streamviewer.rest.youtube.live.InsertLiveChatMessage;
import com.streamviewer.rest.youtube.live.YoutubeSearch;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *  Class represent REST API for YOUTUBE related data
 *  Search, livegames, getLiveChatId for the broadcast, chats by username etc.
 *  Base URL - http://localhost:8080/api/v1/yt/
 */

@Path("yt")
public class YoutubeResource {
  private ChatDatabase chatDb = Injection.provideChatDatabase();
  private Gson gson = Injection.provideGSONObject();


  /**
   * Fetches all the lives games of from youtube.
   * @param pageToken - represent pageToken from next page
   * @param httpheaders
   * @return
   */
  @GET
  @Path("/livegames")
  @Produces(MediaType.APPLICATION_JSON)
  public Response liveGames(@QueryParam("pagetoken") String pageToken,  @Context HttpHeaders httpheaders) {
    String accesstoken = httpheaders.getHeaderString("token");
    String searchresult = YoutubeSearch.search(pageToken, accesstoken);
    if("limitexceeded".equals(searchresult)) {
      JsonObject obj = new JsonObject();
      obj.addProperty("desc", "Api limit has been reached. Please try again later!");
      return Response.status(Response.Status.TOO_MANY_REQUESTS).entity(obj.toString()).build();
    }
    return Response.ok(searchresult).build();
  }


  /**
   * Fetch livechatid associated with live video
   * @param videoid
   * @return
   */

  @GET
  @Path("/livechatid")
  @Produces(MediaType.APPLICATION_JSON)
  public Response liveChatId(@QueryParam("videoid") String videoid) {
    //StringBuilder messageBuilder = new StringBuilder(User_LIST_HEADER);
    String chatid = GetLiveChatId.fetch(videoid);
    JsonObject obj = new JsonObject();
    obj.addProperty("chatid",chatid);
    return Response.ok(obj.toString()).build();
  }


  /**
   * Fetches username and count of chat by them
   * @param limit
   * @return
   */

  @GET
  @Path("/chatsbyusername")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getChatsByUsername(@QueryParam("limit") int limit) {
    List<JsonObject> obj = chatDb.getAllChatsByMessageCount();
    return Response.ok(obj.toString()).build();
  }


  /**
   * Fetches all the chats for specified username
   * @param q
   * @return
   */

  @GET
  @Path("/searchuserchat")
  @Produces(MediaType.APPLICATION_JSON)
  public Response searchChatsByUsername(@QueryParam("q") String q) {
    List<ChatMessage> obj = chatDb.searchChat(q);
    return Response.ok(gson.toJson(obj)).build();
  }

  /**
   * Insert Live Chat in the the Live Video Chat Session.
   * @param jsonObject
   * @param httpheaders
   * @return
   */
  @POST
  @Path("/sendchat")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response sendChat(String jsonObject, @Context HttpHeaders httpheaders) {
    String token = httpheaders.getHeaderString("token");
    JsonObject convertedObject = gson.fromJson(jsonObject, JsonObject.class);

    String liveChatId = String.valueOf(convertedObject.get("liveChatId"));
    ChatMessage chat = gson.fromJson(jsonObject, ChatMessage.class);

   // System.out.println("livechatid=" + liveChatId + ", chat=" + chat.getMessage());
    InsertLiveChatMessage.insertChat(chat.getMessage(), liveChatId, token);
    chatDb.addChat(chat);
    return Response.ok().build();
  }

}
