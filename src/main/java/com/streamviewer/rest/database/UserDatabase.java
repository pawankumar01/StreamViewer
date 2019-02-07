package com.streamviewer.rest.database;

import com.streamviewer.rest.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A note database which connects and persists data to PostgreSQL database.
 */
public class UserDatabase extends  Database{
  private static final String TABLE_NAME = "users";
  private static final String ID_KEY = "id";
  private static final String EMAIL = "email";
  private static final String NAME = "name";
  private static final String IMAGEURL = "imageurl";

  public UserDatabase() {

  }

  public void createTableIfNotExists() {
    try {
      Statement createTableStatement = db.createStatement();
      createTableStatement.execute(
          "CREATE TABLE IF NOT EXISTS "
              + TABLE_NAME
              + " ("
              + NAME
              + " VARCHAR (300),"
              + EMAIL
              + " VARCHAR (200) PRIMARY KEY, "
              + IMAGEURL
              + " TEXT)");
      createTableStatement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<User> getUsers() {
    List<User> users = new ArrayList<>();
    try {
      Statement query = db.createStatement();
      ResultSet resultSet =
          query.executeQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME);
      while (resultSet.next()) {
        String name = resultSet.getString(NAME);
        String email = resultSet.getString(EMAIL);
        String imageurl = resultSet.getString(IMAGEURL);
        User user = new User(email, name, imageurl );
        users.add(user);
      }
      query.close();
      resultSet.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return users;
  }

  public String addUser(User user) {

    /**
     *  If user already exists do nothing for now
     */

    String insertSql =
        "INSERT INTO " + TABLE_NAME + "(" + NAME +"," + EMAIL +"," + IMAGEURL +") VALUES ('" + user.getName() +"', '"+ user.getEmail() +"' , '" + user.getImageUrl() + "')";
          //ON CONFLICT("+ EMAIL+")" +  " DO NOTHING";
    try {

      System.out.println("query is" + insertSql);

      PreparedStatement statement = db.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new SQLException("Creating user failed, no rows affected.");
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          String row = generatedKeys.getString(2);
          statement.close();
          return row;
        } else {
          throw new SQLException("Creating user failed, no ID obtained.");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean isUserExist(String email){
    String query = "select exists(select 1 from " + TABLE_NAME + " where " + EMAIL + " ='"+email+"')";
    try {
      Statement stmt = db.createStatement();
      ResultSet resultSet =
              stmt.executeQuery(query);

      while (resultSet.next()) {
        return  true;
      }
      stmt.close();
      resultSet.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }


  public User getUser(String email) {
    User user = null;
    try {
      Statement query = db.createStatement();
      ResultSet resultSet =
          query.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + EMAIL + " = '" + email + "'" );
      while (resultSet.next()) {
        int id = resultSet.getInt(ID_KEY);;
        String name = resultSet.getString(NAME);
        String imageurl = resultSet.getString(IMAGEURL);
        user = new User(email, name, imageurl);
      }
      query.close();
      resultSet.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return user;
  }

}
