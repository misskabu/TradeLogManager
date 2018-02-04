/**
 * 
 */
package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import customControl.CustomTextField;
import customControl.PLTextFieldTableCell;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.converter.DateStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;
import propertyBeans.TradeLogRecord;
import sqlPublication.SQLDeleteTradeLog;
import sqlPublication.SQLReadAllBookInfo;
import sqlPublication.SQLReadAllTradeLog;
import sqlPublication.SQLReadTradeLogByCode;
import sqlPublication.SQLReadTradeLogByDate;
import sqlPublication.SQLUpdateMemo;
import sqlPublication.SQLUpdateTradeLog;

/**
 * @author misskabu
 *
 */
public  class TradeLogTableStageController implements Initializable{
	private static final int MAX_CHARCTER_COUNTS_IN_MEMO = 255; 
	@FXML public TableView<TradeLogRecord> tableView;
	@FXML private TableColumn<TradeLogRecord,Integer> idColumn;
	@FXML private TableColumn <Object,java.util.Date>dateColumn;
	@FXML private TableColumn<Object,Object> codeColumn;
	@FXML private TableColumn<TradeLogRecord,String> bookNameColumn;
	@FXML private TableColumn<TradeLogRecord,String> marcketColumn;
	@FXML private TableColumn<TradeLogRecord,Number> purchasePriceColumn;
	@FXML private TableColumn<TradeLogRecord,Integer> purchaseNumColumn;
	@FXML private TableColumn<TradeLogRecord,Number> sellingPriceColumn;
	@FXML private TableColumn<TradeLogRecord,Integer> sellingNumColumn;
	@FXML private TableColumn<TradeLogRecord,Number> pLColumn;
	@FXML private TableColumn<TradeLogRecord, String> memoColumn;
	@FXML private TextArea memoArea;
	@FXML private Label idCountText;
	@FXML private TextField yearText;
	@FXML private ChoiceBox<Integer> monthChoice;
	@FXML private ComboBox<Integer> codeCombo;
	@FXML protected void onShowAddLogWindowMenuClick(ActionEvent evt){
		System.out.println("starting onShowAddLogWindowMenuClick was successed.");
		this.createBoderPaneStage("AddLogStage.fxml","AddLogStage",350,0, 400, 400);
	}
	@FXML protected void onShowAddBookInfoWindowMenuClick(ActionEvent evt){
		System.out.println("starting onShowAddBookInfoWindowMenuClick was successed.");
		this.createBoderPaneStage("AddBookInfoStage.fxml","AddBookInfoStage",350,0, 400, 170);
	}
	@FXML protected void onShowBookInfoTableWindowMenuClick(ActionEvent evt){
		System.out.println("starting onShowBookInfoTableWindowMenuClick was successed.");
		this.createBoderPaneStage("BookInfoTableStage.fxml","BookInfoTableStage",100,100, 400, 500);
	}
	@FXML protected void onHelpMenuClick(ActionEvent evt){
		System.out.println("starting onHelpMenuClick was successed.");
		this.createBoderPaneStage("HelpStage.fxml", "HelpStage", 0, 0, 800, 600);
	}
	
	/**
	 * @param fxmlFileName
	 * @param posX  this is not absolute number,Input relative position from first Stage. 
	 * @param posY
	 * @param width
	 * @param height
	 * 
	 */

	private void createBoderPaneStage(String fxmlFileName,String title,int posX,int posY,int width,int height){
		Stage stage = new Stage();
		BorderPane root;
		try {
			root = (BorderPane)FXMLLoader.load(getClass().getResource("/fxml/" + fxmlFileName));
			Scene scene = new Scene(root,width,height);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			stage.setX(stage.getX()+posX);
			stage.setY(stage.getY()+posY);
			stage.setTitle(title);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override public String toString(){
		return "This is the Controller which is Controll fxml.";
		
	}
	@FXML protected void onReloadLogsClick(ActionEvent evt){
		System.out.println("starting onReloadLogs Menu Click was successed.");
		this.printRecord();
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		this.setCellValueFactoryes();
		this.setCellFactoryes();
		this.printRecord();
		this.initializeMonthChoice();
		this.initializeCodeCombo();
		
		PLTextFieldTableCell.tradeLogTableStageController = this;
		
		LocalDateTime dateTime = LocalDateTime.now();
		this.yearText.setText(String.valueOf(dateTime.getYear()));
		this.monthChoice.setValue(dateTime.getMonthValue());
	}
	private void initializeMonthChoice(){
		monthChoice.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
	}
	private void initializeCodeCombo(){
		this.codeCombo.getItems().addAll(getSecuritiesCodeList());
	}

	/**
	 * setCellFactory Method make a column Editable. 
	 * dateColumn.setCellFactory(DatePickerTableCell.setTableColumn(dateColumn);
	 */
	@SuppressWarnings({ })
	private void setCellFactoryes(){
		SQLReadAllTradeLog sqlReadAllTradeLog = new SQLReadAllTradeLog();
		@SuppressWarnings("unused")
		H2DBConnector connector = new H2DBConnector(sqlReadAllTradeLog);
  
		dateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter()));
		codeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(this.getSecuritiesCodeList().toArray()));
		purchasePriceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
		purchaseNumColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		sellingPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
		sellingNumColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		//pLColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
		// TODO 行ごとに色を帰るにはsetCellFactoryの引数で無名クラスを使うようだ。まだよくわからない
		//pLColumn.setCellFactory(e-> {return new PLTextFieldTableCell();});
		pLColumn.setCellFactory(CustomTextField.forTableColumn(new NumberStringConverter()));
		memoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		
	}
	private void setCellValueFactoryes(){
		//引数の"id","date"などの文字列がPropertyBeansクラスのTradeLogRecordのprivate変数名と完全一致させると
		//TableViewと関連づけられる。文字列を間違えるとデータを表示できない。
		idColumn.setCellValueFactory(new PropertyValueFactory<TradeLogRecord,Integer>("id"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<Object,java.util.Date>("date"));
		codeColumn.setCellValueFactory(new PropertyValueFactory<Object,Object>("code"));
		bookNameColumn.setCellValueFactory(new PropertyValueFactory<TradeLogRecord,String>("name"));
		marcketColumn.setCellValueFactory(new PropertyValueFactory<TradeLogRecord,String>("marcket"));
		purchasePriceColumn.setCellValueFactory(new PropertyValueFactory<TradeLogRecord,Number>("purchasePrice"));
		purchaseNumColumn.setCellValueFactory(new PropertyValueFactory<TradeLogRecord,Integer>("purchaseNum"));
		sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<TradeLogRecord,Number>("sellingPrice"));
		sellingNumColumn.setCellValueFactory(new PropertyValueFactory<TradeLogRecord,Integer>("sellingNum"));
		pLColumn.setCellValueFactory(new PropertyValueFactory<TradeLogRecord,Number>("PL"));
		memoColumn.setCellValueFactory(new PropertyValueFactory<TradeLogRecord,String>("memo"));

	}
	private ArrayList<Integer> getSecuritiesCodeList(){
		ArrayList<Integer> list = new ArrayList<>();
		SQLReadAllBookInfo sqlReadAllbookInfo = new SQLReadAllBookInfo();
		@SuppressWarnings("unused")
		H2DBConnector connector = new H2DBConnector(sqlReadAllbookInfo);
		sqlReadAllbookInfo.recordList.forEach(e->{
			list.add(e.securitiesCodeProperty().get());
		});
		return list;
  
	}

	/**
	 * @param event CellEditEvent have informations where was edited from,what text was inputed.
	 * tableView.getItems means all recored.
	 * ObservableList<TradeLogRecord> means all record.
	 */
	@FXML protected void onStart(){
		System.out.println("start");
	}
	
	// TODO カスタムクラスを指定しているときはそのクラス内のcommit()で行う
	@FXML protected void onPLColumnCommit(CellEditEvent<TradeLogRecord,Number> event){
		System.out.println("onPLColumnCommit Start");
		event.getRowValue().setPLProperty(event.getNewValue().intValue());
		
		this.updateRecord();
		this.printRecord();
	}
	@FXML protected void onSecuritiesCodeColumnCommit(CellEditEvent<TradeLogRecord, Integer> event){
		System.out.println("onSecuritiesCodeColumnCommit Start");
		event.getRowValue().setCodeProperty(event.getNewValue());
		this.updateRecord();
		this.printRecord();
	}
	@FXML protected void onPurchasePriceColumnCommit(CellEditEvent<TradeLogRecord, Number> event){
		System.out.println("onPurchasePriceColumnCommit Start");
		event.getRowValue().setPurchasePriceProperty(event.getNewValue());
		this.updateRecord();
	}
	@FXML protected void onPurchaseNumberColumnCommit(CellEditEvent<TradeLogRecord, Integer> event){
		System.out.println("onPurchaseNumberColumnCommit Start");
		event.getRowValue().setPurchaseNumberProperty(event.getNewValue());
		this.updateRecord();
	}
	@FXML protected void onSellingPriceColumnCommit(CellEditEvent<TradeLogRecord, Number> event){
		System.out.println("onSellingPriceColumnCommit Start");
		event.getRowValue().setSellinPriceProperty(event.getNewValue());
		this.updateRecord();
	}
	@FXML protected void onSellingNumberColumnCommit(CellEditEvent<TradeLogRecord, Integer> event){
		System.out.println("onSellingNumberColumnCommit Start");
		event.getRowValue().setSellingNumberProperty(event.getNewValue());
		this.updateRecord();
	}
	@FXML protected void onContextMenuRequested(){
		System.out.println("onContextMuenuRequested Start");
		int indexRow = this.tableView.getSelectionModel().getSelectedIndex();
		System.out.println(indexRow);
		ObservableList<TradeLogRecord> recordList = tableView.getItems();
		try{
			TradeLogRecord record = recordList.get(indexRow);
			SQLDeleteTradeLog sqlDeleteTradeLog = new SQLDeleteTradeLog(record.idProperty().intValue());
			@SuppressWarnings("unused")
			H2DBConnector con = new H2DBConnector(sqlDeleteTradeLog);}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Record is not selected.Please select any record.");
		}
		this.printRecord();
	}

	@FXML protected void onMouseClicked(){
		System.out.println("onMouseClicked");
		int indexRow = this.tableView.getSelectionModel().getSelectedIndex();
		ObservableList<TradeLogRecord> recordList = tableView.getItems();
		TradeLogRecord record = recordList.get(indexRow);
		String memo = record.memoProperty().getValue();
		System.out.println(memo);
		this.memoArea.setText(memo);
	}
	@FXML protected void onAction(){
		System.out.println("onButtonClicked");
		if(this.memoArea.getText().length()>MAX_CHARCTER_COUNTS_IN_MEMO){
			System.out.println("too many char.plese write" +  MAX_CHARCTER_COUNTS_IN_MEMO + "chars.");
		}
		int indexRow = this.tableView.getSelectionModel().getSelectedIndex();
		ObservableList<TradeLogRecord> recordList = tableView.getItems();
		TradeLogRecord record = recordList.get(indexRow);
		ISQLExecutable sqlUpdateMemo = new SQLUpdateMemo(
				record.idProperty().intValue(),
				memoArea.getText());
		@SuppressWarnings("unused")
		H2DBConnector mySQLConnector = new H2DBConnector(sqlUpdateMemo);	
		this.printRecord();
	}
	@FXML protected void onKeyTyped(){
		System.out.println("onKeyTyped");
		int length = this.memoArea.getText().length();
		this.idCountText.setText(String.valueOf(length) + "/" + MAX_CHARCTER_COUNTS_IN_MEMO);
		
	}
	@FXML protected void onFilterByDateButtonClicked(){
		System.out.println("onFilterByDateButtonClicked");
		System.out.println(yearText.getText());
		System.out.println(monthChoice.getValue());
		this.printRecord(Integer.valueOf(yearText.getText()), monthChoice.getValue().intValue());
	}
	@FXML protected void onFilterByCodeButtonClicked(){
		System.out.println("onFilterByDateButtonClicked");
		System.out.println(this.codeCombo.getValue());
		this.printRecord(this.codeCombo.getValue());
	}
	public void updateRecord(){
		int indexRow = tableView.getSelectionModel().getSelectedIndex(); 

		ObservableList<TradeLogRecord> recordList = tableView.getItems();
		TradeLogRecord record = recordList.get(indexRow);

		System.out.println("Selected Row =" + indexRow);
		System.out.println(record.idProperty());
		System.out.println(record.dateProperty().toString());
		System.out.println(record.nameProperty());
		System.out.println(record.purchasePriceProperty());
		System.out.println(record.PLProperty());

		ISQLExecutable sqlUpdateTradeLog = new SQLUpdateTradeLog(
				record.idProperty().intValue(),
				new java.sql.Date(record.dateProperty().getValue().getTime()),
				record.codeProperty().intValue(),
				record.purchasePriceProperty().intValue(),
				record.purchaseNumProperty().intValue(),
				record.sellingPriceProperty().intValue(),
				record.sellingNumProperty().intValue(),
				record.PLProperty().intValue());
		@SuppressWarnings("unused")
		H2DBConnector mySQLConnector = new H2DBConnector(sqlUpdateTradeLog);		
	}
	public void printRecord(int code){
		SQLReadTradeLogByCode sqlTradeLogByCode = new SQLReadTradeLogByCode(code);
		@SuppressWarnings("unused")
		H2DBConnector mysqlConnector = new H2DBConnector(sqlTradeLogByCode);
		try{
		for ( int i = 0; i<tableView.getItems().size(); i++) {
			tableView.getItems().clear();
		}}catch(IndexOutOfBoundsException e){
			System.out.println("IndexOutOfBoudsException. please check look counter in the printrecord");
		}
		sqlTradeLogByCode.recordList.forEach(e->{
			this.tableView.getItems().add(new TradeLogRecord(
					e.idProperty().get(),
					e.dateProperty().get(),
					e.codeProperty().get(),
					e.nameProperty().get(),
					e.marcketProperty().get(),
					e.purchasePriceProperty().get(),
					e.purchaseNumProperty().get(), 
					e.sellingPriceProperty().get(), 
					e.sellingNumProperty().get(),
					e.PLProperty().get(),
					e.memoProperty().get()));
		});
	}
	public void printRecord(int year,int month){
		SQLReadTradeLogByDate sqlTradeLogByDate = new SQLReadTradeLogByDate(year,month);
		@SuppressWarnings("unused")
		H2DBConnector mysqlConnector = new H2DBConnector(sqlTradeLogByDate);
		try{
		for ( int i = 0; i<tableView.getItems().size(); i++) {
			tableView.getItems().clear();
		}}catch(IndexOutOfBoundsException e){
			System.out.println("IndexOutOfBoudsException. please check look counter in the printrecord");
		}
		sqlTradeLogByDate.recordList.forEach(e->{
			this.tableView.getItems().add(new TradeLogRecord(
					e.idProperty().get(),
					e.dateProperty().get(),
					e.codeProperty().get(),
					e.nameProperty().get(),
					e.marcketProperty().get(),
					e.purchasePriceProperty().get(),
					e.purchaseNumProperty().get(), 
					e.sellingPriceProperty().get(), 
					e.sellingNumProperty().get(),
					e.PLProperty().get(),
					e.memoProperty().get()));
		});
	}
	public void printRecord(){
		SQLReadAllTradeLog sqlReadAllTradeLog= new SQLReadAllTradeLog();
		@SuppressWarnings("unused")
		H2DBConnector mysqlConnector = new H2DBConnector(sqlReadAllTradeLog);
		try{
		for ( int i = 0; i<tableView.getItems().size(); i++) {
			tableView.getItems().clear();
		}}catch(IndexOutOfBoundsException e){
			System.out.println("IndexOutOfBoudsException. please check look counter in the printrecord");
		}
		sqlReadAllTradeLog.recordList.forEach(e->{
			this.tableView.getItems().add(new TradeLogRecord(
					e.idProperty().get(),
					e.dateProperty().get(),
					e.codeProperty().get(),
					e.nameProperty().get(),
					e.marcketProperty().get(),
					e.purchasePriceProperty().get(),
					e.purchaseNumProperty().get(), 
					e.sellingPriceProperty().get(), 
					e.sellingNumProperty().get(),
					e.PLProperty().get(),
					e.memoProperty().get()));
		});
	}
}
