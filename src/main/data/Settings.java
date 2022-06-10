/*
 *
 */
package main.data;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

/**
 * This class prevents the version number to be hard coded into
 * the GUI and version number can be adjusted easily in this class.
 *
 * @author Paul Lee, Gabriel Bryan
 * @version Spring 2022
 */
public class Settings {

    /** The name of our team name */
    private static final String TEAM_NAME = "MacroSoft";

    /**
     * Version number of the application.
     */
    private static final double VERSION_NUMBER = 1.1;

    private static final String[] collaborators = {
            "Gabe Bryan - gabeb544@uw.edu",
            "Anteh Hsu - gan86650@uw.edu",
            "Wei Wei Chien - weiwei88@uw.edu",
            "Alex Larsen - alexlars@uw.edu",
            "Paul Lee - plee83@uw.edu"
    };

    /**
     * Gets the version number.
     *
     * @return the version number.
     */
    public static double getVersionNumber() {
        return VERSION_NUMBER;
    }
    public static String getTeamName() { return TEAM_NAME; }

    public static String printAbout(){
        StringBuilder content = new StringBuilder();

        //prints off the user
        if(User.activeUser != null) {
            content.append("This app is registered to:\n" + User.activeUser.toString());
        }else{
            content.append("Error: No User");
        }

        //Prints off our team name
        content.append("\nThis app is provided by " + getTeamName());

        //Print collaborators
        content.append("\nAuthors: ");
        for(int i = 0; i < collaborators.length; i++){
            content.append("\n");
            content.append(collaborators[i]);
        }

        //Prints of the version
        content.append("\nVersion " + getVersionNumber());
        return content.toString();
    }

    public static void saveSettings() throws FileNotFoundException {
        Stage aStage = new Stage();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files (*.CSV)", "*.csv");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);

        try {
            File theFile = fileChooser.showSaveDialog(aStage);

            PrintWriter writer = new PrintWriter(theFile);
            writer.println(User.activeUser.getUserName() + "," + User.activeUser.getPassword());
            writer.close();
        }catch (NullPointerException e) {
            System.out.println("No File Selected");
        }

    }

    public static void loadSettings() throws Exception {
        Stage aStage = new Stage();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files (*.CSV)", "*.csv");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        try {
            File theFile = fileChooser.showOpenDialog(aStage);

            FileReader fileReader = new FileReader(theFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String dataRow = reader.readLine();
            String[] data = dataRow.split(",");

            User.activeUser = new User(data[0], data[1]);
        } catch (NullPointerException e){
            System.out.println("No File Selected");
        }
    }
}
