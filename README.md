# Project Planner

An Android application for planning projects as connected tasks. Users can create plans, define task durations and dependencies, follow progress, and keep a lightweight profile with project statistics.

## Highlights

- Create plans with titles and descriptions.
- Add tasks with flexible duration input, such as `2d`, `3h 30m`, or `1mo 5d`.
- Link a task to a preceding task or start it immediately.
- Calculate plan duration from the longest task dependency chain.
- Track plan and task states, including waiting, in progress, late, and completed.
- Automatically mark a plan complete when all of its tasks are completed.
- View plan details, task counts, completion progress, expected end date, and project duration.
- Save plans and tasks locally with SQLite/Room and persist the display name using SharedPreferences.

## Tech Stack

- Java
- Android SDK (min SDK 28, target SDK 36)
- AndroidX and Material Components
- RecyclerView and ConstraintLayout
- SQLite with Room dependencies
- Gradle Kotlin DSL

## Getting Started

### Prerequisites

- Android Studio with Android SDK 36 installed
- JDK 11

### Run the App

1. Clone the repository.
2. Open the `ProjectPlanner` folder in Android Studio.
3. Allow Gradle to synchronise dependencies.
4. Select an emulator or a physical device running Android 9 (API 28) or later.
5. Run the `app` configuration.

## Project Structure

```text
app/src/main/
├── java/com/orabi/project_planner/
│   ├── MainActivity.java          # Plans overview and filtering
│   ├── AddPlanActivity.java       # Plan creation
│   ├── AddTaskActivity.java       # Task creation and dependencies
│   ├── PlanDetailsActivity.java   # Plan progress and task details
│   ├── ProfileActivity.java       # User name and summary statistics
│   ├── DBHelper*.java             # Local data persistence helpers
│   └── *Adapter.java              # RecyclerView adapters
└── res/                           # Layouts, drawables, fonts, and resources
```

## Task Dependencies

Each task can begin immediately or after another task finishes. The app follows these links to determine the longest task chain, which represents the plan's overall expected duration.

## Build from the Command Line

On Windows:

```powershell
.\gradlew.bat assembleDebug
```

On macOS or Linux:

```bash
./gradlew assembleDebug
```

## License

This project is intended for educational and portfolio use. Add a license file before distributing it publicly.
