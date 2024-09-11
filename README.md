# Resell - Cornell Marketplace

<p align="center"><img src="https://github.com/cuappdev/assets/blob/master/app-icons/resell-icon.png" width=210 /></p>

Resell is an app that collects, filters, and compares different items that people want to resell in order to connect sellers with buyers and to facilitate resource utilization. Resell is one of the latest apps by [Cornell AppDev](http://cornellappdev.com), an engineering project team at Cornell University focused on mobile app development. Download the current release on the [App Store](https://apps.apple.com/us/app/resell-cornell-marketplace/id1622452299)!

<br />

## Using Firebase

Resell uses two databases per environment. We have a PostgreSQL database that is associated with our Digital Ocean backend server. We also have a Firebase Firestore database (a NoSQL database) under the Resell Firebase project in our Cornell AppDev Google acount.

For Firestore, the `(default)` database corresponds to our development environment and `resell-prod` corresponds to production. **Please be aware of which database to use since frontend is responsible for managing data in Firestore.**

### Install an Android Studio Emulator

If you don't have an Android device available to test with, use the default emulator that comes with Android Studio. Follow the instructions in [this guide](https://docs.expo.dev/workflow/android-studio-emulator/).

## Importing Environment Variables and Secrets

For AppDev members, you can find the files from the `#resell-frontend` Slack channel.

(Outdated info from react native; TODO to fix)

1. Create a `.env` file in the **root directory** and copy/paste the values from the pinned message.
   1. Note that there are two different `.env` files: development and production.
2. Download `GoogleService-Info.plist` and `google-services.json` and place both files in the `/config` folder.
3. Also, drag `GoogleService-Info.plist` into `ios/Resell` folder. DO NOT do this through Xcode.

3. A GitHub Actions workflow should automatically run. Note that EAS Build and EAS Submit can be quite buggy sometimes so manual deployment may need to be done. If that's the case, make sure that you update the versions properly through the native files (such as Xcode).
