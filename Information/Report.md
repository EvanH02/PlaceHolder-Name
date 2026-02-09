# Report of our project

#### Sources:
https://www.freecodecamp.org/news/cold-start-problem-in-recommender-systems/
https://github.com/Prajna-Ramamurthy/Music-streaming-System

## Purpose
The primary purpose of a music streaming app is to provide on demand access to a large database of audio content. Ideally the audio files should seemlessly integrate
into the app, so that the user can easily select music (whether its an album or song), search for music, create a queue of songs, create a collection of songs 
(playlist), and easily download said audio content for listening without an internet connection. 

## Key Characterestics / Trends
Music streaming platforms also typically involve a discover page, where the user receives music suggestions from the app (usually derived from a personalized 
discovery algoirthm). In recent years, the music streaming domain is mostly characterized by the industry shift from users owning audio content, to instead being 
granted access to content. A common hurdle most applications in this domain face is what is known as the "cold start problem in reccomender systems", an issue where
the system as insuffcient information about the user to give reccomendations. This happens to new users due to most of these reccomendation systems operating off 
the user's history in the app. To fix this problem, it is important to consider prompting the user when they install the app, to get a base to build their 
reccomendations off of. In terms of data structures, typically Arrays are used for ordered playlists, Doubly Linked Lists for rapid next/previous song playback, 
Hash Tables/Maps for fast lookup of audio content, and graph logic for reccomendation systems. 

## Examples of Music Streaming Apps 


Spotify

Spotify's way of solving the “cold start” problem is by when making a new account prompting the user on selecting their favourite artist or genre, they then use this data to recommend new content based on those choices, this is then done until the user has enough data collected. Spotify reflects industry shift from ownership to access by when paying for subscription having access to streaming rather than owning the audio files outside the app. In terms of data structures Spotify seems to employ the use of arrays for playlist, a queue system using doubly linked list for playback, hash tables being used for the search option and Graph based models being used for recommendations. Spotify back end infrastructure primarily runs on Google Cloud.

Spotify follows a design that opens to a home page which provides at the top bar the users recently listened to music, playlist and podcast, under this having a plethora of suggested music and podcasts that is algorithmically generated depending on your personal preference. Spotify uses black and green as the main colors and has a very simple interface with 4 pages accompassing the app being the home,  search , Library and Create pages being at the bottom of the screen with the current or last play button being always right on top of it. Spotify prides itself on being a music streaming app that consistently introduces users to new content and with the layout of the app this is very clear, on all 4 pages you are always shown options for new playlist and new podcast. At core of what Spotify values is discovery with it having a clear, concise and dark interface with an algorithm that is very confident in its ability to provide its user with new content.




Apple music 

Apple music has a much more vibrant color palette than Spotify, it has 3 sections for users to discover which are the home, radio and library pages. Apple music is apart of IOS so the app takes inspiration from Apples other apps and interface, the blur effects ,vibrant colors, live backgrounds and the IOS font (San Francisco) are all examples of this. Apple music solves the “cold-start” problem by when creating a new account going through a survey which asks them their favourite artists and genre which then allows the algorithm to piggy back off this data and recommend new content. Unlike Spotify, Apple Music is a purely subscription based app so the business model of granting users access to content rather than them owning the audio files is taken to the extreme here compared to Spotify which provides the users music listening with ads for free. The basic backend (so the playback, search feature, playlist and recommendations) seems to follow the same format as spotify but the infrastructure runs on there own cloud service iCloud. Apple Music is not as hung up on users discovering content through them as much as Spotify but rather prioritizes its playlist and recommendations to be very high level with them being a lot of the times set already and being done by real humans. Apple music wants to provide users with quality content consistently with them heavily pushing albums and human curation. 




