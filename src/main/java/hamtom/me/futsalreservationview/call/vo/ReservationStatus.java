package hamtom.me.futsalreservationview.call.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ReservationStatus {
    private String erntNo;
    private String erntDay;
    private String erntDayNm;
    private List<EachTimeStatus> erntTimeList;

    @Getter @Setter
    public class EachTimeStatus {
        private String erntTime;
        private String erntTimeNxt;
        private String erntTimeAt;
    }
}
