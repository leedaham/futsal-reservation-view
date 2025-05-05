package hamtom.me.futsalreservation.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationValues {


    @Value("${game.date.auto.enabled}")
    private boolean isDateAuto;
    @Value("${game.date.auto.day-of-week}")
    private int autoDayOfWeek;
    @Value("${game.date.manual.set-value}")
    private String manualSetGameDate;

    @Value("${game.isTwoHoursRental}") @Getter
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

    @Value("${privacy-info.name}") @Getter
    private String name;
    @Value("${privacy-info.phone}") @Getter
    private String phone;
    @Value("${privacy-info.email}") @Getter
    private String email;
    @Value("${privacy-info.address1}") @Getter
    private String address1;
    @Value("${privacy-info.address2}") @Getter
    private String address2;
    @Value("${privacy-info.zip}") @Getter
    private String zip;

    @Getter
    private List<String> gameDateTime;
    @Getter
    private List<String> stadiumList;

    @PostConstruct
    public void init() {
        setGameDateTime();
        setStadiumList();
    }

    private void setGameDateTime() {
        List<String> gameDateTimeList = new ArrayList<>();
        String gameDateTime1;

        String setGameDate = manualSetGameDate;
        if (isDateAuto) {
            LocalDateTime plusDays = LocalDateTime.now().plusDays(31);

            DayOfWeek dayOfWeek = plusDays.getDayOfWeek();
            DayOfWeek expectedDayOfWeek = DayOfWeek.of(autoDayOfWeek);
            if (dayOfWeek != expectedDayOfWeek) {
                LocalDateTime previousTuesday = plusDays.with(TemporalAdjusters.previous(expectedDayOfWeek));
                LocalDateTime nextTuesday = plusDays.with(TemporalAdjusters.next(expectedDayOfWeek));

                // 두 화요일 간의 거리 계산
                long daysToPreviousTuesday = plusDays.until(previousTuesday, ChronoUnit.DAYS);
                long daysToNextTuesday = plusDays.until(nextTuesday, ChronoUnit.DAYS);

                // 가장 가까운 화요일 반환
                LocalDateTime localDateTime = (daysToPreviousTuesday >= daysToNextTuesday) ? previousTuesday : nextTuesday;
                plusDays = localDateTime;
            }

            String autoDate = plusDays.format(DateTimeFormatter.ofPattern("yyyyMMdd"));


            setGameDate = autoDate;
        }

        gameDateTime1 = setGameDate + gameTime1;
        boolean isValidGameDateTime1 = checkGameDateTime(gameDateTime1);
        if (isValidGameDateTime1) gameDateTimeList.add(gameDateTime1);
        else throw new RuntimeException(String.format("유효하지 않은 포맷입니다. >%s", gameDateTime1));

        if (isTwoHoursRental) {
            String gameDateTime2 = setGameDate + gameTime2;
            boolean isValidGameDateTime2 = checkGameDateTime(gameDateTime2);
            if (isValidGameDateTime2) gameDateTimeList.add(gameDateTime2);
            else throw new RuntimeException(String.format("유효하지 않은 포맷입니다. >%s", gameDateTime2));
        }

        this.gameDateTime = gameDateTimeList;
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

    private void setStadiumList() {
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
        this.stadiumList = stadiumList;
    }

}
