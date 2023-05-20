package airportController;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static airportDataService.AirportSearch.getResult;
import static airportDataService.AirportSearch.initializeIndexes;
import static utils.Front.*;

public class Main {
    public static void main(String[] args) {
        getWelcome();
        Scanner scanner = new Scanner(System.in);
        sayDisclaimer();
        while (true) {
            initializeIndexes();
            sayGetAirport();
            String airport = scanner.nextLine();
            if (airport.equals("!quit")) {
                sayBye();
                return;
            }
            sayGetFilter();
            String filter = scanner.nextLine();

            long startTime = System.nanoTime();
            List<String[]> result = getResult(airport, filter);
            long endTime = System.nanoTime();

            List<String> filterAndSortList = getFilterAndSortList(result);
            for (String s : filterAndSortList) {
                System.out.println(s);
            }

            System.out.print("\nКоличество найденных строк: " + filterAndSortList.size());
            System.out.println(" Время затраченное на поиск: " + ((endTime - startTime) / 1000000) + " мс");

        }
    }

    public static List<String> getFilterAndSortList(List<String[]> list) {
        return list.stream()
                .sorted(Comparator.comparing(arr -> arr[1]))
                .map(arr -> String.format("%s %s", Arrays.toString(Arrays.copyOfRange(arr, 1, 2)), Arrays.toString(arr)))
                .collect(Collectors.toList());
    }

}
