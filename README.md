# India Police Hackathon 2019 - Unified Communication App - Team Invictus

### Project Synopsis

The task is to build a user-friendly messaging application similar to WhatsApp and Telegram. It should serve as a complete commnunication tool for official purposes. It should provide security in the form of end-to-end encryption and should be able to send all forms of media such as images, videos and should also facilitate location sharing. The messaging application should also be able to provide audit trails i.e. it should be able to deliver data logs with timestamp.

### List of tools used

Dev environment:
 * Android Studio 3.5 
 * OpenFire 4.4 (for XMPP variant)
 * Firebase

Opensource Libraries used:
 * Firebase API
 * Smack
 * MaterialDrawer
 * AboutLibrary
 * Android-Iconics
 * Crossfader
 * CrossfadeDrawerLayout
 * Glide image loading
 * Groupie
 * Emoji
 * JASPYT

Versions to all the libraries can be found in the `./app/build.gradle` files of each variant.

### Code

Two variants are present. One uses Firebase as the backend, while the other used XMPP as the backend. 
Please refer to the presentation in `./slides/pres.pdf` for more details.

### Design diagrams / document

The slides mentioned above contains the same.

### Steps to run / recreate the environment

#### Firebase variant

 * Create a Google account (or use an existing account).
 * Head over to `console.firebase.google.com` to create a Firebase project.
 * Once done, add the app with the package name of the application and the debug signing certificate.
 * To create the debug signing certificate, use the following commands:
    ```
    Windows:
    keytool -list -v -alias androiddebugkey -keystore %USERPROFILE%\.android\debug.keystore
    Linux/Mac:
    keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
    ```
    You'll be presented with the SHA1 fingerprint which is to be copied into the form to add the application.
 * Download the file `google-services.json` and place in the `app` directory.
 * Build the application using Android Studio and run on your device.
 * Initialize Cloud Firestore and Storage modules in Firebase console before first launch of the application.


#### XMPP variant

 * Download OpenFire from [this link](https://www.igniterealtime.org/projects/openfire/).
 * Install with default permissions, and setup the server with your hostname as both the host and the service name.
 * Use any password for the admin console.
 * The admin console can be accessed from `http://localhost:9090`.
 * Change the variables in the file `./app/src/main/java/com/invictus/reehbayse/xmpp/XMPP.java` to reflect the hostname and the service name.
 * Build and run the application.
 * Add users via the admin console or via the register button in the application.
