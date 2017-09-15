package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import sqlPublication.SQLAddTradeLog;
import sqlPublication.SQLReadAllBookInfo;

public class AddLogStageController implements Initializable{
	@FXML TextField idText;
	@FXML DatePicker datePicker;
	@FXML ComboBox<Integer> codeCombo;
	@FXML TextField purchasePriceText;
	@FXML TextField purchaseNumText;
	@FXML TextField sellingPriceText;
	@FXML TextField sellingNumText;
	
	//ここにテキストボックスに入力されたデータを吸い上げてデータベースに書き込む処理を書く
	@FXML protected void onAddButtonClick(ActionEvent evt){
	
		System.out.println("AddButton was Clicked from AddLogStage");
		System.out.println(this.idText.getText());
		System.out.println(this.datePicker.getValue());
		System.out.println(codeCombo.getValue());
		System.out.println(purchasePriceText.getText());
		
		ISQLExecutable sqlExecutable= 
				new SQLAddTradeLog(	Integer.parseInt(this.idText.getText()),
									java.sql.Date.valueOf(this.datePicker.getValue()),
									Integer.parseInt(this.codeCombo.getValue().toString()),
									Integer.parseInt(this.purchasePriceText.getText()),
									Integer.parseInt(this.purchaseNumText.getText()),
									Integer.parseInt(this.sellingPriceText.getText()), 
									Integer.parseInt(this.sellingNumText.getText()));
    	@SuppressWarnings("unused")
		MySQLConnector mysqlConnector = new MySQLConnector(sqlExecutable);
		TradeLogTableStageController controller;
		controller = Main.tradeLogTableStageController;
		controller.printRecord();
	}
	@FXML protected void onTestButtonClick(ActionEvent evt){
		System.out.println("TestButton Clicked");
		TradeLogTableStageController controller;
		controller = Main.tradeLogTableStageController;
		System.out.println(controller.toString());
	
	}
	
	private ArrayList<Integer> getSecuritiesCodeList(){
		ArrayList<Integer> list = new ArrayList<>();
		SQLReadAllBookInfo sqlReadAllbookInfo = new SQLReadAllBookInfo();
		@SuppressWarnings("unused")
		MySQLConnector connector = new MySQLConnector(sqlReadAllbookInfo);
		sqlReadAllbookInfo.recordList.forEach(e->{
			list.add(e.securitiesCodeProperty().get());
		});
		return list;
  
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.codeCombo.getItems().addAll((this.getSecuritiesCodeList()));
	}
}
