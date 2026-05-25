import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManeuverScript {
    // This java file will load and store the flight maneuver sequence in a CSV file.

    public static class Maneuver {
        public final int seconds;
        public final double roll;
        public final double pitch;
        public final double yaw;

        public Maneuver(int seconds, double roll, double pitch, double yaw) {
            this.seconds = seconds;
            this.roll = roll;
            this.pitch = pitch;
            this.yaw = yaw;
        }

        @Override
        public String toString() {
            return String.format("Maneuver(seconds=%d, roll=%.2f, pitch=%.2f, yaw=%.2f)", seconds, roll, pitch, yaw);
        }
    }

}

    

