package pt.ulisboa.tecnico.surething.prover.utils;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class api {


    public static HttpURLConnection makePOSTRequest(String type, String URL, byte[] bytes) throws IOException {

        URL url = new URL(URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(type);
        connection.setRequestProperty("Content-Type", "application/x-protobuf; utf-8");
        connection.setRequestProperty("Accept", "application/x-protobuf");
        connection.setDoOutput(true);
        connection.connect();

        if(bytes != null){
            OutputStream os = connection.getOutputStream();
            os.write(bytes);
            os.flush();
            os.close();
        }

        return connection;

    }

    public static HttpURLConnection makeAuthPOSTRequest(String type, String URL, byte[] bytes, String token) throws IOException {

        URL url = new URL(URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty ("Authorization", token);
        connection.setRequestMethod(type);
        connection.setRequestProperty("Content-Type", "application/x-protobuf; utf-8");
        connection.setRequestProperty("Accept", "application/x-protobuf");
        connection.setDoOutput(true);
        connection.connect();

        if(bytes != null){
            OutputStream os = connection.getOutputStream();
            os.write(bytes);
            os.flush();
            os.close();
        }

        return connection;

    }

    public static HttpURLConnection makeGETRequest(String URL, String name, Object parameter) throws IOException {

        Log.d("ENTREI AQUI", "ENTREI AQUI no makegetreq");
        URL url = new URL(URL + "?" + name + "=" + parameter);
        Log.d("URL", url.toString());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.connect();

        return connection;

    }

    public static HttpURLConnection makeAuthGETRequest(String URL, String name, Object parameter, String token) throws IOException {

        URL url;
        if(parameter != null){
            url = new URL(URL + "?" + name + "=" + parameter);
        }
        else{
            url = new URL(URL); //no parameters
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty ("Authorization", token);
        connection.setRequestMethod("GET");
        //connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/x-protobuf");
        connection.setDoOutput(true);
        connection.connect();

        return connection;

    }

    public static byte[] readResponse(HttpURLConnection connection) {
        /*StringBuilder responseGet = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            responseGet.append(responseLine.trim());
        }
        System.out.println("RESPONSE OF GET REQ: " + responseGet.toString());
        return responseGet.toString();*/

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try{
            is = connection.getInputStream();
            byte[] byteChunk = new byte[4096];
            int n;

            while((n = is.read(byteChunk)) > 0){
                baos.write(byteChunk, 0, n);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] getRequest(String URL, String name, Object parameter){

        HttpURLConnection connection;
        Log.d("ENTREI AQUI", "ENTREI AQUI no getReq");
        try {
            connection = makeGETRequest(URL, name, parameter);

            int responseCode = connection.getResponseCode();

            if (responseCode != 200 && responseCode != 302) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else if(responseCode == 302){
                String newURL = connection.getHeaderField("location");
                connection = makeGETRequest(newURL, name, parameter);
            }
            return readResponse(connection);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAuthRequest(String URL, String name, Object parameter, String token){

        HttpURLConnection connection;
        try {
            connection = makeAuthGETRequest(URL, name, parameter, token);

            int responseCode = connection.getResponseCode();

            if (responseCode != 200 && responseCode != 302) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else if(responseCode == 302){
                String newURL = connection.getHeaderField("location");
                System.out.println("NEW URL WHERE BUG: " + newURL);
                connection = makeAuthGETRequest(newURL, null, null, token);
            }
            return null;
            //return readResponse(connection);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] postRequest(String URL, byte[] bytes) throws IOException {

        HttpURLConnection connection;

        connection = makePOSTRequest("POST", URL, bytes);

        int responseCode = connection.getResponseCode();

        if (responseCode != 200 && responseCode != 302) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else if(responseCode == 302){
            String newURL = connection.getHeaderField("location");
            System.out.println("NEW URL: " + newURL);
            connection = makePOSTRequest("POST", newURL, bytes);
        }

        return readResponse(connection);

    }

    public static byte[] postAuthRequest(String URL, byte[] bytes, String token) {

        HttpURLConnection connection;

        try {
            connection = makeAuthPOSTRequest("POST", URL, bytes, token);

            int responseCode = connection.getResponseCode();

            if (responseCode != 200 && responseCode != 302) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else if (responseCode == 302) {
                String newURL = connection.getHeaderField("location");
                System.out.println("NEW URL TO STORE LP: " + newURL);
                connection = makeAuthPOSTRequest("POST", newURL, bytes, token);
            }
            return readResponse(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

