package com.example.laborator6;

import Exception.DasElementExistiertException;
import Controller.EnrolledController;
import Controller.KursController;
import Controller.LehrerController;
import Controller.StudentController;
import Modele.Enrolled;
import Modele.Kurs;
import Modele.Lehrer;
import Modele.Student;
import Repository.EnrolledRepository;
import Repository.KursRepository;
import Repository.LehrerRepository;
import Repository.StudentRepository;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import Exception.ListIsEmptyException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloApplication extends Application {

    private StudentController studentController;
    private LehrerController lehrerController;
    private EnrolledController enrolledController;
    private KursController kursController;
    String settings = """
            -fx-background-color:\s
                   #D994D3,
                   linear-gradient(#fafdfe, #e8f5fc),
                   linear-gradient(#E8BDDF 0%, #FBC3F6 49%, #E9AFDE 50%, #E9AFDE 100%);
               -fx-background-insets: 0,1,2;
               -fx-background-radius: 3,2,1;
               -fx-padding: 3 30 3 30;
               -fx-text-fill: black;
               -fx-font-size: 15px;""".indent(1);
    String background = " -fx-background-color: #FDDAF6";
    String textUserStyle = """
            -fx-background-color:\s
                   #D994D3,
                   linear-gradient(#fafdfe, #e8f5fc),
                   linear-gradient(#E8BDDF 0%, #FEFEFE 49%, #FEFEFE 50%, #FEFEFE 100%);
               -fx-background-insets: 0,1,2;
               -fx-background-radius: 3,2,1;
               -fx-text-fill: black;""";

    /**
     * Input fur Repositorys und Controllers
     */
    public void input()
    {
        StudentRepository studentRepository = new StudentRepository();
        KursRepository kursRepository = new KursRepository();
        LehrerRepository lehrerRepository = new LehrerRepository();
        EnrolledRepository enrolledRepository = new EnrolledRepository();
        kursController = new KursController(kursRepository,studentRepository,lehrerRepository,enrolledRepository);
        lehrerController = new LehrerController(lehrerRepository);
        studentController = new StudentController(studentRepository);
        enrolledController =  new EnrolledController(kursRepository,studentRepository,enrolledRepository);
    }

    /**
     * Das Anfang GUI
     * @param stage, fur GUI
     */
    @Override
    public void start(Stage stage) {
        Label label = new Label("Lehrer oder Student?");
        label.setFont(new Font("Cambria", 18));
        Button loginLehrer = new Button();
        loginLehrer.setText("Lehrer");
        loginLehrer.setStyle(settings);
        Button loginStudent = new Button();
        loginStudent.setText("Student");
        loginStudent.setStyle(settings);

        loginStudent.setOnAction(event -> showLoginScreenStudent());

        loginLehrer.setOnAction(event -> showLoginScreenLehrer());

        VBox box = new VBox();
        box.setPadding(new Insets(10));
        box.setStyle(background);

        box.setAlignment(Pos.BASELINE_CENTER);

        box.getChildren().add(label);
        box.getChildren().add(loginLehrer);
        box.getChildren().add(loginStudent);

        Scene scene = new Scene(box, 450, 250);

        stage.setTitle("Anwendung fur Lehrer/Studenten");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Gibt einen Alert Fenster, wenn derStudent/Lehrer existiert nicht
     * @param user, ID der falsch ist
     */
    public void falschesLog(String user) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Falsches ID");
        alert.setHeaderText("Warning");
        String s = user + " existiert nicht!";
        alert.setContentText(s);
        alert.show();
    }

    /**
     * Wenn der Student existiert
     * @param ID des Studenten
     */
    public void gutesLogStudent(Long ID) {

        Stage stage = new Stage();
        input();
        AtomicInteger anzahlKredits = new AtomicInteger();
        Button button = new Button();
        button.setText("Register");
        button.setStyle(settings);
        Label labelKurs = new Label("ID des Kurses, wo Sie sich einschreiben wollen.");
        labelKurs.setFont(new Font("Cambria", 18));
        TextField textKurs = new TextField();
        textKurs.setPromptText("ID Kurs");

        button.setOnAction(actionEvent -> {
            String idKurs = textKurs.getText();
            long IdKurs= Long.parseLong(idKurs);
            Enrolled enrolled = new Enrolled(ID, IdKurs);
            try {
                if(enrolledController.create(enrolled) != null)
                {
                    textKurs.clear();
                    registered();
                }
                else
                {
                    textKurs.clear();
                    notRegistered();
                }
            } catch (IOException | SQLException | DasElementExistiertException | ListIsEmptyException e) {
                e.printStackTrace();
            }
        });

        Button button2 = new Button();
        button2.setText("Anzahl Kredits");
        button2.setStyle(settings);
        VBox box = new VBox();
        box.setPadding(new Insets(5));
        box.getChildren().add(button);
        box.getChildren().add(labelKurs);
        box.getChildren().add(textKurs);
        box.getChildren().add(button2);
        box.setStyle(background);
        Scene scene = new Scene(box, 450, 250);
        stage.setScene(scene);
        stage.setTitle("Student der Universität");
        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-font-size: 15px; -fx-font-family: 'SketchFlow Print';");
        button2.setOnAction(actionEvent -> {

            List<Student> listeStudenten = new ArrayList<>();
            try {
                listeStudenten = studentController.getAll();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            for(Student student : listeStudenten){
                if(student.getStudentID() == ID)
                {
                    anzahlKredits.set(student.getTotalKredits());
                    break;
                }
            }
            listView.getItems().clear();
            String message = "Sie haben insgesamt " + anzahlKredits + " Kredits.";
            listView.getItems().add(message);
            box.getChildren().add(listView);
        });

        stage.show();

    }

    /**
     * wenn der Lehrer existiert
     * @param ID des Lehrers
     */
    public void gutesLogLehrer(Long ID) {
        input();
        Button button = new Button();
        button.setText("Sehen Sie die Studenten + Refresh");
        button.setStyle(settings);
        Stage stage = new Stage();
        input();
        VBox box = new VBox();
        box.setPadding(new Insets(5));
        Label label = new Label("Studenten eingeschrieben zu Ihren Kursen");
        label.setFont(new Font("Cambria", 18));
        box.getChildren().add(label);
        box.getChildren().add(button);
        box.setStyle(background);
        Scene scene = new Scene(box, 350, 250);
        stage.setScene(scene);
        stage.setTitle("Lehrer der Universität");
        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-font-size: 15px; -fx-font-family: 'SketchFlow Print';");
        button.setOnAction(event -> {
            listView.getItems().clear();
            List<Kurs> alleKurse = null;
            try {
                alleKurse = kursController.getAll();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }

            List<Long> kurseLehrer = new ArrayList<>();
            assert alleKurse != null;
            for(Kurs kurs : alleKurse)
            {
                if(kurs.getLehrer() == ID)
                {
                    kurseLehrer.add(kurs.getID());
                }
            }

            List<Enrolled> enrolled = new ArrayList<>();
            try {
                enrolled = enrolledController.getAll();
            } catch (SQLException | ListIsEmptyException | IOException e) {
                e.printStackTrace();
            }
            int ct = 0;

            for(Enrolled e : enrolled)
            {

                if(kurseLehrer.contains(e.getIdKurs()))
                {
                    ct++;
                    listView.getItems().add(String.valueOf((e.getIdStudent())));
                }
            }
            if(ct == 0)
            {
                Label label1 = new Label("Kein Student hat sich bei einem Kurs eingeschrieben.");
                label1.setFont(new Font("Cambria", 15));
                box.getChildren().add(label1);
            }
            box.getChildren().add(listView);
        });

        stage.show();
    }


    /**
     * gibt die Anwendungen des Students
     */
    public void showLoginScreenStudent() {
        Stage stage = new Stage();

        input();
        VBox box = new VBox();
        box.setPadding(new Insets(10));

        box.setStyle(background);
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Bitte geben Sie das ID.");
        label.setFont(new Font("Cambria", 18));

        TextField textUser = new TextField();
        textUser.setStyle(textUserStyle);
        textUser.setPromptText("ID");

        Button btnLogin = new Button();
        btnLogin.setText("Login");
        btnLogin.setStyle(settings);
        btnLogin.setOnAction(event -> {

            List<Student> listeStudenten = null;
            input();
            try {
                listeStudenten = studentController.getAll();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            boolean wahr = false;
            String id = textUser.getText();
            long ID = Long.parseLong(id);
            for(Student student : Objects.requireNonNull(listeStudenten))
            {
                if(student.getStudentID() == ID)
                {
                    wahr = true;
                    break;
                }
            }
            if(wahr)
            {
                gutesLogStudent(ID);
            }
            else
            {
                falschesLog(textUser.getText());
            }
            stage.close();

        });

        box.getChildren().add(label);
        box.getChildren().add(textUser);
        box.getChildren().add(btnLogin);
        Scene scene = new Scene(box, 350, 250);
        stage.setScene(scene);
        stage.setTitle("Studenten der Universität");
        stage.show();
    }

    /**
     * gibt die Anwendungen des Lehrers
     */
    public void showLoginScreenLehrer() {
        Stage stage = new Stage();

        input();
        VBox box = new VBox();
        box.setPadding(new Insets(10));

        box.setStyle(background);
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Bitte geben Sie das ID.");
        label.setFont(new Font("Cambria", 18));
        TextField textUser = new TextField();
        textUser.setPromptText("ID");
        textUser.setStyle(textUserStyle);
        Button btnLogin = new Button();
        btnLogin.setText("Login");
        btnLogin.setStyle(settings);
        btnLogin.setOnAction(event -> {

            List<Lehrer> listeLehrer = null;
            try {
                listeLehrer = lehrerController.getAll();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            boolean wahr = false;
            String id = textUser.getText();
            long ID = Long.parseLong(id);
            for(Lehrer lehrer : Objects.requireNonNull(listeLehrer))
            {
                if(lehrer.getLehrerID() == ID)
                {
                    wahr = true;
                    break;
                }
            }
            if(wahr)
            {
                gutesLogLehrer(ID);
            }
            else
            {
                falschesLog(textUser.getText());
            }
            stage.close();

        });

        box.getChildren().add(label);
        box.getChildren().add(textUser);
        box.getChildren().add(btnLogin);
        Scene scene = new Scene(box, 350, 250);
        stage.setScene(scene);
        stage.setTitle("Lehrer der Universität");
        stage.show();
    }

    /**
     * Alert Fenster, wenn der Student nicht registered wurde
     */
    public void notRegistered() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error Registration!");
        alert.setHeaderText("Warning");
        String s = " Sie wurden nicht zu dem Kurs eingeschrieben!";
        alert.setContentText(s);
        alert.show();
    }

    /**
     * Alert Fenster, wenn der Student registered wurde
     */
    public void registered() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success Registration!");
        alert.setHeaderText("Success");
        String s = "Sie wurden zu dem Kurs eingeschrieben!";
        alert.setContentText(s);
        alert.show();
    }

    public static void main(String[] args) {
        launch();
    }
}