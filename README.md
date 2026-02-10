# WeatherApp Challenge (Android)

A simple Weather app built with **Kotlin + Jetpack Compose** that supports:
- Search weather by **US city name**
- Fetch weather using **device location** (with graceful fallback)
- Clean architecture with **MVVM + Repository + Hilt DI**
- Defensive handling for common edge cases (missing API key, permission denied, network errors, ambiguous city results)

State management: Uses Compose state (mutableStateOf) in the ViewModel. 
Flow/StateFlow wasn't necessary for this app, but can be introduced if the app grows (eg: multi-screen navigation, DB observation, reactive streams).

## Screenshot

Verified on Pixel 9 Pro XL:
## Use Location (current location):
<img width="1008" height="2244" alt="image" src="https://github.com/user-attachments/assets/87dd1099-20fb-4e15-9e46-8465c7e40a03" />

### City Search
[Weather - San Jose]

<img width="1008" height="2244" alt="image" src="https://github.com/user-attachments/assets/57011737-a211-490c-ad8c-fe9425bd1629" />

## Project Structure (High level)
This project is organized with clear separation of concerns:

- **UI (Compose screens)**: renders state, sends user actions
- **ViewModel**: owns UI state, triggers use-cases/repository calls
- **Repository**: data (geocode + weather + prefs)
- **Data sources**
  - Remote (OpenWeather endpoints)
  - Location provider (Android location)
  - Local storage (last searched city)

## Features
- Jetpack Compose UI
- Search screen (basic UI + input)
- Weather results section (current conditions card)
- Loading/error states
- Network layer with Retrofit + OkHttp
- Dependency Injection with Hilt
- Kotlinx Serialization for JSON parsing
- Unit tests + UI tests (Compose)

## Tech Stack
- **Kotlin**
- **Jetpack Compose** (Material 3)
- **Navigation Compose**
- **Coroutines(viewModelScope)**
- **Compose state(mutableStateOf)**
- **Retrofit + OkHttp**
- **Kotlinx Serialization**
- **Hilt (DI)**
- **Coil** (image loading)

## API Used
- OpenWeather Current Weather API  
  Example: https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99&appid={API_Key}

## Project Setup
1) Clone the repository:
git clone https://github.com/<YOUR_GITHUB_USERNAME>/weatherapp-challenge.git

2) Add your OpenWeather API key
Create/update this file: local.properties (project root):

sdk.dir=/Users/<your_user>/Library/Android/sdk

OPEN_WEATHER_API_KEY=YOUR_API_KEY_HERE


## Edge Cases Handled (Defensive Coding)
- **Empty city input** -> prevents search / shows a validation error
- **City not found / invalid** -> shows an error state/message
- **Multiple geocode results** ->  selects the best US match deterministically
- **No network / API error** -> shows an error state/message (no crash)
- **Location permission denied / unavailable** -> shows message + falls back to last searched city
- **Missing API key** -> fails fast with a clear exception (prevents silent 401 errors)

## Location Usage Notes
The **Use Location** flow requires:
- **Location permission** granted at runtime
- **Location services** enabled on the device/emulator
- Emulator has a **set location**: Extended Controls -> Location -> Send

### Graceful fallback
If location cannot be obtained (permission denied, services disabled, emulator missing fix):
- The app shows a message (example: **“Could not get location. Loading last searched city.”**)
- Falls back to the **last searched city** (if available)


## Sample logs captured from local device (Logcat)

Logs show: city -> geocode (lat/lon) -> weather fetch success + icon + temp, for two different inputs (case-insensitive).

2026-02-05 14:20:18.742 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Geocoding city="Fremont" -> query="Fremont,US"

2026-02-05 14:20:19.053 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Geocode results size=5

2026-02-05 14:20:19.054 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Picked: Fremont, state=California, country=US, lat=37.5482697, lon=-121.988571

2026-02-05 14:20:19.054 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Saved last city="Fremont"

2026-02-05 14:20:19.109 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Weather success city=Fremont, tempF=70.84, icon=01d

2026-02-05 14:20:58.762 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Geocoding city="LIVERMORE" -> query="LIVERMORE,US"

2026-02-05 14:20:58.952 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Geocode results size=5

2026-02-05 14:20:58.953 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Picked: Livermore, state=California, country=US, lat=37.6820583, lon=-121.768053

2026-02-05 14:20:58.953 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Saved last city="LIVERMORE"

2026-02-05 14:20:59.003 12935-12935 WeatherApp              com.example.weatherapp               I  Repo: Weather success city=Livermore, tempF=67.42, icon=50d

