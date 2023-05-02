package model;

import lombok.Data;
import java.util.List;

@Data
public class Request {
    private final List<Integer> indexes;
    private final String filter;
}
