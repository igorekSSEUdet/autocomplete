package model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Filter {
    private final Integer columnNumber;
    private final String operator;
    private final String value;
}
