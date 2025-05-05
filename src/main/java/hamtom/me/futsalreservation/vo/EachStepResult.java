package hamtom.me.futsalreservation.vo;

import hamtom.me.futsalreservation.enums.Stadium;
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

    public boolean isSuccess() {
        if (result == null) {
            return false;
        }

        return result.equalsIgnoreCase("S");
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public String finishReport(){
        String stadiumName = Stadium.getStadiumName(this.stadiumNo);
        return (isSuccess() && stadiumName !=null) ? stadiumName + " 구장 예약 성공" : "구장 예약 실패";
    }
}
