This little app lets user login and save maintenace information for a motorcycle or other vehicle

It's a small API with 3 endponts: for logging in, getting data and saving data. It uses MongoDB as data store.
<br>
App is written using simple 3 layer architecture. It has web, service and data access layers.


<h3>Running the app locally</h3>
1) Create `application.properties` file in `src/main/resources` and provide all the necessary properties in it. You can look in `main.kt` for all the properties the app uses.
2) Have local or remote instance of mongo running. You must specify connection string in properties
3) With intellij idea run `main.kt` file. Or run `./gradlew clean build`, then go to /gradle/libs and run `java -jar mc-maintenance-api-1.0-SNAPSHOT.jar`