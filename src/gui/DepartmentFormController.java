package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;
	private DepartmentService service;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void subscribeDataChangeListeners(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity is null!");
		}
		if (service == null) {
			throw new IllegalStateException("Service is null!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} 
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for(DataChangeListener listeners : dataChangeListeners) {
			listeners.onDataChanged();
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		ValidationException exception = new ValidationException("ValidationError");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		/*Se a caixa de texto for nula ou estiver vazia.
		* .trim() ? para eliminar qualquer espa?o em branco que esteja no in?cio ou no final*/
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name","Field can't be empty");
		}
		
		//Se no map de erros h? pelo menos um erro.
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		obj.setName(txtName.getText());
		
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	// Esse m?todo vai jogar nos formul?rios, os dados que est?o guardados no
	// Department entity.
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}

		txtId.setText(String.valueOf(entity.getId())); // A caixa de texto trabalha com String, ent?o ? preciso
														// converter o Id para String.
		txtName.setText(entity.getName());

	}
	
	private void setErrorMessages(Map<String,String> errors) {
		Set<String> fields = errors.keySet();
		
		//Se existir a chave name, quer dizer que h? um erro.
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}
