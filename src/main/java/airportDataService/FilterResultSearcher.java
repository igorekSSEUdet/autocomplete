package airportDataService;


import model.Request;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FilterResultSearcher {

    private static final DataFilter dataFilter = new DataFilter();
    private static final Map<Request,List<String[]>> cache = new WeakHashMap<>();
    private static final String filename = "C:\\Users\\mateb\\dev\\airport-quick-filter\\src\\fileSources\\airports.csv";

    public static List<String[]> getSearchResult(List<Integer> indexes, String filter) {
        if (cache.containsKey(new Request(indexes,filter))) {
            return cache.get(new Request(indexes,filter));
        }
        List<String[]> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int firstColumn = Integer.parseInt(parts[0].trim());
                if (isInList(firstColumn, indexes)) {
                    result.add(parts);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Ошибка: неправильный путь к файлу " + filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!filter.isEmpty()) {
            List<String[]> resultWithStables = getResultIfFilterExist(filter, result);
            cache.put(new Request(indexes,filter),resultWithStables);
            return resultWithStables;
        }
        cache.put(new Request(indexes,filter),result);
        return result;
    }

    private static List<String[]> getResultIfFilterExist(String filter, List<String[]> result) {
        List<String[]> allResult = new ArrayList<>();
        if (filter.contains(")") || filter.contains("(")) {
            for (String[] column : result) {
                if (dataFilter.getResponseWithStaples(filter, column)) {
                    allResult.add(column);
                }
            }
        } else {
            for (String[] column : result) {
                if (dataFilter.getResponseWithoutStaples(filter, column)) {
                    allResult.add(column);
                }
            }
        }
        return allResult;
    }

    private static boolean isInList(int value, List<Integer> numbers) {
        if(numbers.size() == 0) return false;
        if (value < numbers.get(0) || value > numbers.get(numbers.size() - 1)) {
            return false;
        }

        int index = Collections.binarySearch(numbers, value);

        if (index >= 0) {
            return true;
        } else {
            int insertIndex = -index - 1;
            return insertIndex > 0 && value == numbers.get(insertIndex - 1);
        }
    }

}