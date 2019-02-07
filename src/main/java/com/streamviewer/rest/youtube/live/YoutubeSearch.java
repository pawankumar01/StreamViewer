/*
 * Copyright (c) 2012 Google Inc.
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
import com.google.api.services.youtube.model.SearchListResponse;
import com.streamviewer.rest.util.Injection;

import java.io.IOException;

/**
 * Print a list of videos matching a search term.
 *
 */
public class YoutubeSearch {

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */

    private static final long NUMBER_OF_VIDEOS_RETURNED = 27;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     */
    public static String search(String nextPageToken, String accesstoken) {

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("StreamViewer").build();


            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            String apiKey = Injection.getAPIKey();

            search.setKey(apiKey);
           // search.setOauthToken(accesstoken);
            search.setEventType("live");
            search.setType("video");
            search.setVideoCategoryId("20");
            search.setRegionCode("US");
            if(nextPageToken != null)
                search.setPageToken(nextPageToken);

            search.setFields("items(id/videoId,snippet/title,snippet/thumbnails/medium/url), nextPageToken");

            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);




            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            //System.out.println("The next page Token is" + searchResponse.getNextPageToken());

            //List<SearchResult> searchResultList = searchResponse.getItems();

            return searchResponse.toString();

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            return "limitexceeded";
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "[]";
    }


}
