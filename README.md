# Grama-Suvidha: Rural Infrastructure Transparency Portal

**Grama-Suvidha** (ಗ್ರಾಮ ಸುವಿಧಾ) is a premium, Material Design 3-based Android application designed to bring complete digital transparency to village-level government projects. It bridges the gap between citizens and administration by providing real-time tracking of local infrastructure development.

[![Live Project Website](https://img.shields.io/badge/VIEW-LIVE%20PROJECT%20WEBSITE-7b61ff?style=for-the-badge)](https://github.com/dhrutin2904/GramaSuvidha) [![Download Latest APK](https://img.shields.io/badge/DOWNLOAD-LATEST%20APK-ff4b82?style=for-the-badge)](https://github.com/dhrutin2904/GramaSuvidha/releases/tag/v1.0.0)

---

## 🌟 Key Features

* 📍 **Smart Location Tracking:** Instantly filter and find nearby projects using live GPS or manual search.
* 📊 **Live Progress Updates:** View budgets, timelines, and real-time completion percentages for ongoing work.
* 🌍 **Bilingual Accessibility:** Fully localized in both English and Kannada (ಕನ್ನಡ) for broader rural reach.
* 🗣️ **Citizen Voice (Feedback):** Built-in rating system allowing citizens to report issues directly to admins.
* 📴 **Offline-First Architecture:** Caches data locally using Room DB, ensuring the app works perfectly even with poor internet connectivity.
* 🎨 **Material Design 3:** Sleek, modern UI with dynamic theming and smooth animations for a premium user experience.

---

## 📱 Screenshots

<!-- Add your screenshots here -->
<!-- ![Splash Screen](screenshots/splash.png) -->
<!-- ![Login Screen](screenshots/login.png) -->
<!-- ![Citizen Dashboard](screenshots/citizen_dashboard.png) -->
<!-- ![Project Details](screenshots/project_detail.png) -->
<!-- ![Admin Dashboard](screenshots/admin_dashboard.png) -->

> 📸 *Screenshots will be added soon*

---

## 🏗️ Project Structure

```
GramaSuvidha/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── assets/
│   │   │   └── mock_projects.json          # Sample project data
│   │   ├── java/com/gramasuvidha/
│   │   │   ├── GramaSuvidhaApp.kt          # Application class
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── AppDatabase.kt      # Room Database
│   │   │   │   │   └── ProjectDao.kt       # Data Access Object
│   │   │   │   ├── mock/
│   │   │   │   │   └── MockApiService.kt   # Mock API simulation
│   │   │   │   ├── model/
│   │   │   │   │   ├── Feedback.kt         # Feedback model
│   │   │   │   │   └── Project.kt          # Project model
│   │   │   │   └── repository/
│   │   │   │       └── ProjectRepository.kt # Data repository
│   │   │   ├── ui/
│   │   │   │   ├── adapter/
│   │   │   │   │   └── ProjectAdapter.kt    # RecyclerView adapter
│   │   │   │   ├── admin/
│   │   │   │   │   ├── AddEditProjectActivity.kt
│   │   │   │   │   └── AdminMainActivity.kt
│   │   │   │   ├── auth/
│   │   │   │   │   └── LoginActivity.kt
│   │   │   │   ├── citizen/
│   │   │   │   │   ├── CitizenMainActivity.kt
│   │   │   │   │   ├── FeedbackActivity.kt
│   │   │   │   │   └── ProjectDetailActivity.kt
│   │   │   │   ├── splash/
│   │   │   │   │   └── SplashActivity.kt
│   │   │   │   └── viewmodel/
│   │   │   │       └── ProjectViewModel.kt
│   │   │   └── utils/
│   │   │       ├── AppLogger.kt
│   │   │       ├── Constants.kt
│   │   │       ├── LocaleHelper.kt
│   │   │       └── PrefsManager.kt
│   │   └── res/
│   │       ├── drawable/                    # Custom drawables
│   │       ├── layout/                      # All XML layouts
│   │       ├── menu/                        # Toolbar menus
│   │       ├── values/                      # English strings, colors, themes
│   │       └── values-kn/                   # Kannada translations
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── LICENSE                                  # MIT License
└── README.md
```

---

## ⚙️ Technical Stack

| Component | Technology |
|-----------|------------|
| **Language** | Kotlin |
| **Architecture** | MVVM + LiveData + ViewModel |
| **Local Database** | Room DB (offline cache) |
| **Remote Database** | Firebase Firestore |
| **Authentication** | Firebase Auth (Phone OTP) |
| **Location Services** | Google Play Services (FusedLocationProvider) |
| **Image Loading** | Glide 4.16 |
| **UI Framework** | Material Design 3 |
| **Navigation** | Activity-based with Intents |
| **Languages** | English + Kannada (ಕನ್ನಡ) |
| **Min SDK** | API 26 (Android 8.0) |
| **Target SDK** | API 34 (Android 14) |

---

## 🔐 Authentication & Access

### Admin Login
| Field | Value |
|-------|-------|
| **Username** | `admin` |
| **Password** | `admin123` |

> ⚠️ Hardcoded credentials for demo purposes. In production, use Firebase Auth or a proper backend.

### Citizen Login
- **Phone OTP** via Firebase Authentication
- Enter 10-digit phone number → Receive OTP → Verify

### Guest Browse
- Browse projects without logging in
- **Feedback is disabled** for guest users (must login to submit feedback)

---

## 👤 Citizen Features

| Feature | Description |
|---------|-------------|
| 📋 **Project List** | Browse all projects as Material Design cards with progress bars |
| 📍 **Location Filter** | Filter projects by location — manual text entry or GPS live location |
| 📖 **Project Detail** | Animated progress bar, before/after images, status badges |
| ⭐ **Feedback** | Star rating (1-5) and issue reporting → Firestore (requires login) |
| 🌐 **Language Toggle** | Switch between English and Kannada — all content updates dynamically |
| 📴 **Offline Support** | Room database caches all data locally |
| ◀️ **Back Navigation** | Proper back navigation from all screens |

## 🛠️ Admin Features

| Feature | Description |
|---------|-------------|
| 📊 **Dashboard** | Summary stats — total, completed, in-progress projects |
| ➕ **Add Project** | Create new projects with all details |
| ✏️ **Edit Project** | Update existing project information |
| 🗑️ **Delete Project** | Remove projects from database |
| 🎚️ **Progress Slider** | Update completion percentage (0-100%) |
| 🔄 **Real-time Sync** | Changes sync to Firebase → visible to citizens instantly |

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17**
- **Android SDK** API 34
- **Firebase Project** ([Create one here](https://console.firebase.google.com/))

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/dhrutin2904/GramaSuvidha.git
   cd GramaSuvidha
   ```

2. **Set up Firebase**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project named `GramaSuvidha`
   - Add an Android app with package name `com.gramasuvidha`
   - Download `google-services.json` and place it in the `app/` directory

3. **Enable Firebase Services**
   - **Authentication** → Enable **Phone** sign-in method
   - **Firestore Database** → Create database in **test mode**
   - **Storage** → Enable Cloud Storage (optional)

4. **Open in Android Studio**
   - Open the project folder in Android Studio
   - Let Gradle sync complete
   - Run on emulator or physical device (API 26+)

### Firestore Security Rules (Production)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /projects/{projectId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    match /feedback/{feedbackId} {
      allow create: if true;
      allow read: if request.auth != null;
    }
  }
}
```

---

## 🌍 Localization

The app supports **English** and **Kannada (ಕನ್ನಡ)** with full localization:

- All UI labels and buttons
- Project status labels (Not Started / In Progress / Completed)
- Project type labels (Road, Borewell, Bridge, etc.)
- Error messages and toasts
- Location filter labels

Toggle language from any screen using the **language button** in the toolbar area.

---

## 📱 MockApiService

The `MockApiService.kt` simulates REST API responses using `assets/mock_projects.json` for development and testing.

### JSON Structure
```json
{
  "projects": [
    {
      "id": "001",
      "name": "Main Road Repair",
      "type": "Road",
      "budget": 500000,
      "start_date": "2024-01-01",
      "end_date": "2024-06-01",
      "progress": 75,
      "status": "In Progress",
      "description": "Repair of the main village road.",
      "location": "Halasuru Village",
      "images": {
        "before": "https://example.com/before.jpg",
        "after": "https://example.com/after.jpg"
      }
    }
  ]
}
```

### Available Methods
| Method | Returns | Description |
|--------|---------|-------------|
| `getProjects()` | `List<Project>` | All projects from JSON |
| `getProjectById(id)` | `Project?` | Single project by ID |
| `getProjectsByStatus(status)` | `List<Project>` | Filter by status |
| `addProject(project)` | `Boolean` | Simulates adding (returns true) |
| `updateProgress(id, %)` | `Boolean` | Simulates progress update |

---

## ✅ Features Checklist

- [x] App works offline after first data load (Room DB caching)
- [x] Progress bar exactly matches database percentage value
- [x] Language toggle works across all screens (English ↔ Kannada)
- [x] All project statuses and types are fully localized
- [x] Location-based project filtering (GPS + manual entry)
- [x] Guest users cannot submit feedback (login required)
- [x] Proper back navigation from all screens
- [x] No UI overlap on login page
- [x] MockApiService documented with sample JSON
- [x] Admin can push updates that citizens see in real time
- [x] MVVM architecture with ViewModel + LiveData
- [x] Material Design 3 components throughout
- [x] Star rating and issue reporting for citizen feedback

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

---

## 📝 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [Material Design 3](https://m3.material.io/) for the design system
- [Firebase](https://firebase.google.com/) for backend services
- [Room Database](https://developer.android.com/training/data-storage/room) for local persistence
- [Glide](https://github.com/bumptech/glide) for image loading
- [Google Play Services](https://developers.google.com/android/guides/setup) for location services

---

<p align="center">
  Made with ❤️ for Rural India 🇮🇳
</p>
