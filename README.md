# AkashicRecords
AkashicRecords is a cross platform anime file manager.

Instructions
Download https://drive.google.com/file/d/1b6OXSb9LjtvLPJSXG_I5Nr7e8MNOoRls/view?usp=sharing
Run AkashicRecords.jar
It should create a folder called AkashicRecords in your user directory
for Linux /home/UserName/AkashicRecords/
for Mac /Users/UserName/AkashicRecords/
for Windows C:\Users\UserName\AkashicRecords
Open AkashicRecords/JSON/Other/Settings.json
in localFolder replace the NULL with the path to your anime folder
you can leave linuxNetworkFolder, windowsNetworkFolder and macNetworkFolder NULL
after you save the changes to Settings.json you can run AkashicRecords.jar again
click the icon on the top right to update the folders
you should get a notification that says it is searching the web for information for each show
this can take some time, it will only need run once
after it's done you should have all of your shows displayed by an icon
clicking the icon will take you to the shows page, where all of the information will be displayed and you can watch your anime

click the heart icon if you want it to remember the last episode you played


Settings.json multiple anime folders with network paths example:

  "showFolderPaths": [
    {
      "localFolder": "/home/user/Desktop/Anime_1/",
      "linuxNetworkFolder": "/home/user/Desktop/Anime_1",
      "windowsNetworkFolder": "\\\\USER\\Anime_1\\",
      "macNetworkFolder": "////Volumes/Anime_1/"
    },
      {
      "localFolder": "/home/user/Desktop/Anime_2/",
      "linuxNetworkFolder": "/home/user/Desktop/Anime_2",
      "windowsNetworkFolder": "\\\\USER\\Anime_2\\",
      "macNetworkFolder": "////Volumes/Anime_2/"
    }
  ]


Benefits
Creates icons, tags, and gets show info from MAL and AniDb
Supports multiple anime folders
Supports sorting anime by airing, watching, name, date, rating, 
Supports using local network file sharing between MAC, Windows and Linux
Supports season sub folders

