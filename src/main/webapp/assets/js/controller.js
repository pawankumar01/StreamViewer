app.controller('svCtrl', ["$scope", "$http", "utilService", "$cookies", function($scope, $http, $utilService, $cookies){
    console.log('In My controller');

    /**
     * Sign Out User on button Click
     * @returns {boolean}
     */
    $scope.signOut = function(){
        $utilService.signOut();
        return false;
    }

    $scope.videoPageToken = null;
    $scope.chatPageToken = null;
    $scope.playimagePath ='assets/images/play-button.png';
    $scope.videos = [];
    $scope.chats =[];
    $scope.liveChatId = "";
    $scope.myProfileUrl = $cookies.get('imageUrl');
    $scope.myProfileName = $cookies.get("name") ==undefined?"":  $cookies.get("name").split(" ")[0];
    $scope.videoId = null;

    $scope.livechatFields = "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
        + "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
        + "nextPageToken,pollingIntervalMillis";



    /**
     * Plays Selected Live Video
     * @returns {boolean}
     */
    $scope.playVideo = function(videoId){
        $('.mainvideowrapper').slideDown('fast');
        var innerhtml = '<iframe width="100%" height="360px" frameborder="0" src="https://www.youtube.com/embed/'+ videoId +'?autoplay=1&rel=0" allow="autoplay"> </iframe>';
        $('.mainvideo').html(innerhtml);
        $("html, body").animate({ scrollTop: 0 }, "slow");
        $scope.videoId = videoId;


        var fullurl = "http://localhost:8080/api/v1/yt/livechatid?videoid=" + videoId;
        var options={
            "url": fullurl,
            "method": "GET",
            "Content-Type": "application/json",
        };
        $http(options)
        .then(function(response){
                console.log("response is", response);
                $scope.liveChatId = response.data.chatid;
                $scope.chatPageToken = null;
                $scope.chats = [];
                fetchChatLiveMessage();
                calculateRollingWindowMessage(0);
            },
            function(err){
                console.log(err);
                if(err.status == -1){
                }else{}
        });



    }



    fetchData();
    $scope.loadMore = function(){
        fetchData();
    }

    /**
     *  Fetch Top live videos of game category
     */
    function fetchData(){

        $('.videowrapper').removeClass('limitreached');

        var fullurl = "http://localhost:8080/api/v1/yt/livegames";
        if($scope.videoPageToken != null){
            fullurl = fullurl + "?pagetoken=" + $scope.videoPageToken;
        }


        var options={
            "url": fullurl,
            "method": "GET",
            "Content-Type": "application/json",
        };
        $http(options)
        .then(function(response){
                $('.videowrapper').removeClass('fixheight');

                console.log("response is", response);
                $scope.videoPageToken = response.data.nextPageToken;
                $scope.videos =  $scope.videos.concat(response.data.items);
            },
            function(err){
                console.log(err);
                if(err.status == 429){
                        $('.videowrapper').addClass('limitreached').addClass('fixheight');
                }
        });
    }

    /**
     * Send chat to Stream Viewer Backend Server, which insert chat to live chat and persist it for future use.
     * @param item
     */
    $scope.sendChatToServer = function(item){
        if(!$scope.chattext){
            alert("Please write something!");
        }

        var token = {
            token:gapi.auth2.getAuthInstance().currentUser.get().getAuthResponse().access_token
        }

        var profile = gapi.auth2.getAuthInstance().currentUser.get().getBasicProfile();

        var obj = {
            id: 2,
            message: $scope.chattext,
            liveChatId:  $scope.liveChatId,
            videoId: $scope.videoId,
            userName: profile.getName(),
            email: profile.getEmail()
        };


        $('input.sendchat').val('');


        var options={
            "url": "http://localhost:8080/api/v1/yt/sendchat",
            "method": "POST",
            "Content-Type": "application/json",
            "data" : obj,
            "headers": token
        };
        $http(options).then(function(response){
            },
        function(err){
            console.log(err);
            if(err.status == -1){
            }else{}
        });
    }


    $scope.messageRollingWindow = [];


    /**
     *  This method directly fetches the chats from the Youtube API
     *  for associated youtube live video
     */
    function fetchChatLiveMessage(){

        if($scope.liveChatId == null) return;

        var options = {
                part: 'snippet, authorDetails',
                liveChatId:  $scope.liveChatId,
                fields: $scope.livechatFields,
        };

        if($scope.chatPageToken != null){
            options.pageToken = $scope.chatPageToken;
        }

        gapi.client.request({
            path: '/youtube/v3/liveChat/messages', params: options
           ,callback: function(response) {
                if (response.error) {
                    console.log(response.error.message);
                } else {
                    $scope.chats =  $scope.chats.concat(response.items);


                    $scope.$apply();
                    $('#chatarea').scrollTop($('#chatarea')[0].scrollHeight)

                    $scope.chatPageToken = response.nextPageToken;
                    var STATUS_POLLING_INTERVAL_MILLIS = response.pollingIntervalMillis;
                    setTimeout( fetchChatLiveMessage, STATUS_POLLING_INTERVAL_MILLIS);


                }
            }
        });
    }


    /***
     *  Update rolling message chat stats
     *  1) Total number of messages in  every 15sec
     *  2) Who is winning in sending most message
     *
     *  First display is after 30 secs
     * @param lastindex
     */
    function calculateRollingWindowMessage(lastindex){
        if(lastindex == 0){
            var lastindex = $scope.chats.length;
        }else{
            var newmessages = $scope.chats.length - lastindex;

            var obj = []; var max=0; var finalAuthorName = "";
            for(var i = lastindex; i<$scope.chats.length; i++){
                var authorName = $scope.chats[i].authorDetails.displayName;
                var freq = 0;
                if(obj[authorName]){
                    freq = obj[authorName];
                    obj[authorName] = freq + 1;
                }else{
                    obj[authorName] =1;
                    freq = 1;
                }

                if(freq > max){
                    max = freq;
                    finalAuthorName = authorName;
                }

            }

               // console.log("new messages are " + newmessages + " in 15sec" + " winner=" + finalAuthorName, "hello", obj);

                $scope.totalmessagesin15s = newmessages;
                $scope.finalAuthorName = finalAuthorName;


                lastindex = $scope.chats.length;
        }


        setTimeout( function(){
           calculateRollingWindowMessage(lastindex)
           }
           , 15000
       );
    }

    /**
     *  Listen to signin events and update photo and name
     */
    $('body').on('signedin', function(){
        var profile = gapi.auth2.getAuthInstance().currentUser.get().getBasicProfile();
        if(profile) {
            $scope.myProfileUrl = profile.getImageUrl();
            $scope.myProfileName = profile.getGivenName();
            $scope.$apply();
        }
    })


}]);




app.controller('statsCtrl', ["$scope", "$http", "utilService", "$cookies", function($scope, $http, $utilService, $cookies){
    console.log('In stats controller');
    /**
     *  Checks if user is signed in
     */
    // isSignedIn();


    /**
     * Sign Out User on button Click
     * @returns {boolean}
     */
    $scope.signOut = function(){
        $utilService.signOut();
        return false;
    }

    $scope.myProfileUrl = $cookies.get('imageUrl');
    $scope.myProfileName = $cookies.get("name") ==undefined?"":  $cookies.get("name").split(" ")[0];

    //$scope.$apply();

    $scope.query = {
        order: 'name',
        limit: 10,
        page: 1
    };

    $scope.userquery = {
        order: 'name',
        limit: 10,
        page: 1
    };


    /**
     *  Search chats from Stream Viewer Server associated with user name
     */
    $scope.searchChat = function(){
        var fullurl = "http://localhost:8080/api/v1/yt/searchuserchat?q=" + $scope.searchQuery;

        var options={
            "url": fullurl,
            "method": "GET",
            "Content-Type": "application/json",
        };
        $http(options)
        .then(function(response){
                console.log("response is", response);
                $scope.messages = response.data;
            },
            function(err){
                console.log(err);
                if(err.status == 429){
                    setTimeout(function(){
                        alert(err.data.desc);
                    }, 3000);
            }
        });
    }


    /**
     *  Fetch the Username and message count stats
     */
    fetchChatData();
    function fetchChatData(){
        var fullurl = "http://localhost:8080/api/v1/yt/chatsbyusername";

        var options={
            "url": fullurl,
            "method": "GET",
            "Content-Type": "application/json",
        };
        $http(options)
            .then(function(response){
                   // console.log("response is", response);
                    $scope.chatsdata = response.data;
                    $scope.chatsdata.count = response.data.length;
                },
                function(err){
                  //  console.log(err);
                    if(err.status == 429){
                        setTimeout(function(){
                            alert(err.data.desc);
                        }, 3000);
                    }
                });
    }

    /**
     *  Fetch all Users
     */
    fetchUserData();
    function fetchUserData(){
        var fullurl = "http://localhost:8080/api/v1/user";

        var options={
            "url": fullurl,
            "method": "GET",
            "Content-Type": "application/json",
        };
        $http(options)
            .then(function(response){
                 //   console.log("response is", response);
                    $scope.usersdata = response.data;
                    $scope.usersdata.count = response.data.length;
                },
                function(err){
                   // console.log(err);
                    if(err.status == 429){
                        setTimeout(function(){
                            alert(err.data.desc);
                        }, 3000);
                    }
                });
    }


    /**
     *  Listen to signin events and update photo and name
     */
    $('body').on('signedin', function(){
        var profile = gapi.auth2.getAuthInstance().currentUser.get().getBasicProfile();
        if(profile) {
            $scope.myProfileUrl = profile.getImageUrl();
            $scope.myProfileName = profile.getGivenName();
            $scope.$apply();
        }
    })

}]);







app.controller('loginCtrl', ["$scope", "$http", "utilService", "$cookies", "$window", function($scope, $http, $utilService, $cookies, $window){
    console.log('In login controller');


    /**
     *  SignIn User with Google API
     */
    $scope.signIn = function() {
        // Get `GoogleAuth` instance
        var auth2 = gapi.auth2.getAuthInstance();

        // Sign-In
        auth2.signIn()
            .then($scope.changeProfile);
    };

    $scope.changeProfile = function(googleUser) {
       // console.log("googleUser", googleUser);
        if (googleUser) {
            var profile = googleUser.getBasicProfile();
             $scope.profile = {
                name: profile.getName(),
                email: profile.getEmail(),
                imageUrl: profile.getImageUrl()
            };
          //  console.log($scope.profile);
            $cookies.put("name", $scope.profile.name);
            $cookies.put("email", $scope.profile.email);
            $cookies.put("imageUrl", $scope.profile.imageUrl);
            var options={
                "url": "http://localhost:8080/api/v1/user",
                "method": "POST",
                "Content-Type": "application/json",
                "data" : $scope.profile
            };
            $http(options)
            .then(function(response){
                  //  console.log("data has been saved", response);
                    $window.location.href = '/#!/home';
                },
                function(err){
                    alert("Something is not right. Please try again!");
                    $utilService.signOut();
                    if(err.status == -1){
                }else{}
            });
        } else {
            alert("Something is not right. Please try again!");

        }
    };
}]);



app.controller('callbackCtrl', ["$scope", "$http", "utilService", function($scope, $http, $utilService){
    console.log('In callback controller');
}]);