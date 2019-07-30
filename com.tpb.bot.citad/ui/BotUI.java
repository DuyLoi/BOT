package com.tpb.bot.citad.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


import org.apache.log4j.Logger;


import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.optionalusertools.PickerUtilities;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.tpb.bot.citad.config.Config;
import com.tpb.bot.citad.constant.Constant;
import com.tpb.bot.citad.job.CitadBotJob;
import com.tpb.bot.citad.util.FileUtil;
import com.tpb.bot.citad.util.LogFile;

public class BotUI extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;

	public static final Logger logger = Logger.getLogger(BotUI.class);
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_TIME_FORMAT);


	public static DatePicker datePickerStart;
	public static DatePicker datePickerStop;
	
	public static TimePicker timePickerStart;
	public static TimePicker timePickerStop;
	
	public static ArrayList<LocalDate> dateStartArr = new ArrayList<LocalDate>();

	public static int period = 0;
	public static final LocalDate today = LocalDate.now();
	
	public static int intevalPeriod;
	public static String saveFile;

	
	public static String startDateStr;
	public static String startTimeStr;
	
	public static boolean jrdOneTime;
	public static boolean jrdDaily;
	
	public static boolean jrdAllDay;
	public static boolean jrdChooseTime;
	
	public static String endDateStr;
	public static String endTimeStr;
	public static JTextArea display;
	
	public static int amountExchange;
	public static String statusExchange;

	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private JTable table;
	DefaultTableModel model;
	JScrollPane scrollPane;
	Timer timer;
	
	@Override
	public void update(Observable o, Object data) {
		StringBuffer sb = new StringBuffer(display.getText());
		sb.append(sdf.format(new Date()) + ": " + (String) data + "\r\n");
		display.setText(sb.toString());
	}

	public BotUI() throws Exception {
		createGUI();
		setDisplay();
	}

	private void setDisplay() {
		setTitle("Đặt lịch");
		pack();
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void createGUI() throws Exception {
		getContentPane().add(createTabbedPane());
	}

	private JTabbedPane createTabbedPane() throws Exception {

		JTabbedPane tablePane = new JTabbedPane();
		JPanel panelMonitor = createPanelMonitor();
		JPanel panelReport = createPanelReport();
		JPanel panelConfig = createPanelConfig();
		
		setSizeColor(tablePane,Color.white);
		setSizeColor(panelMonitor,Color.white);
		setSizeColor(panelReport,Color.white);
		setSizeColor(panelConfig,Color.white);

		tablePane.addTab("Monitor", null, panelMonitor);
		tablePane.addTab("Báo cáo", null, panelReport);
		tablePane.addTab("Thiết lập", null, panelConfig);
		
		tablePane.setSelectedIndex(1);
		
		return tablePane;
	}

	private JPanel createPanelMonitor() {

		JPanel panelMonitor = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		
		panelMonitor.setLayout(layout);
		display = new JTextArea(16, 58);
		display.setEditable(false);

		JScrollPane scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panelMonitor.add(scroll);

		return panelMonitor;
	}

	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	private JPanel createPanelReport() throws ClassNotFoundException{
		
		JPanel panelResult = new JPanel();
		panelResult.setLayout(new GridBagLayout());

		Vector<String> cols = new Vector<String>();
		cols.addElement("TT");
		cols.addElement("Số bút toán");
		cols.addElement("Thời gian thực hiện");
		cols.addElement("Trạng thái");
		cols.addElement("Ghi chú");
		model = new DefaultTableModel(null, cols) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		model.fireTableDataChanged();
		
		scrollPane = new JScrollPane(createTable(table));
		Insets istscrollPane = new Insets(0, 0, 0, 0);
		addComponent(istscrollPane, panelResult, scrollPane, 0, 0, 4, 4, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 100, 150);
		//===========================================================================
		JLabel fromDate = new JLabel("Từ ngày: ");
		JLabel toDate = new JLabel("Đến ngày: ");
		
		//===========================================================================
		DatePickerSettings dateStartDatePickerSettings = new DatePickerSettings();
		dateStartDatePickerSettings.setFormatForDatesCommonEra("dd-MM-yyyy");
		DatePicker jdStartDateRs = new DatePicker(dateStartDatePickerSettings);
		jdStartDateRs.setDateToToday();
		
		DatePickerSettings dateEndDatePickerSettings = new DatePickerSettings();
		dateEndDatePickerSettings.setFormatForDatesCommonEra("dd-MM-yyyy");
		DatePicker jdEndDateRs = new DatePicker(dateEndDatePickerSettings);
		jdEndDateRs.setDateToToday();
		jdEndDateRs.setEnabled(false);
		
		Insets istFromDate = new Insets(10, 20, 0, 0);
		Insets istStartDate = new Insets(10, 5, 0, 0);
		Insets isttoDate = new Insets(10,-200,0,0);
		Insets istEndDate = new Insets(10, -140, 0, 0);
		
		addComponent(istFromDate, panelResult, fromDate, 0, 6, 1, 1, 0.0 ,0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE,0, 0);
		addComponent(istStartDate, panelResult, jdStartDateRs, 1, 6, 1, 1, 0.0 ,0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0);
		addComponent(isttoDate, panelResult, toDate, 2, 6, 1, 1, 0.0 ,0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0);
		addComponent(istEndDate, panelResult, jdEndDateRs, 2, 6, 1, 1, 0.0 ,0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0);
		
		jdStartDateRs.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				dateEndDatePickerSettings.setDateRangeLimits(today
						.minusDays(FileUtil
								.getDiffWithTodayByDay(jdStartDateRs)), today
						.plusDays(3000));
				jdEndDateRs.setEnabled(true);

			}
		});
		
		//===========================================================================
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		
		JButton btView = new JButton("Hiển thị báo cáo");
		JButton btExport = new JButton("Xuất báo cáo");	
		
		Insets istbtView = new Insets(10, 20, 20, 0);
		Insets istbtExport = new Insets(10, -200, 20, 0);
		
		addComponent(istbtView, panelResult, btView, 0, 7, 2, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE,40,0);
		addComponent(istbtExport, panelResult, btExport, 2, 7, 2, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE,60,0);
		
		btView.addActionListener(e -> {
			LogFile queryJob = new LogFile(
					jdStartDateRs, jdEndDateRs);
			Vector<Vector> listRowData = new Vector<>();
			listRowData.removeAllElements();
			try {
				listRowData = queryJob.getAllData();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			int rowCount = model.getRowCount();
			for (int i = rowCount - 1; i >= 0; i--) {
				model.removeRow(i);
			}
			for (int i = 0; i < listRowData.size(); i++) {
				model.addRow(listRowData.get(i));
			}

			model.fireTableDataChanged();
			table = new JTable(model);
			table.setFillsViewportHeight(true);
			scrollPane = new JScrollPane(table);
		});
		//===========================================================================
		btExport.addActionListener(e -> {
			String nameFile = ("Báo cáo từ ngày "
					+ jdStartDateRs.getDate().format(dtf) + " đến ngày " + jdEndDateRs
					.getDate().format(dtf));
			LogFile queryJob = new LogFile(jdStartDateRs, jdEndDateRs);
			
			try {
				List<LogFile> log =  queryJob.getAllDataToPrintPDF();
				
				Iterator<LogFile> it = log.iterator();
				System.out.println("Print Log in Export file");
				System.out
						.println("===========================================");
				while (it.hasNext()) {
					
					LogFile value = it.next();	
					System.out.println("1. " + value.getDateFileDownload());
					System.out.println("2. " + value.getName());
					System.out.println("3. " + value.getStatus());
					System.out.println("4. " + value.getNote());
				}

				LogFile createTableLog = new LogFile(log,
						nameFile, jdStartDateRs, jdEndDateRs);
				createTableLog.drawTablePDF(log, nameFile);

			} catch (Exception ex) {
				ex.printStackTrace();
		}
		});
		return panelResult;
	}

	private JPanel createPanelConfig() throws Exception {
		// 1. panelConfig =========================================================================================================
		JPanel panelConfig = new JPanel();
		GridBagLayout layoutConfig = new GridBagLayout();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		panelConfig.setBounds(0, 0, screenSize.width / 2, screenSize.height / 2);
		panelConfig.setLayout(layoutConfig);
		Border blacklineEstablishPanel = BorderFactory.createTitledBorder("");
		panelConfig.setBorder(blacklineEstablishPanel);
		panelConfig.setBackground(Color.white);
		
		// 1.1. panel panelTaskControl=================================================================================
		JPanel panelTaskControl = new JPanel();
		GridBagLayout layoutTaskControl = new GridBagLayout();
		panelTaskControl.setLayout(layoutTaskControl);

		JButton btStart   = new JButton("START");
		JButton btStop    = new JButton("STOP");
		JButton btSave    = new JButton("Lưu cấu hình");
		JLabel noteStatus = new JLabel();
		
		btStart.setEnabled(true);
		btStop.setEnabled(false);
		btSave.setEnabled(true);
		noteStatus.setVisible(true);
		
		Insets istStart   = new Insets(10, 20, 5, -60);
		Insets istStop    = new Insets(10, -100, 5, 0);
		Insets istSave    = new Insets(10, 0, 5, 20);
		Insets istNode    = new Insets(0, 0, 0, 0);
		
		noteStatus.setForeground(Color.RED);
		
		addComponent(istStart, panelTaskControl, btStart, 0, 0, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE);
		addComponent(istStop , panelTaskControl, btStop , 1, 0, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE);
		addComponent(istSave , panelTaskControl, btSave, 4, 0, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE);
		addComponent(istNode , panelTaskControl, noteStatus, 0, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		
		// 1.2. panel Setting=====================================================================================
		JPanel panelSetting = new JPanel();
		GridBagLayout layoutSetting = new GridBagLayout();
		panelSetting.setLayout(layoutSetting);
		Border blacklineSetting = BorderFactory.createTitledBorder("Đặt lịch");
		panelSetting.setBorder(blacklineSetting);
		panelSetting.setBackground(Color.white);
		
		// 1.1.1. panelIntervalSimple
		JPanel panelIntervalSimple = new JPanel();
		GridBagLayout layoutIntervalSimple = new GridBagLayout();
		panelIntervalSimple.setLayout(layoutIntervalSimple);
		panelSetting.setLayout(layoutIntervalSimple);
		Border blacklineIntervalSimple = BorderFactory.createTitledBorder("");
		panelIntervalSimple.setBorder(blacklineIntervalSimple);
		//panelIntervalSimple.setBackground(Color.white);

		//============ RadioButton "Một lần"====RadioButton "Hàng ngày"=============
		JRadioButton r1 = new JRadioButton();
		JRadioButton r2 = new JRadioButton();
		
		r1.setText("Một lần");
		r2.setText("Hàng ngày");
		r1.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
		r2.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
		
		ButtonGroup buttonGroupRadio = new ButtonGroup();
		buttonGroupRadio.add(r1);
		buttonGroupRadio.add(r2);

		Insets istR1 = new Insets(10, 5, 5, 5);
		Insets istR2 = new Insets(5, 5, 10, 5);
		
		addComponent(istR1, panelIntervalSimple, r1, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		addComponent(istR2, panelIntervalSimple, r2, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

		// =======Start Time=============================================================
		// Panel StartTime
		JPanel panelStartTime = new JPanel();
		GridBagLayout layoutStartTime = new GridBagLayout();
		panelStartTime.setLayout(layoutStartTime);
		//panelStartTime.setBackground(Color.white);
		JTextField textFieldRecur = new JTextField();

		JSpinner textIntervalPeriod = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		textIntervalPeriod.setBorder(null);
		
		textFieldRecur.setEnabled(false);
		textIntervalPeriod.setEnabled(false);
		// add label Start to Panel Setting:
		JLabel startLb = new JLabel("Hiệu lực từ:");
		JLabel stopLb = new JLabel("Đến: ");
		startLb.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
		stopLb.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
		
		//========================= datePickerStart ======================================================
		DatePickerSettings datePickerSettingsStart = new DatePickerSettings();
		datePickerSettingsStart.setFormatForDatesCommonEra("dd-MM-yyyy");
		datePickerStart = new DatePicker(datePickerSettingsStart);
		datePickerStart.setDateToToday();

		//========================= datePickerStop ======================================================
		DatePickerSettings datePickerSettingsStop = new DatePickerSettings();
		datePickerSettingsStop.setFormatForDatesCommonEra("dd-MM-yyyy");
		datePickerStop = new DatePicker(datePickerSettingsStop);
		datePickerStop.setDateToToday();
		
		Insets inset1010510 = new Insets(10, 10, 5, 10);

		addComponent(inset1010510, panelStartTime, startLb, 0, 0, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH);
		addComponent(inset1010510, panelStartTime, datePickerStart, 1, 0, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH);
		addComponent(inset1010510, panelStartTime, stopLb, 3 , 0, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH);
		addComponent(inset1010510, panelStartTime, datePickerStop, 4, 0, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH);
		
		//========================= datePickerStart, datePickerStop setEnabled ===========================
		datePickerStart.setEnabled(false);
		datePickerStop.setEnabled(false);
		//========================= datePickerStart, datePickerStop addDateChangeListener ====================
		datePickerStart.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				if (datePickerStart.getDate() != null) {
					datePickerSettingsStop.setDateRangeLimits(
							today.minusDays(FileUtil
									.getDiffWithTodayByDay(datePickerStart)),
							today.plusDays(3000));
					textFieldRecur.setEnabled(false);
					textIntervalPeriod.setEnabled(true);
				} else {
					textFieldRecur.setEnabled(false);
					textIntervalPeriod.setEnabled(false);
				}
			}
		});

		datePickerStop.addDateChangeListener(new DateChangeListener() {
					@Override
					public void dateChanged(DateChangeEvent arg0) {
						FileUtil.checkDateStop(textFieldRecur, r2, datePickerStart,datePickerStop);
						noteStatus.setVisible(false);
						try {
							FileUtil.noteStatusAction(btStart, btStop,
									noteStatus, datePickerStop.getDate());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		//====================================================================================================
		Insets inset510 = new Insets(5, 10, 5, 10);
		JLabel jlScantime = new JLabel("Thời gian quét:");
		jlScantime.setFont(new Font("TimesRoman", Font.CENTER_BASELINE,13));
		addComponent(inset510, panelStartTime, jlScantime, 0, 1, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		
		//====================================================================================================
		JRadioButton r3 = new JRadioButton();
		JRadioButton r4 = new JRadioButton();
		
		r3.setText("Cả ngày");
		r4.setText("Từ");
		
		r3.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
		r4.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
		
		r3.setEnabled(false);
		r4.setEnabled(false);
		
		addComponent(inset510, panelStartTime, r3, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH);
		addComponent(inset510, panelStartTime, r4, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH);
		
		ButtonGroup buttonGroupRadio1 = new ButtonGroup();
		buttonGroupRadio1.add(r3);
		buttonGroupRadio1.add(r4);

		//====================================================================================================
		JLabel s2= new JLabel("Đến:");
		s2.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
		
		//=======================================================================================================
		TimePickerSettings t1 = new TimePickerSettings();
		TimePickerSettings t2 = new TimePickerSettings();
		
		t1.setInitialTimeToNow();
		t2.setInitialTimeToNow();
		
		t1.setFormatForDisplayTime(PickerUtilities.createFormatterFromPatternString("HH:mm",t1.getLocale()));
		t2.setFormatForDisplayTime(PickerUtilities.createFormatterFromPatternString("HH:mm",t2.getLocale()));
		
		timePickerStart = new TimePicker(t1);
		timePickerStop = new TimePicker(t2);
		
		timePickerStart.setEnabled(false);
		timePickerStop.setEnabled(false);
		
		addComponent(inset510, panelStartTime, timePickerStart, 2, 2, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		addComponent(inset510, panelStartTime, s2, 3, 2, 1, 1, GridBagConstraints.LAST_LINE_END, GridBagConstraints.BOTH);
		addComponent(inset510, panelStartTime, timePickerStop, 4, 2, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		
		//===================================== Panel Interval =======================================================
		JPanel panelInterval = new JPanel();
		GridBagLayout layoutInterval = new GridBagLayout();
		panelInterval.setLayout(layoutInterval);

		JLabel intervalPeriod = new JLabel("Chu kỳ lặp lại: ");
		
		intervalPeriod.setFont(new Font("TimesRoman", Font.CENTER_BASELINE,13));
		addComponent(inset510, panelInterval, intervalPeriod, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

		Border blacklineIntervalPeriod = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		textIntervalPeriod.setBorder(blacklineIntervalPeriod);

		textIntervalPeriod.setFont(new Font("TimesRoman", Font.CENTER_BASELINE,16));
		JLabel intervalUnitTime = new JLabel("phút");
		intervalUnitTime.setFont(new Font("TimesRoman", Font.CENTER_BASELINE,13));
		
		addComponent(inset510, panelInterval, textIntervalPeriod, 1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		addComponent(inset510, panelInterval, intervalUnitTime, 2, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

		// =======Start Time=====================


		// ============ panel StartTime to add Panel Setting================================================================
		addComponent(panelSetting, panelIntervalSimple, 0, 0, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
		addComponent(panelSetting, panelStartTime, 1, 0, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
		addComponent(panelSetting, panelInterval, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE);

		// ======== panel Advance panelSaveSetting=============================================================
		JPanel panelSaveSetting = new JPanel();
		GridBagLayout layoutSaveSetting = new GridBagLayout();
		panelSaveSetting.setLayout(layoutSaveSetting);
		
		Border blacklineSaveSetting = BorderFactory.createTitledBorder("Thư mục lưu báo cáo");
		panelSaveSetting.setBorder(blacklineSaveSetting);
		panelSaveSetting.setBackground(Color.white);

		// Label Save textField
		JLabel lbSaveFolder = new JLabel("Đường dẫn:");
		JTextArea textSaveFolder = new JTextArea(1, 40);
		
		lbSaveFolder.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
		Border blacklineTxtSaveFolder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		textSaveFolder.setBorder(blacklineTxtSaveFolder);
		textSaveFolder.setFont(new Font("TimesRoman", Font.CENTER_BASELINE,13));
		Insets insets25 = new Insets(0, 15, 0, 0);

		// Label Save textField

		textSaveFolder.getDocument().addDocumentListener(
				new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent arg0) {
						btStart.setEnabled(true);
						btStop.setEnabled(false);
						btSave.setEnabled(true);
					}

					@Override
					public void insertUpdate(DocumentEvent arg0) {
						btStart.setEnabled(true);
						btStop.setEnabled(false);
						btSave.setEnabled(true);
						FileUtil.downloadFolder = textSaveFolder.getText();
					}

					@Override
					public void changedUpdate(DocumentEvent arg0) {
						btStart.setEnabled(true);
						btStop.setEnabled(false);
						btSave.setEnabled(true);
						FileUtil.downloadFolder = textSaveFolder.getText();
					}
				});
		
		// add thư mục báo cáo đường dẫn
		
		addComponent(insets25, panelSaveSetting, lbSaveFolder, 0, 0, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
		addComponent(insets25, panelSaveSetting, textSaveFolder, 1, 0, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
		
		// số tiền và trạng thái
		JPanel panelConditionSetting = new JPanel();
		GridBagLayout layoutConditionSetting = new GridBagLayout();
		panelConditionSetting.setLayout(layoutConditionSetting);
		
		Border bd1 = BorderFactory.createTitledBorder("Điều kiện giao dịch");
		panelConditionSetting.setBorder(bd1);
		panelConditionSetting.setBackground(Color.white);

		JLabel lbMoney = new JLabel("Số tiền:");
		JTextArea textMoney = new JTextArea(1, 40);
		JLabel lbStatus = new JLabel("Trạng thái:");
		JTextArea textStatus = new JTextArea(1, 40);
		textStatus.setText("Nhập dữ liệu");
		textStatus.setEnabled(false);
		
		textMoney.getDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void removeUpdate(DocumentEvent arg0) {
						btStart.setEnabled(true);
						btStop.setEnabled(false);
						btSave.setEnabled(true);
					}

					@Override
					public void insertUpdate(DocumentEvent arg0) {
						btStart.setEnabled(true);
						btStop.setEnabled(false);
						btSave.setEnabled(true);
						FileUtil.downloadFolder = textSaveFolder.getText();
					}

					@Override
					public void changedUpdate(DocumentEvent arg0) {
						btStart.setEnabled(true);
						btStop.setEnabled(false);
						btSave.setEnabled(true);
						FileUtil.downloadFolder = textSaveFolder.getText();
					}
				});
				
		lbMoney.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
		lbStatus.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 13));
				
		Border blacklineTextMoney = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		textMoney.setBorder(blacklineTextMoney);
		textMoney.setFont(new Font("TimesRoman", Font.CENTER_BASELINE,13));
		textStatus.setBorder(blacklineTextMoney);
		textStatus.setFont(new Font("TimesRoman", Font.CENTER_BASELINE,13));
		
		addComponent(new Insets(0, 15, 0, 10),panelConditionSetting, lbMoney, 0, 0, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL);
		addComponent(new Insets(0, -100, 0, 20),panelConditionSetting, textMoney, 1, 0, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL);
		addComponent(new Insets(0, 15, 0, 10),panelConditionSetting, lbStatus, 2, 0, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL);
		addComponent(new Insets(0, -100, 0, 20),panelConditionSetting, textStatus, 3, 0, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL);
		
		System.out
				.println("==================================Loading last status=========================================");

		File checkLoading = new File(Config.getParam("bot.aml.saveSettings"));
		if (checkLoading.exists()) {
			if (FileUtil.loadFileAndReturnElement().size() != 0 && FileUtil.loadFileAndReturnElement().size() == 12) {
				FileUtil.loadingRecentStatus(textIntervalPeriod,
						FileUtil.loadFileAndReturnElement().get(11),
						r1, Boolean.parseBoolean(FileUtil.loadFileAndReturnElement().get(0)),
						r2, Boolean.parseBoolean(FileUtil.loadFileAndReturnElement().get(1)),
						datePickerStart, LocalDate.parse((FileUtil.loadFileAndReturnElement().get(2))),
						datePickerStop, LocalDate.parse((FileUtil.loadFileAndReturnElement().get(3))),
						r3, Boolean.parseBoolean(FileUtil.loadFileAndReturnElement().get(4)),
						r4, Boolean.parseBoolean(FileUtil.loadFileAndReturnElement().get(5)),
						timePickerStart, LocalTime.parse((FileUtil.loadFileAndReturnElement().get(6))),
						timePickerStop, LocalTime.parse((FileUtil.loadFileAndReturnElement().get(7))),
						textMoney, FileUtil.loadFileAndReturnElement().get(8),
						textStatus, FileUtil.loadFileAndReturnElement().get(9),
						textSaveFolder, FileUtil.loadFileAndReturnElement().get(10));
				FileUtil.noteStatusAction(btStart, btStop, noteStatus,LocalDate.parse((FileUtil.loadFileAndReturnElement().get(3))));
			} else {
				FileUtil.loadingRecentStatus(textIntervalPeriod, "0",
						r1, true,
						r2, false,
						datePickerStart, LocalDate.now(),
						datePickerStop,LocalDate.now(),
						r3, true,
						r4, false,
						timePickerStart,LocalTime.now(),					
						timePickerStop,LocalTime.now(),
						textMoney,"",
						textStatus,"Nhập dữ liệu",
						textSaveFolder,"");
			}
		} else {
			FileUtil.loadingRecentStatus(textIntervalPeriod, "0",
					r1, true,
					r2, false,
					datePickerStart, LocalDate.now(),
					datePickerStop,LocalDate.now(),
					r3, true,
					r4, false,
					timePickerStart,LocalTime.now(),					
					timePickerStop,LocalTime.now(),
					textMoney,"",
					textStatus,"Nhập dữ liệu",
					textSaveFolder,"");
		}
//=======================================Sự kiện================================================

		r1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textFieldRecur.setEnabled(false);
				textIntervalPeriod.setEnabled(false);
				datePickerStart.setEnabled(false);
				datePickerStop.setEnabled(false);
				period = 1;
				
				r3.setEnabled(false);
				r4.setEnabled(false);
				
				timePickerStart.setEnabled(false);
				timePickerStop.setEnabled(false);
				
				btStop.setEnabled(false);
			}
		});

		r2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textFieldRecur.setEnabled(false);
				datePickerStart.setEnabled(true);
				datePickerStop.setEnabled(true);
				
				r3.setEnabled(true);
				r4.setEnabled(true);
				
				textIntervalPeriod.setEnabled(true);
				btStop.setEnabled(false);
				if (r3.isSelected()) {
					timePickerStart.setEnabled(false);
					timePickerStop.setEnabled(false);
				} else if (r4.isSelected()) {
					timePickerStart.setEnabled(true);
					timePickerStop.setEnabled(true);
				}
			}
		});
		
		r3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textFieldRecur.setEnabled(true);
				textIntervalPeriod.setEnabled(true);
					
				timePickerStart.setEnabled(false);
				timePickerStop.setEnabled(false);
				period = 1;
			}
		});
		
		r4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textFieldRecur.setEnabled(true);
				datePickerStop.setEnabled(true);
				timePickerStart.setEnabled(true);
				timePickerStop.setEnabled(true);
				textIntervalPeriod.setEnabled(true);
				period = 24 * 60;
			}
		});
//============================Lưu cài đặt vào file=============================================
		btSave.addActionListener(e -> {
			btStop.setEnabled(false);
			BotUI.jrdOneTime = r1.isSelected();
			BotUI.jrdDaily = r2.isSelected();
			
			BotUI.jrdAllDay = r3.isSelected();
			BotUI.jrdChooseTime = r4.isSelected();
			
			if (!textIntervalPeriod.getValue().equals(0)) {
				BotUI.intevalPeriod = Integer.parseInt(textIntervalPeriod
						.getValue().toString());
			} else {
				BotUI.intevalPeriod = 0;
			}
			
			BotUI.amountExchange = Integer.parseInt(textMoney.getText());
			BotUI.statusExchange = textStatus.getText();
			BotUI.saveFile = textSaveFolder.getText();
			
			
			BotUI.startDateStr = BotUI.datePickerStart.getDate().toString();
			BotUI.startTimeStr = BotUI.timePickerStart.getTime().toString();

			BotUI.endDateStr = BotUI.datePickerStop.getDate().toString();
			BotUI.endTimeStr = BotUI.timePickerStop.getTime().toString();

			String saveString = BotUI.jrdOneTime + "@"
					+ BotUI.jrdDaily + "@"
					+ BotUI.startDateStr + "@"
					+ BotUI.endDateStr + "@"					
					+ BotUI.jrdAllDay + "@"
					+ BotUI.jrdChooseTime + "@"
					+ BotUI.startTimeStr + "@"
					+ BotUI.endTimeStr + "@"
					+ BotUI.amountExchange + "@"
					+ BotUI.statusExchange + "@"
					+ BotUI.saveFile + "@"
					+ BotUI.intevalPeriod;
			LogFile.saveTextEstablish(saveString);
			noteStatus.setText("Đã lưu cài đặt.");
			CitadBotJob.displayAndWriteLog("Lưu cài đặt.");
			try {
				FileUtil.loadFileAndReturnElement();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});	
		// Start Button =================== Chạy chương trình==============
		btStart.addActionListener(e -> {
			 Pattern pattern = Pattern.compile("\\d*");
		        Matcher matcher = pattern.matcher(textMoney.getText());
		        if (textMoney.getText().isEmpty()) {
					noteStatus.setVisible(true);
					noteStatus.setText("Số tiền không được để trống:");
					textMoney.requestFocusInWindow();
				}else{
			        if (!matcher.matches()) {
			        	noteStatus.setVisible(true);
						noteStatus.setText("Số tiền nhập vào phải là số");
						textMoney.requestFocusInWindow();
			        } else {
			        	BotUI.amountExchange = Integer.parseInt(textMoney.getText());
			        	BotUI.statusExchange = textStatus.getText();
			        	
			        	timer = FileUtil.actionBtStart(textIntervalPeriod.getValue().toString(),
								datePickerStart, timePickerStart,datePickerStop,timePickerStop,
								textSaveFolder, dateStartArr);
			        	System.out.println(timer);
						datePickerStart.setEnabled(false);
						datePickerStop.setEnabled(false);
						timePickerStart.setEnabled(false);
						timePickerStop.setEnabled(false);
						
						r1.setEnabled(false);
						r2.setEnabled(false);
						
						btStart.setEnabled(false);
						btStop.setEnabled(true);
						btSave.setEnabled(false);
						noteStatus.setVisible(true);
						if (textSaveFolder.getText().isEmpty()) {
							noteStatus.setVisible(true);
							noteStatus.setText("Dữ liệu tải xuống được lưu vào tệp mặc định: " + FileUtil.downloadFolder);
						}
					}
			      }
		       CitadBotJob.displayAndWriteLog("Chạy Chương trình");
		       noteStatus.setText("Chạy Chương trình");
			}
		);
		
		// Stop Button ============== Dừng chương trình===================
		btStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				r1.setEnabled(true);
				r2.setEnabled(true);
				
				btStart.setEnabled(true);
				btStop.setEnabled(false);
				btSave.setEnabled(true);
				
				timer.cancel();
				CitadBotJob.displayAndWriteLog("Dừng chương trình");

			}
		});
		addComponent(panelConfig, panelSetting, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		addComponent(panelConfig, panelConditionSetting , 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		addComponent(panelConfig, panelSaveSetting, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		addComponent(panelConfig, panelTaskControl, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		return panelConfig;
	}

	JTable createTable(JTable table){
		table = new JTable(model);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);
		// STT
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		// Kind
		table.getColumnModel().getColumn(1).setPreferredWidth(140);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		// Time creating
		table.getColumnModel().getColumn(2).setPreferredWidth(180);
		table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		// Status
		table.getColumnModel().getColumn(3).setPreferredWidth(130);
		table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		// Note
		table.getColumnModel().getColumn(4).setPreferredWidth(130);
		table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		return table;
	}

	 private static void addComponent(Container container, Component component, int gridx, int gridy,
		      int gridwidth, int gridheight, int anchor, int fill) {
		    GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0,
		        anchor, fill, new Insets(0,0,0,0), 0, 0);
		    container.add(component, gbc);
		  }
	 
	 private static void addComponent(Insets insets, Container container, Component component, int gridx, int gridy,
		      int gridwidth, int gridheight, int anchor, int fill) {
		    GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0,
		        anchor, fill, insets, 0, 0);
		    container.add(component, gbc);
		  }

	 private static void addComponent(Insets insets,Container container, Component component, int gridx, int gridy,
		      int gridwidth, int gridheight, double weightx, double weighty,int anchor, int fill,int ipadx, int ipady) {
		    GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, weightx, weighty,
		        anchor, fill, insets, ipadx, ipady);
		    container.add(component, gbc);
		  }
	 
	 private static void setSizeColor(Container container, Color color){
		 Dimension dimension = new  Dimension(screenSize.width / 2,screenSize.height *1/2);
		 container.setPreferredSize(dimension);
		 container.setBackground(color);
	 }
}
