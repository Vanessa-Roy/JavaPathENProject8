package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	private final RewardCentralServiceAsync rewardCentralServiceAsync = new RewardCentralServiceAsync();
	private final GpsUtilServiceAsync gpsUtilServiceAsync = new GpsUtilServiceAsync();
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewards(User user) {
		List<Attraction> attractions = gpsUtilServiceAsync.getAttractionsAsync().join();

		List<VisitedLocation> userLocations = new CopyOnWriteArrayList<>(user.getVisitedLocations());
		userLocations.forEach((visitedLocation) -> {
			attractions.stream()
					.filter(a -> (nearAttraction(visitedLocation, a)) && user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(a.attractionName)))
					.toList().forEach(a -> user.addUserReward(new UserReward(visitedLocation, a, rewardCentralServiceAsync.getAttractionRewardPointsAsync(a.attractionId,user.getUserId()).join())));
		});
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

	public RewardCentral getRewardsCentral() {
		return rewardsCentral;
	}

}
