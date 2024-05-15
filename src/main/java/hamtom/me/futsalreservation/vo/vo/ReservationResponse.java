package hamtom.me.futsalreservation.vo.vo;

import lombok.Data;

@Data
public class ReservationResponse {
    private String result;
    private String msg;
    private String erntApplcntNo;

    public boolean isEmpty() {
        return result == null;
    }
    public boolean isSuccess() {
        return result.equalsIgnoreCase("S");
    }
}
