# ScopeTechnology
Application has two screens. Main screen with user list that is shown in a Recyclerview is created as Fragment. Second screen is map view and can be opened after clicking on user in main screen.
Main screen has refresh(SwipeRefreshLayout) and you can use it by dragging your finger from top to bottom.
If user will not give permission to gps, MapView will be closed.
I did not write much comments, because biggest part of the method names are self explanatory and code is not complicated.

Things that I did not implement:
1)Local database was not implemented. Code was implemented, however I did not manage to make Room database to build. In future I should have used kotlin coroutines as well, however I do not have expierience with it.
2)User list is not kep up-to-date. Code is implemented, however without database data is not saved localy. I had two options in mind for this, I could have saved date when I recieve new data in shared Preferences or I could have saved in database update date.
3)data caching and data reload for map is not implemented. 
4)I started to implement functionality nearest rout from vehicle to current location with(https://developers.google.com/maps/documentation/directions/overview), however had problems with recieving the route.
5) did not create custom callout, it shows only the vechicles ID. Used too much of the time for database and route issues.

Things that should be updated or implemented:
1)In MapView - last update time.
2)Better error toasts e.g. for network issues I could have added some icon or something that is displayed on the screen(in corner or in the bottom) til user turns on the network.
3)After opening MapView and giving permission initially your loccation will not be displayed(small issue, but did not have time to fix).
4)TESTS(Unit and Integration tests)


Improvements that I should learn:
1)Should learn and use MVVM architecture
2)Kotlin coroutines.
3)Live data with mapView.
