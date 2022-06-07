package main.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public static Database db = null;
    private String workingDir;
    private ArrayList<Item> items;
    private ArrayList<String> tags;

    /*
     * Constructor
     *
     * @param workingDir the working directory of the database
     */
    public Database(String workingDir) {
        this.workingDir = workingDir;
        items = new ArrayList<Item>();
    }

    /*
     * Constructor
     *
     * @param json a JSON object that represents the database
     */
    public Database(JSONObject json) throws IllegalArgumentException {
        if (!json.has("workingDir"))
            throw new IllegalArgumentException("JSONObject must have a workingDir key");
        if (!json.has("items"))
            throw new IllegalArgumentException("JSONObject must have a items key");

        this.workingDir = json.getString("workingDir");

        JSONArray itemsArray = json.getJSONArray("items");
        items = new ArrayList<Item>();
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemObj = itemsArray.getJSONObject(i);
            createItem(itemObj);
        }

        tags = new ArrayList<String>();
        if (json.has("tags")) {
            JSONArray tagsArray = json.getJSONArray("tags");
            for (int i = 0; i < tagsArray.length(); i++) {
                JSONObject tagObj = tagsArray.getJSONObject(i);
                tags.add(tagObj.toString());
            }
        }
    }

    /*
     * Returns the working directory string
     *
     * @return the working directory
     */
    public String getWorkingDirectory() {
        return workingDir;
    }

    public DatabaseView createView(DatabaseFilter filter) {
        DatabaseView view = new DatabaseView(this, filter);
        return view;
    }


    /**
     * Adds an item to the database
     *
     * @param item the item that is being added
     */
    private void addItem(Item item) throws IllegalArgumentException {
        if (hasItem(item.getName()))
            throw new IllegalArgumentException("Item with same name already exists");

        items.add(item);
    }

    /*
     * Creates a new item
     *
     * @param name the name of the item to be created
     */
    public Item createItem(String name) {
        Item item = new Item(this, name);
        addItem(item);
        return item;
    }

    /*
     * Creates a new item
     *
     * @param name the name of the item to be created
     * @param tags the tags of the item to be created
     * @param description the description of the item to be created
     */
    public Item createItem(String name, String[] tags, String description) {
        Item item = new Item(this, name, tags, description);
        addItem(item);
        return item;
    }

    /*
     * Creates a new item
     *
     * @param json a JSONObject representing the item
     */
    public Item createItem(JSONObject json) {
        Item item = new Item(this, json);
        addItem(item);
        return item;
    }

    /**
     * Removes an item in the database
     *
     * @param index the index of the item to be removed.
     * @throws IllegalArgumentException if the item does not exist.
     */
    private void deleteItem(int index) throws IllegalArgumentException {
//        if (!hasItem(item.getName())) {
//            throw new IllegalArgumentException("Item with this name does not exist");
//        }
        if (index >= items.size() || index < 0) {
            throw new IllegalArgumentException("Item does not exist");
        }

        items.remove(index);
    }

    /**
     * Removes an item
     *
     * @param index index of the arraylist to be removed
     */
    public void removeItem(int index) {
        deleteItem(index);
    }

    /*
     * Returns an item contains within the database
     *
     * @param name the name of the item to be retrieved
     * @return the retrieved item, or null if the item does not exist
     */
    public Item getItem(String name) {
        for (Item item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }

        return null;
    }

    /*
     * Returns whether or not the item is contained within the database
     *
     * @param name the name of the item that is being checked for existence
     * @return true if the item is exists, false if otherwise
     */
    public boolean hasItem(String name) {
        return getItem(name) != null;
    }

    /*
     * Returns the items in the database
     *
     * @return an array of items
     */
    public Item[] getItems() {
        return (Item[]) items.toArray(new Item[items.size()]);
    }

    public String[] getTags() {
        return (String[]) tags.toArray().clone();
    }

    public void addTag(String theTag) {
        if(hasTag(theTag))
            tags.add(theTag.toLowerCase());
    }

    public void removeTag(String theTag) {
        tags.remove(theTag);
    }

    public boolean hasTag(String theTag) {
        for(String aTag : tags) {
            if(aTag.toLowerCase().equals(theTag)) {
                return true;
            }
        }
        return false;
    }


    public int getIndex(Item theItem){
        for(int i = 0; i < items.size(); i++){
            if(theItem.equals(items.get(i)))
            {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }

    /*
     * Returns the database represented with a JSONObject
     *
     * @return a JASONObject
     */
    public JSONObject toJSONObject() {
        List<JSONObject> itemsArray = new ArrayList<JSONObject>();
        for (Item item : items)
            itemsArray.add(item.toJSONObject());
        JSONObject obj = new JSONObject();
        obj.put("workingDir", workingDir);
        obj.put("tags", tags);
        obj.put("items", new JSONArray(itemsArray));
        return obj;
    }

    /*
     * Saves the database JSON file
     */
    public void save() throws IOException {
        File dir = new File(workingDir);
        if (!dir.exists()  || !dir.isDirectory()) {
            dir.mkdir();
        }

        File dbFile = new File(workingDir + "/database.json");

        JSONObject json = toJSONObject();

        FileWriter fw = new FileWriter(dbFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(json.toString(2));
        bw.close();
    }

    /*
     * Caches all the files locally
     */
    public void cacheAllFiles() throws IOException {
        for (Item item : items) {
            item.cacheFiles();
        }
    }
}