# WeatherApp Challenge (Android)

A simple Weather app built with **Kotlin + Jetpack Compose**, following a clean MVVM-style structure.
It uses the **OpenWeather** API to fetch current weather based on latitude/longitude.

## Features
- Jetpack Compose UI
- Search screen (basic UI + input)
- Weather details screen (current conditions)
- Loading / error states
- Network layer with Retrofit + OkHttp
- Dependency Injection with Hilt
- Kotlinx Serialization for JSON parsing
- Unit tests + UI tests (Compose)

## Tech Stack
- **Kotlin**
- **Jetpack Compose** (Material 3)
- **Navigation Compose**
- **Coroutines**
- **Retrofit + OkHttp**
- **Kotlinx Serialization**
- **Hilt (DI)**
- **Coil** (image loading)

## API Used
- OpenWeather Current Weather API  
  Example: https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99&appid={API_Key}

  
## Project Setup
Clone the repository:
git clone https://github.com/<YOUR_GITHUB_USERNAME>/weatherapp-challenge.git
cd weatherapp-challenge
