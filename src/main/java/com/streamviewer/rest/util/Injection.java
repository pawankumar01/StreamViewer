package com.streamviewer.rest.util;

import com.google.gson.Gson;
import com.streamviewer.rest.database.ChatDatabase;
import com.streamviewer.rest.database.UserDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A simple dependence injector which creates and provides app's objects.
 */
public class Injection {
  private static UserDatabase userDatabase;
  private static ChatDatabase chatDatabase;
  private static Gson gson = new Gson();
  private static final String PROPERTIES_FILENAME = "youtube.properties";
  private static String apiKey = null;


  /**
   * Returns a singleton UserDatabase.
   */
  public static UserDatabase provideUserDatabase() {
    if (userDatabase == null) {
      userDatabase = new UserDatabase();
    }
    return userDatabase;
  }

  public static ChatDatabase provideChatDatabase() {
    if (chatDatabase == null) {
      chatDatabase = new ChatDatabase();
    }
    return chatDatabase;
  }

  public static Gson provideGSONObject(){
    return gson;
  }

  public static String getAPIKey(){

      if(apiKey != null) return apiKey;

      Properties properties = new Properties();
      try {
        InputStream in = Injection.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
        properties.load(in);

      } catch (IOException e) {
        System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                + " : " + e.getMessage());
        System.exit(1);
      }
      apiKey = properties.getProperty("youtube.apikey");
      return apiKey;
  }


}
