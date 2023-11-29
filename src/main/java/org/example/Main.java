package org.example;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(20);
        var avg = new ConcurrentSkipListSet<Double>();

        var s3s = new ArrayList<String>();
        String fileName = "dataset.csv";
        FileResourcesUtils resourcesUtils = new FileResourcesUtils();
        InputStream is = resourcesUtils.getFileFromResourceAsStream(fileName);
        try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                s3s.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 20; i++) {
            int finalI = i;
            executor.execute(() -> {
                var q = new HashMap<String, String>();
                q.put("name", String.valueOf(finalI));
                new Handler().apply(new Request(s3s.subList(500 * finalI, 500 * (finalI + 1)), q, avg));


//                    Request request = new Request.Builder()
//                        .url("https://functions.yandexcloud.net/d4ebg0rhmjkh4nai6b7c?name="+ finalI)
//                        .method("GET", null)
//                        .build();
//                    Response response = client.newCall(request).execute();
//                    System.out.println(response.body().string()); // print or process the response
//                    response.close(); // Close the response to avoid resource leaks

            });
        }
        executor.shutdown();
    }
}