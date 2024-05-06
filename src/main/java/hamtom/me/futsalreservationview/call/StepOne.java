package hamtom.me.futsalreservationview.call;

import hamtom.me.futsalreservationview.call.vo.LoginCookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static hamtom.me.futsalreservationview.call.Common.*;
import static hamtom.me.futsalreservationview.call.StepOne.StepOneForm.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StepOne {
    private String apiHost;
    private String apiStepOneUri;

    @Value("${game.date}")
    private String gameDate;
    @Value("${game.isTwoHoursRental}")
    private boolean isTwoHoursRental;

    @Value("${game.time1}")
    private String gameTime1;
    @Value("${game.time2}")
    private String gameTime2;
    @Value("${game.stadium1}")
    private String gameStadium1;
    @Value("${game.stadium2}")
    private String gameStadium2;
    @Value("${game.stadium3}")
    private String gameStadium3;
    @Value("${game.stadium4}")
    private String gameStadium4;

    public StepOne(String apiHost, String apiStepOneUri) {
        this.apiHost = apiHost;
        this.apiStepOneUri = apiStepOneUri;
    }

    public void stepOne(LoginCookie loginCookie) {
        log.info("================ Login Start ================");
        log.info("============== Step One  Start ==============");

        String url = makeUrl(apiHost, apiStepOneUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);  // Content-Type 헤더 설정
        headers.setAccept(Collections.singletonList(MediaType.ALL));  // Accept 헤더 설정
        headers.set("Accept-Encoding", "gzip, deflate, br");  // Accept-Encoding 헤더 설정
        headers.set("Connection", "keep-alive");  // Connection 헤더 설정

        String cookieForHeader = loginCookie.makeCookieForHeader();
        headers.add(HttpHeaders.COOKIE, cookieForHeader); //cookie 설정
        log.debug("cookieForHeader: {}", cookieForHeader);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(erntResveNo_KEY, "");
        body.add(erntYmdh_KEY, "");
        body.add(dscntChk_KEY, dscntChk_DEFAULT_VALUE);
        body.add(dscntPt_KEY, dscntPt_DEFAULT_VALUE);
        body.add(dscntAt_KEY, dscntAt_DEFAULT_VALUE);
        body.add(dscntNm_KEY, dscntNm_DEFAULT_VALUE);
        body.add(noName_KEY, noName_DEFAULT_VALUE);


        // 요청 엔티티 구성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 요청 보내기
        ResponseEntity<String> response = new RestTemplate().postForEntity(url, requestEntity, String.class);
    }

    static class StepOneForm {
        public static final String erntResveNo_KEY = "erntResveNo";
        public static final String erntYmdh_KEY = "erntYmdh";
        public static final String dscntChk_KEY = "dscntChk";
        public static final String dscntPt_KEY = "dscntPt";
        public static final String dscntAt_KEY = "dscntPt";
        public static final String dscntNm_KEY = "dscntPt";
        public static final String noName_KEY = "noName";
        public static final String dscntChk_DEFAULT_VALUE = "N";
        public static final String dscntPt_DEFAULT_VALUE = "0";
        public static final String dscntAt_DEFAULT_VALUE = "N";
        public static final String dscntNm_DEFAULT_VALUE = "해당없음";
        public static final String noName_DEFAULT_VALUE = "";
    }

    static class StepOneResponse {
        public static final String SUCCESS ="S";
        public static final String FAIL ="F";

    }
}
