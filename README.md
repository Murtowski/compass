# Compass Application

Simple Android Application that shows four cardinal directions of the world (**left image**).

User can chose also set custom direction to which azimuth will point out. It could select one of the predefined destination, like: `Wroclaw` or `Los Angeles`, or it could type in its own coordinates (**right image**).

![Compass app README screenshot](https://user-images.githubusercontent.com/12357195/116441743-00415b80-a852-11eb-924b-1ab331d31118.jpg)

## Technical aspects

Application support Android from Lolipop 5.0 to current version

### General
- application is composed following `Clean Architecture`
- for view and logic connection is used `MVVM Architecture` 
- app is written using Kotlin language

### Technology stack
- coroutines, flow (async)
- koin (di)
- lifecycle components (livedata, viewmodel)
- androidX and view data-binding (view)
- android play services (localization)
- mockk (testing)
