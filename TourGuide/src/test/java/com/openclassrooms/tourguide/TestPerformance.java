package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

// has to be launched with mvn command such as : mvn test -Dtest=TestPerformance.java -Dspring.profiles.active=performance
// or -Dtest=TestPerformance.java -DcustomVariable.userNumber=1000 (the number of user you need, here 1000)
// Careful with those on windows, please use the backtick "`" like this mvn test `-Dtest=TestPerformance.java `-Dspring.profiles.active=performance
@SpringBootTest
public class TestPerformance {

	/*
	 * A note on performance improvements:
	 * 
	 * The number of users generated for the high volume tests can be easily
	 * adjusted via this method:
	 * 
	 * InternalTestHelper.setInternalUserNumber(100000);
	 * 
	 * 
	 * These tests can be modified to suit new solutions, just as long as the
	 * performance metrics at the end of the tests remains consistent.
	 * 
	 * These are performance metrics that we are trying to hit:
	 * 
	 * highVolumeTrackLocation: 100,000 users within 15 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 *
	 * highVolumeGetRewards: 100,000 users within 20 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	@Value("${customVariable.userNumber}")
	private int userNumber;

	// has to be launched with mvn command such as : mvn test -Dtest=TestPerformance.java -Dspring.profiles.active=performance
	// or -Dtest=TestPerformance.java -DcustomVariable.userNumber=1000 (the number of user you need, here 1000)
	// Careful with those on windows, please use the backtick "`" like this mvn test `-Dtest=TestPerformance.java `-Dspring.profiles.active=performance
	@Test
	public void highVolumeTrackLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		// Users should be incremented up to 100,000, and test finishes within 15
		// minutes
		System.out.println("highVolumeTrackLocation userNumber: " + userNumber);
		InternalTestHelper.setInternalUserNumber(userNumber);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		List<User> allUsers = new ArrayList<>(tourGuideService.getAllUsers());

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		allUsers.parallelStream().forEach(tourGuideService::trackUserLocation);

		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toMillis(stopWatch.getTime()) + " milliseconds." + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	// has to be launched with mvn command such as : mvn test -Dtest=TestPerformance.java -Dspring.profiles.active=performance
	// or -Dtest=TestPerformance.java -DcustomVariable.userNumber=1000 (the number of user you need, here 1000)
	// Careful with those on windows, please use the backtick "`" like this mvn test `-Dtest=TestPerformance.java `-Dspring.profiles.active=performance
	@Test
	public void highVolumeGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		// Users should be incremented up to 100,000, and test finishes within 20
		// minutes
		System.out.println("highVolumeGetRewards userNumber: " + userNumber);
		InternalTestHelper.setInternalUserNumber(userNumber);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		Attraction attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = new CopyOnWriteArrayList<>(tourGuideService.getAllUsers());
		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		allUsers.parallelStream().forEach(rewardsService::calculateRewards);

		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		for (User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toMillis(stopWatch.getTime()) + " milliseconds." + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}
