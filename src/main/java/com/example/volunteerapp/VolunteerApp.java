package com.example.volunteerapp;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

public class VolunteerApp {
    public static void main(String[] args) throws IOException {
        Volunteer volunteer = new Volunteer("Fred", "Flintstone", "651-555-1234", LocalDate.of(2000, Month.MARCH, 10), new File(VolunteerApp.class.getResourceAsStream("images/Fred_Flintstone.png").toString()));
        System.out.printf("Our volunteer is: %s%n", volunteer);
    }
}
