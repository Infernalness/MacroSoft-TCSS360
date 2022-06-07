package main.GUI.tabs;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.GUI.Tab;
import main.data.Database;
import main.data.Item;
import main.data.ItemFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A class to make the Search GUI and search the database for
 * the item or file the user is searching for.
 *
 *
 * @author Wei Wei Chien
 * @version Spring 2022
 */

public class Search extends Tab {

    /**
     * Default size of buttons
     */
    private static final int BUTTON_SIZE = 100;

    /**
     * Overall border pane of the GUI
     */
    //private BorderPane borderPane;
    private FlowPane flowPane;
    /**
     * Grid pane that contains GUI elements
     */
    private GridPane gridPane;

    private ScrollPane fileDisplay;
    /**
     * Array of Items
     */
    //private String[] itemsListData = {"Remote", "Example1", "Example2"};
    private ArrayList<ItemFile> itemsFiles;
    private Item[] items;
    private Item[] itemsList;
    private Item[] filesList;

    //private Item[] itemFiles;
    private Button[] fileButtons;

    /**
     * Text to show Search
     */
    private final Text selectText;
    private final Text searchingText;
    //private final Text errormsg;
    /**
     * Drop down box
     */
    private ChoiceBox<String> choiceBox;

    /**
     * Text field that allows user to search
     */
    private final TextField searchingBox;

    /**
     * Buttons
     */
    private final Button magGlass;

    private final double FILE_BUTTON_WIDTH = 500;

    //private final Button selectButton;

    /**
     * Constructor that class the super method from the tabs class.
     * Builds the GUI for Search. Includes Text, user input field, and
     * Search button.
     *
     * @param buttonName name of the button - Search
     * @param icon image of the button
     */
    public Search(String buttonName, Image icon) {

        super(buttonName, icon);

        //Text to prompt which type of search
        selectText = new Text("Select");
        selectText.getStyleClass().add("white-text");


        //Dropdown box for user to choose type
        choiceBox = new ChoiceBox<>();

        choiceBox.getItems().add("Items");
        choiceBox.getItems().add("Files");
        //default dropdown box setting
        choiceBox.setValue("Files");

        String choice = choiceBox.getValue();

//        selectButton = new Button("Select");
//
//        selectButton.setOnAction(e -> getChoice(choiceBox));


        //Text to prompt which type of search
        searchingText = new Text("Searching");
        searchingText.getStyleClass().add("white-text");

        //Text field for the user to type what they're searching
        searchingBox = new TextField();
        searchingBox.setMinSize(100, 25);

        //Sets up the Search button
        magGlass = new Button();
        magGlass.getStyleClass().add("transparent-square-button");
        ImageView Arrowimage = new ImageView(new Image("/searchIcon.png"));
        Arrowimage.setFitHeight(BUTTON_SIZE * .20);
        Arrowimage.setFitWidth(BUTTON_SIZE * .40);
        magGlass.setGraphic(Arrowimage);

        //get what user is searching for
        final String userSearching = "";
        magGlass.setOnAction(e -> {
            itemsFiles = searchFile(searchingBox.getText());
            fileDisplay.setContent(buildFileViewer());
        });
        String userSearch = userSearching;

        //Depending on what user choose, different database
        if (choice.equals("Items")) {
            String item = searchingBox.getText().toLowerCase();
            searchItem(userSearch);
        } else { //if (choice.equals("Files"))
            String file = searchingBox.getText().toLowerCase();
            searchFile(userSearch);
        }


        //sets up error message
//        javafx.scene.text.Font font = Font.font("Verdana", FontWeight.BOLD, 25);
//        errormsg = new Text("This does not exist!");
//        errormsg.setFill(Color.RED);
//        errormsg.setVisible(false);
//        errormsg.setFont(font);
    }

    /**
     * Builds the GUI for Search. Aligns and size everything in pane.
     *
     * @param stage to add in GUI
     */
    @Override
    public Pane buildView(Stage stage) {
        //create pane
        //borderPane = new BorderPane();
        flowPane = new FlowPane(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.TOP_CENTER);
        gridPane = new GridPane();

        //set size and align elements
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.add(selectText, 0, 0);
        gridPane.add(choiceBox, 1, 0);

        gridPane.add(searchingText, 0, 2);
        gridPane.add(searchingBox, 1, 2);
        gridPane.add(magGlass, 2, 2);
        //gridPane.add(errormsg, 1, 4);
        flowPane.getChildren().add(gridPane);

        fileDisplay = new ScrollPane();
        fileDisplay.getStyleClass().add("scroll-pane");
        fileDisplay.setStyle("-fx-font-size: 16");

        fileDisplay.setContent(buildFileViewer());
        fileDisplay.setMaxHeight(stage.getHeight() - 300);
        fileDisplay.setMinWidth(FILE_BUTTON_WIDTH);
        fileDisplay.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        flowPane.getChildren().add(fileDisplay);
        return flowPane;
    }


    /**
     * Searching Items(database)
     * @param Item - item user is searching for
     * @return list of items that contains the keywords
     */
    public Item[] searchItem(String Item) {
        items = Database.db.getItems();
        for (int i = 0; i < items.length; i++) {
            //if have searched term, add to list
            if (items[i].equals(Item)) {
                itemsList[i] = items[i];
            }
        }

        //should be able to search with shorter characters
//        for (int i = 0; i < items.length; i++) {
//            //if have searched term, add to list
//            if (items[i].indexOf(Item)) {
//                itemsList[i] = items[i];
//            }
//        }

          //error message if nothing matched
//        if (itemsList == null) {
//            errormsg.setVisible(true);
//        }
//        if (itemsList.length < 1) {
//            errormsg.setVisible(true);
//        }
        return itemsList;
    }


    /**
     * Searching Files
     * @param File - file user is searching for
     * @return list of files that contains the keywords
     */
    public ArrayList searchFile(String File) {
        itemsFiles = new ArrayList<>();
        if (File == null) {
            File = "";
        }
        //items.getFiles();
        items = Database.db.getItems();
        for (int i = 0; i < items.length; i++) {
            //if have searched term, add to list

            itemsFiles.addAll(Arrays.asList(items[i].getFiles()));

        }
        //double check
        String finalFile = File;
        itemsFiles.removeIf(s -> !s.getName().toLowerCase().contains(finalFile));

//        if (itemsList.length == 0) {
//            errormsg.setVisible(true);
//        }

        return itemsFiles;
    }

    private VBox buildFileViewer() {


        fileButtons = new Button[itemsFiles.size()];

        itemsFiles.sort(new Comparator<ItemFile>(){
            @Override
            public int compare(ItemFile file1, ItemFile file2) {
                return file1.getName().compareTo(file2.getName());
            }});



        //Desktop is used for opening files
        Desktop desktop = Desktop.getDesktop();
        Font fileButtonFont = new Font(24);
        for(int i = 0; i < itemsFiles.size(); i++){
            //Initializes the item button
            Button currFileButton = new Button("Error...");
            currFileButton.getStyleClass().add("transparent-square-button");
            currFileButton.setFont(fileButtonFont);
            currFileButton.setAlignment(Pos.CENTER_LEFT);

            File selectedFile = new File(itemsFiles.get(i).getPath());
            currFileButton.setText(itemsFiles.get(i).getName() + "\t : \t" + selectedFile.getName());
            currFileButton.setMinWidth(FILE_BUTTON_WIDTH);
            int index = i;//action listeners can't take in i
            currFileButton.setOnAction(clickEvent -> {
                //Gets the file from the path
                //Makes sure the file exists
                if(selectedFile.exists()){
                    try {
                        desktop.open(selectedFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else{
                    System.out.println("Error: file does not exist");
                }
            });
            fileButtons[i] = currFileButton;
        }

        VBox buttonGroup = new VBox(fileButtons);
        return buttonGroup;
    }



    public HBox createToolBar() {
        HBox toolBar = new HBox();
        toolBar.getStyleClass().add("toolbar");
        toolBar.setAlignment(Pos.CENTER_RIGHT);
        toolBar.setSpacing(10);
        toolBar.setPadding(new Insets(5));


        //Creates the close button
        Button close = new Button(" x ");
        close.getStyleClass().add("close-button");
        close.setOnAction(e -> {
            flowPane.setVisible(false);
        });
        toolBar.getChildren().add(close);


        return toolBar;
    }

    /**
     * getting the user choice of file or item
     * @param choiceBox
     * @return type - user choice of file/item
     */
    private String getChoice(ChoiceBox<String> choiceBox) {
        String type = choiceBox.getValue();
        return type;
    }

}
