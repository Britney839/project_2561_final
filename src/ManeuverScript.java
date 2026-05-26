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
    private final List<Maneuver> maneuvers;

    public ManeuverScript(String filePath) throws IOException {
        this.maneuvers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            int lineNumber = 0;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] fields = trimmed.split(",", -1);
                if (fields.length != 4) {
                    String msg = "Invalid number of fields at line " + lineNumber + ": expected 4 but got " + fields.length;
                    System.err.println(msg);
                    throw new IllegalArgumentException(msg);
                }

                int seconds = parseIntField(lineNumber, 1, "seconds", fields[0].trim());
                double roll = parseDoubleField(lineNumber, 2, "roll", fields[1].trim());
                double pitch = parseDoubleField(lineNumber, 3, "pitch", fields[2].trim());
                double yaw = parseDoubleField(lineNumber, 4, "yaw", fields[3].trim());

                this.maneuvers.add(new Maneuver(seconds, roll, pitch, yaw));
            }
        }

        if (this.maneuvers.isEmpty()) {
            String msg = "Script error: file contains no valid maneuvers.";
            System.err.println(msg);
            throw new IllegalArgumentException(msg);
        }

        this.maneuvers = Collections.unmodifiableList(this.maneuvers);
        System.out.println("Successfully Loaded " + this.maneuvers.size() + " maneuvers from " + filePath);
    }
}
 

