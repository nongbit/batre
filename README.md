# Batre - Real-time Battery Monitor

A lightweight, efficient, and open-source Android application designed to provide real-time battery insights. Batre features a persistent notification and a home screen widget to keep you informed about your device health, voltage, and charging speed.

## For Users

### Key Features
- Live Tracking: Monitor battery level, health, temperature, and voltage.
- Charging Insights: Identify if you are on Fast Charging (AC), Slow Charging (USB), or Wireless Charging.
- Home Screen Widget: Access your battery status at a glance without opening the app.
- Persistent Notification: Stay updated via a non-intrusive status bar notification.
- Privacy First: No data collection. All battery processing happens locally on your device.

### How to Install
1. Download the latest APK from the Releases section.
2. Enable Install from Unknown Sources in your Android settings if prompted.
3. Open the app to initialize the battery service.
4. Important: For Android 13 and above, please allow the Notification Permission to see the live status in your tray.


### Crucial: Battery Optimization Settings
Android systems often "kill" background apps to save power. To ensure Batre provides accurate, real-time data, you **must** perform the following steps:
1. Open the App Info for Batre (long-press the app icon > Info).
2. Go to Battery or Battery Usage.
3. Select Unrestricted or Disable Battery Optimization.
4. If you use a device with a heavy skin (like Xiaomi, OPPO, or vivo), ensure "Auto-start" is enabled.

Without these settings, the system may pause the monitor, causing the widget or notification to show outdated information until you reopen the app.

---

## For Contributors

### Core Logic and Architecture

Batre is designed with a focus on real-time accuracy and extreme battery efficiency. Unlike many battery apps that use "Polling" (checking battery status every X minutes), Batre uses an **Event-Driven Architecture**.

#### Why not Polling?
- Power Consumption: Running a timer to check battery status every minute consumes CPU cycles unnecessarily, which ironically drains the battery we are trying to monitor.
- Lag in Data: If the battery drops 2% in between intervals, a polling app will show outdated information.
- System Restrictions: Modern Android versions heavily throttle background timers (AlarmManager or WorkManager) to save power, making polling unreliable for real-time updates.

#### Our Approach: Event-Driven Monitoring
Batre registers a dynamic `BroadcastReceiver` for the `ACTION_BATTERY_CHANGED` intent.
- Efficiency: The app remains idle most of the time. It only wakes up for a few milliseconds when the Android system broadcasts a change in battery state (e.g., level drops, charger plugged in).
- Real-time Accuracy: Because we listen to system-level events, the widget and notification update the exact second a change occurs.
- Foreground Service: By using a Foreground Service with the `specialUse` type, we ensure the system does not kill our listener, maintaining a consistent real-time stream of data.

### Technical Stack
- Language: Kotlin
- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)
- UI: XML with RemoteViews (for Widget)
- Background: Foreground Service with specialUse type.

### Project Structure
- **BatteryUtils.kt**: The "Brain" of the app. It contains the `BatteryData` model and the `parseIntent` logic. By centralizing the battery data extraction here, we ensure that the UI, Service, and Widget always display consistent information.
- **BatteryService.kt**: The "Dispatcher." It manages the lifecycle of the `BroadcastReceiver` and pushes updates to both the Persistent Notification and the Home Screen Widget.
- **MainActivity.kt**: A streamlined UI controller. It handles the initial setup, Android 13+ notification permissions, and displays real-time data only when the app is in the foreground.
- **BatteryWidget.kt**: A simple Widget Provider that ensures the background service is kicked into gear whenever the widget is active on the home screen.

### Setup Instructions
1. Clone the repository:
   git clone [https://github.com/nongbit/batre.git](https://github.com/nongbit/batre.git)
2. Open the project in Android Studio.
3. Sync Gradle and ensure Android SDK 34 is installed.
4. Build the project:
   `cd /path/to/batre/ && ./gradlew assembleDebug`

### Contribution Guidelines
1. Fork the repository.
2. Create a Feature Branch (git checkout -b feature/AmazingFeature).
3. Commit your changes (git commit -m 'Add some AmazingFeature').
4. Push to the branch (git push origin feature/AmazingFeature).
5. Open a Pull Request.

---

## License
Distributed under the MIT License. See LICENSE for more information.

## Contact
Developer: nongbit ([https://github.com/nongbit](https://github.com/nongbit))
Project Link: [https://github.com/nongbit/batre](https://github.com/nongbit/batre)