package impl.cell.value;

import api.CellValue;
import impl.EngineImpl;
import impl.cell.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionValue implements CellValue {
    private final FunctionType functionType;
    List<CellValue> arguments = new ArrayList<>();
    private final Object effectiveValue;


    public FunctionValue(String functionDefinition) {
        List<String> argsStr = extractArguments(functionDefinition);
        functionType = parseFunctionType(argsStr.getFirst());
        for (String argument : argsStr.subList(1, argsStr.size())) {
            CellValue value = EngineImpl.convertStringToCellValue(argument);
            arguments.add(value);
        }
        effectiveValue = this.eval();
    }

    public static List<String> extractArguments(String input) {
        List<String> arguments = new ArrayList<>();
        int level = 0;
        int start = 0;
        boolean insideArgument = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '{') {
                if (level == 0) {
                    start = i;
                }
                level++;
            } else if (c == '}') {
                level--;
                if (level == 0) {
                    arguments.add(input.substring(start, i).trim());
                    insideArgument = false;
                }
                else{
                    insideArgument = true;
                }
            } else if (c == ',' && level == 1) {
                if (insideArgument) {
                    arguments.add(input.substring(start, i).trim());
                }
                start = i + 1;
                insideArgument = false;
            } else if (level == 1 && !insideArgument && c != ' ') {
                start = i;
                insideArgument = true;
            }
        }

        // Add the last argument if there is one
        if (insideArgument) {
            arguments.add(input.substring(start).trim());
        }

        return arguments;
    }

    @Override
    public Object eval() {
        switch (functionType) {
            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
            case POW:
                try {
                    checkNumOfArguments(2, "2 arguments");
                    double arg1 = (double) arguments.get(0).eval();
                    double arg2 = (double) arguments.get(1).eval();
                    return functionType.apply(arg1, arg2);
                }
                catch (ClassCastException e) {
                    throw new RuntimeException("Error: One or more arguments are not numeric. Ensure that all inputs for this function are numbers.");
                }
            case ABS:
                try {
                    checkNumOfArguments(1, "1 argument");
                    double arg = (double) arguments.getFirst().eval();
                    return functionType.apply(arg);
                }
                catch (ClassCastException e) {
                    throw new RuntimeException("Error: argument is not numeric. Ensure that all inputs for this function are numbers.");
                }
            case CONCAT:
                checkNumOfArguments(2, "2 arguments");
                try {
                    String str1 = (String) arguments.get(0).eval();
                    String str2 = (String) arguments.get(1).eval();
                    return functionType.apply(str1, str2);
                }
                catch (ClassCastException e) {
                    throw new RuntimeException("Error: One or more arguments are not valid text. Please check that all arguments are correctly formatted as text.");
                }
            case SUB:
                checkNumOfArguments(3, "3 arguments");
                try {
                    String str = (String) arguments.get(0).eval();
                    int idx1 =  ((Double) arguments.get(1).eval()).intValue();
                    int idx2 =  ((Double) arguments.get(2).eval()).intValue();
                    return functionType.apply(str,idx1,idx2);
                }
                catch (ClassCastException e) {
                    throw new RuntimeException("Error: One or more arguments are not valid. Please check that all arguments are correctly formatted.");
                }//TODO
        }
        return null;
    }

    public enum FunctionType {
        PLUS {
            @Override
            public double apply(double arg1, double arg2) {
                return arg1 + arg2;
            }
        },
        MINUS {
            @Override
            public double apply(double arg1, double arg2) {
                return arg1 - arg2;
            }
        },
        TIMES {
            @Override
            public double apply(double arg1, double arg2) {
                return arg1 * arg2;
            }

        },
        MOD{
            @Override
            public double apply(double arg1, double arg2) {
                return arg1 % arg2;
            }
        },
        DIVIDE {
            @Override
            public double apply(double arg1, double arg2) {
                if (arg2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return arg1 / arg2;
            }
        },
        POW {
            @Override
            public double apply(double arg1, double arg2) {
                return Math.pow(arg1, arg2);
            }
        },
        ABS{
            @Override
            public double apply(double arg) {
                return Math.abs(arg);
            }
        },
        CONCAT {
            @Override
            public String apply(String str1, String str2) {
                return str1 + str2;
            }
        },
        SUB {
            @Override
            public String apply(String source, int startIndex, int endIndex) {
                if (startIndex < 0 || endIndex >= source.length() || startIndex > endIndex) {
                    return "!UNDEFINED!";
                }
                return source.substring(startIndex, endIndex + 1);
            }
        };

        // Overloaded methods to handle different argument types
        public double apply(double arg1, double arg2) {
            throw new UnsupportedOperationException("This function does not support numeric operations");
        }

        public String apply(String str1, String str2) {
            throw new UnsupportedOperationException("This function does not support string concatenation");
        }

        public String apply(String source, int startIndex, int endIndex) {
            throw new UnsupportedOperationException("This function does not support substring operations");
        }

        public double apply(double arg) {
            throw new UnsupportedOperationException("This function does not support numeric operations");
        }
    }

    private void checkNumOfArguments(int numOfArgumentsExp, String numArgsStr) throws IllegalArgumentException {
        if (arguments.size() != numOfArgumentsExp) {
            throw new IllegalArgumentException("Error: Function " + functionType.name() + " expected " + numArgsStr + ", got " + arguments.size() + ".");
        }
    }

    @Override
    public Object getEffectiveValue() {
        return effectiveValue;
    }

    public boolean isValid() {
        return functionType != null;
    }

    private FunctionType parseFunctionType(String functionName) {
        try {
            return FunctionType.valueOf(functionName);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid function definition: " + functionName);
        }
    }






}
