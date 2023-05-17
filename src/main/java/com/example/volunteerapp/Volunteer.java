package com.example.volunteerapp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class Volunteer {
    private String firstName, lastName, phoneNumber;
    private LocalDate birthDate;
    private File imageFile;

    public Volunteer(String firstName, String lastName, String phoneNumber, LocalDate birthDate) {
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
        setBirthDate(birthDate);
        setImageFile(new File(Volunteer.class.getResourceAsStream("images/defaultPerson.png").toString()));
        // setImageFile(new File(Volunteer.class.getResource("/com/example/volunteerapp/images/defaultPerson.png").getFile()));
    }

    public Volunteer(String firstName, String lastName, String phoneNumber, LocalDate birthDate, File imageFile) throws IOException, URISyntaxException {
        this(firstName, lastName, phoneNumber, birthDate); // it will call the constructor with default image file and then will override it in the next line
        setImageFile(imageFile);
        copyImageFile();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * area code    city    house
     * NXX          -XXX    -XXXX
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        if(phoneNumber.matches("^[2-9]\\d{2}[-.]?\\d{3}[-.]\\d{4}$")){
            this.phoneNumber = phoneNumber;
        } else {
            throw new IllegalArgumentException("Phone number must be in the pattern NXX-XXX-XXXX");
        }
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * This will validate volunteer is between the age 10 and 100
     * @param birthDate
     */
    public void setBirthDate(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears(); // to get the number of years in between birthday and today

        if(age >= 10 || age <= 100){
            this.birthDate = birthDate;
        } else {
            throw new IllegalArgumentException("Volunteers must be 10-100 years of age.");
        }
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * This method will copy the file specified to the image directory on this server and give it
     */
    public void copyImageFile() throws IOException, URISyntaxException {
        // create a new Path to the image into a local directory
        Path sourcePath = imageFile.toPath();

        String uniqueFileName = getUniqueFileName(imageFile.getName());
        // professor Jaret's code
        Path targetPath = Paths.get("src/main/resources/com/example/volunteerapp/images/" + uniqueFileName);

        // Path targetPath = Paths.get(Volunteer.class.getResourceAsStream("images/" + uniqueFileName).toString());
        // Path targetPath = Paths.get(Volunteer.class.getResource("/com/example/volunteerapp/images/" + uniqueFileName).getFile());

        // more of professor's code
        File fileSource = new File(sourcePath.toString());
        if(fileSource.exists()){
            // copy the file to the new directory
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

            // update the imageFile to point to new File
            imageFile = new File(targetPath.toString());
        }
    }

    /**
     * This method will recieve a String thet represents a file name and return a String with a random,
     * unique set of letters prefixed to it.
     */
    private String getUniqueFileName(String oldFileName) throws URISyntaxException {
        String newName;

        //create a Random Number Generator (rng)
        SecureRandom rng = new SecureRandom();

        //loop until we have q unique file name
        do{
            newName = "";

            //generate 32 random characters
            for(int count = 1; count <= 32; count++){
                int nextChar;

                do{
                    nextChar = rng.nextInt(123);
                } while (!validCharacterValue(nextChar));

                newName = String.format("%s%c", newName, nextChar); // here even when the nextCharacter is an integer, but we are using %c => character, String class will convert it into a character(char)
            }
            newName += oldFileName;

        } while (!uniqueFileInDirectory(newName));

        return newName;
    }

    /**
     * This method will search the iamge directoy and ensure that the file name
     * is unique
     */
    public boolean uniqueFileInDirectory(String fileName) throws URISyntaxException {
        // File directory = new File(Volunteer.class.getResourceAsStream("images/").toString());
        // File directory = new File(Volunteer.class.getResource("/com/example/volunteerapp/images/").getFile());

        // Professor Jaret's code
        String path = Volunteer.class.getResource("images/").toURI().getPath();
        File directory = new File(path);

        // more of professor's code
        if(directory.isDirectory()){
            File[] dir_contents = directory.listFiles();

            for(File file : dir_contents){
                if(file.getName().equals(fileName)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method will validate if the integer given corresponds to a valid
     * ASCII character that could be used in a file name
     */
    public boolean validCharacterValue(int asciiValue){
        // 0-9 = ASCII range  48 to 57
        if(asciiValue >= 48 && asciiValue <= 57){
            return true;
        }

        // A-Z = ASCII range 65 to 90
        if(asciiValue >= 65 && asciiValue <= 90){
            return true;
        }

        // a-z = ASCII range 97 to 122
        if(asciiValue >= 97 && asciiValue <= 122){
            return true;
        }

        return false;
    }

    /**
     * This will return a formatted String with the person's first name, last name, and age
     * @return
     */
    public String toString(){
        return String.format("%s %s is %d years old", firstName, lastName, Period.between(birthDate, LocalDate.now()).getYears());
    }

    /**
     * This method will write the instance of the Volunteer into the database
     */
    public void insertIntoDB() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try{
            //1. Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/volunteer", "root", "password");
            //2. Create a String that holds the query with ? as a user inputs
            String sql = "INSERT INTO volunteers (firstName, lastName, phoneNumber, birthDate, imageFile) " +
                    "Values (?,?,?,?,?)";
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);
            //4. Convert the birthdate to a SQL date
            Date db = Date.valueOf(birthDate);
            //5. Bind the values to the parameters
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setDate(4, db); // birthDate
            preparedStatement.setString(5, imageFile.getName()); // name of the image file

            preparedStatement.executeUpdate();
        } catch (Exception e){
            System.err.println(e.getMessage());
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }

            if(conn != null){
                conn.close();
            }
        }
    }
}
