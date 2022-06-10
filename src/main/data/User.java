/*
 *
 */
package main.data;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * User class
 *
 * @author Gabriel Bryan
 * @version Spring 2022
 */
public class User {

    private static final String FILE_NAME = "user.json";

    public static User activeUser;

    /** Name of the user */
    private String myName;

    /** The email address of the user */
    private String myPassword;

    /**
     * Constructor to initialize the name and password address
     * when a new object is made.
     *
     * @param name the name of the user.
     * @param password the password of the user.
     */
    public User(String name, String password){
        myName = name;
        myPassword = password;
        try{
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs into an existing user
     *
     * @param userData the file being loaded into the user
     */
    public User(JSONObject userData) {
        if(!userData.has("pass"))
            throw new IllegalArgumentException("JSON does not contain a password");
        if(!userData.has("name")){
            throw new IllegalArgumentException("JSON does not contain a name");
        }

        myName = userData.getString("name");
        myPassword = userData.getString("pass");
    }

    public String getUserName(){ return myName;}

    public String getPassword() {return myPassword;}

    /**
     * Overrides the toString method to print out the correct
     * string of the User object.
     *
     * @return the correct string format.
     */
    public String toString(){
        if(myName.isEmpty()) return "-";
        return myName;
    }

    public JSONObject toJSONObject(){
        JSONObject myData = new JSONObject();
        myData.put("name", myName);
        myData.put("pass", myPassword);
        return myData;
    }

    /**
     * Builds the user JSON file in the users folder
     * @throws IOException
     */
    public void save() throws IOException {
        JSONObject theData = toJSONObject();

        //Ensure there is a users folder
        File userDir = new File(Database.db.getWorkingDirectory() + "/users/");
        if(!userDir.exists())
            userDir.mkdir();

        File theFile = new File(getFileDirectory(myName));

        //Write the JSON file
        FileWriter fileWriter = new FileWriter(theFile);
        fileWriter.write(theData.toString(2));
        fileWriter.close();
    }

    /**
     * @param name is the name of the user
     * @return a file path string to the users JSON file
     */
    public static String getFileDirectory(String name) {
        return Database.db.getWorkingDirectory() + "/users/" + name + '_' + FILE_NAME;
    }
}
