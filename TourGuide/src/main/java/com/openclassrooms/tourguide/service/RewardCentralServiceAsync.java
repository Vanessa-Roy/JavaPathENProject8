package com.openclassrooms.tourguide.service;

import rewardCentral.RewardCentral;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class RewardCentralServiceAsync extends RewardCentral {
    public CompletableFuture<Integer> getAttractionRewardPointsAsync(UUID attractionId, UUID userId) {
        return CompletableFuture.supplyAsync(
                () -> this.getAttractionRewardPoints(attractionId, userId), Executors.newCachedThreadPool()
        );
    }
}
