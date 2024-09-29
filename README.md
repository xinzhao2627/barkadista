# National University Computer Laboratory Monitoring System
Hello, this is a monitoring system application developed by the group **Barkadista**

## Overview
The application distinctively follows the systematic approach of ITRO, on where they track the conditions of each computers inside a computer laboratory.
The use of a database and JavaFX library was implemented in order to efficiently track the computers and their components with an addition of a report ticketing system in which
a student or an administrator may use the app to file a complain on specific computers that has an issue. Submitted reports can be seen by an administrator through a built-in dashboard feature
within the application.

## Requirements / Dependencies
* XAMPP 5.5.35
* IntelliJ Ultimate Edition
* Scenebuilder _(optional, for development purposes)_
* Jfoenix - https://search.maven.org/remotecontent?filepath=com/jfoenix/jfoenix/9.0.10/jfoenix-9.0.10.jar
* Mysql Connector - https://dev.mysql.com/downloads/file/?id=525082
* barkadista Database (SQL FILE)- https://drive.google.com/file/d/1EANd20fmEByFCPsyVSFXxAy_Pk93Gyaa/view?usp=sharing

## Application Setup
**Note:** The application was not tested to run on other environments, except the **Intellij Ultimate Edition**.
1. Download and install all dependencies and IDE in the requirements section, as they are needed in order to properly run the application.
   - Installing IntelliJ Ultimate Edition would include the latest version of JavaFX and its components.
3. There are two methods to import the source code:
   - **Using Git** (Method 1):
       * Open intelliJ ultimate edition, on the 'projects' tab, click the '**Get from VCS**' and paste this url _https://github.com/xinzhao2627/barkadista.git_
       * Select your own directory and clone the repository.
       * After cloning the repository, the project would open in intellij.
   - **Zip File** (Method 2):
       * Download the source code at https://github.com/xinzhao2627/barkadista
       * Extract the project folder into your chosen directory
       * Open IntelliJ and on the 'projects' tab, click 'open' and select the directory of the source code.
4. Import the Mysql Connector and Jfoenix:
   - first, extract the mysql connector in your desired folder, then remember the directory of its JAR file.
   - also keep the JAR file of the Jfoenix.
   - Open the project in IntelliJ, in the upper left corner go to files > project structure > modules > barkadista > dependency. Below the 'module SDK', click the plus button > JAR or Directories
   - browse through the directories of the installed jar files then click ok.
   - check the toggle for both the imported mysql and jfoenix JAR file and click 'apply'.
6. Open XAMPP and import the database:
   - Open XAMPP control panel, start 'Apache' and 'MySQL'.
   - go to http://localhost/phpmyadmin/ and click the 'new' button to create a database
   - Name the database 'barkadista_db'.
   - After creating an empty database, import the installed SQL FILE 'barkadista Database' in the requirements section.

## Running the Application
1. Open XAMPP control panel, start Apache and MySQL
2. Open the project in IntelliJ and go to App.java
3. If you are executing the app for the first time, there would be a prompt to download the Java 21 SDK/JDK, make sure to install that.
4. Run the application
