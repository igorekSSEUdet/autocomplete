package airportDataService;


import exceptions.FilterParametersException;
import model.Filter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataFilter {

    private static final String CREATEFILTER = "(\\w+)\\[(\\d+)]([><=!]+)(['\"]?)([\\w\\s]+)(\\4)";
    private static final String PARSEFILTER = "([^&|()]+)(\\s*[=<>]+\\s*)([^&|()]+)";

    public boolean getResponseWithStaples(String filter, String[] column) {
        List<Filter> filters = getFilters(filter);
        List<Boolean> filterResults = new ArrayList<>();
        for (Filter f : filters) {
            filterResults.add(filter(column, f));
        }
        List<Object> result = parseBooleanAndOperators(replaceSubstringsWithBoolean(filterResults, filter));
        return evaluateExpression(result);
    }

    public boolean getResponseWithoutStaples(String filter, String[] column) {
        List<String> operators = getOperators(filter);
        List<Filter> filters = getFilters(filter);
        List<Boolean> filterResults = new ArrayList<>();
        for (Filter f : filters) {
            filterResults.add(filter(column, f));
        }
        List<Object> values = mergeListsWithoutStaples(operators, filterResults);
        return evaluateExpression(values);
    }

    private static List<Object> parseBooleanAndOperators(String inputString) {
        List<Object> result = new ArrayList<>();
        String regex = "(true|false|\\(|\\)|&|\\|\\|)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);

        while (matcher.find()) {
            String match = matcher.group();
            if (match.equals("true")) {
                result.add(true);
            } else if (match.equals("false")) {
                result.add(false);
            } else {
                result.add(match);
            }
        }

        return result;
    }

    private static String replaceSubstringsWithBoolean(List<Boolean> filterResults, String filter) {
        StringBuilder sb = new StringBuilder(filter);
        String regex = "([^&|()]+)(\\s*[=<>]+\\s*)([^&|()]+)";
        Matcher matcher = Pattern.compile(regex).matcher(filter);
        int resultIndex = 0;
        while (matcher.find()) {

            Boolean booleanValue = filterResults.get(resultIndex);
            sb.replace(matcher.start(), matcher.end(), booleanValue.toString());
            resultIndex++;
            matcher.reset(sb.toString());

        }
        return sb.toString();
    }

    private static List<Object> mergeListsWithoutStaples(List<String> operators, List<Boolean> filterResults) {
        List<Object> result = new ArrayList<>();
        int j = 0;
        for (Boolean aBoolean : filterResults) {
            result.add(aBoolean);
            if (j < operators.size()) {
                result.add(operators.get(j));
            }
            j = j + 1;
        }
        return result;
    }

    private static int getPriority(String operator) {
        switch (operator) {
            case "&":
                return 2;
            case "|":
            case "||":
                return 1;
            case "(":
                return 0;
        }
        throw new IllegalArgumentException("В условии фильтрации передан неизвестный оператор: " + operator);
    }

    private static List<String> getOperators(String str) {
        List<String> operators = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '&' || c == '(' || c == ')') {
                operators.add(String.valueOf(c));
            } else if (c == '|') {
                operators.add(String.valueOf(c));
                i++;
            }
        }
        return operators;
    }

    private static List<Filter> getFilters(String filter) {
        List<String> filters = parseFilter(filter);
        return filters.stream()
                .map(f -> {
                    Pattern pattern = Pattern.compile(CREATEFILTER);//тут проходим по списку фильтров(строчек) и создаем объекты Filter
                    Matcher matcher = pattern.matcher(f);
                    String column = null;
                    String operator = null;
                    String value = null;
                    if (matcher.find()) {
                        column = matcher.group(2);
                        operator = matcher.group(3);
                        value = matcher.group(5);
                    }
                    if (value == null || column == null || operator == null) {
                        throw new FilterParametersException("Передан неверный формат фильтра");
                    }
                    if (value.contains("'") || value.contains("\"")) {
                        value = value.replaceAll("['\"]", "").toLowerCase().trim();
                    }
                    return Filter.builder()
                            .columnNumber(Integer.parseInt(column))
                            .operator(operator)
                            .value(value)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static List<String> parseFilter(String filter) {
        List<String> conditions = new ArrayList<>();
        Pattern pattern = Pattern.compile(PARSEFILTER);//тут достаем из исходной строки фильтры и формируем список
        Matcher matcher = pattern.matcher(filter);
        while (matcher.find()) {
            String condition = matcher.group(1).trim() + matcher.group(2).trim() + matcher.group(3).trim();
            conditions.add(condition);
        }
        return conditions;
    }

    private static boolean filter(String[] data, Filter filter) {
        String actualValue = data[filter.getColumnNumber() - 1].replaceAll("\"", "").trim().toLowerCase();
        switch (filter.getOperator()) {
            case "=":
                return actualValue.equals(filter.getValue().toLowerCase());
            case "<>":
                return !actualValue.equals(filter.getValue());
            case ">":
                return Integer.parseInt(actualValue) > Integer.parseInt(filter.getValue());
            case "<":
                return Integer.parseInt(actualValue) < Integer.parseInt(filter.getValue());
            default:
                return false;
        }
    }

    private static boolean evaluateExpression(List<Object> values) {
        Stack<Boolean> stack = new Stack<>();
        Stack<String> operators = new Stack<>();

        try {
            for (Object value : values) {
                if (value instanceof Boolean) {
                    stack.push((Boolean) value);
                    continue;
                }

                String operator = (String) value;
                if (operator.equals("(")) {
                    operators.push(operator);
                } else if (operator.equals(")")) {
                    while (!operators.peek().equals("(")) {
                        reduce(stack, operators);
                    }

                    operators.pop(); // Удаляем (
                } else {
                    while (!operators.isEmpty() && getPriority(operators.peek()) >= getPriority(operator)) {
                        reduce(stack, operators);
                    }

                    operators.push(operator);
                }
            }

            while (!operators.isEmpty()) {
                reduce(stack, operators);
            }

            return stack.pop();
        } catch (EmptyStackException | ClassCastException ex) {
            throw new FilterParametersException("Ошибка определения результата фильтров");
        }
    }

    private static void reduce(Stack<Boolean> stack, Stack<String> operators) {
        try {
            String operator = operators.pop();
            boolean a = stack.pop();
            boolean b = stack.pop();
            switch (operator) {
                case "&":
                    stack.push(a && b);
                    break;
                case "|":
                case "||":
                    stack.push(a || b);
                    break;
            }
        } catch (EmptyStackException exception) {
            throw new FilterParametersException("Ошибка: передан неверный формат строки");
        }
    }
}
