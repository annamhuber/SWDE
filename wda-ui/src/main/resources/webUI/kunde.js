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
	socket_getCustomersDataRequest();
	
	var dataTableCustomers	 = $('#dataTableCustomers').DataTable( {
		responsive:true,
		data: dataSetTable,
		columns: [
			{ title: "ID" },
			{ title: "Name" },
			{ title: "Data" },
			{ title: "Bearbeiten" }
		]
	} );
	
	window.renderCustomers = function(data) {
		jsondata = JSON.parse(data);
		console.log("Rendering JSON Data");
		
		for(var i = 0; i < jsondata.length; i++) {
			var obj = jsondata[i];
			dataTableCustomers.row.add([obj.kundeid, obj.name, obj.data, '<a href="#"><i class="far fa-edit"></i></a>']).draw(false);
		}
	}
	
	$('#dataTableCustomers tbody').on( 'click', 'a', function () {
		//Get data from row where edit was clicked
        var data = dataTableCustomers.row( $(this).parents('tr') ).data();
		showEditCustomerContainer(data[0], data[1], data[2]);
    } );

	
	$( "#newcustomer_form" ).submit(function(event) {
		event.preventDefault();
		var new_customername = $("#new_customername").val();
		var new_customerdata = $("#new_customerdata").val();
		
		// Reset input fields
		$("#new_customername").val("");
		$("#new_customerdata").val("");
		
		addCustomerDB(new_customername, new_customerdata);
		document.location.reload();
	});	
	
	$( "#editcustomer_form" ).submit(function(event) {
		event.preventDefault();
		var modify_customername = $("#modify_customername").val();
		var modify_customerdata = $("#modify_customerdata").val();
		var kundeid = $("#kundeid").text();

		// Reset input fields
		$("#modify_customername").val("");
		$("#modify_customerdata").val("");
		$("#kundeid").html("");
		editCustomerDB(kundeid, modify_customername, modify_customerdata);
		document.location.reload();
	});	
	
	$( "#deletecustomer_button" ).click(function(event) {
		var kundeid = $("#kundeid").text();

		// Reset input fields
		$("#modify_username").val("");
		$("#modify_password").val("");
		$("#kundeid").html("");

		deleteCustomerDB(kundeid);
		document.location.reload();
	});	
	
	$( "#newcustomer_button" ).click(function(event) {
		showNewCustomersContainer();
	});	
});	

function showEditCustomerContainer(id, name, data){
	$("#kundeid").html(id);
	$("#modify_customername").val(name);
	$("#modify_customerdata").val(data);
	$("#newcustomer_container").addClass("d-none");
	$("#editcustomer_container").removeClass("d-none");
	$("#showcustomers_container").addClass("d-none");
}

function showOverviewCustomersContainer(){
	$("#newcustomer_container").addClass("d-none");
	$("#editcustomer_container").addClass("d-none");
	$("#showcustomers_container").removeClass("d-none");
}

function showNewCustomersContainer(){
	$("#newcustomer_container").removeClass("d-none");
	$("#editcustomer_container").addClass("d-none");
	$("#showcustomers_container").addClass("d-none");
}

function renderCustomers(data){
	window.renderCustomers(data);
}

function addCustomerDB(new_customername, new_customerdata){
	var customerData = [];
	customerData.push(
		{
			action : "add",
			name : new_customername,
			data : new_customerdata
		}
	);
	
	//Convert it to string because emit can't use a json array
	customerData = JSON.stringify(customerData);	
	socket_addCustomer(customerData);
}

function editCustomerDB(id, new_customername, new_customerdata){
	var customerData = [];
	customerData.push(
		{
			action : "edit",
			kundeID : id,
			name : new_customername,
			data : new_customerdata
		}
	);
	
	//Convert it to string because emit can't use a json array
	customerData = JSON.stringify(customerData);	
	socket_editCustomer(customerData);
}

function deleteCustomerDB(id){
	var customerData = [];
	customerData.push(
		{
			action : "delete",
			kundeID : id
		}
	);
	
	//Convert it to string because emit can't use a json array
	customerData = JSON.stringify(customerData);	
	socket_deleteCustomer(customerData);
}