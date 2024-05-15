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
        List<MultiValueMap<String, String>> bodyList = makeForm();


        ReservationResponse reservationResponse = new ReservationResponse();
        String stadium = "";
        for (MultiValueMap<String, String> body : bodyList) {
            stadium = body.getFirst(STADIUM_NO);
            reservationResponse = requestReservation(url, body, headers);

            if (reservationResponse.isSuccess()) {
                break;
            }
        }

        if(reservationResponse.isSuccess()){
            eachStepResult.setResult(reservationResponse.getResult());
            eachStepResult.setMsg(reservationResponse.getMsg());
            eachStepResult.setStadiumNo(stadium);
            eachStepResult.setReservationNo(reservationResponse.getErntApplcntNo());
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
            body.add(STADIUM_NO, stadium);
            formList.add(body);
        }

        return formList;
    }

}
