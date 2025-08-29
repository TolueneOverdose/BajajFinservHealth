
package com.example.bajaj_challenge.services;

import com.example.bajaj_challenge.dto.SolutionRequest;
import com.example.bajaj_challenge.dto.WebhookRequest;
import com.example.bajaj_challenge.dto.WebhookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class ChallengeRunner implements CommandLineRunner {

    private final RestTemplate restTemplate;

    @Value("${challenge.user.name}")
    private String name;

    @Value("${challenge.user.regNo}")
    private String regNo;

    @Value("${challenge.user.email}")
    private String email;

    @Value("${challenge.api.generate-webhook.url}")
    private String generateWebhookUrl;

    public ChallengeRunner(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Starting Bajaj Finserv Health Challenge ---");

        // Step 1: Generate the webhook
        WebhookResponse webhookResponse = generateWebhook();
        if (webhookResponse == null || webhookResponse.getWebhookUrl() == null) {
            System.err.println("Failed to generate webhook. Exiting.");
            return;
        }

        System.out.println("Successfully generated webhook. URL: " + webhookResponse.getWebhookUrl());

        // Step 2: Solve the SQL problem
        String finalQuery = solveSqlProblem();
        System.out.println("SQL Query to submit: " + finalQuery);


        // Step 3: Submit the solution
        submitSolution(webhookResponse.getWebhookUrl(), webhookResponse.getAccessToken(), finalQuery);

        System.out.println("--- Challenge process completed. ---");
    }

    private WebhookResponse generateWebhook() {
        System.out.println("Step 1: Sending POST request to generate webhook...");
        WebhookRequest requestBody = new WebhookRequest(name, regNo, email);
        try {
            return restTemplate.postForObject(generateWebhookUrl, requestBody, WebhookResponse.class);
        } catch (Exception e) {
            System.err.println("Error during webhook generation: " + e.getMessage());
            return null;
        }
    }

    private String solveSqlProblem() {
        System.out.println("Step 2: Solving the SQL problem based on RegNo: " + regNo);
        // Extract the last two digits from the registration number.
        // Assuming the number part is at the end.
        String numericPart = regNo.replaceAll("[^0-9]", "");
        if (numericPart.length() < 2) {
            System.err.println("Registration number does not have at least two digits.");
            // Defaulting to odd for safety
            return getQueryForOdd();
        }

        int lastTwoDigits = Integer.parseInt(numericPart.substring(numericPart.length() - 2));

        if (lastTwoDigits % 2 == 0) {
            // Logic for EVEN registration number 
            //System.out.println("RegNo ends in an EVEN number. Using Question 2.");
            return getQueryForOdd();
        } else {
            // Logic for ODD registration number 
            System.out.println("RegNo ends in an ODD number. Using Question 1.");
            return getQueryForOdd();
        }
    }

    private void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        System.out.println("Step 3: Submitting the final SQL query...");

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // Set the JWT token in the Authorization header [cite: 5]
        headers.set("Authorization", "Bearer " + accessToken);

        // Create request body
        SolutionRequest requestBody = new SolutionRequest(finalQuery);

        // Create HttpEntity
        HttpEntity<SolutionRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Send POST request
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);
            System.out.println("Submission successful! Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error during solution submission: " + e.getMessage());
        }
    }

   

    private String getQueryForOdd() {
        
        return "SELECT p.AMOUNT AS SALARY, e.FIRST_NAME || ' ' || e.LAST_NAME AS NAME, CAST(strftime('%Y', 'now') - strftime('%Y', e.DOB) AS INTEGER) - (strftime('%m-%d', 'now') < strftime('%m-%d', e.DOB)) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE CAST(strftime('%d', p.PAYMENT_TIME) AS INTEGER) != 1 ORDER BY p.AMOUNT DESC LIMIT 1;";
    }

}