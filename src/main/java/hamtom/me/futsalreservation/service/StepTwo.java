package hamtom.me.futsalreservation.service;

import hamtom.me.futsalreservation.vo.EachStepResult;
import hamtom.me.futsalreservation.vo.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static hamtom.me.futsalreservation.service.Common.*;
import static hamtom.me.futsalreservation.service.Common.makeUrl;

@Slf4j
@Service
@RequiredArgsConstructor
public class StepTwo {
    private String url;
    private String name;
    private String phone;
    private String email;
    private String address1;
    private String address2;
    private String zip;

    public void setUri(String apiHost, String apiStepTwoUri) {
        this.url = makeUrl(apiHost, apiStepTwoUri);
    }

    public void setPrivacyValues(ReservationValues reservationValues) {
        this.name = reservationValues.getName();
        this.phone = reservationValues.getPhone();
        this.email = reservationValues.getEmail();
        this.address1 = reservationValues.getAddress1();
        this.address2 = reservationValues.getAddress2();
        this.zip = reservationValues.getZip();
    }

    public EachStepResult executeStep(EachStepResult eachStepResult) {
        log.info("============== Step  Two  Start ==============");

        //headers, body
        HttpHeaders headers = makeHeader(MediaType.MULTIPART_FORM_DATA, eachStepResult.getCookie());
        MultiValueMap<String, String> body = makeForm(eachStepResult);

        ReservationResponse reservationResponse = requestReservation(url, body, headers);

        if (reservationResponse.isSuccess()) {
            eachStepResult.setResult(reservationResponse.getResult());
            eachStepResult.setMsg(reservationResponse.getMsg());
        }

        log.info("=============== Step  Two  END ===============");
        log.info("");
        return eachStepResult;
    }

    private MultiValueMap<String, String> makeForm(EachStepResult eachStepResult) {
        String stadiumNo = eachStepResult.getStadiumNo();
        String reservationNo = eachStepResult.getReservationNo();

        String[] emails = email.split("@");
        String id = emails[0];
        String domain = emails[1];

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(STADIUM_NO, stadiumNo);
        body.add(RESERVATION_NO, reservationNo);
        body.add("checkAgress", "on");
        body.add("expectNmpr", "12");
        body.add("usePurps", "소규모 축구장 이용");
        body.add("erntApplcntNm", name);
        body.add("erntApplcntGrpNm", "");
        body.add("erntApplcntTelno", "");
        body.add("erntApplcntMbtlnum", phone);
        body.add("email1", id);
        body.add("email2", domain);
        body.add("erntApplcntEmail", email);
        body.add("erntApplcntZip", zip);
        body.add("erntApplcntAdres", address1);
        body.add("erntApplcntAdresDetail", address2);

        return body;
    }

}
