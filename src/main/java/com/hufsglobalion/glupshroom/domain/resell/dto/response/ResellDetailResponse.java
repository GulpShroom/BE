package com.hufsglobalion.glupshroom.domain.resell.dto.response;

import java.util.List;

public record ResellDetailResponse(
        Long resellId,
        String officialName,
        String sellerNickname,
        boolean isAuthor,
        Long price,
        String conditionGrade,
        String postStatus,
        List<PhotoDetail> photos,
        Summary summary,
        LockedJourney lockedJourney
) {
    public record PhotoDetail(
            Long photoId,
            String photoUrl,
            Integer sortOrder
    ) {}

    public record Summary(
            long journeyCount,
            long generationCount,
            long countryCount,
            long cityCount,
            boolean isAuthenticated,
            Integer productAgeYears,
            Integer provenanceScore,
            int verifyRatio
    ) {}

    public record LockedJourney(
            List<String> cities,
            long countryCount,
            long cityCount,
            boolean hasLetter,
            boolean hasCareTip,
            boolean hasSelectedTags
    ) {}
}
