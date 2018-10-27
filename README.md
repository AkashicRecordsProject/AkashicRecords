# AkashicRecords
AkashicRecords is a cross platform anime file manager.

Instructions<br/>
1) Download https://drive.google.com/file/d/1b6OXSb9LjtvLPJSXG_I5Nr7e8MNOoRls/view?usp=sharing<br />
2) Run AkashicRecords.jar<br />
3) It should create a folder called AkashicRecords in your user directory<br />
for Linux /home/UserName/AkashicRecords/<br />
for Mac /Users/UserName/AkashicRecords/<br />
for Windows C:\Users\UserName\AkashicRecords<br />
4) Open AkashicRecords/JSON/Other/Settings.json<br />
5) In localFolder replace the NULL with the path to your anime folder for windows you have to replace all \ with \\\ <br/>
you can leave linuxNetworkFolder, windowsNetworkFolder and macNetworkFolder NULL<br />
6) After you save the changes to Settings.json you can run AkashicRecords.jar again<br />
7) Click the icon on the top right to update the folders<br />
you should get a notification that says it is searching the web for information for each show<br />
this can take some time, it will only need run once<br />
after it's done you should have all of your shows displayed by an icon<br />
clicking the icon will take you to the shows page, where all of the information will be displayed and you can watch your anime<br />
click the heart icon if you want it to remember the last episode you played<br />

<br/>Optional<br/><br/>
You can download some custom background and cover images from <br/>https://drive.google.com/drive/folders/1eEvHeYEig68BYtVf4gmF8_rafQeN94uI?usp=sharing<br />
Extract the images and put them in the folder with the same name in AkashicRecords/Images/<br/>
You will need to delete the thumbnails from Cover_Thumbnails if you make changes to the Cover_Show folder<br/>


Settings.json multiple anime folders with network paths example:<br />

  "showFolderPaths": [<br />
    {<br />
      "localFolder": "/home/user/Desktop/Anime_1/",<br />
      "linuxNetworkFolder": "/home/user/Desktop/Anime_1",<br />
      "windowsNetworkFolder": "\\\\USER\\Anime_1\\",<br />
      "macNetworkFolder": "////Volumes/Anime_1/"<br />
    },<br />
      {<br />
      "localFolder": "/home/user/Desktop/Anime_2/",<br />
      "linuxNetworkFolder": "/home/user/Desktop/Anime_2",<br />
      "windowsNetworkFolder": "\\\\USER\\Anime_2\\",<br />
      "macNetworkFolder": "////Volumes/Anime_2/"<br />
    }<br />
  ]<br />


<br/>Benefits<br/><br/>
Creates icons, tags, and gets show info from MAL and AniDb<br />
Supports multiple anime folders<br />
Supports sorting anime by airing, watching, name, date, rating, <br />
Supports using local network file sharing between MAC, Windows and Linux<br />
Supports season sub folders<br />

