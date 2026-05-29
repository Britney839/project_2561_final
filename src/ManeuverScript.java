import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManeuverScript {
    // This java file will load and store the flight maneuver sequence in a CSV
    // file.

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
        List<Maneuver> maneuverList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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
                    if (!trimmed.toLowerCase().startsWith("seconds")) {
                        System.err.println("Warning: first data line does not look like a header.");
                    }
                    continue;
                }
                String[] fields = trimmed.split(",", -1);
                if (fields.length != 4) {
                    errors.add("Script error on line " + lineNumber + ": expected 4 fields but found " + fields.length);
                    continue;
                }

                int seconds = 0;
                double roll = 0, pitch = 0, yaw = 0;
                boolean rowValid = true;

                try {
                    seconds = Integer.parseInt(fields[0].trim());
                } catch (NumberFormatException e) {
                    errors.add("Script error on line " + lineNumber + " field 1 (\"seconds\"): \"" + fields[0].trim()
                            + "\" is not an integer");
                    rowValid = false;
                }

                try {
                    roll = Double.parseDouble(fields[1].trim());
                } catch (NumberFormatException e) {
                    errors.add("Script error on line " + lineNumber + " field 2 (\"roll\"): \"" + fields[1].trim()
                            + "\" is not a number");
                    rowValid = false;
                }

                try {
                    pitch = Double.parseDouble(fields[2].trim());
                } catch (NumberFormatException e) {
                    errors.add("Script error on line " + lineNumber + " field 3 (\"pitch\"): \"" + fields[2].trim()
                            + "\" is not a number");
                    rowValid = false;
                }

                try {
                    yaw = Double.parseDouble(fields[3].trim());
                } catch (NumberFormatException e) {
                    errors.add("Script error on line " + lineNumber + " field 4 (\"yaw\"): \"" + fields[3].trim()
                            + "\" is not a number");
                    rowValid = false;
                }

                if (rowValid) {
                    if (seconds < 1) {
                        errors.add("Script error on line " + lineNumber + " field 1 (\"seconds\"): " + seconds
                                + " is out of range [1, ∞)");
                        rowValid = false;
                    }
                    if (roll < -180 || roll > 180) {
                        errors.add("Script error on line " + lineNumber
                                + " field 2 (\"roll\"): " + roll + " is out of range [-180, 180]");
                        rowValid = false;
                    }
                    if (pitch < -90 || pitch > 90) {
                        errors.add("Script error on line " + lineNumber
                                + " field 3 (\"pitch\"): " + pitch + " is out of range [-90, 90]");
                        rowValid = false;
                    }
                    if (yaw < -180 || yaw > 180) {
                        errors.add("Script error on line " + lineNumber
                                + " field 4 (\"yaw\"): " + yaw + " is out of range [-180, 180]");
                        rowValid = false;
                    }
                }

                if (rowValid) {
                    maneuverList.add(new Maneuver(seconds, roll, pitch, yaw));
                }
            }
        }

        if (!errors.isEmpty()) {
            errors.forEach(System.err::println);
            throw new IllegalArgumentException("Script contains " + errors.size() + " error(s). See above for details.");
        }

        if (maneuverList.isEmpty()) {
            String msg = "Script error: file contains no valid maneuvers.";
            System.err.println(msg);
            throw new IllegalArgumentException(msg);
        }

        this.maneuvers = Collections.unmodifiableList(maneuverList);
        System.out.println("Successfully Loaded " + maneuvers.size() + " maneuvers from " + filePath);
    }

    public List<Maneuver> getManeuvers() {
        return maneuvers;
    }
}
