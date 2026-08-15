# WatchDone Project Guidelines & Architecture Overview

WatchDone is an Android application designed for managing personal movie and TV series watchlists. It allows users to search, discover, track watch status (at title and individual episode levels), view detailed media metadata (cast, crew, ratings, OTT platform availability), and share poster graphics.

---

## 🏗️ Architecture & Project Structure

The project follows **Clean Architecture** principles and a multi-module setup inspired by Google's *Now in Android* pattern. Build configurations are managed using custom Gradle convention plugins in `gradle/build-logic`.

### Module Breakdown

* **Application (`:app`)**:
  * Root entry point, main `ComponentActivity`, global navigation host, and top-level app configuration.
* **Feature UI Modules (`:ui:*`)**:
  * `:ui:watchlist` — Core watchlist management (add/remove, mark as watched, episode tracking).
  * `:ui:discover` — Content discovery feeds (Trending, Popular, Upcoming).
  * `:ui:media` — Media detail views, cast/crew info, OTT platform availability.
  * `:ui:search` — Multi-search across movies, TV shows, and actors.
  * `:ui:profile` — User profile and statistics.
  * `:ui:settings` — App settings and theme preferences.
  * `:ui:recommended` — Personalized media recommendations.
* **Domain Layer (`:domain`)**:
  * Business logic, use cases, models, and repository interfaces.
* **Data & Persistence Layer (`:data:*`)**:
  * `:data:database-room` — Local Room database (`WatchdoneDatabase`) for caching media items, discovery feeds, genres, and recommendations.
  * `:data:datastore` & `:data:datastore-proto` — DataStore preferences for local user settings.
  * `:data:tmdb-auth` / `:data:tmdb-account` — TMDb account and authentication handling.
* **API & Networking (`:api:tmdb`, `:tmdb-api`)**:
  * TMDb API client integration for fetching media metadata, posters, and streaming availability.
* **Core & Common Modules (`:common:ui:*`, `:core:*`, `:ards`, `:base`, `:utils`)**:
  * Shared UI components, theme definitions, logging utilities, base ViewModels, and test frameworks.

---

## 🛠️ Technology Stack & Library Versions

| Component                | Library / Framework                                    |
|:-------------------------|:-------------------------------------------------------|
| **Language**             | Kotlin                                                 |
| **Android SDK Target**   | Target & Compile SDK `37` \| Min SDK `23`              |
| **UI Framework**         | Jetpack Compose + Material Design 3                    |
| **Navigation**           | Navigation Compose + Adaptive Navigation Suite         |
| **Dependency Injection** | Dagger Hilt                                            |
| **Local Database**       | Room Database                                          |
| **Preferences**          | Jetpack DataStore / Proto DataStore                    |
| **Cloud Backend**        | Firebase (Auth, Firestore, Crashlytics, Remote Config) |
| **Image Loading**        | Coil / Glide                                           |
| **Async Operations**     | Kotlin Coroutines & Flow                               |
| **Build System**         | Gradle with Convention Plugins                         |

---

## 🌟 Key Features

1. **Watchlist & Episode Tracking**: Add movies and TV series to a personal list; mark whole titles or individual episodes as watched.
2. **Multi-search & Discovery**: Browse trending movies/shows or search by title, genre, actor, or release date.
3. **Rich Media Details**: Access cast/crew breakdowns, release details, user ratings, and available OTT streaming providers.
4. **Cloud Sync & Social Sharing**: Firebase sync for personal watchlist data across devices, plus poster sharing to external apps.
