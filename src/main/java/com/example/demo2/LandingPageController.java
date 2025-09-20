package com.example.demo2;

import com.example.demo2.db.ConnDbOps;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextInputDialog;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * LandingPageController -> class that loads / creates projects , settings & logout button.
 */
public class LandingPageController {
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private VBox projectListVBox;
    @FXML
    private Button newProjectButton;
    @FXML
    private Label welcomeLabel;


    private final ConnDbOps dbOps = new ConnDbOps();
    private int currentUserId = Session.getUserId(); // no getInstance needed

    @FXML
    public void initialize() {
        ThemeManager.getTheme();

        String username = new ConnDbOps().getUsernameById(Session.loggedInUserId);
        welcomeLabel.setText("Welcome Back, " + (username != null ? username : "User") + "!");
        loadProjects();
    }

    /**
     * method to load projects from db
     */
    private void loadProjects() {
        List<String> projects = dbOps.getProjectsForUser(currentUserId);

        projectListVBox.getChildren().clear(); // Clear old content

        if (projects.isEmpty()) {
            Label noProjectsLabel = new Label("No projects yet. Click 'New Project' to get started!");
            projectListVBox.getChildren().add(noProjectsLabel);
        } else {
            for (String name : projects) {
                HBox row = new HBox(10);
                row.setStyle("-fx-padding: 5px; -fx-alignment: center-left; -fx-text-fill: #0D47A1;");

                Label projectLabel = new Label(name);
                projectLabel.setStyle("-fx-font-size: 16px; -fx-cursor: hand;  -fx-text-fill: #0D47A1; ");
//                projectLabel.getStyleClass().add(ThemeManager.getTheme());


                projectLabel.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        projectLabel.setStyle("-fx-text-fill: #90CAF9");
                    } else {
                        projectLabel.setStyle(" -fx-text-fill: #0D47A1;");
                    }
                });

                projectLabel.setOnMouseClicked(_ -> openProject(name));

                Button deleteButton = new Button("Delete");

                deleteButton.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                    if (isNowHovered) {
                        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    } else {
                        deleteButton.setStyle("-fx-background-color: #0D47A1; -fx-text-fill: white;");
                    }
                });

//                deleteButton.setStyle("-fx-background-color: #0D47A1; -fx-text-fill: white;");

                deleteButton.setOnAction(_ -> {
                    dbOps.deleteProject(currentUserId, name);
                    loadProjects(); // Refresh UI after deletion
                });

                row.getChildren().addAll( deleteButton , projectLabel);
                projectListVBox.getChildren().add(row);

            }
        }
    } // End loadProjects method


    @FXML
    private void openNewProject() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setHeaderText("Create a New Project");
        dialog.setContentText("Project Name:");

        dialog.showAndWait().ifPresent(projectName -> {
            String trimmedName = projectName.trim();

            Task<Integer> loadProject = new Task<Integer>() {

                @Override
                protected Integer call() throws Exception {
                    if (!trimmedName.isEmpty()) {
                        int status = dbOps.insertProject(currentUserId, trimmedName);
                        if (status == -1) {
                            throw new SQLException();
                        }
                        return status;
                    }
                    return 0;
                }
            };

            loadProject.setOnSucceeded(_ -> {
                loadProjects(); // refresh UI
                openProject(trimmedName);
            });

            loadProject.setOnFailed(_ -> {
                if (loadProject.getException() instanceof SQLException) {
                    showAlert("Loading failed", "Could not connect to server. Please try again later.");
                }
            });

            Thread thread = new Thread(loadProject);
            thread.setDaemon(true);
            thread.start();
        });
    } // End openNewProject method


    private void openProject(String projectName) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Loading Project");
        popupStage.setWidth(300);
        popupStage.setHeight(200);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setResizable(false);
        VBox popupVbox = new VBox();
        popupVbox.setAlignment(Pos.CENTER);
        Label headerLabel = new Label("Please wait");
        headerLabel.setStyle("-fx-font-family: System; -fx-font-size: 25px; -fx-font-weight: bold;");
        popupVbox.getChildren().add(headerLabel);
        Scene popup = new Scene(popupVbox);
        popupStage.setScene(popup);

        //Perform login operation on task
        Task<Integer> loadProject = new Task<Integer>() {

            @Override
            protected Integer call() throws Exception {
                int projectId = dbOps.getProjectIdByName(currentUserId, projectName);

                if (projectId == -1) {
                    throw new NullPointerException();
                }
                if (projectId == -2) {
                    throw new SQLException();
                }
                return projectId;
            }
        };

        loadProject.setOnSucceeded(_ -> {
            int projectId = loadProject.getValue();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Project-view.fxml"));
                Parent root = loader.load();


                // Get the controller and set project name
                ProjectController projectController = loader.getController();

                Project project = new Project(projectId, projectName);

                projectController.setCurrentProject(project);

                Stage stage = (Stage) newProjectButton.getScene().getWindow();
                stage.setResizable(true);

                // Create the NEW Scene for the project screen
                Scene projectScene = new Scene(root, 1200, 720); // Use a variable for the new scene

                // apply save theme
                ThemeManager.applySavedTheme(projectScene);
                // set stage to saved theme
                stage.setScene(projectScene);
                stage.show();
                popupStage.close();
            } catch (IOException _) {
            }
        });

        loadProject.setOnFailed(_ -> {
            popupStage.close();
            if (loadProject.getException() instanceof NullPointerException) {
                showAlert("Loading failed", "Project no longer exists.");
            }
            if (loadProject.getException() instanceof SQLException) {
                showAlert("Loading failed", "Could not connect to server. Please try again later.");
            }
        });

        Thread thread = new Thread(loadProject);
        thread.setDaemon(true);
        thread.start();

        popupStage.show();
    } // End openProject method

    @FXML
    private void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 720); // width: 680, height: 400
            stage.setScene(scene);
//            ThemeManager.applyTheme(scene) ;
            ThemeManager.applySavedTheme(scene); // 👈 Restore the saved theme

            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // End openSettings method

    @FXML
    private void logout() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("LoginRegister.fxml"));
            stage.setScene(new Scene(root));
//            ThemeManager.applyTheme(stage.getScene());
            ThemeManager.applySavedTheme(stage.getScene()); // 👈 Restore the saved theme

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // End logout method

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    } // End showAlert method

} // End LandingPageController class
