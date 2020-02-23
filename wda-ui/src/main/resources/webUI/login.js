/*
Author: Cedric Hildbrand
Last Edit: 16.05.2019
*/

$(function () {
	$( "#nav_logout" ).click(function(event) {
		Cookies.remove('session');
		Cookies.remove('userid');
		document.location.reload();
	});	
});

$(function () {
	$( '.logincontainer' ).submit(function( event ) {
		// When login formular gets submitted
		
		event.preventDefault();
		var usernameinput = $('#inputUsername').val();
		var passwordinput = $('#inputPassword').val();
		
		var userData = [];
		userData.push(
			{
				username : usernameinput,
				password : passwordinput
			}
		);
		
		//Convert it to string because emit can't use a json array
		userData = JSON.stringify(userData);	
		socket_checkLogin(userData);
	});
});	

function logIn(session, userID){
	alert("Logged in!");
	console.log("Setting Cookie with session");
	Cookies.set('session', session);
	Cookies.set('userid', userID); // Should be encrypted, but whatever
	
	window.location = "http://localhost:5000/wetterdaten";
}

function checkSession(){
	/*
	On every page refresh, this function gets called. Sends an socket.io emit to the server and checks if the cookiedata
	is actually a real user and not fake data. Session hijacking is possible. Also if this function is not called (JS Editing)
	it doesn't get checked. To be safe this should get checked on server (PHP) not in JS
	*/
	
	if(!Cookies.get('session') || !Cookies.get('userid')){
		console.log("No Session found!");
		window.location = "http://localhost:5000/login";
	}else{
		var currentsession = Cookies.get('session');
		var currentuserid = Cookies.get('userid');
		
		if(Cookies.get('session') && Cookies.get('userid')){

			var sessionData = [];
			sessionData.push(
				{
					session : currentsession,
					userID : currentuserid
				}
			);
			sessionData = JSON.stringify(sessionData);
			socket_checkSession(sessionData);
		}
	}
}