# Call-Analytics-SpringBoot-API
## Simple API that records call logs and generate a analytic by given date

### Setup


1. **Clone the application**

	```bash
	git clone https://github.com/ShirishSaxena/Call-Analytics-SpringBoot-API.git
	cd Call-Analytics-SpringBoot-API
	```

2. **Create MySQL database**

	```bash
	create database analytics
	```

3. **Change MySQL username and password as per your MySQL installation**

	+ open `src/main/resources/application.properties` file.

	+ change `spring.datasource.username` and `spring.datasource.password` properties as per your mysql installation

4. **Run the app**

	You can run the spring boot app by typing the following command -

	```bash
	mvn spring-boot:run
	```

	The server will start on port 8080.

	You can also package the application in the form of a `jar` file and then run it like so -

	```bash
	mvn package
	java -jar target/analytics-0.0.1-SNAPSHOT.jar
	```


## API supports GET and POST requests
### GET requests
