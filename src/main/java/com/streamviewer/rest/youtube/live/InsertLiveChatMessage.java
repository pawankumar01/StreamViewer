/*
 * Copyright (c) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.streamviewer.rest.youtube.live;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatTextMessageDetails;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

/**
 * Inserts a message into a live broadcast of the current user or a video specified by id.
 *
 * The videoId is often included in the video's url, e.g.:
 * https://www.youtube.com/watch?v=L5Xc93_ZL60
 *                                 ^ videoId
 * The video URL may be found in the browser address bar, or by right-clicking a video and selecting
 * Copy video URL from the context menu.
 *
 */
public class InsertLiveChatMessage {


    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Inserts a message into a live broadcast.
     *
     * @param message The message to insert (required) followed by the livechatid (optional).
     *
     */
    public static void insertChat(String message, String liveChatId, String token) {



        // This OAuth 2.0 access scope allows for write access to the authenticated user's account.
        List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE_FORCE_SSL);

        try {
            // Authorize the request.
            // Auth.authorize(scopes, "insertlivechatmessage");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("StreamViewer").build();



            // Insert the message into live chat
            LiveChatMessage liveChatMessage = new LiveChatMessage();
            LiveChatMessageSnippet snippet = new LiveChatMessageSnippet();
            snippet.setType("textMessageEvent");
            snippet.setLiveChatId(liveChatId);
            LiveChatTextMessageDetails details = new LiveChatTextMessageDetails();
            details.setMessageText(message);
            snippet.setTextMessageDetails(details);
            liveChatMessage.setSnippet(snippet);


            YouTube.LiveChatMessages.Insert liveChatInsert =
                youtube.liveChatMessages().insert("snippet", liveChatMessage);
           // liveChatInsert.setKey(apiKey);
            liveChatInsert.setOauthToken(token);
            LiveChatMessage response = liveChatInsert.execute();
            System.out.println("Inserted message id " + response.getId());

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }
}
