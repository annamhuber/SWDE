/*
Author: Cedric Hildbrand
Last Edit: 16.05.2019
*/

var socket = io.connect('http://localhost:9092');
socket.on('connect', function() { 
	console.log('Connected to socket.io Server.');
});

// Listeners
socket.on('loginUserCorrect', function(data) {
	console.log("Socket.io: Userlogin correct!");
	jsondata = JSON.parse(data);
	
	var obj = jsondata[0];
	session = obj.session;
	userID = obj.userID;
	
	console.log(session + " " + userID);
	logIn(session, userID);
});

socket.on('loginUserWrong', function() {
	console.log("Socket.io: Userlogin wrong!");
	alert("Benutzername oder Passwort falsch!");
});

socket.on('sessionCorrect', function() {
	console.log("Socket.io: Session correct");
});

socket.on('sessionWrong', function() {
	console.log("Socket.io: Session wrong");
	window.location = "http://localhost:5000/login"; // Sessioncheck should be done on webserver
});

socket.on('getUserDataResponse', function(data) {
	console.log("Socket.io: Got Users-List");
	renderUsers(data);
});

socket.on('getCustomersDataResponse', function(data) {
	console.log("Socket.io: Got Customers-List");
	renderCustomers(data);
});

socket.on('getCitiesResponse', function(data) {
	console.log("Socket.io: Got Cities-List (A01)");
	
	jsondata = JSON.parse(data);
	for(var i = 0; i < jsondata.length; i++) {
		var obj = jsondata[i];
		$("#cityselect").append('<option value="'+obj.cityID+'">'+obj.name+'</option>')
	}
	$('.selectpicker').selectpicker('refresh');
});

socket.on('weatherdataResponseA02', function(data) {
	console.log("Socket.io: Got response for A02");
	renderA02(data);
});

socket.on('weatherdataResponseA03', function(data) {
	console.log("Socket.io: Got response for A03");
	renderA03(data);
});

socket.on('weatherdataResponseA04', function(data) {
	console.log("Socket.io: Got response for A04");
	renderA04(data);
});

socket.on('weatherdataResponseA05', function(data) {
	console.log("Socket.io: Got response for A05");
	renderA05(data);
});

socket.on('weatherdataResponseA06', function(data) {
	console.log("Socket.io: Got response for A06");
	renderA06(data);
});

socket.on('weatherdataResponseA07', function(data) {
	console.log("Socket.io: Got response for A07");
	renderA07(data);
});

socket.on('weatherdataResponseA08', function(data) {
	console.log("Socket.io: Got response for A08");
	renderA08(data);
});

socket.on('weatherdataResponseA09', function(data) {
	console.log("Socket.io: Got response for A09");
	renderA09(data);
});

socket.on('weatherdataResponseA10', function(data) {
	console.log("Socket.io: Got response for A10");
	renderA10(data);
});

socket.on('weatherdataResponseA11', function(data) {
	console.log("Socket.io: Got response for A11");
	renderA11(data);
});

socket.on('weatherdataResponseA12', function(data) {
	console.log("Socket.io: Got response for A12");
	renderA12(data);
});


//Emits
function socket_checkLogin(data){
	console.log("Socket.io: Sent CheckLogin request");
	socket.emit('checkLogin', data);
}

function socket_checkSession(data){
	console.log("Socket.io: Sent CheckSession request");
	socket.emit('checkSession', data);
}

function socket_getCitiesRequest(){
	console.log("Socket.io: Sent Cities-List request");
	socket.emit('getCitiesRequest');
}

function socket_getWeatherDataRequest(data){
	console.log("Socket.io: Sent Weatherdata request");
	socket.emit('getWeatherdataRequest', data);
}

function socket_getUserDataRequest(){
	console.log("Socket.io: Sent Users-List request");
	socket.emit('getUserDataRequest');
}

function socket_addUser(data){
	console.log("Socket.io: Sent addUser request");
	socket.emit('addUser', data);
}

function socket_editUser(data){
	console.log("Socket.io: Sent editUser request");
	socket.emit('editUser', data);
}

function socket_deleteUser(data){
	console.log("Socket.io: Sent deleteUser request");
	socket.emit('deleteUser', data);
}

function socket_getCustomersDataRequest(){
	console.log("Socket.io: Sent Customers-List request");
	socket.emit('getCustomersDataRequest');
}

function socket_addCustomer(data){
	console.log("Socket.io: Sent addCustomer request");
	socket.emit('addCustomer', data);
}

function socket_editCustomer(data){
	console.log("Socket.io: Sent editCustomer request");
	socket.emit('editCustomer', data);
}

function socket_deleteCustomer(data){
	console.log("Socket.io: Sent deleteCustomer request");
	socket.emit('deleteCustomer', data);
}

// Checking if connection is established
var connectchecks = 0;
function checkConnectionStatus(){
	if(!socket.connected){
		connectchecks = connectchecks + 1;
		if (connectchecks >= 10){
			// 10 tries (1 Second passed), connection not established -> refresh page to try again 
			location.reload();
		}else{
			// Not connected yet, check again in 100ms
			setTimeout(checkConnectionStatus, 100);
		}
	}
}
checkConnectionStatus();