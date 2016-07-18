# RemoteGeoFix
This tool will let you set your Android device Location remotely through your browser.
The Android backend exists of a Webserver and the frontend is developed in javascript using
Leaflet.

The user just has to open the Browser, browse to his mobile and he can "walk" since then 
using WASD on the map.

So how does it work:
* Enable Mock Location Providers on your Android phone under "Developer Options"
* Find out the IP Address of your mobile and connect to it via your Browser (Port: 4444)
* If the page loads your ready to go! Click somewhere you wanna go

The backend uses a restfull api which responds in plaintext:
* api/?cmd=heartbeet&lat=50&lng=50 // Keep alive package, needs to be send every ~10s
* api/?cmd=fix&lat=X&lng=y         // Set the new Location fix at latitude and longitude, the altitude is queried by the backend which talks to the google elevation api
* api/?cmd=exit                    // Will shutdown the httpd service and the MockLocation Service

# Dirty Code, I know
I just needed a very quick solution to cheat on Pokemon Go, so here you got. But install the
"Mock Mock Location" package for the Xposed framework first as Pokemon Go wont start without.
