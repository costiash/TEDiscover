# TEDiscover
TEDiscover is an Android app that allows you to watch TED-talk videos directly from the app.
<br>The content of each TED-talk is scraped in real-time from the TED website based on user queries, using simple scraping tools.
<br>The app is linked to Firebase to manage users, so after signing up for TEDiscover, each user can create playlists, 
<br>like and save favorite talks, and view a history of all the talks that have been watched.
<br>The user can perform a search on the Discover screen, with the option of adding filters to the search query.
<br>The search can be based on the user's input, with or without additional filters.
<br>The goal of this app was to practice writing Android apps in Kotlin using the Android mvvm architecture and Android Jetpack libraries 
<br>such as the Paging 3 library for infinite content scrolling and the Navigation library.
<br>Furthermore, I attempted to adhere to the Common architectural principles outlined in Google's official Android Developers guides.
<br>
<br> You can download an installation apk from <a href="https://drive.google.com/u/0/uc?export=download&confirm=VbSV&id=1b0w-4O4TvnY0R1xZZwjn1GC919r9AfcA">here</a>

# Screenshots
<img src="https://github.com/costiash/TEDiscover/blob/master/TEDiscover_Screenshots/Screenshot01.jpg?raw=true" alt="Screenshot01" style="width:180px;height:370px;"> <img src="https://github.com/costiash/TEDiscover/blob/master/TEDiscover_Screenshots/Screenshot02.jpg?raw=true" alt="Screenshot02" style="width:180px;height:370px;">
<img src="https://github.com/costiash/TEDiscover/blob/master/TEDiscover_Screenshots/Screenshot03.jpg?raw=true" alt="Screenshot03" style="width:180px;height:370px;">
<img src="https://github.com/costiash/TEDiscover/blob/master/TEDiscover_Screenshots/Screenshot04.jpg?raw=true" alt="Screenshot04" style="width:180px;height:370px;">

<img src="https://github.com/costiash/TEDiscover/blob/master/TEDiscover_Screenshots/Screenshot05.jpg?raw=true" alt="Screenshot05" style="width:180px;height:370px;"> <img src="https://github.com/costiash/TEDiscover/blob/master/TEDiscover_Screenshots/Screenshot06.jpg?raw=true" alt="Screenshot06" style="width:180px;height:370px;">

# IMPORTANT NOTES
First and foremost, this app's purpose is educational!! I do not own any of the TED content and have no intention of using it commercially. 
I didn't include my 'google-services.json' file because this app uses Firebase. 
<br>To use the app, you must first create your own file. You can find more information about it 
<br>here: https://nabeelj.medium.com/how-to-connect-your-android-app-to-firebase-7b2ccdc98f7e
<br>
<br>Finally, because TED does not have a public API, I had to scrape the content from their website using scraping tools. 
<br>The 'Repo.kt' file in the project is the file that basically contains the methods for the actual scraping; 
<br>however, due to legal terms, I have omitted these methods from the shared code, despite the fact that the scraping is fairly simple, 
and I have used the following video to write them:
https://www.youtube.com/watch?v=fe11mXSeJGo&t=1s
