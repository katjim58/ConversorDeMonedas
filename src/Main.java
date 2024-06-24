import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
    private static final String API_KEY = "ad08e8fd90497ea65e0fde2319adf833d4d1494ab3faec027361cc2a78be8eb1";
    private static final String API_URL = "https://www.banxico.org.mx/SieAPIRest/service/v1/series/SF43718/datos/oportuno";

    public static void main(String[] args) {
        Scanner leer = new Scanner(System.in);
        while (true) {
            System.out.println("CONVERSOR DE MONEDAS");
            System.out.println("Seleccione la moneda de origen (ej. USD, EUR, PEN, MXN, COP):");
            String monedaOrigen = leer.next().toUpperCase();
            System.out.println("Seleccione la moneda de destino (ej. USD, EUR, PEN, MXN, COP):");
            String monedaDestino = leer.next().toUpperCase();

            if (monedaOrigen.equals("SALIR") || monedaDestino.equals("SALIR")) {
                System.out.println("Cerrando programa");
                return;
            }

            System.out.println("Ingrese la cantidad de " + monedaOrigen + ":");
            double cantidadDeMoneda = leer.nextDouble();

            double tasaDeCambio = obtenerTasaDeCambio(monedaOrigen, monedaDestino);
            if (tasaDeCambio == -1) {
                System.out.println("Error obteniendo la tasa de cambio.");
                continue;
            }

            double cantidadConvertida = cantidadDeMoneda * tasaDeCambio;
            cantidadConvertida = (double) Math.round(cantidadConvertida * 100d) / 100;
            System.out.println("-----------------------------------------");
            System.out.println("Usted tiene la cantidad de " + cantidadConvertida + " " + monedaDestino);
            System.out.println("-----------------------------------------");
        }
    }

    static double obtenerTasaDeCambio(String monedaOrigen, String monedaDestino) {
        try {
            URL url = new URL(API_URL + "?token=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HTTP error code : " + responseCode);
            }

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(in).getAsJsonObject();

            // Acceder a la estructura correcta del JSON para obtener la tasa de cambio
            JsonObject bmx = json.getAsJsonObject("bmx");
            JsonArray seriesArray = bmx.getAsJsonArray("series");
            JsonObject series = seriesArray.get(0).getAsJsonObject();
            JsonArray datosArray = series.getAsJsonArray("datos");
            JsonObject datos = datosArray.get(0).getAsJsonObject();
            double tasaDeCambio = datos.get("dato").getAsDouble();

            return tasaDeCambio;

        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Código de error
        }
    }
}
