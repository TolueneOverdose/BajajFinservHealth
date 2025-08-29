Bajaj Finserv Health - Java Qualifier ChallengeThis project is a Spring Boot application built to solve the Java Qualifier 1 challenge from Bajaj Finserv Health. The application is designed to run an automated workflow upon startup, interacting with a remote API to receive and submit a solution to a given SQL problem.Task OverviewThe core objective is to build a standalone Spring Boot application that performs the following actions automatically on startup:Initiate the Challenge: Sends a POST request containing personal details to a specified API endpoint to register and receive a challenge.Receive Webhook & Token: The API responds with a unique webhook URL and a JWT accessToken.Determine the SQL Problem: The application logic checks the user's registration number (regNo). Based on whether the last two digits are odd or even, it selects the corresponding SQL problem to solve.Submit the Solution: The final SQL query (the solution) is sent as a JSON payload to the received webhook URL. This request is authenticated using the provided accessToken as a Bearer Token in the Authorization header.The entire process is non-interactive and is triggered once the application context is loaded, with no need for manual intervention via controllers or API endpoints.How to Configure and Run1. ConfigurationBefore running the application, you must configure your personal details in the src/main/resources/application.properties file.# Your Personal Details
challenge.user.name=Your Name
challenge.user.regNo=YourRegNo123
challenge.user.email=your.email@example.com


#Locating the Final JAR File
The mvn clean package command compiles the project and packages it into an executable .jar file (not a .tar file). This is the file required for submission.

You can find the final .jar file inside the target/ directory, located in the root of your project folder.


# API Endpoints
challenge.api.generate-webhook.url=[https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA](https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA)
2. Running the ApplicationYou can run the application in two ways:A) From your IDE:Open the project in your preferred IDE (e.g., IntelliJ IDEA, VS Code).Locate the BajajChallengeApplication.java file.Run it as a standard Java application. The process will start automatically, and you can monitor the logs in the console.B) Using the Executable JAR:First, build the project using Maven to create the JAR file:mvn clean package
The executable JAR will be created in the target/ directory (e.g., target/bajaj-challenge-0.0.1-SNAPSHOT.jar).Run the JAR file from your terminal:java -jar target/bajaj-challenge-0.0.1-SNAPSHOT.jar
SQL Problem Solved (Question 1)This implementation solves the SQL problem assigned to registration numbers ending in an odd number.Problem StatementGiven three tables (DEPARTMENT, EMPLOYEE, PAYMENTS), the task is to find the highest salary that was credited to an employee, but only for transactions that were not made on the 1st day of any month. Along with the salary, the query must also extract the employee's full name, current age, and department name.Final SQL QueryThe following query was implemented to solve the problem:SELECT
    p.AMOUNT AS SALARY,
    e.FIRST_NAME || ' ' || e.LAST_NAME AS NAME,
    CAST(strftime('%Y', 'now') - strftime('%Y', e.DOB) AS INTEGER) - (strftime('%m-%d', 'now') < strftime('%m-%d', e.DOB)) AS AGE,
    d.DEPARTMENT_NAME
FROM
    PAYMENTS p
JOIN
    EMPLOYEE e ON p.EMP_ID = e.EMP_ID
JOIN
    DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE
    CAST(strftime('%d', p.PAYMENT
