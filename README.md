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
4. **Generate Logs (Optional)**
	```pip install pyperclip```
	
	Run `Generate Random POST Requests.py`
	Change these variables as needed
	```
	# Random number generator for Adjetter Media assignment (Java)
	numToGenerate = 2
	mobileNoStart = 8

	# seconds
	MinimumOffsetBetween2Date = 60
	MaximumOffsetBetween2Date = 240

	# Timestamp range
	TimeFormat = '%d-%m-%Y %H:%M:%S'

	randomDate_start = "01-01-2021 00:00:00"
	randomDate_end = "31-12-2021 23:00:00"
	```
	It'll generate List of POST req and copy to your clipboard. Finally, paste it on POSTMAN...

## Updates
	* 20-Dec-2021
		Enabled Caching for getDate.
	* 18-Dec-2021
		Optimized batch add function (Increase in POST request from avg 30 req/second to 400 req/second) ~Mileage may vary (tested with remote sql).
		Two hibernate sequence for two tables instead of one.
		Reformatted & cleaned some code.


## API supports GET and POST requests
### GET requests
* Get Analytics

	Date format should be in : yyyy-MM-dd
	
	```
	localhost:8080/api/getDate/2021-12-13
	```
	
	![Result with over 10k records](https://user-images.githubusercontent.com/6762915/146669530-3f028ce7-bd53-4c9b-a112-54271ac576f6.png)
	
* Get List of all logs saved in database

        localhost:8080/api/getAll/
	
	![Result with over 10k records](https://i.imgur.com/LqGm1KQ.png)
	
### POST requests
* Save single log

	```
	localhost:8080/api/
	```

	```
	{
		"number": 8610483610,
		"startTime": "06-11-2021 11:27:52",
		"endTime": "06-11-2021 11:29:53"
	}
	```
	
	![POST req single](https://user-images.githubusercontent.com/6762915/146578293-31d7e04e-2594-49ca-a4a0-1016397a619f.png)

* Save multiple log
	```
	localhost:8080/api/saveCalls/
	```
	
	```
	[
		{
			"number": 8008216218,
			"startTime": "05-05-2021 02:26:17",
			"endTime": "05-05-2021 02:30:14"
		},
		{
			"number": 8200845038,
			"startTime": "30-12-2021 13:21:53",
			"endTime": "30-12-2021 13:25:20"
		}
	]
	
	```

	
	![POST req multi](https://i.imgur.com/AaLknZD.jpeg)

## To-do

	Delete LogByNumber
	Delete LogByNumberAndStartTimeAndEndTime
	Update LogByNumber/ID
	Create Java API to get weekNo and DayName from Date


