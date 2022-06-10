package main.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import main.data.Database;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.prefs.Preferences;

/**
 * Picks a working directory to use for the database.
 * Progresses to log in screen when a valid directory is received.
 * @author Gabriel Bryan
 */
public class WorkDirectoryChooser {

    /** Constants */
    private static final double WIDTH = 600;
    private static final double HEIGHT = 250;
    private static final double FONT_SIZE = 14;

    private final LoginScreen myLoginView;
    private final GUIController myController;

    /** The stage the application takes place on */
    private final Stage myStage;

    /** Text elements on the scene */
    private final Text myErrorDisplay;
    private final Text myPrompt;

    /** Text field for entering the working directory */
    private final TextField directoryInput;
    private File myFile;

    /** Button elements */
    private final Button confirmButton;
    private final Button selectButton;

    /** Panes used to construct the view */
    BorderPane mainPane;
    GridPane contentPane;

    /**
     * Sets up all the GUI elements but does not build the panes yet.
     */
    public WorkDirectoryChooser(LoginScreen theLogin, GUIController theController, Stage theStage){
        //Initialize the field variables passed as parameters
        myLoginView = theLogin;
        myStage = theStage;
        myController = theController;

        //Sets up the size of the stage
        myStage.setHeight(HEIGHT);
        myStage.setWidth(WIDTH);

        //Initialize the text
        myErrorDisplay = new Text();
        myErrorDisplay.setFill(Color.RED);
        myPrompt = new Text("                  Choose a working directory: ");
        myPrompt.getStyleClass().add("white-text");

        //Initialize the text field
        directoryInput = new TextField();
        directoryInput.getStyleClass().add("custom-text-entry");
        final double FIELD_WIDTH = 300;
        final double FIELD_HEIGHT = 30;
        directoryInput.setPrefSize(FIELD_WIDTH, FIELD_HEIGHT);

        //Initialize the button
        confirmButton = new Button("Confirm");
        confirmButton.setMinSize(FIELD_WIDTH, FIELD_HEIGHT);
        confirmButton.getStyleClass().add("squircle-button");
        selectButton = new Button("Select");
        selectButton.getStyleClass().add("squircle-button");

        setFontSizes();
    }

    /**
     * Builds the panes and the button actions.
     * Used to set the layout for the application.
     */
    public Pane setUpGUI(){
        //Initialize the panes;
        mainPane = new BorderPane();
        contentPane = new GridPane();

        //Creates the toolbar
        mainPane.setTop(myController.createToolBar());

        //Checks for a stored directory
        directoryInput.setText(checkSavedDirectory());

        //Set up button actions
        selectButton.setOnAction(clickEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            myFile = directoryChooser.showDialog(myStage);
            if(myFile != null) {
                directoryInput.setText(myFile.getPath());
                myErrorDisplay.setText("");
            }
        });
        //When the confirm button is clicked, we check to see
        //if the directory is valid or display an error message.
        confirmButton.setOnAction(clickEvent -> {
            if(verifyDirectory()){
                File jsonFile = new File(myFile.getPath() + "/database.json");
                System.out.println(jsonFile.getPath());
                if(jsonFile.exists()) {
                    try {
                        FileReader jsonReader = new FileReader(jsonFile);
                        JSONTokener tokener = new JSONTokener(jsonReader);
                        System.out.println("Found JSON");
                        JSONObject theJSON = new JSONObject(tokener);
                        Database.db = new Database(theJSON);
                        saveDirectory();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else{
                    Database.db = new Database(myFile.getPath());
                }
                promptLogin();
            } else{
                myErrorDisplay.setText("Error, invalid directory. Please try again.");
            }
        });

        //Reset the error message when the text field is clicked on
        directoryInput.setOnMouseClicked(clickEvent -> myErrorDisplay.setText(""));

        //Sets up and adds everything to the content pane
        contentPane.setAlignment(Pos.CENTER);
        contentPane.add(myPrompt, 0, 0);
        contentPane.add(directoryInput, 0, 1);
        contentPane.add(selectButton, 1, 1);
        contentPane.add(confirmButton, 0, 2);
        contentPane.add(myErrorDisplay, 0, 3);
        contentPane.setVgap(10);
        contentPane.setHgap(5);

        mainPane.setCenter(contentPane);

        return mainPane;
    }

    /**
     * Creates a new scene that uses the login gui and switches the
     * stage over to such scene.
     */
    private void promptLogin(){
        Scene loginScene = new Scene(myLoginView.loginGUI());
        loginScene.getStylesheets().add("/StyleSheet.css");
        myStage.setScene(loginScene);
    }

    /**
     * Sets the fonts for all elements involving text.
     */
    private void setFontSizes(){
        Font theFont = new Font(FONT_SIZE);

        directoryInput.setFont(theFont);
        selectButton.setFont(theFont);
        myPrompt.setFont(theFont);
        myErrorDisplay.setFont(theFont);

        confirmButton.setFont(theFont);
        selectButton.setFont(theFont);
    }

    /**
     * @return true if the file exists and is a directory
     */
    private boolean verifyDirectory() {
        myFile = new File(directoryInput.getText());
        return myFile.exists() && myFile.isDirectory();
    }

    /**
     * Checks to see if there is a working directory
     * @return the file path to the directory
     */
    private String checkSavedDirectory() {
        /*try {
            FileReader dirReader = new FileReader(getDir());
            BufferedReader buffReader = new BufferedReader(dirReader);
            String directory = buffReader.readLine();
            if(directory == null) return "";
            buffReader.close();
            dirReader.close();
            File dirFile;
            dirFile = new File(directory);
            return dirFile.exists() ? directory : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }*/
        Preferences prefs = Preferences.userRoot();
        return prefs.get("filepath", "");
    }

    /**
     * Saves the file path to the filepath.txt
     */
    private void saveDirectory() {
        /*try {
            FileWriter dirWriter = new FileWriter(getDir());
            dirWriter.write(directoryInput.getText());
            dirWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }*/
        Preferences prefs = Preferences.userRoot();
        prefs.put("filepath", directoryInput.getText());

    }

    private String getDir(){
        return Objects.requireNonNull(getClass().getClassLoader().getResource("filepath.txt")).getPath();
    }
}
