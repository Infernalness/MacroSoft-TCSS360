package main.GUI.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.GUI.Tab;
import main.data.Database;
import main.data.Item;
import main.data.ItemFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static javafx.application.Application.launch;

/**
 * ItemController Class that will control the function of
 * Inserting document, which is shown to the user when they
 * click on the "insert" button on the left side of the
 * program.
 *
 * @author Gabriel Bryan, Anteh Hsu
 * @version Spring 2022
 */

public class InsertDocument extends Tab {

    /**
     * Default size of buttons
     */
    private static final int BUTTON_SIZE = 100;

    /**
     * default scaling variables
     */
    private static final double SCALE = 2.5;

    /**
     * default font size
     */
    private static final double FONT_SIZE = 16;

    /**
     * Overall border pane of the GUI
     */
    private BorderPane borderPane;

    /**
     * Grid pane that contains GUI elements
     */
    private GridPane gridPane;

    /**
     * Text for file Path
     */
    private final Text filePath;

    /**
     * Text for the Item
     */
    private final Text obj;

    /**
     * Text for the file name
     */
    private final Text name;

    /**
     * Text field for the file path
     */
    private final TextField filePathBox;

    /**
     * the Dropdown menu for the Items
     */
    private ComboBox objectBox;

    /**
     * Text field for file name
     */
    private final TextField nameBox;

    /**
     * Select button
     */
    private final Button select;

    /**
     * Button to insert the file
     */
    private final Button insertDoc;

    /** String for filepath and name */
    private String pathStr = "";
    private String nameStr = "";

    /**
     * The Item selected by user
     */
    private Item selectItem = null;

    /**
     * Import file object
     */
    private File myFile;

    /**
     * Error message that will pop in future
     */
    private final Text errormsg;

    /**
     * Success message pop up when insert file is success
     */
    private final Text SuccessMsg;




    /**
     * Constructor of the InsertDocument GUI, calling
     * methods from the super tab class
     *
     *
     * @param buttonName name of the button
     * @param icon icon of the button
     */
    public InsertDocument(String buttonName, Image icon) {
        super(buttonName, icon);

        //sets up the text
        filePath = createText("File Path");
        obj = createText("Item");
        name = createText("Name");

        //sets up the text fields
        filePathBox = createField();

        /*
        //sample items setup
        String[] nameOfItem = {"DishWasher", "Wifi", "RentalDocuments", "SomeOtherGoodThings"};
        // test for a simple item list
        ArrayList<Item> items =  new ArrayList<Item>();
        for(int i = 0; i < nameOfItem.length; i++){
            Item temp = new Item(Database.db, nameOfItem[i]);
            items.add(temp);
        }*/


        nameBox = createField();

        //sets up the select button
        select = new Button("Select");
        select.getStyleClass().add("squircle-button");
        select.setFont(new Font(FONT_SIZE));

        //sets up the insert document button
        insertDoc = new Button("Insert Document");
        insertDoc.setPrefSize(300 * SCALE, 25 * SCALE);
        insertDoc.getStyleClass().add("squircle-button");
        insertDoc.setFont(new Font(FONT_SIZE));
        Font font = Font.font("Verdana", FontWeight.BOLD, 25);
        errormsg = new Text("All fields need to be filled before inserting!");
        errormsg.setFill(Color.RED);
        errormsg.setVisible(false);
        errormsg.setFont(font);
        SuccessMsg = new Text("Successfully inserting Document.");
        SuccessMsg.setFill(Color.GREEN);
        SuccessMsg.setVisible(false);
        SuccessMsg.setFont(font);
        addButtonListener();


    }

    /**
     * Builds the overall GUI of the InsertDocument
     * Including the Buttons to let user select file
     * and insert document.
     *
     * @param stage
     * @return
     */
    @Override
    public Pane buildView(Stage stage) {
        errormsg.setText("");
        SuccessMsg.setText("");
        clearTextFields();

        ArrayList<Item> items = new ArrayList<Item>();
        items.addAll(Arrays.asList(Database.db.getItems()));
        objectBox = createItemDropDown(items);
        objectBox.setOnAction( e -> {
            if(objectBox.getValue() != null)
                System.out.println(objectBox.getValue().getClass());
        });

        borderPane = new BorderPane();
        gridPane = new GridPane();
        gridPane.setPrefSize(stage.getWidth()/2,  stage.getMaxHeight()/2);
        gridPane.setHgap(5);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(filePath, 0, 0);
        gridPane.add(name, 0, 1);
        gridPane.add(obj, 0, 2);
        gridPane.add(filePathBox, 1, 0);
        gridPane.add(objectBox, 1, 2);
        gridPane.add(nameBox, 1, 1);
        gridPane.add(insertDoc, 1, 3);
        gridPane.add(select, 2, 0);
        gridPane.add(errormsg, 1, 4);
        gridPane.add(SuccessMsg,1,4);
        borderPane.setCenter(gridPane);

        //borderPane.setMaxSize(500, 250);
        //borderPane.setTop(createToolBar());
        return borderPane;
    }

    public Pane buildView(Stage theStage, int selectedItem){
        buildView(theStage);
        objectBox.getSelectionModel().select(selectedItem);
        return borderPane;
    }

    /**
     * Method that clears the text in all text fields
     *
     */
    public void clearTextFields() {
        filePathBox.clear();
        nameBox.clear();
    }

    /**
     * Metehod that create the textfield
     * @return the created textfield
     */
    public TextField createField() {
        TextField theField = new TextField();
        theField.setMinSize(100 * SCALE, 25 * SCALE);
        theField.getStyleClass().add("custom-text-entry");
        theField.setFont(new Font(FONT_SIZE));
        return theField;
    }

    /**
     * Method that creates an javafx text object
     * @param text the text want to be filled in into the javafx text object
     * @return the created text object
     */
    public Text createText(String text) {
        Text theText = new Text(text);
        theText.getStyleClass().add("white-text");
        theText.setFont(new Font(FONT_SIZE));
        return theText;
    }



    /**
     * create the comboBox item that has list of item in the database
     * for user to select
     * @param items the database item list
     * @return the created ComboBox item
     */
    public ComboBox<Item> createItemDropDown(ArrayList<Item> items){

        ObservableList<Item> options = FXCollections.observableArrayList();
        Item[] list = Database.db.getItems();
        for(int i = 0; i < list.length; i++){
            options.add(list[i]);
        }
        ComboBox temp = new ComboBox(options);
        temp.getStyleClass().add("combo-box");

        temp.setMinSize(800, 60);
        return temp;
        
    }

    /**
     * Add Action listener to the buttons
     */
    public void addButtonListener(){

        select.setOnAction(e -> {
            Stage temp = new Stage();
            FileChooser chooser = new FileChooser();
            myFile = chooser.showOpenDialog(temp);
            if(myFile.exists() && myFile.isFile()){
                errormsg.setVisible(false);
                filePathBox.setText(myFile.getPath());
                nameBox.setText(myFile.getName());
                System.out.println(filePathBox.getText());
            } else {
                errormsg.setText("Invalid file path or File does not exist!");
                errormsg.setVisible(true);
            }

            }
        );

        insertDoc.setOnAction(e -> {
            myFile = new File(filePathBox.getText());
            selectItem = (Item) objectBox.getValue();
            if(filePathBox.getText().equals("") || objectBox.getValue() == null || nameBox.getText().equals("")) {
                StringBuilder str = new StringBuilder();
                str.append("Missing ");
                if(filePathBox.getText().equals("")){
                    str.append("(filePath) ");
                }
                if(objectBox.getValue() == null){
                    str.append("(Item) ");
                }
                if(nameBox.getText().equals("")){
                    str.append("(Name) ");
                }
                str.append("!");
                errormsg.setText(str.toString());
                SuccessMsg.setVisible(false);
                errormsg.setVisible(true);
            }
            else if(!myFile.exists()){
                errormsg.setText("File does not exist!");
                SuccessMsg.setVisible(false);
                errormsg.setVisible(true);
            }else if(selectItem.hasFilePath(myFile.getPath())){
                errormsg.setText("File already Exist!");
                SuccessMsg.setVisible(false);
                errormsg.setVisible(true);
            }
            else{
                errormsg.setVisible(false);
                errormsg.setText("All fields need to be filled before inserting!");
                pathStr = filePathBox.getText();
                nameStr = nameBox.getText();
                ItemFile doc = new ItemFile(nameStr, pathStr);
                selectItem.addFile(doc);
                SuccessMsg.setText("Inserting Successful!");
                SuccessMsg.setVisible(true);

                
            }

            }
        );
    }

    /** all the getters */
    public BorderPane getBorderPane() {
        return borderPane;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public Text getFilePath() {
        return filePath;
    }

    public Text getObj() {
        return obj;
    }

    public Text getName() {
        return name;
    }

    public TextField getFilePathBox() {
        return filePathBox;
    }

    public ComboBox getObjectBox() {
        return objectBox;
    }

    public TextField getNameBox() {
        return nameBox;
    }

    public Button getSelect() {
        return select;
    }

    public Button getInsertDoc() {
        return insertDoc;
    }

    public String getPathStr() {
        return pathStr;
    }

    public String getNameStr() {
        return nameStr;
    }

    public Item getSelectItem() {
        return selectItem;
    }

    public File getMyFile() {
        return myFile;
    }

    public Text getErrormsg() {
        return errormsg;
    }

    public Text getSuccessMsg() {
        return SuccessMsg;
    }


}
