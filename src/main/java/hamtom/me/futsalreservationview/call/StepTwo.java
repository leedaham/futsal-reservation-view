package hamtom.me.futsalreservationview.call;

import hamtom.me.futsalreservationview.call.vo.EachStepResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static hamtom.me.futsalreservationview.call.Common.makeUrl;
import static hamtom.me.futsalreservationview.call.StepTwo.StepTwoForm.*;
import static hamtom.me.futsalreservationview.call.StepTwo.StepTwoResponse.SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class StepTwo {
    private String apiHost;
    private String apiStepTwoUri;

    public StepTwo(String apiHost, String apiStepTwoUri) {
        this.apiHost = apiHost;
        this.apiStepTwoUri = apiStepTwoUri;
    }

    public EachStepResult stepOne(EachStepResult eachStepResult) {
        log.info("============== Step Two  Start ==============");
        String url = makeUrl(apiHost, apiStepTwoUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);  // Content-Type 헤더 설정
        headers.setAccept(Collections.singletonList(MediaType.ALL));  // Accept 헤더 설정
        headers.set("Accept-Encoding", "gzip, deflate, br");  // Accept-Encoding 헤더 설정
        headers.set("Connection", "keep-alive");  // Connection 헤더 설정

        String cookieForHeader = eachStepResult.getCookieForHeader();
        headers.add(HttpHeaders.COOKIE, cookieForHeader); //cookie 설정
        log.debug("cookieForHeader: {}", cookieForHeader);

        MultiValueMap<String, String> body = makeForm(eachStepResult.getErntResveNo(), eachStepResult.getErntApplcntNo());

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
        }

        log.info("=============== Step Two  END ===============");
        log.info("");
        return eachStepResult;
    }

    public MultiValueMap<String, String> makeForm(String erntResveNo, String erntApplcntNo) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("erntResveNo", erntResveNo);
        body.add("erntApplcntNo", erntApplcntNo);
        body.add("checkAgress", "on");
        body.add("expectNmpr", "12");
        body.add("usePurps", "소규모 축구장 이용");
        body.add("erntApplcntNm", "이다함");
        body.add("erntApplcntGrpNm", "");
        body.add("erntApplcntTelno", "");
        body.add("erntApplcntMbtlnum", "010-9901-3591");
        body.add("email1", "mydaham1004");
        body.add("email2", "gmail.com");
        body.add("erntApplcntEmail", "mydaham1004@gmail.com");
        body.add("erntApplcntZip", "07057");
        body.add("erntApplcntAdres", "서울특별시 동작구 여의대방로22나길 104-6");
        body.add("erntApplcntAdresDetail", "보라매하이파크");

        return body;
    }

}
