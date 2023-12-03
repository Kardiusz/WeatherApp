package org.Weather;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws IOException {
        PrintWriter localizationData = new PrintWriter(new FileOutputStream("Localizations.csv", true));

        int optionNumber;
        Scanner scanner = new Scanner(System.in);


        do {

            System.out.println("1. Zapisz lokalizację do pliku");
            System.out.println("2. Odczytanie Miast z zapisanych lokalizacji w pliku");
            System.out.println("3. Pobierz prognozę dla danej lokalizacji");
            System.out.println("6. Zamknij aplikację");


            optionNumber = scanner.nextInt();

            switch (optionNumber) {

                case 1: // zapis nowej lokalizacji do pliku Localizations.csv



                    int putLokalizationOptions;
                    do {


                        localizationData.println("\nLokalizacja: ");
                        System.out.println("Aby dodać lokalizację do pliku...");

                        ///////////////////////////////////////////////////////////////////////////////
                        System.out.println("Podaj miasto: ");
                        String city = scanner.next();
                        scanner.nextLine();

                        ////////////////////////////////////////////////////////////////////////
                        System.out.println("Następnie podaj kraj: ");
                        String country = scanner.nextLine();

                        /////////////////////////////////////////////////////////////////////////////////
                        Double lat = 0.0;
                        boolean validInput = false;
                        do {
                            try {
                                System.out.println("Teraz podaj szerokość geograficzną: ");
                                lat = scanner.nextDouble();
                                scanner.nextLine();
                                if (lat > -90 && lat < 90) {
                                    validInput = true;
                                }else {
                                    System.out.println("Podano nie prawidłową wartość");
                                }
                                }catch (InputMismatchException e) {
                                System.out.println("Wprowadzono niepoprawny format liczby");
                                scanner.nextLine();
                            }
                            }while (!validInput);


                        Double lon = 0.0;
                        boolean validInput1 = false;
                        do {
                            try {
                                System.out.println("Teraz podaj długość geograficzną: ");
                                lon = scanner.nextDouble();
                                scanner.nextLine();
                                if (lon > -180 && lon < 180) {
                                    validInput1 = true;
                                }else {
                                    System.out.println("Podano nie prawidłową wartość");
                                }
                            }catch (InputMismatchException e){
                                System.out.println("Wprowadzono niepoprawny format liczby");
                                scanner.nextLine();
                            }
                        }while (!validInput1);

                        // przypisanie uuid do kraju oraz miasta
                        byte[] countryBytes = country.getBytes();
                        byte[] cityBytes = city.getBytes();
                        UUID uuidOfCountry = UUID.nameUUIDFromBytes(countryBytes);
                        UUID uuidOfCity = UUID.nameUUIDFromBytes(cityBytes);

                        System.out.println(
                                "Kraj: " + country + " uuid: " + uuidOfCountry +
                                "\nMiasto: " + city + " uuid: " + uuidOfCity +
                                "\nLat: " + lat +
                                "\nLon: " + lon);

                        // panel opcji po podaniu wszystkich danych lokalizacji
                        System.out.println("1. Podaj kolejną lokalizację");
                        System.out.println("2. Wstecz");
                        putLokalizationOptions = scanner.nextInt();
                        scanner.nextLine();


                        // zapis do pliku Localizations.csv
                            localizationData.println("Miasto: " + city);
                            localizationData.println("Kraj: " + country);
                            localizationData.println("lat: " + lat);
                            localizationData.println("lon: " + lon);



                    } while (putLokalizationOptions != 2);

                    localizationData.close();

                    break;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                    case 2: //// odczytywanie z pliku Localizations.csv


                        File localizationFileToRead = new File("Localizations.csv");
                        Scanner scanner1 = new Scanner(localizationFileToRead);
                        while (scanner1.hasNextLine()){
                            String line = scanner1.nextLine();

                                if (line.startsWith("Miasto: ")) {
                                    String city = line.substring("Miasto: ".length());
                                    System.out.println(city);
                                }

                        }
                        scanner1.close();
                        break;
                        ///////////////////////////////////////////////////////////////////////////////////////
                case 3:// sprawdzanie uśrednionej prognozy pogody dla danej lokalizacji z zwenętrznego api


                    File localizationFileToRead1 = new File("Localizations.csv");
                    Scanner scanner2 = new Scanner(localizationFileToRead1);
                    while (scanner2.hasNextLine()) {
                        String line = scanner2.nextLine();

                        if (line.startsWith("Miasto: ")) {
                            String city = line.substring("Miasto: ".length());
                            System.out.println("Prognoza pogody dla miasta: " + city);
                            JSONObject weatherData = getWeatherForecast(city);

                            if (weatherData != null) {
                                // Odczytaj potrzebne informacje z obiektu JSON i wyświetl je
                                double temperature = weatherData.getJSONObject("main").getDouble("temp");
                                double pressure = weatherData.getJSONObject("main").getDouble("pressure");
                                double humidity = weatherData.getJSONObject("main").getDouble("humidity");
                                double windSpeed = weatherData.getJSONObject("wind").getDouble("speed");
                                double windDirection = weatherData.getJSONObject("wind").getDouble("deg");

                                System.out.println("Temperatura: " + temperature + " K");
                                System.out.println("Ciśnienie: " + pressure + " hPa");
                                System.out.println("Wilgotność: " + humidity + "%");
                                System.out.println("Prędkość wiatru: " + windSpeed + " m/s");
                                System.out.println("Kierunek wiatru: " + windDirection + "°");
                            } else {
                                System.out.println("Nie można pobrać prognozy pogody dla tego miasta.");
                            }
                        }
                    }
                    scanner2.close();
                    break;




            } // wyświetlanie informacji podczas zamykania aplikacji
            if (optionNumber == 6) {
                System.out.println("Zamykanie aplikacji...");
            }
            // warunek zamknięcia aplikacji
        }while (optionNumber != 6);
        scanner.close();


    }

    public static JSONObject getWeatherForecast (String city) throws IOException { // metoda przyjmuje miasto i zwraca obiekt JSON z danymi o pogodzie

        try {

            //stworzenie url
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=96672deade0208ffb4be9eb5d914935b";
            URL url = new URL(apiUrl);

            //tworzenie połącenia http
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // odczytywanie odpowiedzi z api
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse;


        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}