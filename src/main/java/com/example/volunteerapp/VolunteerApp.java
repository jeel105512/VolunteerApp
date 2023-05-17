package com.example.volunteerapp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

public class VolunteerApp {
    public static void main(String[] args) throws IOException, URISyntaxException, SQLException {
        Volunteer volunteer = new Volunteer("Wilma", "Flintstone", "651-555-1234", LocalDate.of(2012, Month.MARCH, 12), new File(VolunteerApp.class.getResource("images/Fred_Flintstone.png").toURI()));
        System.out.printf("Our volunteer is: %s%n", volunteer);
        volunteer.insertIntoDB();
    }
}
