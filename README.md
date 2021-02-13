# Spank Fury

A kinky themed beat 'em up game inspired by [Kung Fury: Street Rage](https://store.steampowered.com/app/373180/Kung_Fury_Street_Rage/).

[Youtube trailer](https://youtu.be/Xf3JDAgd-fQ)

[Play it now, for free!](https://github.com/Slideshow776/Spank-Fury/tree/master/release)

![screenShot of gameplay](https://user-images.githubusercontent.com/4059636/102523112-6bef0d00-4097-11eb-8eb0-c645b8f39faf.PNG)


# Game Design Document

1. Overall Vision
    * **Write a short paragraph explaining the game**
    Spank Fury is a kink themed arcade rhythm game. The player controls the domina in the center of the screen and spanks submissives coming in from the left or right. Submissives spawn time slowly increases, as well as time unlocks new submissives with new challenges. Survive the longest to achieve a high score.
        
    * **Describe the genre**
    This is a single-player rhythm arcade game.
    
    * **What is the target audience?**
    The game is appropriate for teenagers and older and is only thought to be enjoyed by casual gamers. The target platform is Android phones and will also be available for free on desktop.
    
    * **Why play this game?**    
    This game is progressive-paced with gameplay sessions only meant to be a few minutes long. The simple gameplay is easy to get in to, and offers quick gratification and is harder to master. The unique theme is complemented by visual effects and other telltale gameplay information.
    
2. Mechanics: the rules of the game world
    * **What are the character's goals?**
    The goal is to survive by spanking all the submissives encountered for the longest possible time to achieve the highest score. If the player is unable to spank a submissive at the right time the game ends. This is a rhythm game, spanking at the right time assures success.
        
    * **What abilities does the character have?**
    The player, centered in the screen, will be able to spank left or right by touching the respective screen areas.
    
    * **What obstacles or difficulties will the character face?**
    A progressively faster pace and spawn of more challenging submissives. Also, spank streaks will award an even higher score.
    
    * **What items can the character obtain**
    The player may obtain no items.
    
    * **What resources must be managed?**
    The player must manage their rhythm, the spank streak, and avoiding missing any submissives.
    
    * **Describe the game-world environment.**
    The player will inhabit a domina avatar centered on the screen. Submissives will spawn at the left and right edges and move toward the domina. The player must then spank the submissives when they are close enough. The game will take place in a dungeon depicting an appropriate BDSM theme.

    By game-over and first entering the game, a simple touch-to play screen will be shown, also showing the overall highest score.
    
3. Dynamics: the interaction between the player and the game mechanics
    * **What hardware is required by the game?**
    Android devices will be required to have a functional touch screen. Desktop will be arrow left and right button presses, and simple controller support will be added. The control scheme will be explored through a brief tutorial.
    
    * **What type of proficiency will the player need to develop to become proficient at the game?**
    The player needs to figure out when the right time to spank is, and also memorizing the different enemies and their current status. Also maintaining the streak will be a challenge.
        
    * **What gameplay data is displayed during the game?**
    Always at the top of the play screen, the total amount of score, health, high score, and the bonus modifier streak.
    
    * **What menus, screens, or overlays will there be?**
    A splash screen showing the author of the game.
    A menu screen showing the highest score and a click-to-play prompt.
    Game options screen, and lastly the game itself.
    (Possibly a small intro video will be created)
    
    * **How does the player interact with the game at the software level?**
    There is a pause button when playing. The player may quit the game or control its options by the operating system's controls, or the options menu via the regular menu.
    
4. Aesthetics: the visual, audio, narrative, and psychological aspects of the game
    * **Describe the style and feel of the game.**
    The game has a fun and casual atmosphere. The action takes place inside a BDSM themed dungeon with parallax effects and other visual interests. From either side of the screen, different submissives appear and move to the avatar wanting to be spanked. The music will be somewhat action-based and fast-paced.

    * **Does the game use pixel art, line art, or realistic graphics?**
    The graphics will be in a pixel art style.
    
    * **What style of background music, ambient sounds will the game use?**
    The game will feature background music, ambient music, and various effects for different actions and happenings.
        
    * **What is the relevant backstory for the game?**
    The background story is that the avatar is doing a "spank 'em all challenge" at her local BDSM club.
        
    * **What emotional state(s) does the game try to provoke?**
    A sense of satisfaction by spanking submissives. A sense of urgency hoping not to end the game. The emotional state aims to be curiosity, a sense of achievement, and stress to not end the game, as well as humor and silliness over the theme of the game.
        
    * **What makes the game fun?**
    Exploring and optimising the core mechanics of surviving, spanking, and achieving the highest score possible. Also the humor of the theme and easiness of the game-play
    
5. Development
    
    * **List the team members and their roles, responsibilities, and skills.**    
    This project will be completed individually; graphics and audio will be obtained from third-party websites that make their assets available under the Creative Commons license, and so the main task will be programming and creating some graphics.
    
    * **What equipment is needed for this project?**    
    A computer (with keyboard, mouse, and speakers) and internet access will be necessary to complete this project.
    
    * **What are the tasks that need to be accomplished to create this game?**    
    This project will use a simple Kanban board hosted on the project's GitHub page.
    The main sequence of steps to complete this project is as follows:    
        * Setting up a project scaffold
        * **Programming game mechanics and UI**
        * **Creating and obtaining graphical assets**
        * Obtaining audio assets
        * Controller support
        * **Polishing**
        * Deployment

    * **What points in the development process are suitable for playtesting?**    
    The main points for playtesting are when the basic game mechanics of the level screen are implemented, and when it is visualised. The questions that will be asked are: 
        * Is the gameplay and UI understandable?
        * Is the gameplay interesting?
        * How do the controls feel?
        * How is the pace of the game?
        * Are there any improvement suggestions?        
    
    * **What are the plans for publication?**
    This game will possibly be made available for free on desktop. It will be deployed on the Google Play store for 10-15 NOK and advertised to various indie game-portal websites (LibGDX, r/incremental_games). Gameplay images and a trailer video will be posted and marketed via social media.

# Music License
Voxel Revolution by Kevin MacLeod
Link: https://incompetech.filmmusic.io/song/7017-voxel-revolution
License: https://filmmusic.io/standard-license

Drive by Alex (c) copyright 2013 Licensed under a Creative Commons Attribution (3.0) license. http://dig.ccmixter.org/files/AlexBeroza/43098 Ft: cdk & Darryl J

Other music by https://www.bensound.com

# Project comments
## Google Play Services (GPS)
These three resources helped me to a great extent implementing GPS.
* Leaderboards in Android Game: https://developers.google.com/games/services/android/leaderboards
* Google Play Services for Libgdx: https://stackoverflow.com/questions/48135531/signinsilently-failure-when-trying-to-sign-in-to-googleplay-game-services-w/48135617#48135617
* Implementing and using an interface for cross-platofrm uasbility: https://github.com/libgdx/libgdx/wiki/Interfacing-with-platform-specific-code

For project specifics check out the [commits](https://github.com/Slideshow776/Spank-Fury/commits/master).
