package hamtom.me.futsalreservation.service;

import hamtom.me.futsalreservation.vo.vo.EachStepResult;
import hamtom.me.futsalreservation.vo.vo.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static hamtom.me.futsalreservation.service.Common.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StepThree {
    private String url;

    public void setUri(String apiHost, String apiStepThreeUri) {
        this.url = makeUrl(apiHost, apiStepThreeUri);
    }

    public EachStepResult executeStep(EachStepResult eachStepResult) {
        log.info("============== Step Three Start ==============");
        String cookie = eachStepResult.getCookie();

        //header, body
        HttpHeaders headers = makeHeader(MediaType.MULTIPART_FORM_DATA, cookie);
        MultiValueMap<String, String> body = makeForm(eachStepResult);

        //request > response
        ReservationResponse reservationResponse = requestReservation(url, body, headers);

        if (reservationResponse.isSuccess()) {
            eachStepResult.setResult(reservationResponse.getResult());
            eachStepResult.setMsg(reservationResponse.getMsg());
        }

        log.info("=============== Step Three END ===============");
        log.info("");
        return eachStepResult;
    }

    private MultiValueMap<String, String> makeForm(EachStepResult eachStepResult) {
        String stadiumNo = eachStepResult.getStadiumNo();
        String reservationNo = eachStepResult.getReservationNo();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(STADIUM_NO, stadiumNo);
        body.add(RESERVATION_NO, reservationNo);
        body.add("noneName", "");

        return body;
    }

}
