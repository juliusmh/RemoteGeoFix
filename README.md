# RemoteGeoFix
This tool will let you set your Android device Location remotely through your browser.
The Android backend exists of a Webserver and the frontend is developed in javascript using
Leaflet.

The user just has to open the Browser, browse to his mobile and he can "walk" since then 
using WASD on the map.

So how does it work:
* Enable Mock Location Providers on your Android phone under "Developer Options"
* Find out the IP Address of your mobile and connect to it via your Browser (Port: *4444*)
* If the page loads your ready to go! Click somewhere you wanna go

* __Pro TIP : __ Dont "move" to fast as the requests are kind of slow due to http things and the google api.. 

# Api
The backend uses a restfull api which responds to your GET requests in plaintext:
* __api/?cmd=heartbeet__ Keep alive package, needs to be send every ~10s
* __api/?cmd=fix&lat=X&lng=y__         Set the new Location fix at latitude and longitude, the altitude is queried by the backend which talks to the google elevation api
* __api/?cmd=exit__                    Will shutdown the httpd service and the MockLocation Service

# Todo
I just needed a very quick solution to cheat on Pokemon Go, so here is the result (But install the
"Mock Mock Location" package for the Xposed framework first as Pokemon Go wont start without) .
Im going to develop the code further and even publish an finished app. Things to come are for sure:
* Not a ui standard preset, not the mail button, not these colors.. An UI is going to come, in Material design (its going to be awesome)
* An Icon
* A settings panel for changing some parameters (e.g. fetchAltitude, httpdPort, ...) -> SharedPreferences
* A real Service implementation that is robust (my Httpd Server is kind of dirty and initialized on the ui thread)
* More administrative functions through the web
* A production ready app for geeks and cheaters that is easy to use but flexible
