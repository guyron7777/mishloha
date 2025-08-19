# GitHub Trending Repositories

A modern Android application that displays trending GitHub repositories with a beautiful, responsive UI. Built with Jetpack Compose and following Clean Architecture principles.

**This project was created as part of a job interview for Mishloha.**

## 👨‍💻 Developer Information
- **Name**: Guy Ron
- **Email**: guyron7@gmail.com

---

## 🏗️ Architecture Overview

I chose to implement this app using **Clean Architecture (using MVVM)** with a clear separation of concerns across three main layers:

### **Domain Layer** (Core Business Logic)
- **Models**: `Repository`, `Owner`, `TimeFrame` - Pure Kotlin data classes representing business entities
- **Use Cases**: Business logic encapsulated in single-responsibility classes like `GetTrendingRepositoriesUseCase`, `AddToFavoritesUseCase`
- **Repository Interfaces**: Contracts defining data operations without implementation details

### **Data Layer** (Data Management)
- **Repository Implementations**: `GitHubRepositoryImpl`, `FavoritesRepositoryImpl` - Concrete implementations of domain interfaces
- **Local Storage**: Room database for offline favorites with `RepositoryEntity` and `RepositoryDao`
- **Remote API**: Retrofit service for GitHub API integration with DTOs for data transfer
- **Mappers**: `RepositoryMapper` for converting between data layers

### **Presentation Layer** (UI & State Management)
- **ViewModels**: `TrendingRepositoriesViewModel`, `FavoritesViewModel`, `RepositoryDetailViewModel` - Managing UI state and business logic
- **Composables**: Jetpack Compose UI components with reusable design system
- **Navigation**: Compose Navigation with type-safe routing

## 🤔 Technical Decisions & Reasoning

### **Why Clean Architecture?**
I chose Clean Architecture because it provides:
- **Testability**: Each layer can be tested independently with clear boundaries
- **Maintainability**: Changes in one layer don't ripple through the entire app
- **Scalability**: Easy to add new features or modify existing ones
- **Team Collaboration**: Clear separation allows multiple developers to work on different layers

### **Jetpack Compose Over XML**
- **Declarative UI**: More intuitive and less error-prone than imperative XML layouts
- **Better State Management**: Built-in state hoisting and unidirectional data flow
- **Reusability**: Composable functions are easily shared and tested
- **Modern Android Development**: Aligns with Google's current recommendations

### **Room Database for Local Storage**
- **Type Safety**: Compile-time SQL query validation
- **Coroutines Support**: Native async/await support for database operations
- **Migration Support**: Built-in schema migration capabilities
- **Offline-First**: Enables full offline functionality for favorites

### **Paging 3 for Infinite Scrolling**
- **Memory Efficiency**: Only loads visible items, reducing memory footprint
- **Network Optimization**: Built-in request deduplication and error handling
- **Smooth UX**: Seamless loading states and error recovery
- **Compose Integration**: Native `LazyPagingItems` support

### **Hilt for Dependency Injection**
- **Compile-Time Safety**: Catches dependency issues at build time
- **Scoping**: Proper lifecycle management for ViewModels and repositories
- **Testing**: Easy to swap implementations for testing
- **Code Organization**: Centralized dependency management

## 📱 Features Implemented

### ✅ **Core Features**
- **Trending Repositories**: Display repositories sorted by stars with timeframe selection (Day/Week/Month)
- **Repository Details**: Comprehensive detail view with language, forks, creation date, and GitHub link
- **Infinite Scrolling**: Smooth pagination using Paging 3 library
- **Favorites System**: Add/remove repositories with local storage and offline access
- **Search Functionality**: Real-time search across repository names, descriptions, and owners
- **Responsive Design**: Adaptive layouts for both phone and tablet with side-by-side master-detail view

### ✅ **Technical Features**
- **Offline Support**: Full offline access to favorite repositories and their details
- **Error Handling**: Graceful error states with retry mechanisms
- **Loading States**: Proper loading indicators and skeleton screens
- **Image Caching**: Efficient avatar loading with Coil library
- **Material 3**: Modern design system with dynamic theming

## 🚧 Features I Didn't Implement (And Why)

### **Custom Image Caching Strategy**
*Why I didn't implement it:*
The assignment mentioned implementing custom image caching, but I chose to use Coil instead. Coil is a mature, well-tested library that provides excellent caching out of the box.

### **Advanced Search Filters**
*Why I didn't implement it:*
The current search is basic but functional. I focused on core features first to ensure quality over quantity.

*How I would implement it:*
- Language filter dropdown
- Star count range slider
- Date range picker
- Sort options (stars, forks, updated date)
- Search history with suggestions

### **Repository Analytics**
*Why I didn't implement it:*
This would be a nice-to-have feature but wasn't in the core requirements.

*How I would implement it:*
- Star growth charts over time
- Contributor statistics
- Issue/PR trends
- Repository health metrics

### **Push Notifications**
*Why I didn't implement it:*
This requires backend infrastructure and wasn't part of the assignment scope.

*How I would implement it:*
- Firebase Cloud Messaging integration
- Background job to check for new trending repositories
- User preferences for notification frequency
- Rich notifications with repository previews

### **What I Would Do Differently for Production**

#### **1. Enhanced Error Handling**
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val userMessage: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

#### **2. Comprehensive Logging & Analytics**
- Firebase Analytics for user behavior tracking
- Crashlytics for crash reporting
- Custom event tracking for business metrics
- Performance monitoring with Firebase Performance

#### **3. Security Enhancements**
- API key management with encrypted storage
- Certificate pinning for network requests
- ProGuard/R8 obfuscation for release builds
- Runtime application self-protection (RASP)

#### **4. Performance Optimizations**
- Database indexing for faster queries
- Image preloading for better UX
- Background data prefetching
- Memory leak detection and prevention

#### **5. Testing Strategy**
- Unit tests for all business logic (90%+ coverage)
- Integration tests for repository implementations
- UI tests for critical user flows
- Performance tests for database operations

#### **6. CI/CD Pipeline**
- Automated testing on every commit
- Code quality gates (SonarQube)
- Automated deployment to Play Store
- Feature flag management

### **Scalability Considerations**

#### **Database Optimization**
- Implement database migrations strategy
- Add database indexing for search queries
- Consider using SQLite FTS for full-text search
- Implement database cleanup strategies

#### **Network Layer**
- Implement request caching with OkHttp
- Add retry mechanisms with exponential backoff
- Implement request prioritization
- Add network quality detection

#### **UI/UX Improvements**
- Implement skeleton loading screens
- Add pull-to-refresh functionality
- Implement swipe gestures for favorites
- Add haptic feedback for interactions

## 🛠️ Setup & Installation

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+ (API level 24)
- Java 11 or later

### Build Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mishloha.git
   cd mishloha
   ```

2. Open in Android Studio and sync Gradle files

3. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```

### Configuration
- Uses GitHub's public API (no authentication required)
- Network permissions automatically requested
- Local database created on first launch

## 📊 Project Structure

```
app/src/main/java/com/guyron/mishloha/
├── data/                    # Data layer
│   ├── local/              # Room database, DAOs, entities
│   ├── remote/             # API service, DTOs
│   ├── repository/         # Repository implementations
│   └── mapper/             # Data mappers
├── domain/                 # Domain layer
│   ├── models/             # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Business logic use cases
├── presentation/           # Presentation layer
│   ├── navigation/         # Navigation components
│   ├── ui/                 # Compose UI components
│   ├── utils/              # Theme, utilities
│   └── viewmodels/         # ViewModels
└── di/                     # Dependency injection
```

### **What I Would Improve**
- **Error Handling**: More granular error states and user-friendly messages
- **Performance**: Implement more aggressive caching strategies
- **Accessibility**: Add comprehensive accessibility support
- **Internationalization**: Support for multiple languages

---

*This project represents my approach to modern Android development, focusing on clean architecture, user experience, and maintainable code. I hope you find it useful and educational!*
