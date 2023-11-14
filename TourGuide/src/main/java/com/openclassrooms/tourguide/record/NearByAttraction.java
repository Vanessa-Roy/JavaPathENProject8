package com.openclassrooms.tourguide.record;

public record NearByAttraction(
        String attractionName,
        double latitude,
        double longitude,
        Double distance,
        int reward
) { }
