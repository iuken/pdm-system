package ru.aziattsev.pdm_system.dto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public record EngineeringElementDto(
        String designation,
        String name,
        String section,
        String quantity,
        String parent
) {
    private static final DecimalFormat QUANTITY_FORMAT = new DecimalFormat("#.###",
            new DecimalFormatSymbols(Locale.US));

    public EngineeringElementDto(String designation, String name,
                                 String section, Double quantity, String parent) {
        this(designation, name,
                cleanSection(section),
                formatQuantity(quantity),
                parent);
    }
    private static String cleanSection(String section) {
        return section != null ? section.replace("Спецификации\\", "") : null;
    }
    private static String formatQuantity(Double quantity) {
        if (quantity == null) {
            return "0";
        }
        synchronized (QUANTITY_FORMAT) {
            return QUANTITY_FORMAT.format(quantity.doubleValue());
        }
    }
}