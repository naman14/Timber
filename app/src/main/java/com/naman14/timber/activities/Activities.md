###Activities
<<<<<<< HEAD
=======

>>>>>>> af322ebdc61fc8c0a376a61882ca7f26e43bb050
- The activities handle all the main screens the users will be seeing and how they will be switching between each scene

- Main Activity will likely make up a bulk of the testing since it contains a lot of dependencies and sets up a lot of the variables that extend to other activities within the app such as setting up the Dark theme
	- Might have to refactor a lot of this code for unit testing
	- Need to review methods on here to better understand everything being done to write the tests

- Base activities seem to be handling basic functions of the overall app
	- stopping, skipping, pausing and playing
	- casting to audio device
	- recieving song meta data

- Now Playing activity is the main music player screen and mostly just consists of setting it up with the correct theme
	- Seems like I should be able to test this by having an array of themes and simply reloading the activity

- Donation activity allows users to give money through google play
	- might want to make sure everything here is working properly because we are dealing with money
	- There are various steps and fallbacks in this process and all look like they can be quite easily tested
	- Donations also opens up new 'Now Playing' screen themes, this should be tested to make sure the options remain open for account
	- Only should be possible with google play services enabled

- The Playlist detail activity sets up the playlist scene, it needs to get all the proper information for the custom user made lists
	- Must test new and updated playlist to make sure they always work
		-  Check loaded files
<<<<<<< HEAD

- Search activity is made up of various retrieval methods that queue the most relevent results to what is in typed in the search bar
=======
		
- Search activity is made up of various retreval methods that queue the most relevent results to what is in typed in the search bar
>>>>>>> af322ebdc61fc8c0a376a61882ca7f26e43bb050
	- A lot of testing could likely help improve this feature
	
- Settings seems to allow user to change various different aspects of the app
	- Most of the changes seem to be executed through the main activity
