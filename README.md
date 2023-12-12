Tour Guide Application
Project 8 of the formation "Developpeur d'application - Java" Tour Guide Application using Spring

Used By This project is used by OpenClassRooms in attempt to validate the project 8 from the formation for learn the java technology

The TestPerformance class can be launched with these several methods :
# mvn test -> launch all tests with application.properties either userNumber=10
# mvn test -Dtest=TestPerformance.java -> launch only the performance tests with application.properties either userNumber=10
# mvn test -Dtest=TestPerformance.java -DcustomVariable.userNumber=1000 -> launch only the performance tests with a manually defined userNumber; here userNumber=1000
# mvn test -Dtest=TestPerformance.java -Dspring.profiles.active=performance -> launch only the performance tests with application-performance.properties either userNumber=100000

!! --> Careful with those on windows, please use the backtick symbol like in this example : mvn test -`Dtest=TestPerformance.java ;  <-- !!

Authors @Vanessa-Roy
