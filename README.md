## StreamViewer
StreamViewer fetches the list of live game stream from Youtube using Youtube Data API V3.
Users can watch the Live Game Stream and associated chat with live stream. User can send chat from the application itself.

### Responsive Design
The app is responsive and works well with desktop Web and Mobile devices.

### Chats Stats
- Total no. of messages in 15 secs
- Who messaged most during that time

![alt-text](https://i.ibb.co/0MLjQ51/Screen-Shot-2019-02-07-at-23-10-29.png)

### Other Stats
* Shows different stats such as
    - All Registered Users
    - Users and Message Count
    - Search Messages with Username

### Directory Structure

```
src |
    |-java (backend)
    |-webapp (frontend)
    |-resources (contains youtube api key and credentials)
```
### Technology Used
Backend uses ```Java, Jersey(Rest API) and PostgreSql```

Frontend uses ```HTML5, CSS, Javascript, Jquery, AngularJS```

Build Tools ```Gradle, Gretty(Tomcat)```

So, make sure to install it and replace your own database credentials in `Database.java` file.



### Run
```bash
$ ./gradlew appRun
```

### Functionality
The app implements following feature:-
* Backend
    - Fetch Youtube Live Stream
    - Fetch Current Video's LiveChatId
    - Insert new message in the live chat
    - Store Stats in PostgreSQL

* Frontend
    - Google SignIn
    - Fetch live stream chat directly from Youtube Data API
    - Home page shows different available live streams and allows user to click and play one at a time
    - Stats page show different stats mentioned above


### Hosted
https://streamviewer01.herokuapp.com

### Limitation
API KEY only supports 100 daily request for now. Daily limit exceeds very fast

### Improvement & Things to do
Make app secure
