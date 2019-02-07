package com.streamviewer.rest.model;

import javax.validation.constraints.NotNull;

public class User {
  private String email;
  private String imageUrl;
  private String name;

  public User( @NotNull String email, @NotNull String name, String imageUrl) {
    this.email = email;
    this.name = name;
    this.imageUrl = imageUrl;
  }


  public String getEmail() {
    return email;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

//  @Override
//  public String toString() {
//    return getId() + ". " + getEmail();
//  }
}
