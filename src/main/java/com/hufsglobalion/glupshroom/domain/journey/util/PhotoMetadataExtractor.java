package com.hufsglobalion.glupshroom.domain.journey.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class PhotoMetadataExtractor {

    private static final String GEOCODE_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    private final RestClient restClient;
    private final String googleMapsApiKey;

    public PhotoMetadataExtractor(@Value("${google.maps.api-key:}") String googleMapsApiKey) {
        this.googleMapsApiKey = googleMapsApiKey;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public PhotoMetadata extract(String photoUrl) {
        try (InputStream inputStream = URI.create(photoUrl).toURL().openStream()) {
            return extractFromStream(inputStream);
        } catch (IOException e) {
            log.warn("사진 다운로드 실패: {}", e.getMessage());
            return new PhotoMetadata(null, null, null, null, null, null);
        }
    }

    public PhotoMetadata extractFromBytes(byte[] photoBytes) {
        try (InputStream inputStream = new ByteArrayInputStream(photoBytes)) {
            return extractFromStream(inputStream);
        } catch (IOException e) {
            log.warn("사진 메타데이터 처리 실패: {}", e.getMessage());
            return new PhotoMetadata(null, null, null, null, null, null);
        }
    }

    private PhotoMetadata extractFromStream(InputStream inputStream) {
        Integer year = null;
        Integer month = null;
        String season = null;
        String country = null;
        String city = null;
        LocalDateTime takenAt = null;

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            ExifSubIFDDirectory dateDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (dateDirectory != null) {
                Date takenDate = dateDirectory.getDateOriginal();
                if (takenDate != null) {
                    LocalDateTime dateTime = LocalDateTime.ofInstant(takenDate.toInstant(), ZoneId.systemDefault());
                    year = dateTime.getYear();
                    month = dateTime.getMonthValue();
                    season = toSeason(month);
                    takenAt = dateTime;
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

        return new PhotoMetadata(year, month, season, country, city, takenAt);
    }

    public Coordinates resolveCoordinates(String city, String country) {
        if (city != null) {
            return geocode(buildGeocodeQuery(city, country)).orElse(Coordinates.EMPTY);
        }
        if (country != null) {
            return geocode(country).orElse(Coordinates.EMPTY);
        }
        return Coordinates.EMPTY;
    }

    private String buildGeocodeQuery(String city, String country) {
        return country != null ? city + ", " + country : city;
    }

    private String toSeason(int month) {
        return switch (month) {
            case 3, 4, 5 -> "spring";
            case 6, 7, 8 -> "summer";
            case 9, 10, 11 -> "fall";
            default -> "winter";
        };
    }

    public Optional<Coordinates> geocode(String query) {
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }

        try {
            Map<String, Object> response = restClient.get()
                    .uri(GEOCODE_BASE_URL + "?address={query}&key={key}", query, googleMapsApiKey)
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> result = firstResult(response);
            if (result == null) {
                return Optional.empty();
            }

            if (!(result.get("geometry") instanceof Map<?, ?> geometry)) {
                return Optional.empty();
            }
            if (!(geometry.get("location") instanceof Map<?, ?> location)) {
                return Optional.empty();
            }

            BigDecimal latitude = new BigDecimal(location.get("lat").toString());
            BigDecimal longitude = new BigDecimal(location.get("lng").toString());
            return Optional.of(new Coordinates(latitude, longitude));
        } catch (Exception e) {
            log.warn("지오코딩 실패: query={}, {}", query, e.getMessage());
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> reverseGeocode(double lat, double lon) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(GEOCODE_BASE_URL + "?latlng={lat},{lon}&language=ko&key={key}", lat, lon, googleMapsApiKey)
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> result = firstResult(response);
            if (result == null || !(result.get("address_components") instanceof List<?> components)) {
                return Map.of();
            }

            Map<String, String> address = new HashMap<>();
            String country = extractComponent(components, "country");
            String city = extractComponent(components, "locality");
            if (city == null) {
                city = extractComponent(components, "administrative_area_level_2");
            }
            if (city == null) {
                city = extractComponent(components, "administrative_area_level_1");
            }
            if (country != null) address.put("country", country);
            if (city != null) address.put("city", city);
            return address;
        } catch (Exception e) {
            log.warn("역지오코딩 실패: {}", e.getMessage());
            return Map.of();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> firstResult(Map<String, Object> response) {
        if (response == null || !"OK".equals(response.get("status"))) {
            return null;
        }
        if (!(response.get("results") instanceof List<?> results) || results.isEmpty()) {
            return null;
        }
        return (Map<String, Object>) results.get(0);
    }

    private String extractComponent(List<?> components, String type) {
        for (Object componentObj : components) {
            if (componentObj instanceof Map<?, ?> component
                    && component.get("types") instanceof List<?> types
                    && types.contains(type)) {
                Object longName = component.get("long_name");
                return longName != null ? longName.toString() : null;
            }
        }
        return null;
    }
}