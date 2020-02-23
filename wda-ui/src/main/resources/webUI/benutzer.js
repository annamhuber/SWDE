/*
Author: Cedric Hildbrand
Last Edit: 16.05.2019
*/

var dataSetTable = [];

socket.on('connect', function() { 
	checkSession();
});

$(function () {
	// Everythings unsafe, could be injected from browser. 
	checkSession();
	socket_getUserDataRequest();
	
	var dataTableBenutzer = $('#dataTableBenutzer').DataTable( {
		responsive:true,
		data: dataSetTable,
		columns: [
			{ title: "ID" },
			{ title: "Benutzername" },
			{ title: "Passwort" },
			{ title: "Bearbeiten" }
		]
	} );
	
	window.renderUsers = function(data) {
		jsondata = JSON.parse(data);
		console.log("Rendering JSON Data");
		
		for(var i = 0; i < jsondata.length; i++) {
			var obj = jsondata[i];
			dataTableBenutzer.row.add([obj.userID,obj.username ,obj.password, '<a href="#"><i class="far fa-edit"></i></a>']).draw(false);
		}
	}
	
	$('#dataTableBenutzer tbody').on( 'click', 'a', function () {
		//Get data from row where edit was clicked
        var data = dataTableBenutzer.row( $(this).parents('tr') ).data();
		showEditUserContainer(data[0], data[1], data[2]);
    } );

	$( "#newuser_form" ).submit(function(event) {
		event.preventDefault();
		var new_username = $("#new_username").val();
		var new_password = $("#new_password").val();
		
		// Reset input fields
		$("#new_username").val("");
		$("#new_password").val("");
		
		addUserDB(new_username, new_password);
		
		document.location.reload();
	});	
	
	$( "#edituser_form" ).submit(function(event) {
		event.preventDefault();
		var modify_username = $("#modify_username").val();
		var modify_password = $("#modify_password").val();
		var userid = $("#userid").text();

		// Reset input fields
		$("#modify_username").val("");
		$("#modify_password").val("");
		$("#userid").html("");
		editUserDB(userid, modify_username, modify_password);
		document.location.reload();
	});	
	
	$( "#deleteuser_button" ).click(function(event) {
		var userid = $("#userid").text();

		// Reset input fields
		$("#modify_username").val("");
		$("#modify_password").val("");
		$("#userid").html("");

		deleteUserDB(userid);
		document.location.reload();
	});	
	
	$( "#newuser_button" ).click(function(event) {
		showNewUserContainer();
	});	
});	

function showEditUserContainer(id, username, password){
	$("#userid").html(id);
	$("#modify_username").val(username);
	$("#modify_password").val(password);
	$("#newuser_container").addClass("d-none");
	$("#edituser_container").removeClass("d-none");
	$("#showusers_container").addClass("d-none");
}

function showOverviewUsersContainer(){
	$("#newuser_container").addClass("d-none");
	$("#edituser_container").addClass("d-none");
	$("#showusers_container").removeClass("d-none");
}

function showNewUserContainer(){
	$("#newuser_container").removeClass("d-none");
	$("#edituser_container").addClass("d-none");
	$("#showusers_container").addClass("d-none");
}

function renderUsers(data){
	window.renderUsers(data);
}

function addUserDB(new_username, new_password){
	var userData = [];
	userData.push(
		{
			action : "add",
			username : new_username,
			password : new_password
		}
	);
	
	//Convert it to string because emit can't use a json array
	userData = JSON.stringify(userData);	
	socket_addUser(userData);
}

function editUserDB(id, new_username, new_password){
	var userData = [];
	userData.push(
		{
			action : "edit",
			userID : id,
			username : new_username,
			password : new_password
		}
	);
	
	//Convert it to string because emit can't use a json array
	userData = JSON.stringify(userData);	
	socket_editUser(userData);
}

function deleteUserDB(id){
	var userData = [];
	userData.push(
		{
			action : "delete",
			userID : id
		}
	);
	
	//Convert it to string because emit can't use a json array
	userData = JSON.stringify(userData);	
	socket_deleteUser(userData);
}