package Controller;

import Model.Album;
import Model.FileManager;
import Model.User;
import Repo.AlbumRepo;
import Repo.DBConnector;
import Repo.LocationParser;
import Model.Photo;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.util.*;


public class ImageUploadController {

    @FXML
    private ImageView imageViewer;
    @FXML
    private Button btn_newFolder;
    @FXML
    private TitledPane btn_selectFolder;
    @FXML
    private TextField tBox_tagImg;
    @FXML
    private Button btn_upload;
    @FXML
    private Label lbl_metadata;
    @FXML
    private ComboBox<String> albumNames;

    private Photo photo;

    private  User user;

    private FileManager image;


    @FXML
    private void selectImage(ActionEvent event) {
        image = new FileManager();
        image.chooseImage();
        if (image.getFile() != null) {

            Image imageObj = new Image(image.getFile().toURI().toString());
            imageViewer.setImage(imageObj);

            btn_newFolder.disableProperty().setValue(false);
            btn_selectFolder.disableProperty().setValue(false);
            tBox_tagImg.disableProperty().setValue(false);
            btn_upload.disableProperty().setValue(false);

            photo = new Photo();
            photo.readImageMetadata(image.getFile());

            String metadata = "Date-time:\n" + photo.getDateTime()+"\nSize: "+String.format("%.3f", photo.getSize())+" MB\nGPS: ";




            if(photo.hasLocation()) {
                LocationParser gpsLocation = new LocationParser(photo.getLatitude(), photo.getLongitude());
                photo.setCity(gpsLocation.getCity());
                photo.setCountry(gpsLocation.getCountry());
                metadata += photo.getLongitude()+" "+photo.getLatitude()+"\nLocation: ";

            }

            else
                metadata += "No GPS info!\n";

            if(photo.hasCountry())
                metadata +=photo.getCountry();
            else
                metadata += "No locational Data!";

            if(photo.hasModel())
                metadata += "\nModel: "+photo.getModel();
            else
                metadata += "\nModel: No model found!";

            lbl_metadata.setText(metadata);
            fillComboBox();

        }
    }

    @FXML
    private void imageUpload(ActionEvent event) throws IOException {

        photo.setUser(user);

        Album album = new Album();
        album.setAlbumName(albumNames.getValue());
        album.setUser(user);

        DBConnector con = new DBConnector();
        con.databaseConnect();
        con.setSession(con.getFactory().getCurrentSession()) ;
        con.getSession().beginTransaction();

        AlbumRepo albumRepo = new AlbumRepo();
        album=albumRepo.findByName(album,con);

        photo.setAlbum(album);

        image.saveFile(photo);

        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        con.databaseDisconnect();
        stage.close();
    }

    private void fillComboBox()
    {
        DBConnector con = new DBConnector();
        con.databaseConnect();
        con.setSession(con.getFactory().getCurrentSession()) ;
        con.getSession().beginTransaction();

        AlbumRepo albumRepo = new AlbumRepo();

        List<Album> listAlbum=albumRepo.findByUser(user, con);
        List<String> albumStr = new ArrayList<>();

        for(Album album : listAlbum)
        {
            albumStr.add(album.getAlbumName());
        }
        albumNames.setItems(FXCollections.observableArrayList(albumStr));
        con.databaseDisconnect();
    }

    public void setUser(User user) { this.user = user; }
}