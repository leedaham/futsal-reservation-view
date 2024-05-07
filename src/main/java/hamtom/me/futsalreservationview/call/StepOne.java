package hamtom.me.futsalreservationview.call;

import hamtom.me.futsalreservationview.call.vo.LoginCookie;
import hamtom.me.futsalreservationview.call.vo.EachStepResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static hamtom.me.futsalreservationview.call.Common.*;

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

    public EachStepResult stepOne(LoginCookie loginCookie) {
        EachStepResult eachStepResult = new EachStepResult();

        log.info("============== Step One  Start ==============");
        String url = makeUrl(apiHost, apiStepOneUri);
        List<String> stadiumList = makeStadiumList();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);  // Content-Type 헤더 설정
        headers.setAccept(Collections.singletonList(MediaType.ALL));  // Accept 헤더 설정
        headers.set("Accept-Encoding", "gzip, deflate, br");  // Accept-Encoding 헤더 설정
        headers.set("Connection", "keep-alive");  // Connection 헤더 설정

        String cookieForHeader = loginCookie.makeCookieForHeader();
        headers.add(HttpHeaders.COOKIE, cookieForHeader); //cookie 설정
        eachStepResult.setCookieForHeader(cookieForHeader);
        log.debug("cookieForHeader: {}", cookieForHeader);

        List<String> gameDateTime = makeGameDateTime();

        MultiValueMap<String, String> body = makeForm();
        body.add("erntYmdh", gameDateTime.get(0));
        if (isTwoHoursRental) {
            body.add("erntYmdh", gameDateTime.get(1));
        }

        for (String stadium : stadiumList) {
            if (body.containsKey("erntResveNo")) body.remove("erntResveNo");
            body.add("erntResveNo", stadium);
            log.info("stadium: {}", stadium);

            // 요청 엔티티 구성
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            // 요청 보내기
            ResponseEntity<Map> response = new RestTemplate().postForEntity(url, requestEntity, Map.class);

            // 응답 확인
            HttpStatusCode responseCode = response.getStatusCode();
            Map<String, Object> responseBody = Optional.ofNullable(response.getBody()).orElse(new HashMap<>());

            log.info("responseCode: {}", responseCode);
            log.debug("responseBody: {}", responseBody);

            if (responseBody.size() == 0) {
                throw new RuntimeException("No Response!");
            }

            String result = (String) responseBody.get("result");
            String msg = (String) responseBody.get("msg");
            log.info("result: {}", result);
            log.info("msg: {}", msg);

            if (result.equalsIgnoreCase("S")) {
                String erntApplcntNo = (String) responseBody.get("erntApplcntNo");
                eachStepResult.setResult(result);
                eachStepResult.setMsg(msg);
                eachStepResult.setErntResveNo(stadium);
                eachStepResult.setErntApplcntNo(erntApplcntNo);
                break;
            }
        }

        log.info("=============== Step One  END ===============");
        log.info("");
        return eachStepResult;
    }

    private List<String> makeGameDateTime() {
        List<String> gameDateTimeList = new ArrayList<>();
        String gameDateTime1;
        gameDateTime1 = gameDate + gameTime1;
        boolean isValidGameDateTime1 = checkGameDateTime(gameDateTime1);
        if (isValidGameDateTime1) gameDateTimeList.add(gameDateTime1);
        else throw new RuntimeException(String.format("유효하지 않은 포맷입니다. >%s", gameDateTime1));

        if (isTwoHoursRental) {
            String gameDateTime2 = gameDate + gameTime2;
            boolean isValidGameDateTime2 = checkGameDateTime(gameDateTime2);
            if (isValidGameDateTime2) gameDateTimeList.add(gameDateTime2);
            else throw new RuntimeException(String.format("유효하지 않은 포맷입니다. >%s", gameDateTime2));
        }

        return gameDateTimeList;
    }

    private boolean checkGameDateTime(String gameDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        sdf.setLenient(false);
        try {
            // 지정된 형식으로 문자열을 파싱하여 날짜/시간 객체 생성
            Date parsedDate = sdf.parse(gameDateTime);

            // 현재 시간 가져오기
            Date currentDate = new Date();

            // 파싱된 날짜/시간이 현재 시간보다 빠른지 검사
            if (parsedDate.before(currentDate)) {
                log.info("{}는 유효한 형식이지만 현재 시간보다 빠릅니다. 현재 시간: {}", gameDateTime, currentDate);
                return false;
            }

            log.debug("{}는 유효합니다.", gameDateTime);
            return true;
        } catch (ParseException e) {
            log.error("{}는 유효하지 않은 형식입니다.", gameDateTime);
            return false;
        }
    }

    private List<String> makeStadiumList() {
        List<String> stadiumList = new ArrayList<>();
        stadiumList.add(gameStadium1);
        stadiumList.add(gameStadium2);
        stadiumList.add(gameStadium3);
        stadiumList.add(gameStadium4);

        for (String stadium : stadiumList) {
            boolean isNumeric = stadium.chars().allMatch(Character::isDigit);
            boolean isValidLength = stadium.length() == 6;
            if (isNumeric && isValidLength) log.info("{} 유효한 경기장 형식입니다.", stadium);
            else throw new RuntimeException(String.format("유효하지 않은 포맷입니다. >%s", stadium));
        }
        return stadiumList;
    }

    public MultiValueMap<String, String> makeForm() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("dscntChk", "N");
        body.add("dscntPt", "0");
        body.add("dscntAt", "N");
        body.add("dscntNm", "해당없음");
        body.add("noName", "");

        return body;
    }

}
