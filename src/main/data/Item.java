package main.data;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Locale;

public class Item {
    private Database myDatabase;
    private String myName;
    private ArrayList<ItemFile> myFiles;
    private ArrayList<String> myTags;
    private String myDescription;

    /**
     * Constructor
     *
     * @param name the name of the item
     */
    public Item(Database database, String name) {
        this(database, name, null, "");
    }

    /**
     * @param database database this item exists in
     * @param name name of the item
     * @param tags tags attatched to this item
     * @param description description of the item
     */
    public Item(Database database, String name, String[] tags, String description){
        this.myDatabase = database;
        this.myName = name;
        myFiles = new ArrayList<ItemFile>();
        if(tags == null)
            myTags = new ArrayList<String>();
        else
            for(String tag : tags){
                addTag(tag);
            }
        myDescription = description;

    }

    /**
     * Constructor
     *
     * @param json a JSON object that represents the item
     */
    public Item(Database database, JSONObject json) throws IllegalArgumentException {
        this.myDatabase = database;

        if (!json.has("name"))
            throw new IllegalArgumentException("JSONObject must have a name key");
        if (!json.has("files"))
            throw new IllegalArgumentException("JSONObject must have a files key");

        this.myName = json.getString("name");

        myFiles = new ArrayList<ItemFile>();
        JSONArray filesArray = json.getJSONArray("files");
        for (int i = 0; i < filesArray.length(); i++) {
            JSONObject obj = filesArray.getJSONObject(i);
            myFiles.add(new ItemFile(obj));
        }

        myTags = new ArrayList<String>();
        if (json.has("tags")) {
            JSONArray tagsArray = json.getJSONArray("tags");
            for (int i = 0; i < tagsArray.length(); i++) {
                JSONObject obj = filesArray.getJSONObject(i);
                myTags.add(obj.toString());
            }
        }

        if (json.has("description") {
            myDescription = json.getString("description");
        }
    }

    /**
     * Returns the name of the item
     *
     * @return the name of the item
     */
    public String getName() {
        return myName;
    }

    public String getDescription() {return myDescription;}

    public String[] getTags() {
        return (String[]) myTags.toArray().clone();
    }

    public void addTag(String tag) {
        if (myDatabase.hasTag(tag))
            myTags.add(tag.toLowerCase());
    }

    public void removeTag(String tag) {
        myTags.remove(tag.toLowerCase());
    }

    public boolean hasTag(String tag) {
        for (String str : myTags) {
            if (str.equalsIgnoreCase(tag))
                return true;
        }

        return false;
    }

    /**
     * Adds an ItemFile to the item
     *
     * @param file the ItemFile to add
     * @param cache when set to true, the file is cached locally, which should be the default option
     */
    public void addFile(ItemFile file, boolean cache) throws IllegalArgumentException {
        if (hasFile(file.getName()))
            throw new IllegalArgumentException("File already exists with same name");

        if (cache) {

        }

        myFiles.add(file);
    }

    /**
     * Adds an ItemFile to the item
     *
     * @param file the ItemFile to add
     */
    public void addFile(ItemFile file) throws IllegalArgumentException {
        addFile(file, true);
    }

    /**
     * Removes an ItemFile from the item
     *
     * @param file the ItemFile to remove
     */
    public void removeFile(ItemFile file) {
        if (file == null)
            return;

        myFiles.remove(file);
    }

    /**
     * Removes an ItemFile from the item
     *
     * @param name the name of the ItemFile to remove
     */
    public void removeFile(String name) {
        removeFile(getFile(name));
    }

    /**
     * Returns the files in the item
     *
     * @return a ItemFile array of the files contained within the object
     */
    public ItemFile[] getFiles() {
        return (ItemFile[]) myFiles.toArray(new ItemFile[myFiles.size()]);
    }

    /**
     * Returns the file with a given name
     *
     * @param name the name of the ItemFile to retrieve
     * @return an ItemFile with the given name, or null if such ItemFile does not exist
     */
    public ItemFile getFile(String name) {
        for (ItemFile f : myFiles) {
            if (f.getName().equals(name))
                return f;
        }

        return null;
    }

    public boolean hasFilePath(String path){
        for (ItemFile f: myFiles){
            if(f.getPath().equals(path)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether or not an ItemFile exists within the item
     *
     * @return true if the ItemFile exists, false otherwise
     */
    public boolean hasFile(String name) {
        return getFile(name) != null;
    }

    /**
     * Caches the files locally
     */
    public void cacheFiles() throws IOException {
        File filesDir = new File(myDatabase.getWorkingDirectory() + "/files/");
        if (!filesDir.exists())
            filesDir.mkdir();

        File localDir = new File(myDatabase.getWorkingDirectory() + "/files/" + myName + "/");
        if (!localDir.exists())
            localDir.mkdir();

        String localDirStr = localDir.getAbsolutePath();

        for (int i = 0; i < myFiles.size(); i++) {
            ItemFile itemFile = myFiles.get(i);
            File srcFile = new File(itemFile.getPath());
            String cachedPath = localDirStr + "/" + FilenameUtils.getName(srcFile.getPath());
            Files.copy(srcFile.toPath(), (new File(cachedPath).toPath()), StandardCopyOption.REPLACE_EXISTING);
            ItemFile cachedItemFile = new ItemFile(itemFile.getName(), (new File(cachedPath).toPath()).toString());
            myFiles.set(i, cachedItemFile);
        }
    }

    /**
     * Returns a JSONObject representing the item
     *
     * @return a JSONObject representing the item
     */
    public JSONObject toJSONObject() {
        JSONArray filesArray = new JSONArray();
        for (ItemFile file : myFiles) {
            filesArray.put(file.toJSONObject());
        }

        JSONObject object = new JSONObject();
        object.put("name", myName);
        object.put("files", filesArray);
        object.put("tags", myTags);
        object.put("description", myDescription);

        return object;
    }

    public String toString(){
        return myName;
    }
}