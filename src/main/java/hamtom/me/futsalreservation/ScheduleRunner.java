package hamtom.me.futsalreservation;

import hamtom.me.futsalreservation.service.*;
import hamtom.me.futsalreservation.vo.vo.EachStepResult;
import hamtom.me.futsalreservation.vo.vo.LoginCookie;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleRunner {

    @Value("${api.host}")
    private String apiHost;
    @Value("${api.uri.login}")
    private String apiLoginUri;
    @Value("${api.uri.step1}")
    private String apiStepOneUri;
    @Value("${api.uri.step2}")
    private String apiStepTwoUri;
    @Value("${api.uri.step3}")
    private String apiStepThreeUri;
    @Value("${login.userId}")
    private String userId;
    @Value("${login.userPasswd}")
    private String userPasswd;

    private final ReservationValues reservationValues;
    private final Login login;
    private final StepOne stepOne;
    private final StepTwo stepTwo;
    private final StepThree stepThree;

    @PostConstruct
    public void init() {
        login.setValues(apiHost, apiLoginUri, userId, userPasswd);
        stepOne.setUri(apiHost, apiStepOneUri);
        stepOne.setReservationValues(reservationValues);
        stepTwo.setUri(apiHost, apiStepTwoUri);
        stepThree.setUri(apiHost, apiStepThreeUri);
    }

    //요일 (day of week): 0~6 (0은 일요일, 6은 토요일을 의미함)
    //토요일 실행시 화요일 예약, 금요일 실행시 월요일 예약
    @Scheduled(fixedDelay = 100000)
//    @Scheduled(cron = "56 59 23 ? * 6")
    public void cron() {
        LoginCookie loginCookie = login.executeLogin();
        log.info("* [RESULT]     Login: {}", loginCookie.toString());
        if(!loginCookie.isLoginStatus()) return;

        String cookie = loginCookie.makeCookieForHeader();

        EachStepResult eachStepResult = stepOne.executeStep(cookie);
        log.info("* [RESULT]   StepOne: {}", eachStepResult.toString());
        if(!eachStepResult.isResultExist()) return;

        eachStepResult = stepTwo.executeStep(eachStepResult);
        log.info("* [RESULT]   StepTwo: {}", eachStepResult.toString());
        if(!eachStepResult.isResultExist()) return;

        eachStepResult = stepThree.executeStep(eachStepResult);
        log.info("* [RESULT] StepThree: {}", eachStepResult.toString());

    }




}