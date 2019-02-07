package com.streamviewer.rest.model;

import javax.validation.constraints.NotNull;

public class ChatMessage {
  private String email;
  private int id;
  private String message;
  private String videoId;
  private String userName;
  private String userid;


  public ChatMessage(@NotNull int id, @NotNull String message, String videoId, String username, @NotNull String email) {
    this.id = id;
    this.message = message;
    this.videoId = videoId;
    this.userName = username;
    this.email = email;
  }

  public ChatMessage(){

  }

  public int getId() {
    return id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getVideoId() {  return videoId;  }

  public void setVideoId(String videoId) { this.videoId = videoId;  }

  public String getUserName() {   return userName; }

  public void setUserName(String username) { this.userName = username; }

  public String getUserid() { return userid; }

  public void setUserid(String userid) { this.userid = userid; }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
