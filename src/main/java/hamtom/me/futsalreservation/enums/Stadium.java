package hamtom.me.futsalreservation.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Getter
@Slf4j
public enum Stadium {
    // 1-1: 140105, 1-2: 140107, 2-1: 140326, 2-2: 140327
    ONE("1-1", "140105"),
    TWO("1-2", "140107"),
    THREE("2-1", "140326"),
    FOUR("2-2", "140327");

    private final String stadiumName;
    private final String stadiumNo;

    Stadium(String stadiumName, String stadiumNo) {
        this.stadiumName = stadiumName;
        this.stadiumNo = stadiumNo;
    }

    public static String getStadiumName(String stadiumNo){
        for(Stadium stadium : Stadium.values()){
            if(stadium.getStadiumNo().equals(stadiumNo)){
                return stadium.getStadiumName();
            }
        }
        log.info("No stadium found for stadiumNo={}", stadiumNo);
        return null;
    }
}
