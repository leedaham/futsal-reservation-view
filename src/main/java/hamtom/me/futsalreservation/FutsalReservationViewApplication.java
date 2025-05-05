package hamtom.me.futsalreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
class FutsalReservationViewApplication {

	public static void main(String[] args) {
		SpringApplication.run(FutsalReservationViewApplication.class, args);
	}


}
