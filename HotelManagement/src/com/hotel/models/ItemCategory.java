package com.hotel.models;

public enum ItemCategory {
    VEG("Veg"),
    NON_VEG("Non-Veg");

    private final String label;

    ItemCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ItemCategory fromString(String text) {
        if (text == null) return null;
        String clean = text.trim().toLowerCase().replace("-", "").replace(" ", "");
        if (clean.equals("veg")) return VEG;
        if (clean.equals("nonveg")) return NON_VEG;
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}
