var CLIENT_ID = '64589802492-netk2vuc9frhlhinl2mkj5crq9totfsb.apps.googleusercontent.com'; //pawan
var DISCOVERY_DOCS = ["https://www.googleapis.com/discovery/v1/apis/youtube/v3/rest"];
var SCOPES = 'https://www.googleapis.com/auth/youtube.force-ssl';
var GoogleAuth;


gapi.load('auth2', initSignIn);

function initSignIn() {
    gapi.load('client:auth2', function () {
        gapi.client.init({
            discoveryDocs: DISCOVERY_DOCS,
            clientId: CLIENT_ID,
            scope: SCOPES
        }).then(function () {

            GoogleAuth = gapi.auth2.getAuthInstance();

            // Listen for sign-in state changes.
            GoogleAuth.isSignedIn.listen(updateSigninStatus);
            var user = GoogleAuth.currentUser.get();
            updateSigninStatus(GoogleAuth.isSignedIn.get());

        });

    });
}

function isSignedIn(){
    if(GoogleAuth && !GoogleAuth.isSignedIn.get()) {return false};
    return true;
}

function updateSigninStatus(signedIn){
    if(signedIn){
        $('body').trigger("signedin");
        if(window.location.href.indexOf("home") !== -1 || window.location.href.indexOf("stats") !== -1){
           // console.log("sign in true but not doing anything");
        }else{
            window.location.href = '/#!/home';
        }
    }
    else
        window.location.href = '/#!/';

}