package hamtom.me.futsalreservationview;

import hamtom.me.futsalreservationview.call.Login;
import hamtom.me.futsalreservationview.call.StepOne;
import hamtom.me.futsalreservationview.call.vo.LoginCookie;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private String apiStepTreeUri;
    @Value("${login.userId}")
    private String userId;
    @Value("${login.userPasswd}")
    private String userPasswd;

    private Login login;
    private StepOne stepOne;

    @PostConstruct
    public void init() {
        login = new Login(apiHost, apiLoginUri, userId, userPasswd);
    }

    @Scheduled(fixedDelay = 100000)
    public void cron() {

        LoginCookie loginCookie = login.login();

    }


}
