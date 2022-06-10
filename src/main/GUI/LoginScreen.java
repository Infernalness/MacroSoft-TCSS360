/*
 *
 */
package main.GUI;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import main.data.User;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * A class to make the log in GUI and checks the
 * user inputted credentials before allowing them
 * into the program.
 *
 * @author Paul Lee, Gabriel Bryan
 * @version Spring 2022
 */
public class LoginScreen {

    /** Dimensions of stage */
    private final double WIDTH = 500;
    private final double HEIGHT = 500;

    /** Layout for the application as a whole */
    private final BorderPane borderPane;

    /** Layout used for the application window */
    private final GridPane gridPane;

    /** Pane for displaying all elements in descending order (vertical)*/
    private final FlowPane viewPane;

    private final Text header;
    /** Text to indicate to the user to input username in the text field */
    private final Text username;
    /** Text to indicate to the user to input password in the text field */
    private final Text password;
    /** Text to indicate to the user to input password a second time */
    private final Text verifyPassword;
    /** Text to display errors when signing in*/
    private final Text errorMsg;
    /** Textfield for the user to input their username */
    private final TextField inputUser;
    /** Textfield for the user to input their password */
    private final PasswordField inputPass;
    /** User inputs their password a second time to verify on creation. */
    private final PasswordField inputPassVerify;

    /** Button to submit both textfield to login */
    private final Button submitButton;
    /** Button to quit the program */
    private final Button quitButton;
    /** Button to create a new user */
    private final Button createUser;

    /** The GUI Controller that the app returns too after finishing the login procedure */
    private final GUIController guiControl;

    private final Stage myStage;
    /**
     * Constructor that initializes the instance field.
     */
    public LoginScreen(GUIController theController, Stage stage) {
        //Initializes the field variables passed as parameters
        guiControl = theController;
        myStage = stage;

        //Initializes the panes for the application
        gridPane = new GridPane();
        borderPane = new BorderPane();
        viewPane = new FlowPane(Orientation.VERTICAL);

        //Initializes the text prompts
        username = new Text("Username: ");
        username.getStyleClass().add("white-text");
        password = new Text("Password: ");
        password.getStyleClass().add("white-text");
        verifyPassword = new Text("Verify Pass: ");
        verifyPassword.getStyleClass().add("white-text");
        header = new Text("MacroSoft Presents: Item Manager");
        header.getStyleClass().add("white-text");
        errorMsg = new Text("Invalid username or password...");
        errorMsg.setFill(Color.RED);

        //Initializes the text field boxes
        inputUser = new TextField();
        inputUser.getStyleClass().add("custom-text-entry");
        inputPass = new PasswordField();
        inputPass.getStyleClass().add("custom-text-entry");
        inputPassVerify = new PasswordField();
        inputPassVerify.getStyleClass().add("custom-text-entry");

        //Initialize buttons
        submitButton = new Button("Submit");
        submitButton.getStyleClass().add("squircle-button");
        quitButton = new Button("Quit");
        quitButton.getStyleClass().add("squircle-button");
        createUser = new Button("Create New User");
        createUser.getStyleClass().add("squircle-button");

        setFontSize(16, 24);
    }

    /**
     * Creates the log in screen of the GUI.
     *
     * @return the log in GUI.
     */
    public Pane loginGUI() {
        errorMsg.setVisible(false);

        myStage.setHeight(HEIGHT);
        myStage.setWidth(WIDTH);

        //Sets the size, and spacing of the login page.
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        viewPane.setVgap(15);

        gridPane.setAlignment(Pos.CENTER);

        buildLoginView();

        createUser.setOnAction(clickEvent -> buildSignUpView());

        //Adds the elements to the border pane.
        errorMsg.setTextAlignment(TextAlignment.CENTER);
        borderPane.setTop(guiControl.createToolBar());
        borderPane.setCenter(viewPane);

        //code for resetting the error message when new input is received
        inputPass.setOnAction(clickEvent -> resetErrorMsg());
        inputPass.setOnAction(clickEvent -> resetErrorMsg());

        return borderPane;
    }

    /**
     * Checks the entries typed in the textfield of the username
     * and password to see if they are correct. If correct, the application
     * will send them to the main page of the application.
     */
    public void accountInput() {

        // Everytime the button is pressed, it checks the
        // text string in the text field.
        submitButton.setOnAction(actionEvent -> {
            String checkUser = inputUser.getText();
            String checkPass = inputPass.getText();
            System.out.println(checkUser + " " + checkPass);

            /* Username and password checker and if correct
            changes the scene to the main page. (case-sensitive).
            return boolean is not necessary, but could be useful.
            If it is not useful, we can easily change it to void and delete the loginComplete. */
            File jsonFile = new File(User.getFileDirectory(checkUser));
            if(jsonFile.exists() && jsonFile.isFile()){
                System.out.println("User JSON found");
                try {
                    FileReader jsonReader = new FileReader(jsonFile);
                    JSONTokener tokener = new JSONTokener(jsonReader);
                    JSONObject theJSON = new JSONObject(tokener);
                    User selectedUser = new User(theJSON);
                    if(selectedUser.getPassword().equals(checkPass)){
                        //LOGIN SUCCESS
                        User.activeUser = selectedUser;
                        guiControl.buildMainView();
                    } else{
                        displayError("Invalid username or password...");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else{
                displayError("Invalid username or password...");
            }

        });

        //Exit application on quit press
        quitButton.setOnAction(e -> System.exit(0));
    }

    /**
     * Sets the submit button to verify info for creating a user
     * checks the password and to make sure username isn't taken
     */
    public void signUp() {
        submitButton.setOnAction(clickEvent -> {
            if(inputPassVerify.getText().equals(inputPass.getText()) && inputUser.getText().length() > 0){
                File newUserFile = new File(User.getFileDirectory(inputUser.getText()));
                if(newUserFile.exists()) {
                    displayError("User name already taken...");
                } else {
                    //Creates the user from the data
                    new User(inputUser.getText(), inputPass.getText());
                    displaySuccess("New user created!");
                }
            } else if(inputUser.getText().length() <= 0) {
                displayError("Please input a username...");
            }
            else{
                displayError("Password doesn't match...");
            }
        });

        quitButton.setOnAction(clickEvent -> buildLoginView());
    }

    /**
     * Creates the view for logging into an existing user
     */
    private void buildLoginView(){
        clearFields();
        //Adds all elements to the grid pane.
        gridPane.getChildren().clear();
        gridPane.add(username, 0, 0);
        gridPane.add(inputUser, 1, 0);
        gridPane.add(password, 0, 1);
        gridPane.add(inputPass, 1, 1);
        gridPane.add(submitButton, 0, 2);
        gridPane.add(quitButton, 1, 2);

        //Builds the view pane
        viewPane.getChildren().clear();
        viewPane.setAlignment(Pos.CENTER);
        viewPane.getChildren().add(header);
        viewPane.getChildren().add(new Separator());
        viewPane.getChildren().add(gridPane);
        viewPane.getChildren().add(createUser);
        viewPane.getChildren().add(errorMsg);

        quitButton.setText("Quit");

        //Generates the submit button as well as login verification.
        accountInput();
    }

    /**
     * Builds the view for signing up as a new user
     */
    private void buildSignUpView(){
        clearFields();
        //Adds all elements to the grid pane.
        gridPane.getChildren().clear();
        gridPane.add(username, 0, 0);
        gridPane.add(inputUser, 1, 0);
        gridPane.add(password, 0, 1);
        gridPane.add(inputPass, 1, 1);
        gridPane.add(verifyPassword, 0, 2);
        gridPane.add(inputPassVerify, 1, 2);
        gridPane.add(submitButton, 0, 3);
        gridPane.add(quitButton, 1, 3);

        viewPane.getChildren().clear();
        viewPane.setAlignment(Pos.CENTER);
        viewPane.getChildren().add(header);
        viewPane.getChildren().add(new Separator());
        viewPane.getChildren().add(gridPane);
        viewPane.getChildren().add(errorMsg);

        quitButton.setText("Back");

        //Generates the submit button and sign up verification
        signUp();
    }

    private void resetErrorMsg(){
        errorMsg.setVisible(false);
    }

    /**
     * Displays an error message
     * @param message the message being displayed
     */
    private void displayError(String message){
        errorMsg.setVisible(true);
        errorMsg.setFill(Color.RED);
        errorMsg.setText(message);
    }

    /**
     * Displays a success message
     * @param message the message being displayed
     */
    private void displaySuccess(String message){
        errorMsg.setVisible(true);
        errorMsg.setFill(Color.LIGHTGREEN);
        errorMsg.setText(message);
    }

    /**
     * Clear all text and password fields
     */
    private void clearFields(){
        inputPassVerify.clear();
        inputPass.clear();
        inputUser.clear();
    }

    /**
     * @param baseSize sets all text/field fonts
     * @param headerSize sets the header's font
     */
    private void setFontSize(double baseSize, double headerSize){
        Font baseFont = new Font(baseSize);
        username.setFont(baseFont);
        password.setFont(baseFont);
        errorMsg.setFont(baseFont);
        inputPass.setFont(baseFont);
        inputUser.setFont(baseFont);
        inputPassVerify.setFont(baseFont);
        verifyPassword.setFont(baseFont);

        Font headerFont = new Font(headerSize);
        header.setFont(headerFont);
    }
}
