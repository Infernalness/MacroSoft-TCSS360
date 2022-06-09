/*
 *
 */
package main.GUI;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;
import main.GUI.tabs.InsertDocument;
import main.GUI.tabs.ItemController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.GUI.tabs.Search;
import main.data.Database;
import main.data.Item;
import main.data.ItemFile;

import java.awt.image.DataBuffer;
import java.io.IOException;

/**
 * Controller of the GUI to call other class's method that would
 * transition between the application.
 *
 * @author Paul Lee, Gabriel Bryan
 * @version Spring 2022
 */
public class GUIController extends Application {

    /** LoginScreen class to be used to call one of the methods */
    private LoginScreen loginScreen;

    /** These are all of the different tabs that represent different features */
    private Tab[] tabs;

    /** This is the stage for the application that will be used throughout its execution */
    private static Stage myStage;

    private static BorderPane mainPane;

    /** keeps track of where the mouse was clicked for drag event */
    private double mouseX, mouseY;

    public static GUIController guiControl;

    /**
     * Starts up with the log in screen
     * in the LoginScreen class to check the credentials before
     * moving to the main screen.
     * @param stage to set the scene and to display the GUI to the user.
     * @authors Gabe, Paul
     */
    @Override
    public void start(Stage stage) {
        guiControl = this;

        myStage = stage;
        myStage.initStyle(StageStyle.UNDECORATED);

        loginScreen = new LoginScreen(this, myStage);

        stage.setTitle("IterationTwo");

        WorkDirectoryChooser dc = new WorkDirectoryChooser(loginScreen, this, myStage);
        Scene wdChooserPage = new Scene(dc.setUpGUI());
        wdChooserPage.getStylesheets().add("/StyleSheet.css");

        stage.setScene(wdChooserPage);
        stage.show();

    }

    /**
     * Builds the application view
     * this method is called after login is successful.
     */
    public void buildMainView() {
        VBox tabChanger = tabChanger();

        mainPane = new BorderPane();

        Scene mainScene = new Scene(mainPane);
        mainScene.getStylesheets().add("/StyleSheet.css");
        myStage.setScene(mainScene);

        //Add the elements
        mainPane.setLeft(tabChanger);
        myStage.setMaximized(true);

        //Sets up the toolbar
        mainPane.setTop(createToolBar());
        //testItemDisplay();
        mainPane.setCenter(tabs[0].buildView(myStage));
    }

    /**
     * Generates a custom title/toolbar for the application
     * @return a horizontal box with min, max, and close buttons
     * @author Gabe
     */
    public HBox createToolBar() {
        HBox toolBar = new HBox();
        toolBar.getStyleClass().add("toolbar");
        toolBar.setAlignment(Pos.CENTER_RIGHT);
        toolBar.setSpacing(10);
        toolBar.setPadding(new Insets(5));

        /**This section is for being able to drag the application from the toolbar.
         * When the mouse is pressed, the coordinates are saved in mouseX, and mouseY.
         * This is so that the application can keep track of its position relative to the mouse.*/
        toolBar.setOnMousePressed(mouseEvent -> {
            mouseX = mouseEvent.getSceneX();
            mouseY = mouseEvent.getSceneY();
            double width = myStage.getWidth();
            double height = myStage.getHeight();
            myStage.setMaximized(false);
            myStage.setHeight(height);
            myStage.setWidth(width);
        });
        toolBar.setOnMouseDragged(mouseEvent -> {
            myStage.setX(mouseEvent.getScreenX()-mouseX);
            myStage.setY(mouseEvent.getScreenY()-mouseY);
        });
        toolBar.setOnMouseDragReleased(mouseEvent -> {
            myStage.setMaximized(true);
        });

        //Creates the minimize button.
        Button min = new Button(" - ");
        min.getStyleClass().add("tool-bar-button");
        min.setOnAction(e -> {
            myStage.setIconified(true);
        });
        toolBar.getChildren().add(min);

        //Creates the maximize button.
        /*Button max = new Button(" o ");
        max.getStyleClass().add("tool-bar-button");
        max.setOnAction(e -> {
            myStage.setMaximized(!myStage.isMaximized());
        });
        toolBar.getChildren().add(max);*/

        //Creates the close button
        Button close = new Button(" x ");
        close.getStyleClass().add("close-button");
        close.setOnAction(e -> {
            System.exit(0);
        });
        toolBar.getChildren().add(close);

        //TODO: Add a title to the toolbar that appears on the left side.

        return toolBar;
    }

    /**
     * This creates the tab changer that exists on the left side of the application.
     * When a tab button is pressed, the tab will change.
     * @return a vertical box with buttons for changing tabs.
     * @author Gabe
     */
    private VBox tabChanger() {
        createTabs();

        VBox tabBar = new VBox();
        tabBar.setSpacing(10);

        //Adds all the tab buttons to the vertical box
        for(Tab aTab: tabs) {
            tabBar.getChildren().add(aTab.getTabButton());
        }
        //Adds the save button
        tabBar.getChildren().add(createSaveTab(Tab.BUTTON_SIZE));

        Background background = new Background(new BackgroundFill(Color.rgb(40, 43, 56), CornerRadii.EMPTY, Insets.EMPTY));
        tabBar.setBackground(background);
        //tabBar.getChildren().addAll(home, search, insertDoc);
        //Set the home screen to be on

        return tabBar;
    }

    /**
     * This initializes the tabs and generates all their tab buttons.
     * @author Gabe
     */
    private void createTabs() {
        //Initialize tabs
        tabs = new Tab[3];
        tabs[0] = new ItemController("Home", new Image("/homeIcon.png"));
        tabs[1] = new Search("Search", new Image("/searchIcon.png"));
        tabs[2] = new InsertDocument("Insert Document", new Image("/documentIcon.png"));

        //Generates tab buttons
        for(int i = 0; i < tabs.length; i++){
            Tab currTab = tabs[i];
            currTab.getTabButton().setOnAction(e -> {
                Pane tabPane = currTab.buildView(myStage);
                mainPane.setCenter(tabPane);
            });
        }
    }

    /**
     * Builds a button that calls save and cacheAllFiles on the database
     * @param buttonSize is the size of both the height and width of the button
     * @return the save button
     * @author Gabe
     */
    private Button createSaveTab(double buttonSize){
        Image icon = new Image("/save.png");

        Button tabButton = new Button();
        tabButton.getStyleClass().add("transparent-square-button");
        tabButton.setPrefSize(buttonSize, buttonSize);
        tabButton.setTooltip(new Tooltip("Save"));

        ImageView iconView = new ImageView(icon);
        iconView.setFitHeight(buttonSize * .8);
        iconView.setFitWidth(buttonSize * .8);
        tabButton.setGraphic(iconView);

        tabButton.setOnAction(clickEvent -> {
            try {
                Database.db.cacheAllFiles();
                Database.db.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return tabButton;
    }

    public void insertDocToItem (int itemIndex){
        mainPane.setCenter(((InsertDocument) tabs[2]).buildView(myStage, itemIndex));
    }

    /**
     * Main method to launch the GUI.
     * @param args argument
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void testItemDisplay(){
        Item item = new Item(Database.db, "A file");
        ItemFile file = new ItemFile("Peer Review", "C:\\Users\\gaber\\OneDrive\\Documents\\Peer Review.docx");
        item.addFile(file);
        ItemDisplay newItemDisplay =  new ItemDisplay(myStage);
        mainPane.setCenter(newItemDisplay.buildView(item));
    }
}