package hamtom.me.futsalreservation.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import hamtom.me.futsalreservation.vo.vo.ReservationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class Common {
    public static final String STADIUM_NO = "erntResveNo";
    public static final String RESERVATION_NO = "erntApplcntNo";

    public static String extractCookieValue(List<String> cookies, String cookieName) {
        if (cookies == null) {
            return null;
        }
        for (String cookie : cookies) {
            // 쿠키가 "쿠키 이름="로 시작하는지 확인
            if (cookie.startsWith(cookieName + "=")) {
                // "="로 쿠키 이름과 값을 분리하고 쿠키 값을 반환
                return cookie.split(";")[0].split("=")[1];
            }
        }
        return null;  // 해당 쿠키 이름의 쿠키가 없을 경우 null 반환
    }

    public static String makeUrl(String host, String uri){
        String fullUrl = String.format("https://%s%s", host, uri);

        return UriComponentsBuilder.fromHttpUrl(fullUrl)
                .encode()
                .toUriString();
    }

    public static List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        return messageConverters;
    }

    public static HttpHeaders makeHeader(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);  // Content-Type 헤더 설정
        headers.setAccept(Collections.singletonList(MediaType.ALL));  // Accept 헤더 설정
        headers.set("Accept-Encoding", "gzip, deflate, br");  // Accept-Encoding 헤더 설정
        headers.set("Connection", "keep-alive");  // Connection 헤더 설정
//        headers.set(HttpHeaders.ACCEPT, "*/*");
//        headers.set("User-Agent", "PostmanRuntime/7.36.1");

        return headers;
    }
    public static HttpHeaders makeHeader(MediaType mediaType, String cookieForHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);  // Content-Type 헤더 설정
        headers.setAccept(Collections.singletonList(MediaType.ALL));  // Accept 헤더 설정
        headers.set("Accept-Encoding", "gzip, deflate, br");  // Accept-Encoding 헤더 설정
        headers.set("Connection", "keep-alive");  // Connection 헤더 설정
        headers.add(HttpHeaders.COOKIE, cookieForHeader);

        return headers;
    }

    public static ReservationResponse requestReservation (String url, MultiValueMap<String, String> body, HttpHeaders headers) {
        // 요청 엔티티 구성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 추가한 부분
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        // 응답 확인
        HttpStatusCode responseCode = response.getStatusCode();
        log.info("responseCode: {}", responseCode);

        String responseBody = response.getBody();
        if (Objects.isNull(responseBody)) {
            throw new RuntimeException("No Response!");
        }
        log.info("responseBody: {}", responseBody);

        JsonElement json = JsonParser.parseString(responseBody);
        Gson gson = new Gson();
        ReservationResponse reservationResponse = gson.fromJson(json, ReservationResponse.class);
        return reservationResponse;
    }
}
