function init_map() {
    window.oncontextmenu = function() { return false };

    var map = L.map('map').setView([50.126596889493804, 8.324348330497742], 16);

    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    var position = null;
    var buttonPressed = false;

	// Start constantly updating task:
	setInterval(updateLocationSilent, 5000); 
	
    function locationFound(position) {
        map.setView([position.coords.latitude, position.coords.longitude]);
    }

	function updateUi(lat, lng, c){
		if (position != null) {
            map.removeLayer(position);
        }
        
		position = L.circle([lat, lng], 10, {color: c});
		map.addLayer(position);
	}
	
	function updateLocationSilent(){
		if (position == null) return;
		var lat = position.getLatLng().lat;
		var lng = position.getLatLng().lng;
		
		$.ajax({
			url: "api/?cmd=heartbeet&lat="+lat+"&lng="+lng
		}).success(function(msg) {
			if(msg == "OK"){
				updateUi(lat, lng, "blue");
			}
		});
	}
	
    function updateLocation(lat, lng) {
		updateUi(lat, lng, "red");
		$.ajax({
			url: "api/?cmd=fix&lat="+lat+"&lng="+lng
		}).success(function(msg) {
			
		});
    }

	// Key down events
    function move(c) {
        if (position == null) return;

        pos = position.getLatLng();
        if (c == 'w') { pos.lat += 0.0001 }
        if (c == 'a') { pos.lng -= 0.0001 }
        if (c == 's') { pos.lat -= 0.0001 }
        if (c == 'd') { pos.lng += 0.0001 }
        
		position.setLatLng(pos);
		
        updateLocation(pos.lat, pos.lng);
    }

	// Make the Map clickable
    function onMousemove(e) {
        if (buttonPressed) {
            updateLocation(e.latlng.lat, e.latlng.lng);
        }
    }

    function onClick(e) {
        updateLocation(e.latlng.lat, e.latlng.lng);
    }

	// Try to find the current position
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(locationFound, function() {});
    }

	// Register events
    map.on('mousemove', onMousemove);
    map.on('click', onClick);

    $(document).mousedown(function(e){
        if (e.which == 3) { 
            buttonPressed = true; 
            e.preventDefault();
        }
    });

    $(document).mouseup(function(e){
        if (e.which == 3 || e.which == 1) { buttonPressed = false; }
    });

    $("#map").keypress(function(e) {
        c = String.fromCharCode(e.which);
        move(c)
    })
}

$(document).ready(init_map)
