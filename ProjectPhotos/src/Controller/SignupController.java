package Controller;


import Authentication.EncryptService;
import Model.User;
import Repo.DBConnector;
import Repo.UserRepo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.UUID;

public class SignupController {

    SignUpService service = new SignUpService();

    @FXML
    private TextField textName;
    @FXML
    private TextField textEmail;
    @FXML
    private TextField textPassword;
    @FXML
    private TextField textConPassword;

    @FXML
    public void createAccount(ActionEvent event)
    {
        if(service.checkValidation(textName, textEmail, textPassword, textConPassword))
        {
            User user=new User();
            user.setName(textName.getText());
            user.setEmail(textEmail.getText());
            user.setUserid(UUID.randomUUID());
            user.setEmail(textEmail.getText());
            user.setPassword(EncryptService.encryptPassword(textPassword.getText()));

            DBConnector con=new DBConnector();
            con.databaseConnect();
            UserRepo repo=new UserRepo();
            repo.dbInsertUser(user,con);
            con.databaseDisconnect();

            Node source = (Node)event.getSource();
            Stage stage = (Stage)source.getScene().getWindow();
            stage.close();

        }
        else
        {
            String messageBox= service.checkWarningStatus("");
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setContentText(messageBox);
            alert.setTitle("Tuxedo View");
            alert.showAndWait();
            service.clearMessage();
        }
    }


}
