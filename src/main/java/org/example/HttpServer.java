package org.example;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;

public class HttpServer {
    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(37000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 37000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir port el puerto 37000...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean isFirstLine = true;
            String firstLine = "";

            while ((inputLine = in.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    firstLine = inputLine;
                }
                if (!in.ready()) {break; }
            }
            System.out.println("Recibí: " + firstLine);

            String path = firstLine.split(" ")[1];

            if (path.startsWith("/compreflex")){
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + extractComando(path);
            } else {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<head>\n"
                        + "<meta charset=\"UTF-8\">\n"
                        + "<title>Title of the document</title>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "<h1>Mi propio mensaje</h1>\n"
                        + "</body>\n"
                        + "</html>\n";
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    private static String extractComando(String path) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String response = "";
        String comando = path.split("=")[1];

        Class c = Math.class;

        if (comando.startsWith("bbl")){
            String[] params = comando.split("\\(")[1].split("\\)")[0].split(",");
            return bubbleSort(params);
        }

        String methodString = comando.split("\\(")[0];
        String[] params = comando.split("\\(")[1].split("\\)")[0].split(",");

//        System.out.println("Number of params: " + params.length);
//        for (int i = 0; i < params.length; i++){
//            System.out.println("Param: " + params[i]);
//        }

        if (params.length == 1){
            Method m = c.getMethod(methodString, double.class);
            response = m.invoke(null, getParamValue(params[0])).toString();
        } else if (params.length == 2){
            Method m = c.getMethod(methodString, double.class, double.class);
            response = m.invoke(null, getParamValue(params[0]), getParamValue(params[1])).toString();
        }

        return response;
    }

    private static Double getParamValue(String param) {
        return Double.valueOf(param);
    }

    private static String bubbleSort(String[] params) {
        int n = params.length;
        int[] numbers = new int[n];

        for (int i = 0; i < n; i++) {
            numbers[i] = Integer.valueOf(params[i]);
        }
        //System.out.println("Numbers Before: " + Arrays.toString(numbers));

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (numbers[j] > numbers[j + 1]) {
                    int temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                }
            }
        }
        //System.out.println("Numbers After: " + Arrays.toString(numbers));

        return Arrays.toString(numbers);
    }
}