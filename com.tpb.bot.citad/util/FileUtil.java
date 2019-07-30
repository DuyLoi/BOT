package com.tpb.bot.citad.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.tpb.bot.citad.config.Config;
import com.tpb.bot.citad.constant.Constant;
import com.tpb.bot.citad.job.CitadBotJob;
import com.tpb.bot.citad.ui.BotUI;


public class FileUtil {
	public static String downloadFolder = defaultPathString();
	public static String printLogOut = "";

	public static void noteStatusAction(JButton btStart, JButton btStop,
			JLabel noteStatus, LocalDate dateMaking) throws Exception {
		String formattedDate = BotUI.today.format(DateTimeFormatter
				.ofPattern("dd-MM-yyyy"));
		if (dateMaking.isBefore(LocalDate.now())) {
			noteStatus.setVisible(true);
			noteStatus
					.setText("*Chú ý: Ngày kết thúc đang có giá trị trước ngày hôm nay: "
							+ "\" " + formattedDate + " \"");
			btStart.setEnabled(true);
			btStop.setEnabled(false);
		} else {

			btStart.setEnabled(true);
			btStop.setEnabled(false);
			noteStatus.setVisible(true);
		}
	}

	public static void loadingRecentStatus(JSpinner textIntervalPeriod,
			String textIntervalPeriodValue, JRadioButton r1, Boolean r1Status,
			JRadioButton r2, Boolean r2Status, DatePicker datePickerStart,
			LocalDate dateStart, DatePicker datePickerStop, LocalDate dateStop,
			JRadioButton r3, Boolean r3Status, JRadioButton r4,
			Boolean r4Status, TimePicker timePickerStart, LocalTime timeStart,
			TimePicker timePickerStop, LocalTime timeStop,	
			JTextArea textnoney, String moneyvalue,
			JTextArea textstatus, String statusvalue,
			JTextArea textSaveFolder, String savePath) throws Exception {
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		
		setStatusAndVisibleOfRadioButtonFrequency(r1, r1Status);
		setStatusAndVisibleOfRadioButtonFrequency(r2, r2Status);
		setStatusAndVisibleOfRadioButtonFrequency(r3, r3Status);
		setStatusAndVisibleOfRadioButtonFrequency(r4, r4Status);

		textIntervalPeriod.setValue(Integer.parseInt(textIntervalPeriodValue));

		if (r1.isSelected()) {
			BotUI.period = 1;
			textIntervalPeriod.setEnabled(false);
			System.out.println(BotUI.period);
		} else if (r2.isSelected()) {
			r3.setEnabled(true);
			r4.setEnabled(true);
			datePickerStart.setEnabled(true);
			datePickerStop.setEnabled(true);
			textIntervalPeriod.setEnabled(true);
			if (r3.isSelected()) {
				timePickerStart.setEnabled(false);
				timePickerStop.setEnabled(false);
				BotUI.period = 1;
			} else if (r4.isSelected()) {
				timePickerStart.setEnabled(true);
				timePickerStop.setEnabled(true);
				
				if (textIntervalPeriod.getValue().toString().equals("0")) {
					BotUI.period = 24 * 60;
				} else {
					BotUI.period = Integer.parseInt(textIntervalPeriodValue);
				}
			}
		}
		
		datePickerStart.setDate(dateStart);
		timePickerStart.setTime(timeStart);
		timePickerStart.setText(timeStart.format(dtf).toString());

		datePickerStop.setDate(dateStop);
		timePickerStop.setTime(timeStop);
		timePickerStop.setText(timeStop.format(dtf).toString());
		textnoney.setText(moneyvalue);
		textstatus.setText(statusvalue);
		textSaveFolder.setText(savePath);

	}

	static void setStatusAndVisibleOfRadioButtonFrequency(JRadioButton r, Boolean rStatus) {
		r.setSelected(rStatus);
	}

	public static String defaultPathString() {
		Preferences pref = Preferences.userRoot();
		return pref.get("DEFAULT_PATH", "");
	}

	@SuppressWarnings("deprecation")
	public static Calendar calendarFromPickers(DatePicker datePicker,
			TimePicker timePicker) {
		Date date = sqlDateFrom(datePicker);
		Time time = sqlTimeFrom(timePicker);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
		calendar.set(Calendar.MINUTE, time.getMinutes());
		calendar.set(Calendar.SECOND, time.getSeconds());
		return calendar;
	}

	private static Time sqlTimeFrom(TimePicker timePicker) {
		LocalTime LcTimeStart = timePicker.getTime();
		return java.sql.Time.valueOf(LcTimeStart);
	}

	private static Date sqlDateFrom(DatePicker datePicker) {
		LocalDate localDate = datePicker.getDate();
		return java.sql.Date.valueOf(localDate);
	}

	public static long getDiffWithTodayByDay(DateTimePicker dateTimeStart) {
		final LocalDate today = LocalDate.now();
		long getDiffWithTodayByDay = today.toEpochDay()
				- dateTimeStart.getDatePicker().getDate().toEpochDay();
		return getDiffWithTodayByDay;
	}

	public static long getDiffWithTodayByDay(DatePicker dateStart) {
		final LocalDate today = LocalDate.now();
		long getDiffWithTodayByDay = today.toEpochDay()
				- dateStart.getDate().toEpochDay();
		return getDiffWithTodayByDay;
	}

	@SuppressWarnings("unused")
	public static void checkDateStop(JTextField textFieldRecur,
			JRadioButton r2, DateTimePicker dateTimePickerStart,
			DateTimePicker dateTimePickerStop) {
		if (dateTimePickerStart.getDatePicker().getDate() != null) {
			if (dateTimePickerStop.getDatePicker().getDate() != null) {
				LocalDate dateStart = dateTimePickerStart.getDatePicker()
						.getDate();
				LocalDate dateStop = dateTimePickerStop.getDatePicker()
						.getDate();

				if (dateStop.compareTo(dateStart.plusWeeks(1)) < 0) {
					if (dateStop.compareTo(dateStart.plusDays(1)) < 0) {
						r2.setEnabled(true);
					}
					if (dateStop.compareTo(dateStart.plusDays(1)) > 0) {
						r2.setEnabled(true);
					}
					Duration diff = Duration.between(dateStart.atStartOfDay(),
							dateStop.atStartOfDay());
					long diffDays = diff.toDays();

				}
				if (dateStop.compareTo(dateStart.plusWeeks(1)) > 0) {
					textFieldRecur.setEnabled(true);
				}

			}
		}
	}

	@SuppressWarnings("unused")
	public static void checkDateStop(JTextField textFieldRecur,
			JRadioButton r2, DatePicker datePickerStart,
			DatePicker datePickerStop) {
		if (datePickerStart.getDate() != null) {
			if (datePickerStop.getDate() != null) {
				LocalDate dateStart = datePickerStart.getDate();
				LocalDate dateStop = datePickerStop.getDate();

				if (dateStop.compareTo(dateStart.plusWeeks(1)) < 0) {
					if (dateStop.compareTo(dateStart.plusDays(1)) < 0) {
						r2.setEnabled(true);
					}
					if (dateStop.compareTo(dateStart.plusDays(1)) > 0) {
						r2.setEnabled(true);
					}
					Duration diff = Duration.between(dateStart.atStartOfDay(),
							dateStop.atStartOfDay());
					long diffDays = diff.toDays();

				}
				if (dateStop.compareTo(dateStart.plusWeeks(1)) > 0) {
					textFieldRecur.setEnabled(true);
				}

			}
		}
	}

	public static void setRadioButtonStatus(JRadioButton r1, JRadioButton r2,
			boolean status) {
		r1.setEnabled(status);
		r2.setEnabled(status);
	}

	public static Timer actionBtStart(String textIntervalPeriod,
			DatePicker datePickerStart, TimePicker timePickerStart,
			DatePicker datePickerStop, TimePicker timePickerStop,
			JTextArea txtSaveFolder, ArrayList<LocalDate> dateStartArr) {
		
		Timer timer = null;
		
		Calendar calEnd = calendarFromPickers(datePickerStop, timePickerStop);
		Calendar calStart = calendarFromPickers(datePickerStart,timePickerStart);

		// RUN BY DAY OF WEEK
		if (dateStartArr.size() > 0) {
			for (int i = 0; i <= dateStartArr.size(); i++) {
				calStart.set(dateStartArr.get(i).getYear(), dateStartArr.get(i)
						.getMonthValue() - 1, dateStartArr.get(i)
						.getDayOfMonth());

				System.out.println(calStart.getTime().toString());

				if (!textIntervalPeriod.equals("0")) {
					if (!textIntervalPeriod.equals("-1")) {
						BotUI.period = Integer.parseInt(textIntervalPeriod);
					} else {
						BotUI.period = 1;
					}
				}
				Timer timerRunDayOfWeek = new Timer();
				timerRunDayOfWeek.schedule(new TimerTask() {

					@Override
					public void run() {
						downloadFile(txtSaveFolder);
						Date dateNow = new Date();
						if (dateNow.compareTo(calEnd.getTime()) > 0) {
							System.out.println("Stop!");
							timerRunDayOfWeek.cancel();
						}

					}
				}, calStart.getTime(), TimeUnit.MILLISECONDS.convert(
						BotUI.period, TimeUnit.MINUTES));
				timer = timerRunDayOfWeek;
			}
		} else {
			// RUN DAILY 8:00 - 17:30
			if (!textIntervalPeriod.equals("0")) {
				if (!textIntervalPeriod.equals("-1")) {
					BotUI.period = Integer.parseInt(textIntervalPeriod);
				} else {
					BotUI.period = 1;
				}
			}
			Timer timerDailyJob = new Timer();
			timerDailyJob.schedule(new TimerTask() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					System.out.println("Running!");
					try {
						downloadFile(txtSaveFolder);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						Date dateNow = new Date();
						dateNow.setSeconds(00);
						Date datePause = new Date();
						datePause.setHours(17);
						datePause.setMinutes(30);
						datePause.setSeconds(00);

						if (dateNow.compareTo(calEnd.getTime()) > 0) {
							System.out.println("Stop!");
							timerDailyJob.cancel();
						}

						if (dateNow.compareTo(datePause) >= 0) {
							System.out.println("Sleeping!");

							try {
								Date dateNow2 = new Date();
								dateNow2.setHours(8);
								dateNow2.setMinutes(00);
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(dateNow2.getHours());
								stringBuilder.append(":");
								stringBuilder.append(dateNow2.getMinutes());
								String time1 = stringBuilder.toString();
								String time2 = Constant.TIME_PAUSE_DAILY;
								SimpleDateFormat format = new SimpleDateFormat("HH:mm");
								Date date1;
								date1 = format.parse(time1);
								Date date2 = format.parse(time2);
								long totalTimeSleep;
								System.out.println(dateNow2);
								if (date1.compareTo(date2) < 0) {
									totalTimeSleep = (date2.getTime() - date1
											.getTime());
								} else {
									totalTimeSleep = (date2.getTime() - date1
											.getTime()) + (24 * 60 * 60 * 1000);
								}
								Thread.sleep(totalTimeSleep);
							} catch (ParseException e) {
								e.printStackTrace();
							}

							System.out.println("Wake up!");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}, calStart.getTime(), TimeUnit.MILLISECONDS.convert(BotUI.period,TimeUnit.MINUTES));
			System.out.println("BotUI.period: "
					+ TimeUnit.MILLISECONDS.convert(BotUI.period,TimeUnit.MINUTES));

			timer = timerDailyJob;
		}
		loadMonitor(printLogOut);
		return timer;
	}

	public static void loadMonitor(String logs) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				BotUI.display.append(logs);
			}
		});
	}

	private static void downloadFile(JTextArea txtSaveFolder) {
		try {
			downloadFolder = txtSaveFolder.getText();
			File dir = new File(downloadFolder);
			if (!dir.exists()) {
				downloadFolder = defaultPathString();
			}
			CitadBotJob abc = new CitadBotJob();
			abc.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> loadFileAndReturnElement() throws Exception {
		ArrayList<String> loadingStatus = new ArrayList<String>();
		File file = new File(Config.getParam("bot.aml.saveSettings"));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		String st = "";
		while ((st = br.readLine()) != null) {
			String s1 = st;
			String[] words = s1.split("@");
			for (String w : words) {
				loadingStatus.add(w);
			}
		}
		br.close();
		return loadingStatus;
	}

	public static ArrayList<String> loadFileLog() throws Exception {
		ArrayList<String> loadingStatus = new ArrayList<String>();
		File file = new File(Config.getParam("bot.aml.resultFilDownload"));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		String st = "";
		while ((st = br.readLine()) != null) {
			String s1 = st;
			String[] paragraph = s1.split("\n");
			for (String line : paragraph) {
				loadingStatus.add(line);
			}
		}
		br.close();
		return loadingStatus;
	}
}