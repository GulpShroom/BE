package com.hufsglobalion.glupshroom.domain.journey.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PhotoMetadataExtractor {

    private final RestClient restClient;

    public PhotoMetadataExtractor() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public PhotoMetadata extract(String photoUrl) {
        Integer year = null;
        Integer month = null;
        String season = null;
        String country = null;
        String city = null;

        try (InputStream inputStream = URI.create(photoUrl).toURL().openStream()) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            ExifSubIFDDirectory dateDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (dateDirectory != null) {
                Date takenDate = dateDirectory.getDateOriginal();
                if (takenDate != null) {
                    LocalDateTime dateTime = LocalDateTime.ofInstant(takenDate.toInstant(), ZoneId.systemDefault());
                    year = dateTime.getYear();
                    month = dateTime.getMonthValue();
                    season = toSeason(month);
                }
            }

            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null && !geoLocation.isZero()) {
                    Map<String, String> address = reverseGeocode(geoLocation.getLatitude(), geoLocation.getLongitude());
                    country = address.get("country");
                    city = address.get("city");
                }
            }
        } catch (ImageProcessingException | IOException e) {
            log.warn("사진 EXIF 메타데이터 추출 실패: {}", e.getMessage());
        }

        return new PhotoMetadata(year, month, season, country, city);
    }

    private String toSeason(int month) {
        return switch (month) {
            case 3, 4, 5 -> "spring";
            case 6, 7, 8 -> "summer";
            case 9, 10, 11 -> "fall";
            default -> "winter";
        };
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> reverseGeocode(double lat, double lon) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}&zoom=10&addressdetails=1", lat, lon)
                    .header("User-Agent", "glupshroom-app")
                    .retrieve()
                    .body(Map.class);

            Map<String, String> result = new HashMap<>();
            if (response != null && response.get("address") instanceof Map<?, ?> address) {
                Object countryVal = address.get("country");
                Object cityVal = address.get("city");
                if (cityVal == null) cityVal = address.get("town");
                if (cityVal == null) cityVal = address.get("village");
                if (countryVal != null) result.put("country", countryVal.toString());
                if (cityVal != null) result.put("city", cityVal.toString());
            }
            return result;
        } catch (Exception e) {
            log.warn("역지오코딩 실패: {}", e.getMessage());
            return Map.of();
        }
    }
}