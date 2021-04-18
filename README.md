# ScopeTechnology
Application has two screens. Main screen with user list that is shown in a Recyclerview. Second screen is map view and can be opened after clicking on user in main screen.
Main screen has refresh(SwipeRefreshLayout) and you can use it by dragging your finger from top to bottom.
If user will not give permission to gps, MapView will be closed.
I did not write much comments, because biggest part of the method names are self explanatory and code is not complicated.

Things that I did not implement:
1)data caching in MapView
2)I started to implement functionality - nearest rout from vehicle to current location with(https://developers.google.com/maps/documentation/directions/overview), however had problems with receiving the route.

Things that should be updated or implemented:
TESTS(Unit and Integration tests)
