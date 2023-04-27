package airportDataService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static airportDataService.FilterResultSearcher.getSearchResult;

public class AirportSearch {
    private static final String filename = "C:\\Users\\mateb\\dev\\airport-quick-filter\\src\\fileSources\\airports.csv";
    private static final Map<String, Integer> airports = new TreeMap<>();

    public static List<String[]> getResult(String airportName, String filter) {
        List<Integer> indexes = getSortIndexes(airportName);
        return getSearchResult(indexes, filter);
    }

    private static List<Integer> getSortIndexes(String airportName) {
        List<Integer> indexes = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : airports.entrySet()) {
            String airportKey = entry.getKey().toLowerCase();
            if (airportKey.startsWith(airportName.toLowerCase())) {
                indexes.add(entry.getValue());
            }
        }
        Collections.sort(indexes);
        return indexes;
    }

    public static void initializeIndexes() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length >= 2) {
                    airports.put(columns[1].toLowerCase().replaceAll("\"", ""), Integer.valueOf(columns[0]));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка: неправильный путь к файлу " + filename);
        }
    }
}
