/*
Author: Cedric Hildbrand
Last Edit: 23.05.2019
*/

var jsondata;
var filename = "undefined";

var chartdata = [];
var chart = new CanvasJS.Chart("chartContainer", {
	animationEnabled: true,
	zoomEnabled:true,
	axisY: {
		title: "",
	},
	axisX: {
        valueFormatString: ""
    },
	toolTip: {
		shared: true
	},
	legend: {
		cursor:"pointer"
	},
	data: chartdata
});

socket.on('connect', function() { 
	checkSession();
});
	


	

$(function () {
	socket_getCitiesRequest(); //Get all Cities from DB
	
	$.fn.datetimepicker.Constructor.Default = $.extend({},
		$.fn.datetimepicker.Constructor.Default,
        { icons:
            { time: 'fas fa-clock',
                date: 'fas fa-calendar',
                up: 'fas fa-arrow-up',
                down: 'fas fa-arrow-down',
                previous: 'fas fa-arrow-circle-left',
                next: 'fas fa-arrow-circle-right',
                today: 'far fa-calendar-check-o',
                clear: 'fas fa-trash',
                close: 'far fa-times' 
			} 
		}
	);
	
		var datetoday = moment();
	var dateyesterday = datetoday.clone();
	var dateyesterday = dateyesterday.subtract(1, 'days');

	$('#datetimepicker8').datetimepicker({
        useCurrent: false,
		ignoreReadonly: true,
		defaultDate: datetoday //Current datetime
    });
	
    $('#datetimepicker7').datetimepicker({
		maxDate: moment(),
		ignoreReadonly: true,
		defaultDate: dateyesterday //Current datetime - 1 day is default
	});
	
    $("#datetimepicker7").on("change.datetimepicker", function (e) {
        $('#datetimepicker8').datetimepicker('minDate', e.date);
    });
	
    $("#datetimepicker8").on("change.datetimepicker", function (e) {
        $('#datetimepicker7').datetimepicker('maxDate', e.date);
    });
	
	$( "#sendabfragebutton" ).click(function() {
		//Selected Data gets sent here
		sendAbfrage();
	});	
	
	$( "#exportbutton" ).click(function() {
		var exportformat = $('#exportselect').val();
		exportData(exportformat);
	});
	


	

	

});	

function exportData(exportformat){
	/*
	Global variable jsondata contains data from DB.
	Global variable filename contains filename
	Uses Download.js plugin to download file to client
	Uses html2canvas.js plugin to convert chart to canvas
	*/
	if(jQuery.isEmptyObject(jsondata) == true){
		alert("Keine Daten verfügbar!");
		return;
	}
	
	if (exportformat == 1){
		// Download as JSON
		// Data is already JSON, just directly download it
		download(JSON.stringify(jsondata,0,4), filename+".json", "text/json;charset=utf-8,");
	} else if (exportformat == 2){
		//Download as CSV
		csvdata = convertToCSV(jsondata); // Convert JSON to CSV
		download(csvdata, filename+".csv", "text/csv;charset=utf-8,");
	} else if (exportformat == 3){
		//Download as XML
		xmldata = convertToXML(jsondata);
		xmldata = formatXml(xmldata); //pretty print it
		download(xmldata, filename+".xml", "text/xml;charset=utf-8,");
	} else if (exportformat == 4){	
		if(getSelectedAbfrage() == 2){
			//A02 export is not implemented because it is a table not a chart
			alert("JPG Export nicht möglich für A02");
			return;
		}
		html2canvas($('#chartContainer'), {
			onrendered: function(canvas) {
				var img = canvas.toDataURL('image/jpeg', 1.0) //Convert canvas to jpg
				download(img, filename+".jpg", "data:image/jpeg;base64,")
		  }
		});
	}
}

function sendAbfrage(){
	$(".datacontainer").addClass("d-none"); // Hide other divs
	$("#resultheader").addClass("d-none"); // Hide other divs
	
	// Get values from inputfields
	var selectedCities = getSelectedCities(); // Array
	var abfrageID = getSelectedAbfrage(); // String
	var startDatetime = getStartDatetime(); // String
	var endDatetime = getEndDatetime(); // String
	
	console.log('Request Data: ' + '\n CityIDs: ' + selectedCities + '\n AbfrageID: ' + abfrageID + '\n Time: ' + startDatetime + ' - ' + endDatetime);

	//Check if all values are selected, if not display error and return
	if(jQuery.isEmptyObject(selectedCities)){
		alert("No City selected");
		return;
	}
	
	if(jQuery.isEmptyObject(abfrageID)){
		alert("No Abfrage selected");
		return;
	}

	if(typeof selectedCities[1] !== 'undefined'){
		if(abfrageID == 2){ 
			alert("Abfrage A02 only allows 1 selected City");
			return;
		}else if(abfrageID == 3){
			alert("Abfrage A03 only allows 1 selected City");
			return;
		}
	}
	
	if(startDatetime == endDatetime){
		//Sometimes moment.js bugs and sets the same start and endtime
		alert("Change Timespan!");
		return;
	}
	
	// Set Header
	var cities = "";
	$("#cityselect option:selected").each(function(){
        cities = cities + this.text + '<br>';
    });
	$('#ortschaftenholder').html(cities);
	$('#abfrageholder').html($('#abfrageselect option:selected').text());
	$('#zeitperiodenholder').html($('#datetimepicker7').datetimepicker('viewDate').format("DD.MM.YYYY HH:mm") + '  -  ' + $('#datetimepicker8').datetimepicker('viewDate').format("DD.MM.YYYY HH:mm"));
	
	//Set filename for export
	filename = "data_";
	filename = filename + $('#abfrageselect option:selected').text() + "_"; //Add which Abfrage
	$("#cityselect option:selected").each(function(){
		filename = filename + this.text + "_";
    });
	
	filename = filename + $('#datetimepicker7').datetimepicker('viewDate').format("DD.MM.YY")
	filename = filename + "-";
	filename = filename + $('#datetimepicker8').datetimepicker('viewDate').format("DD.MM.YY")
	//filename = filename + "_exported_" + moment().format("DD.MM.YY") // unused, adds current date to filename
	//Push all values in a JSON array so it can get sent to Socket
	var citiesJSON = [];

	$.each(selectedCities, function(index) {
		citiesJSON.push(
			{
				cityID: selectedCities[index],
				abfrageID : abfrageID,
				startDatetime : startDatetime,
				endDatetime : endDatetime
			}
		);
	});	
	//Convert it to string because emit can't use a json array
	inputData = JSON.stringify(citiesJSON);		

	// Sends an emit to SocketServer of the Client to tell RMI to get the weatherdata and emit them back	
	socket_getWeatherDataRequest(inputData);
}

function getSelectedCities(){
	var cityids = []; // All selected cities are saved in this array with their ID
	var selectedCities = $('#cityselect option:selected');
	$(selectedCities).each(function(index, brand){
		cityids.push($(this).val());
	});
	return cityids;
}

function getSelectedAbfrage(){
	abfrageId = $('#abfrageselect').val();
	return abfrageId;
}

function getStartDatetime(){
	var date = $('#datetimepicker7').datetimepicker('viewDate').format("YYYY-MM-DD HH:mm:ss");
	return date;
}

function getEndDatetime(){
	var date = $('#datetimepicker8').datetimepicker('viewDate').format("YYYY-MM-DD HH:mm:ss");
	return date;
}

function renderA02(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	if ($.fn.DataTable.isDataTable( '#dataTable' ) ) {
		// If Table already exists delete it
		$('#dataTable').DataTable().destroy().clear(); 
	}
	
	var dataSetTable = [];
	for(var i = 0; i < jsondata.length; i++) {
		var obj = jsondata[i];
		var datetime = moment(obj.update_time).format("DD.MM.YYYY HH:mm");

		dataSetTable.push([
			obj.weatherdataID, datetime, obj.weather_summary, 
			obj.weather_description, obj.temperature_celcius, obj.pressure,
			obj.humidity, obj.wind_speed, obj.wind_direction
		]);
	}	
	
	$('#dataTable').DataTable( {
		responsive:true,
		data: dataSetTable,
		columns: [
			{ title: "weatherdataID" },
			{ title: "update_time" },
			{ title: "weather_summary" },
			{ title: "weather_description" },
			{ title: "temperature_celcius" },
			{ title: "pressure" },
			{ title: "humidity" },
			{ title: "wind_speed" },
			{ title: "wind_direction" }
		]
	} );
	
	$('#dataTable').DataTable().draw();
	$("#dataTableDiv").removeClass("d-none");	
	$("#resultheader").removeClass("d-none");	
	$(window).trigger('resize'); // Trigger resize to make DataTable responsive
}


function renderA03(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	chartdata.length = 0; // Reset
	chart.options.axisX.valueFormatString = "DD.MM";
	chart.options.axisY.title = "";    
	
	var dpsTemperature = [];
	var dpsPressure = [];
	var dpsHumidity = [];
	for(var i = 0; i < jsondata.length; i++) {
		var obj = jsondata[i];
		dpsTemperature.push({x: new Date(obj.update_time), y: obj.temperature_celcius});
		dpsPressure.push({x: new Date(obj.update_time), y: obj.pressure});
		dpsHumidity.push({x: new Date(obj.update_time), y: obj.humidity});	
	}	
	
	chartdata.push(
		{
            name: "Temperature",
            showInLegend:true,
            type: "line",
            xValueFormatString: "DD.MM.YY HH:mm",
            dataPoints: dpsTemperature
        },{
            name: "Pressure",
            showInLegend:true,
            type: "line",
            xValueFormatString: "DD.MM.YY HH:mm",
            dataPoints: dpsPressure
        },{
            name: "Humidity",
            showInLegend:true,
            type: "line",
            xValueFormatString: "DD.MM.YY HH:mm",
            dataPoints: dpsHumidity
        }
	);

	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	
	chart.render();
}

function renderA04(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	var dpsAverageTemperature = [];
	for(var i = 0; i < jsondata.length; i++) {
		var obj = jsondata[i];
		dpsAverageTemperature.push({label: obj.name, y: obj.average_temperature});
	}
	
	chartdata.length = 0; // Reset
	chart.options.axisY.title = "Temperatur (Celcius)";
	chart.options.axisX.valueFormatString = "";
	
	chartdata.push({
		name: "Durchschnittstemperatur",
		showInLegend: false,
		type: "column",
		dataPoints: dpsAverageTemperature
	});

	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	
	chart.render();
}

function renderA05(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	var dpsMinTemperature = [];
	var dpsMaxTemperature = [];
	for(var i = 0; i < jsondata.length; i++) {
		var obj = jsondata[i];
		dpsMinTemperature.push({label: obj.name, y: obj.min_temperature});
		dpsMaxTemperature.push({label: obj.name, y: obj.max_temperature});
	}
	
	chartdata.length = 0; // Reset
	chart.options.axisY.title = "Temperatur (Celcius)";
	chart.options.axisX.valueFormatString = "";
	
	chartdata.push({
		name: "Minimale Temperatur",
		showInLegend: true,
		type: "column",
		dataPoints: dpsMinTemperature
	});
	
	chartdata.push({
		name: "Maximale Temperatur",
		showInLegend: true,
		type: "column",
		dataPoints: dpsMaxTemperature
	});
	
	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	
	chart.render();	
}

function renderA06(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	var dpsAveragePressure = [];
	for(var i = 0; i < jsondata.length; i++) {
		var obj = jsondata[i];
		dpsAveragePressure.push({label: obj.name, y: obj.average_pressure});
	}
	
	chartdata.length = 0; // Reset
	chart.options.axisY.title = "Luftdruck";
	chart.options.axisX.valueFormatString = "";
	
	chartdata.push({
		name: "Durchschnittsluftdruck",
		showInLegend: false,
		type: "column",
		dataPoints: dpsAveragePressure
	});

	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	
	chart.render();
}

function renderA07(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	var dpsMinPressure = [];
	var dpsMaxPressure = [];
	for(var i = 0; i < jsondata.length; i++) {
		var obj = jsondata[i];
		dpsMinPressure.push({label: obj.name, y: obj.min_pressure});
		dpsMaxPressure.push({label: obj.name, y: obj.max_pressure});
	}
	
	chartdata.length = 0; // Reset
	chart.options.axisY.title = "Luftdruck";
	chart.options.axisX.valueFormatString = "";
	
	chartdata.push({
		name: "Minimaler Luftdruck",
		showInLegend: true,
		type: "column",
		dataPoints: dpsMinPressure
	});
	
	chartdata.push({
		name: "Maximaler Luftdruck",
		showInLegend: true,
		type: "column",
		dataPoints: dpsMaxPressure
	});
	
	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	
	chart.render();	
}

function renderA08(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	var dpsAverageHumidity = [];
	for(var i = 0; i < jsondata.length; i++) {
		var obj = jsondata[i];
		dpsAverageHumidity.push({label: obj.name, y: obj.average_humidity});
	}
	
	chartdata.length = 0; // Reset
	chart.options.axisY.title = "Luftfeuchtigkeit";
	chart.options.axisX.valueFormatString = "";
	
	chartdata.push({
		name: "Durchschnittsluftfeuchtigkeit",
		showInLegend: false,
		type: "column",
		dataPoints: dpsAverageHumidity
	});

	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	
	chart.render();
}

function renderA09(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	var dpsMinHumidity = [];
	var dpsMaxHumidity = [];
	for(var i = 0; i < jsondata.length; i++) {
		var obj = jsondata[i];
		dpsMinHumidity.push({label: obj.name, y: obj.min_humidity});
		dpsMaxHumidity.push({label: obj.name, y: obj.max_humidity});
	}
	
	chartdata.length = 0; // Reset
	chart.options.axisY.title = "Luftfeuchtigkeit";
	chart.options.axisX.valueFormatString = "";
	
	chartdata.push({
		name: "Minimale Luftfeuchtigkeit",
		showInLegend: true,
		type: "column",
		dataPoints: dpsMinHumidity
	});
	
	chartdata.push({
		name: "Maximale Luftfeuchtigkeit",
		showInLegend: true,
		type: "column",
		dataPoints: dpsMaxHumidity
	});
	
	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	
	chart.render();	
}

function renderA10(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	chartdata.length = 0; // Reset
	chart.options.axisX.valueFormatString = "DD.MM";
	chart.options.axisY.title = "Temperatur";    

	chartdata.push({}); // Pre-Render if it is empty
	for(var i = 0; i < jsondata.length; i++) {
		if(jsondata[i].length > 0) {
			//One City in this loop
			var citydata = jsondata[i];		
			var dpsCity = [];
	
			var currentCityName = citydata[0].name; //Read name of city from first json object
			for(var x = 0; x < citydata.length; x++) {
				//Data for specific city
				dpsCity.push({x: new Date(citydata[x].update_time), y: citydata[x].temperature_celcius});
			}
			
			chartdata.push(
			{
				name: currentCityName,
				showInLegend:true,
				type: "line",
				xValueFormatString: "DD.MM.YY HH:mm",
				dataPoints: dpsCity
			});
		}
	}	
	
	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	
	chart.render();
}

function renderA11(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	chartdata.length = 0; // Reset
	chart.options.axisX.valueFormatString = "DD.MM";
	chart.options.axisY.title = "Luftfeuchtigkeit";    

	chartdata.push({}); // Pre-Render if it is empty
	for(var i = 0; i < jsondata.length; i++) {
		if(jsondata[i].length > 0) {
			//One City in this loop
			var citydata = jsondata[i];		
			var dpsCity = [];
	
			var currentCityName = citydata[0].name; //Read name of city from first json object
			for(var x = 0; x < citydata.length; x++) {
				//Data for specific city
				dpsCity.push({x: new Date(citydata[x].update_time), y: citydata[x].humidity});
			}
			

			chartdata.push(
			{
				name: currentCityName,
				showInLegend:true,
				type: "line",
				xValueFormatString: "DD.MM.YY HH:mm",
				dataPoints: dpsCity
			});
		}
	}	
	
	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	
	chart.render();
}

function renderA12(data){
	jsondata = JSON.parse(data);
	console.log("Rendering JSON Data: ");
	console.log(jsondata);
	
	chartdata.length = 0; // Reset
	chart.options.axisX.valueFormatString = "DD.MM";
	chart.options.axisY.title = "Luftdruck";    

	chartdata.push({}); // Pre-Render if it is empty
	for(var i = 0; i < jsondata.length; i++) {
		if(jsondata[i].length > 0) {
			//One City in this loop
			var citydata = jsondata[i];		
			var dpsCity = [];
	
			var currentCityName = citydata[0].name; //Read name of city from first json object
			for(var x = 0; x < citydata.length; x++) {
				//Data for specific city
				dpsCity.push({x: new Date(citydata[x].update_time), y: citydata[x].pressure});
			}
			
			chartdata.push(
			{
				name: currentCityName,
				showInLegend:true,
				type: "line",
				xValueFormatString: "DD.MM.YY HH:mm",
				dataPoints: dpsCity
			});
		}
	}	
	
	$("#resultheader").removeClass("d-none");	
	$("#chartContainer").removeClass("d-none");	

	chart.render();
}

function convertToCSV(objArray) {
	// Source: https://stackoverflow.com/questions/11257062/converting-json-object-to-csv-format-in-javascript
    var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
    var str = '';

    for (var i = 0; i < array.length; i++) {
        var line = '';
        for (var index in array[i]) {
            if (line != '') line += ','
            line += array[i][index];
        }
        str += line + '\r\n';
    }
    return str;
}

function convertToXML(obj) {
	// Source: https://stackoverflow.com/questions/48788722/json-to-xml-using-javascript
    var xml = '';
    for (var prop in obj) {
        if (obj[prop] instanceof Array) {
            for (var array in obj[prop]) {
                xml += '<' + prop + '>';
                xml += OBJtoXML(new Object(obj[prop][array]));
                xml += '</' + prop + '>';
            }
        } else {
            xml += '<' + prop + '>';
            typeof obj[prop] == 'object' ? xml += convertToXML(new Object(obj[prop])) : xml += obj[prop];
            xml += '</' + prop + '>';
        }
    }
    var xml = xml.replace(/<\/?[0-9]{1,}>/g, '');
    return xml;
}

function formatXml(xml, tab) { // tab = optional indent value, default is tab (\t)
	// Source: https://stackoverflow.com/questions/376373/pretty-printing-xml-with-javascript
    var formatted = '', indent= '';
    tab = tab || '\t';
    xml.split(/>\s*</).forEach(function(node) {
        if (node.match( /^\/\w/ )) indent = indent.substring(tab.length); // decrease indent by one 'tab'
        formatted += indent + '<' + node + '>\r\n';
        if (node.match( /^<?\w[^>]*[^\/]$/ )) indent += tab;              // increase indent
    });
    return formatted.substring(1, formatted.length-3);
}
