Toy Weather Predictor
===================
Overview
-------------
<p align="justify">To create a toy simulation of the environment (taking into account things like atmosphere, topography, geography, oceanography) that evolves over time. This application takes measurements at various locations (ie., weather stations) and emits weather forecast (twice a day - at 9:00am and 3:00pm) for consecutive five days (ie., today + 4 days). </p>

Background
-----------------
<p align="justify">Statistical forecasting methods are most suitable for rapid-deployed systems as they can predict extreme weather conditions that could have occurred previously and needs negligible time to deploy the algorithm compared to other ANN or Fuzzy forecasting. So this project implements a simple weather forecasting model using Sliding Window algorithm. There is always a slight variation in weather conditions between previous day parameter and present day’s parameter. Also there exists a dependency between the weather conditions persisting in current week in consideration and those of previous years.
The probability that the weather condition of the day in consideration will match the same day in previous year is very less. But the probability that it will match within the span of adjacent fortnight of previous year is very high. So, for the fortnight considered for previous year a sliding window is selected of size equivalent to a week. Every week of sliding window is then matched with that of current year’s week in consideration. The window best matched is made to participate in the prediction.</p>

The mathematical version of the algorithm is depicted as shown below.
1. Take matrix “CD” of last seven days for current year’s data of size 7 × 4.
2. Take matrix “PD” of fourteen days for previous year’s data of size 14 × 4.
3. Make 8 sliding windows of size 7 × 4 each from the matrix “PD” as 𝑊1,𝑊2,𝑊3,,.., 𝑊8
4. Compute the Euclidean distance of each sliding window with the matrix “CD” as ED1, ED2, ED3, . . . , ED8
5. Select matrix 𝑊𝑖 as
	*	𝑊𝑖 = Correponding Matrix (Min.(ED𝑖))
		∀𝑖 ∈ [1, 8]
6. For 𝑘 = 1 to 𝑛,
	* For WC𝑘, compute the variation vector for the matrix “CD” of size 6 × 1 as “VC”.
	* ForWC𝑘, compute the variation vector for the matrix “PD” of size 6 × 1 as “VP”.
	* Mean1 =Mean (VC)
	* Mean2 =Mean (VP)
	* Predicted Variation “𝑉” = (Mean1+ Mean2)/2
	* Add “𝑉” to the previous day’s weather condition in consideration to get the predicted condition.
7. End

Pre requisites
--------------------
•	JDK 1.8 (JAVA_HOME and PATH set) for compile and execution.
•	Apache Maven 3.3 or higher (MVN_HOME and PATH set) for build.


To install Java
```javascript
1: Un-Install Older Version(s) of JDK/JRE
2: Download JDK 8 from Java SE download site @ http://www.oracle.com/technetwork/java/javase/downloads/index.html
3: Run the downloaded installer (e.g., "jdk-8u{xx}-windows-x64.exe"), which installs both the JDK and JRE.
4:  Include JDK's "<JAVA_HOME>\bin" Directory in the PATH
5: To verify installation, run java –version in the command prompt so that the version is displayed.
```

To install Maven:
```javascript
1: Make sure JDK is installed, and “JAVA_HOME” variable is added as Windows environment variable.
2: Download Maven zip file from Maven official website(http://maven.apache.org/download.cgi). 
3: Unzip it to the folder you want to install Maven. Add both M2_HOME  variables in the Windows environment, and point it to your Maven folder.
4: Update PATH variable, append Maven bin folder – %M2_HOME%\bin, so that you can run the Maven’s command everywhere.
5: To verify installation, run mvn –version in the command prompt so that the version is displayed.
```

Application Overview
-------------------------------

<p align="justify">The application implements a weather prediction model for the popular cities of Australia -<i>CANBERRA, SYDNEY, MELBOURNE, BRISBANE, PERTH, ADELAIDE, HOBART, DARWIN and GOLDCOAST</i>. The historical data is downloaded using real time API provided by Bureau of Meteorology ,Australia. They provide observations of a number of weather elements for the last 14 months. Out of many elements, temperature, humidity and pressure parameters are taken into consideration for weather prediction. The application takes location name and output path as input arguments and writes output to the given path. </p>

Running Application
-------------------------------
<b>Step 1</b> : Clone the repository.
```javascript
< git clone https://github.com/irenemathew/weather-prediction >
```
Step 2 : Build the project.
Navigate to the root directory of the project and build the project using the below command:
```javascript
mvn clean install
```
Step 3 : Run the application. By now you will get a jar generated in the target folder. To run the jar execute the below command:
```javascript
Format : java -jar <jar-name.jar> <LOCATION_NAME> <OUTPUT_PATH>
 e.g.,  java -jar target/weatherprediction-0.0.1-SNAPSHOT-jar-with-dependencies.jar /output/
``` 

Sample Output
----------------------

CANBERRA|-35.28|149.13|57.5|2017-11-20T09:00:00Z|18.74|48.25|1022.78|MOSTLY SUNNY
CANBERRA|-35.28|149.13|57.5|2017-11-20T15:00:00Z|22.93|43.08|1021.58|MOSTLY SUNNY
CANBERRA|-35.28|149.13|57.5|2017-11-21T09:00:00Z|19.89|45.27|1022.4|MOSTLY SUNNY
CANBERRA|-35.28|149.13|57.5|2017-11-21T15:00:00Z|23.32|43.17|1021.38|SUNNY
CANBERRA|-35.28|149.13|57.5|2017-11-22T09:00:00Z|20.87|44.96|1022.93|MOSTLY SUNNY
CANBERRA|-35.28|149.13|57.5|2017-11-22T15:00:00Z|23.91|43.1|1022.13|SUNNY
CANBERRA|-35.28|149.13|57.5|2017-11-23T09:00:00Z|21.82|41.79|1023.26|MOSTLY SUNNY
CANBERRA|-35.28|149.13|57.5|2017-11-23T15:00:00Z|24.91|40.44|1022.26|SUNNY
CANBERRA|-35.28|149.13|57.5|2017-11-24T09:00:00Z|22.16|38.36|1023.93|MOSTLY SUNNY
CANBERRA|-35.28|149.13|57.5|2017-11-24T15:00:00Z|26.32|32.56|1022.9|SUNNY
 

Reference
---------
https://www.hindawi.com/journals/isrn/2013/156540/
