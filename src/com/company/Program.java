package com.company;


import java.sql.*;
import java.util.Properties;
import java.util.Scanner;


public class Program {

    private boolean isRunning = true;


    public Program() {
        run();
    }

    private void run() {
        do {
            System.out.println();
            System.out.println("Vad vill du göra?");
            System.out.println("1: Se antal elever per kurs");
            System.out.println("2: Se lista på alla aktiva elever, sorterad på namn");
            System.out.println("3: Radera elev");
            System.out.println("4: Se alla aktiva elevers betyg");
            System.out.println("5: Se alla icke-aktiva elevers betyg");
            System.out.println("6: Förändra databasen");
            System.out.println("7: Avsluta");
            Scanner scan = new Scanner(System.in);
            int menuChoice = scan.nextInt();

            switch (menuChoice) {
                case 1:
                    showTables("EXEC EleverPerKurs");
                    break;
                case 2:
                    showTables("EXEC ElevLista");
                    break;
                case 3:
                    studentToDelete();
                    break;
                case 4:
                    showTables("SELECT * FROM AktivaElevBedömning");
                    break;
                case 5:
                    showTables("SELECT * FROM IckeAktivaElevBedömning");
                    break;
                case 6:
                    changeTable();
                    break;
                case 7:
                    isRunning = false;
                    break;
            }
        } while (isRunning);
    }

    private void showTables (String tableToShow) {
        Connection conn = null;

        try {
            String dbURL = "jdbc:sqlserver://localhost;database=projectDataBase";
            Properties properties = new Properties();
            properties.put("integratedSecurity", "true");
            conn = DriverManager.getConnection(dbURL, properties);
            if (conn != null) {
                Statement stat = conn.createStatement(); //Förbered frågan
                ResultSet resultSet = stat.executeQuery(tableToShow); //Ställ frågan och ta emot svaret i ett resultSet
                ResultSetMetaData metaData = resultSet.getMetaData();// Hämta metadatan
                int nmbOfColumns = metaData.getColumnCount();//Hämta antalet kolumner från metadatan
                System.out.printf("%n%n");

                for (int i = 1; i <= nmbOfColumns; i++) {
                    System.out.printf("%-20s\t", metaData.getColumnName(i));//Hämta kolumnnamnen
                }
                System.out.println();
                while (resultSet.next()) {
                    for (int i = 1; i <= nmbOfColumns; i++) {
                        System.out.printf("%-20s\t", resultSet.getObject(i));//Hämta innehållet cell för cell från resultSet
                    }
                    System.out.println();
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void studentToDelete(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Vilken student vill du ta bort? Mata in elevens ElevId: ");
        String studentNumber = scan.nextLine();
        showTables("EXEC raderaOchFlytta " + "'" + studentNumber + "'");
    }

    private void changeTable(){
        Connection conn = null;
        String dbURL = "jdbc:sqlserver://localhost;database=projectDataBase";
        Properties properties = new Properties();
        properties.put("integratedSecurity", "true");
        try {
            conn = DriverManager.getConnection(dbURL, properties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PreparedStatement insertNewStudent = null;
        try {
            assert conn != null;
            insertNewStudent = conn.prepareStatement("INSERT INTO Elev" +
                    "(ElevId, Namn, KursID)" +
                    "VALUES(?, ?, ?)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            insertNewStudent.setString(1, "E110");
            insertNewStudent.setString(2, "Test");
            insertNewStudent.setString(3, "K101");

            insertNewStudent.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
