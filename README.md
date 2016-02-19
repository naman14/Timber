AppSite
===========================


Material Design Website Template for an app's landing page, built with Project Polymer 
Demo: http://faizmalkani.github.io/appsite

Easy to setup and deploy. 
Download the project as a zip, extract it, edit the HTML to match your app's details, and you're good to go!


Properties to be edited before publishing:
- Page Title, Description, Keywods, Copyright and Author
- Cover Image *(optional)*
- Floating Button color, desktop and mobile *(optional)*
- Floating Button email address, desktop and mobile *(optional)*
- App Name, Developer Name and App Price
- App Description
- Other Apps thumbnails and titles
- App Details


## Vulcanization
Assuming you're familiar with bower, run the following commands from the root of your project directory to *significantly* reduce page load times.

    npm install -g vulcanize
    vulcanize index.html --inline
    
Vulcanize combines your files to reduce the number of network requests and has a number of flags you can tweak based on your preferences. Once vulcanized, it's safe to delete all the folders except the images folder, since the code is all contained in index.html     
