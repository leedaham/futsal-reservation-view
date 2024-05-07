package hamtom.me.futsalreservationview.call;

import hamtom.me.futsalreservationview.call.vo.LoginCookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static hamtom.me.futsalreservationview.call.Common.*;
import static hamtom.me.futsalreservationview.call.vo.LoginCookie.JSESSIONID_KEY;
import static hamtom.me.futsalreservationview.call.vo.LoginCookie.WMONID_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class Login {
    private String apiHost;
    private String apiLoginUri;
    private String userId;
    private String userPasswd;

    public Login(String apiHost, String apiLoginUri, String userId, String userPasswd) {
        this.apiHost = apiHost;
        this.apiLoginUri = apiLoginUri;
        this.userId = userId;
        this.userPasswd = userPasswd;
    }

    public LoginCookie login(){
        log.info("================ Login Start ================");

        String url = makeUrl(apiHost, apiLoginUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  // Content-Type 헤더 설정
        headers.setAccept(Collections.singletonList(MediaType.ALL));  // Accept 헤더 설정
        headers.set("Accept-Encoding", "gzip, deflate, br");  // Accept-Encoding 헤더 설정
        headers.set("Connection", "keep-alive");  // Connection 헤더 설정

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("userPasswd", userPasswd);
        log.info("login ID: {}", userId);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = new RestTemplate().postForEntity(url, requestEntity, String.class);

        // 응답 확인
        HttpStatusCode responseCode = response.getStatusCode();
        String responseBody = Optional.ofNullable(response.getBody()).orElse("");

        log.info("responseCode: {}", responseCode);
        log.debug("responseBody: {}", responseBody);

        boolean loginStatus = responseBody.contains("이미 로그인이 되어 있습니다.") || responseBody.contains("The document has been moved.");

        if (!loginStatus) throw new RuntimeException("Login 실패");

        HttpHeaders responseHeaders = response.getHeaders();
        List<String> cookies = responseHeaders.get(HttpHeaders.SET_COOKIE);

        String wmonid = Optional.ofNullable(extractCookieValue(cookies, WMONID_KEY)).orElse("") ;
        String jsessionid = Optional.ofNullable(extractCookieValue(cookies, JSESSIONID_KEY)).orElse("");
        log.info("{}: {}",WMONID_KEY,  wmonid);
        log.info("{}: {}",JSESSIONID_KEY, jsessionid);

        LoginCookie loginCookie = new LoginCookie();
        if (wmonid.isEmpty() || jsessionid.isEmpty()) {
            loginCookie.setLoginStatus(false);
            throw new RuntimeException("No Cookies!");
        }else{
            loginCookie.setLoginStatus(true);
            loginCookie.setWMONID(wmonid);
            loginCookie.setJSESSIONID(jsessionid);
        }

        log.info("================  Login End  ================");
        log.info("");
        return loginCookie;
    }



    @SuppressWarnings("unused")
    private Map<String, String> makeParams(){
        String erntNo = "140327";
        String erntDay = "2";
        String erntDate = "2024-06-04";
        Map<String, String> params = new HashMap<>();
        params.put("erntNo", erntNo);
        params.put("erntDay", erntDay);
        params.put("erntDate", erntDate);
        return params;
    }
}
