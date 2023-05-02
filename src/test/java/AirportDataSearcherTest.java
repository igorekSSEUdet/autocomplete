import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static airportDataService.AirportSearch.getResult;
import static airportDataService.AirportSearch.initializeIndexes;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AirportDataSearcherTest {

    @BeforeAll
    public static void setUp() {
        initializeIndexes();
    }

    @Test
    public void shouldReturnDataWithoutFilters() {
        String filter = "";
        String airport = "Bo";
        assertEquals(68, getResult(airport,filter).size());
    }

    @Test
    public void shouldReturnFilteredDataWithoutBrackets() {
        String filter = "column[1]<10&column[5]='GKA'";
        String airport = "go";
        assertEquals(1, getResult(airport,filter).size());
    }

    @Test
    public void shouldReturnFilteredDataWithBracketsOnRight() {
        String filter = "column[1]>10&column[5]='AHE' || (column[1]>10&column[6]='OIAW')";
        String airport = "ah";
        assertEquals(2, getResult(airport,filter).size());
    }

    @Test
    public void shouldReturnFilteredDataWithBracketsOnLeft() {
        String filter = "column[1]>10&column[5]='AHE' || (column[1]>10&column[6]='OIAW')";
        String airport = "ah";
        assertEquals(2, getResult(airport,filter).size());
    }

    @Test
    public void shouldReturnFilteredDataWithBracketsInMiddle() {
        String filter = "column[1]>10&(column[3]='Milwaukee' & column[4]=United States)||column[6]=OIAW";
        String airport = "la";
        assertEquals(1, getResult(airport,filter).size());
    }


}
