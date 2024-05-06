package hamtom.me.futsalreservationview.call;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class Common {
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
}
