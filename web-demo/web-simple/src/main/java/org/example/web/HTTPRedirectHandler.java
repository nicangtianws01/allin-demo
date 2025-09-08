package org.example.web;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class HTTPRedirectHandler {
    /**
     * 限制重定向次数
     * @param urlString
     * @return
     * @throws Exception
     */
    public static String fetchWithRedirectsLimit(String urlString) throws Exception {
        int maxRedirects = 5; // Limit the number of redirects to prevent infinite loops
        int redirectCount = 0;

        while (true) {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false); // Disable automatic redirects
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            log.info("Response Code = {}", responseCode);

            // Handle redirect (HTTP 3xx)
            if (responseCode >= 300 && responseCode < 400) {
                redirectCount++;
                if (redirectCount > maxRedirects) {
                    throw new Exception("Too many redirects");
                }
                // Get the "Location" header field for the new URL
                String newUrl = connection.getHeaderField("Location");
                if (newUrl == null) {
                    throw new Exception("Redirect URL not provided by server!");
                }

                urlString = newUrl;
                log.info("Redirecting to: {}", newUrl);

            } else if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successful response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();
                return responseBuilder.toString();

            } else {
                throw new Exception("HTTP response error: " + responseCode);
            }
        }
    }
}