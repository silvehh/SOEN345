# SOEN345 Cloud-based Ticket Reservation Application

Team members: 
- Ahmed Eskaf (40235587)
- Amro Atique (40272828)
- Jana El Madhoun (40272201)
- Joseph Keshishian (40297447)
- Yousef Bisharah (40151411)

### Before running tests:
Make sure you have Firebase CLI installed. The command is 'npm install -g firebase-tools'

### To run tests
1. Open Android Studio
2. Start an emulator device, it is highly recommended to use **Medium Phone API 36.1**
3. Open terminal in project root directory
4. Run "firebase emulators:start --only firestore" get your Firestore emulator up and running.
5. Run "./gradlew createDebugCoverageReport" and the tests should run! 
