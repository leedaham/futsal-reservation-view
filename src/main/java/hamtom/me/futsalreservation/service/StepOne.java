package hamtom.me.futsalreservation.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import hamtom.me.futsalreservation.vo.vo.EachStepResult;
import hamtom.me.futsalreservation.vo.vo.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static hamtom.me.futsalreservation.service.Common.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StepOne {
    private String url;
    private List<String> stadiumList;
    private List<String> gameDateTime;
    private boolean isTwoHoursRental;

    public void setUri(String apiHost, String apiStepOneUri) {
        this.url = makeUrl(apiHost, apiStepOneUri);
    }
    public void setReservationValues(ReservationValues reservationValues) {
        this.stadiumList = reservationValues.getStadiumList();
        this.gameDateTime = reservationValues.getGameDateTime();
        this.isTwoHoursRental = reservationValues.isTwoHoursRental();
    }

    public EachStepResult executeStep(String cookie) {
        log.info("============== Step  One  Start ==============");

        EachStepResult eachStepResult = new EachStepResult();
        eachStepResult.setCookie(cookie);
        log.debug("cookieForHeader: {}", cookie);

        //Header, Body
        HttpHeaders headers = makeHeader(MediaType.MULTIPART_FORM_DATA, cookie);
        List<MultiValueMap<String, String>> formList = makeForm();

        for (MultiValueMap<String, String> form : formList) {
            // 요청 엔티티 구성
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);

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

        }

        for (String stadium : stadiumList) {





            JsonElement json = JsonParser.parseString(responseBody);
            Gson gson = new Gson();
            ReservationResponse reservationResponse = gson.fromJson(json, ReservationResponse.class);


            String result = reservationResponse.getResult();
            String msg = reservationResponse.getMsg();
            if (result.equalsIgnoreCase("S")) {
                String erntApplcntNo = reservationResponse.getErntApplcntNo();
                eachStepResult.setResult(result);
                eachStepResult.setMsg(msg);
                eachStepResult.setStadiumNo(stadium);
                eachStepResult.setReservationNo(erntApplcntNo);
                break;
            }
        }

        log.info("=============== Step  One  END ===============");
        log.info("");
        return eachStepResult;
    }

    private List<MultiValueMap<String, String>> makeForm() {
        List<MultiValueMap<String, String>> formList = new ArrayList<>();

        MultiValueMap<String, String> bodyBase = new LinkedMultiValueMap<>();
        bodyBase.add("erntYmdh", gameDateTime.get(0));
        if (isTwoHoursRental) {
            bodyBase.add("erntYmdh", gameDateTime.get(1));
        }
        bodyBase.add("dscntChk", "N");
        bodyBase.add("dscntPt", "0");
        bodyBase.add("dscntAt", "N");
        bodyBase.add("dscntNm", "해당없음");
        bodyBase.add("noName", "");
        for (String stadium : stadiumList) {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.addAll(bodyBase);
            body.add("erntResveNo", stadium);
            formList.add(body);
        }

        return formList;
    }

}
