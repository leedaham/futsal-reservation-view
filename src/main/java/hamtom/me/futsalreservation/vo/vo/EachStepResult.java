package hamtom.me.futsalreservation.vo.vo;

import lombok.Data;

@Data
public class EachStepResult {
    private String cookie;
    private String result;
    private String msg;
    private String stadiumNo;
    private String reservationNo;

    public boolean isResultExist() {
        if (result == null) {
            return false;
        }

        return result.equalsIgnoreCase("S") || result.equalsIgnoreCase("F");
    }
}
