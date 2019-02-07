package com.streamviewer.rest.database;

import com.google.gson.JsonObject;
import com.streamviewer.rest.model.ChatMessage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A note database which connects and persists data to PostgreSQL database.
 */
public class ChatDatabase extends Database{

  private static final String TABLE_NAME = "chats";
  private static final String ID_KEY = "id";
  private static final String MESSAGE = "message";
  private static final String VIDEO_ID = "videoid";
  private static final String USER_EMAIL = "user_email";
  private static final String USER_NAME = "user_name";



  public ChatDatabase() {

  }


  public void createTableIfNotExists() {
    try {
      Statement createTableStatement = db.createStatement();
      createTableStatement.execute(
          "CREATE TABLE IF NOT EXISTS "
              + TABLE_NAME
              + " ("
              + ID_KEY
              + " SERIAL PRIMARY KEY, "
              + VIDEO_ID
              + " VARCHAR (200),"
              + MESSAGE
              + " TEXT, "
              + USER_NAME
              + " VARCHAR (300), "
              + USER_EMAIL
              + " VARCHAR (200)  REFERENCES users(email))");
      createTableStatement.close();
    } catch (SQLException e) {
      e.printStackTrace();

    }
  }

  public List<JsonObject> getAllChatsByMessageCount() {
    List<JsonObject> list = new ArrayList<>();
    try {
      Statement stmt = db.createStatement();
      String query = "SELECT count(*), " + USER_NAME+ " FROM " + TABLE_NAME + " GROUP BY " +USER_NAME+ " ORDER BY " + USER_NAME ;
      System.out.println("query is" + query);

      ResultSet resultSet =
              stmt.executeQuery(query);
      while (resultSet.next()) {
        JsonObject obj = new JsonObject();
        int count = resultSet.getInt(1);
        String username = resultSet.getString(2 );
        obj.addProperty("count", count);
        obj.addProperty("username", username);
        list.add(obj);
      }
      stmt.close();
      resultSet.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public List<ChatMessage> searchChat(String searchQuery) {
    List<ChatMessage> chats = new ArrayList<>();
    try {
      Statement stmt = db.createStatement();
      if(searchQuery ==null || searchQuery.isEmpty()) return chats;

      String[] splittedName = searchQuery.split(" ");
      StringBuilder WHERE_CLAUSE = new StringBuilder();
     // WHERE_CLAUSE.append("%(");
      for(int i=0; i<splittedName.length; i++ ){
         String s = splittedName[i];
         WHERE_CLAUSE.append(s.toLowerCase());

         if(i < splittedName.length - 1)
           WHERE_CLAUSE.append("|");
      }

  //    WHERE_CLAUSE.append(")%");
      System.out.println("query is" + WHERE_CLAUSE);

      String fullQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + USER_NAME+ " ~* '" + WHERE_CLAUSE +"'";
      System.out.println("full query is" + fullQuery);

      ResultSet resultSet =
          stmt.executeQuery(fullQuery);


      while (resultSet.next()) {
        ChatMessage chat = new ChatMessage();

        String name = resultSet.getString(USER_NAME);
        String message = resultSet.getString(MESSAGE);
        String videoid = resultSet.getString(VIDEO_ID);
        chat.setUserName(name);
        chat.setMessage(message);
        chat.setVideoId(videoid);
        chats.add(chat);
      }
      stmt.close();
      resultSet.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return chats;
  }

  public int addChat(ChatMessage chat) {
//    if(isUserExist(user.getEmail()))
//      return  -1;


    String insertSql =
        "INSERT INTO " + TABLE_NAME + "(" + VIDEO_ID +"," + MESSAGE +"," + USER_NAME  + "," + USER_EMAIL + ") VALUES ('" + chat.getVideoId() +"', '"+ chat.getMessage() +"' , '" + chat.getUserName() +  "' , '" + chat.getEmail() + "')";
    try {

      System.out.println("query is" + insertSql);


      PreparedStatement statement = db.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new SQLException("Creating user failed, no rows affected.");
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          int row = generatedKeys.getInt(1);
          statement.close();
          return row;
        } else {
          throw new SQLException("Creating user failed, no ID obtained.");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }



}
