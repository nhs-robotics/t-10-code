//package intothedeep.auto;
//import com.pocolifo.robobase.bootstrap.AutonomousOpMode;
//
//import javax.json.*;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//
//public class JSONAuto extends AutonomousOpMode {
//    final String baseJSONFilePath = "TeamCode/src/main/java/centerstage/auto/";
//    private final JsonArray jsonArray;
//
//    public JSONAuto(String uniqueJSONFilePath) throws FileNotFoundException {
//        String completeJSONFilePath = this.baseJSONFilePath + uniqueJSONFilePath;
//        this.jsonArray = Json.createReader(new FileReader(completeJSONFilePath)).readArray();
//    }
//
//    @Override
//    public void initialize() {
//
//    }
//
//    @Override
//    public void run() {
//        for (int i = 0; i < this.jsonArray.size(); i++) {
//            runJSONCommand(this.jsonArray.getJsonObject(i));
//        }
//    }
//
//    private void runJSONCommand(JsonObject jsonCommand) {
//        switch (jsonCommand.getString("Action Type")) {
//            case "Lateral Movement":
//
//                break;
//            case "Rotational Movement":
//                break;
//            case "Manipulator Movement":
//                break;
//            case "Sleep":
//                try {
//                    sleep(jsonCommand.getInt("Time"));
//                } catch (InterruptedException ignored) {}
//                break;
//        }
//    }
//}
