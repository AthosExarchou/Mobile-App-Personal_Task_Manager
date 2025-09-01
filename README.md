# Task Management Android Application Project 2024-2025

This application is an exercise for the **Mobile Application Development** course at [Harokopio University of Athens – Dept. of Informatics and Telematics](https://www.dit.hua.gr).

## Overview
This Android application is designed to help users manage their tasks efficiently.
It allows users to create, view, update, delete, and export tasks with a user-friendly interface.
The app ensures that task statuses are updated automatically based on their start time and duration.

## Features
- Create a New Task: Add tasks with details such as name, description, start time, duration, and location.

- Delete a Task: Remove tasks using their unique Task ID, with feedback on the number of rows affected.

- Periodic Status Updates: Automatically updates the status of tasks (e.g., "in-progress", "expired") every hour.

- View Tasks: Display all incomplete tasks, with urgent tasks appearing at the top.

- Mark Tasks as Completed: Change the status of tasks to "completed" with a single button click.

- Location Integration: View task locations on Google Maps if a location is provided.

- Export Tasks: Export incomplete tasks as an HTML file to the Downloads directory, viewable in other applications.

## Architecture

### Language:
Java

### Database:
Room (SQLite)

### Background Processing:
WorkManager for periodic tasks

### UI Components:
Activities, Handlers, and Intents

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/AthosExarchou/ds-exc-2024.git
   ```
2. Open in Android Studio:

   - Open Android Studio

   - Click on File > Open and select the project folder

3. Build the Project:

   - Ensure Gradle sync completes successfully

   - Click Run to install the app on an emulator or device

### Periodic Task Status Update
The app uses WorkManager to automatically update task statuses every hour, even when the app is not actively in use.

### Exporting Tasks
Incomplete tasks can be exported to an HTML file located in the Downloads folder. The exported file includes all task details in a tabular format.

### Database Management
- The app handles database operations like inserting, updating, and deleting tasks using RoomDAO.

- Proper database closure is handled in MyApp.java.

### Google Maps Integration
If a task includes a location, users can view it on Google Maps directly from the app.

## Author

- **Name**: Exarchou Athos
- **Student ID**: it2022134
- **Email**: it2022134@hua.gr, athosexarhou@gmail.com

## License
This project is licensed under the MIT License.
