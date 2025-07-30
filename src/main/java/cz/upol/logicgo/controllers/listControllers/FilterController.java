package cz.upol.logicgo.controllers.listControllers;

import cz.upol.logicgo.list.history.FilterProperties;
import cz.upol.logicgo.misc.enums.SortOption;
import cz.upol.logicgo.misc.enums.Status;
import cz.upol.logicgo.misc.enums.TypeGame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class FilterController implements Initializable {

    @FXML
    public ChoiceBox statusChoiceBox;

    @FXML
    public ChoiceBox gameTypeChoiceBox;

    @FXML
    public ChoiceBox sortOptionsChoiceBox;

    @FXML
    public CheckBox sortCheckbox;

    @FXML
    public Button okButton;

    @FXML
    public Button cancelButton;

    Stage stage;

    FilterProperties filterProperties;

    public FilterController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onOkClick() {
        filterProperties.setSortOption((SortOption) sortOptionsChoiceBox.getSelectionModel().getSelectedItem());
        filterProperties.setTypeGame((TypeGame) gameTypeChoiceBox.getSelectionModel().getSelectedItem());
        filterProperties.setStatus((Status) statusChoiceBox.getSelectionModel().getSelectedItem());
        filterProperties.setAscending(!sortCheckbox.isSelected());
        stage.close();
    }

    public void onCancelClick(){
        filterProperties = null;
        stage.close();
    }

    public void initialize(FilterProperties filterProperties) {
        this.filterProperties = filterProperties;

        ObservableList<Status> statusOptions = FXCollections.observableArrayList();
        statusOptions.add(null);
        statusOptions.addAll(Status.values());
        statusChoiceBox.setItems(statusOptions);
        statusChoiceBox.setValue(null);


        ObservableList<TypeGame> gameTypeOptions = FXCollections.observableArrayList();
        gameTypeOptions.add(null);
        gameTypeOptions.addAll(TypeGame.values());
        gameTypeChoiceBox.setItems(gameTypeOptions);
        gameTypeChoiceBox.setValue(null);

        ObservableList<SortOption> sortOptions = FXCollections.observableArrayList();
        sortOptions.addAll(SortOption.values());
        sortOptionsChoiceBox.setItems(sortOptions);
        sortOptionsChoiceBox.setValue(SortOption.LAST_PLAYED);


        statusChoiceBox.setConverter(new StringConverter<>() {
            @Override public String toString(Object t) {
                if (t instanceof Status status) {
                    return status.getDescription();
                }

                return "žádné";
            }

            @Override public Status fromString(String string) {
                return Status.getInstanceByDescription(string);
            }
        });

        gameTypeChoiceBox.setConverter(new StringConverter<>() {
            @Override public String toString(Object t) {
                if (t instanceof TypeGame type) {
                    return type.getDescription();
                }

                return "žádné";
            }

            @Override public TypeGame fromString(String string) {
                return TypeGame.getInstanceByDescription(string);
            }
        });

        sortOptionsChoiceBox.setConverter(new StringConverter<>() {
            @Override public String toString(Object t) {
                if (t instanceof SortOption sortOption) {
                    return sortOption.getDescription();
                }

                return "";
            }

            @Override public SortOption fromString(String string) {
                return SortOption.getInstanceByDescription(string);
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public FilterProperties getFilterProperties() {
        return filterProperties;
    }
}
