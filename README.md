# Compass Application

Simple Android Application that shows four cardinal directions of the world.
![compass_north](https://user-images.githubusercontent.com/12357195/116439736-2665fc00-a850-11eb-8f12-6c6309918d16.png)

User can chose also set custom direction to which azimuth will point out. It could select one of the predefined destination, like: `Wroclaw` or `Los Angeles`, or it could type in its own coordinates.
![compass_custom_direction](https://user-images.githubusercontent.com/12357195/116439912-590ff480-a850-11eb-9578-67531c9139a2.png)

## Technical aspects

Application support Android from Lolipop 5.0 to current version

### General
- application is composed following `Clean Architecture`
- for view and logic connection is used `MVVM Architecture` 
- app is written using Kotlin language

### Technology stack
- coroutines, flow (async)
- kotin (di)
- lifecycle components (livedata, viewmodel)
- androidX (view)
- android play services (localization)
- mockk (testing)
