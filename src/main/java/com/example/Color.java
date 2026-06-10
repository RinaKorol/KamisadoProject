package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Color {
    ORANGE("#F5844F"),
    RED("#F14950"),
    BLUE("#75C5ED"),
    GREEN("#8FCC6A"),
    YELLOW("#FFDD34"),
    PURPLE("#BB7EB8"),
    BROWN("#A86238"),
    PINK("#F491BD");

    Map<Color, List<Integer>> colorToCellMap = new HashMap<>();

    private String colorCode;

    public String getColorCode(){
        return  colorCode;
    }
    Color(String colorCode) {
        this.colorCode = colorCode;
    }
}