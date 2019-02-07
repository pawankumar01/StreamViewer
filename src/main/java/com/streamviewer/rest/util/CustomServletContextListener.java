package com.streamviewer.rest.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Listens to servlet callbacks
 */
public class CustomServletContextListener implements ServletContextListener {

  @Override public void contextInitialized(ServletContextEvent sce) {
    // Connect to database and create table for the first time
    Injection.provideUserDatabase().connect();
    Injection.provideUserDatabase().createTableIfNotExists();

    Injection.provideChatDatabase().connect();
    Injection.provideChatDatabase().createTableIfNotExists();
  }

  @Override public void contextDestroyed(ServletContextEvent sce) {
    // Disconnect database
    Injection.provideUserDatabase().close();
    Injection.provideChatDatabase().close();
  }
}
