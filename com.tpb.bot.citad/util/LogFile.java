package com.tpb.bot.citad.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.github.lgooddatepicker.components.DatePicker;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.text.pdf.BaseFont;
import com.tpb.bot.citad.config.Config;
import com.tpb.bot.citad.job.CitadBotJob;

public class LogFile {

	private String dateFileDownload;
	private String name;
	private String status;
	private String note;
	
	private DatePicker jdStartDateRs;
	private DatePicker jdStopDateRs;
		
	private static String dateToday;
	public static final String FONT = "font\\vuTimes.ttf";
	public static final int fontTitleSize = 14;
	public static final int fontTitleTableSize = 12;
	public static final int fontContentTableSize = 11;
	
	public LogFile(String executiontime, String numberentries,String status, String note) {
		super();
		this.dateFileDownload = executiontime;
		this.name = numberentries;
		this.status = status;
		this.note = note;
	}
	
	public LogFile(List<LogFile> logLinkedList,String nameFile, DatePicker jdStartDateRs, DatePicker jdStopDateRs) {
		super();
		this.jdStartDateRs = jdStartDateRs;
		this.jdStopDateRs = jdStopDateRs;
	}
	
	public String getDateFileDownload() {
		return dateFileDownload;
	}

	public void setDateFileDownload(String executiontime) {
		this.dateFileDownload = executiontime;
	}


	public String getName() {
		return name;
	}


	public void setName(String numberentries) {
		this.name = numberentries;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LogFile() {
		super();
	}

	@Override
	public String toString() {
		return dateFileDownload + "@" + name + "@"+ status +  "@" + note;
	}
	
	public static void saveTextEstablish(String saveString) {
		File file;
		  try {
			  file = new File(Config.getParam("bot.aml.saveSettings"));
					
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
				if (!file.exists()) {
					file.createNewFile();
				}
				out.append(saveString);
				System.out.println("Save Config Done");
				out.flush();
				out.close();
			        
			    } 
			   catch (UnsupportedEncodingException e) 
			   {
				System.out.println(e.getMessage());
			   } 
			   catch (IOException e) 
			   {
				System.out.println(e.getMessage());
			    }
			   catch (Exception e)
			   {
				System.out.println(e.getMessage());
		} 
	}	

//	public class SingleTaskTimer {
//	    private Timer timer = new Timer();
//	    private TimerTask task = null;
//
//	    public void setTask(TimerTask task) {
//	        this.task = task;
//	    }
//
//	    public void schedule(Date start, Date end, long period) {
//	        SingleTaskTimer self = this;
//	        timer = new Timer();
//	        if (task == null) {
//	            throw new IllegalStateException("Task not specified");
//	        } 
//	        else {
//	        		timer.schedule(task, start, period);
//		            timer.schedule(new TimerTask() {
//		                @Override
//		                public void run() {
//		                    self.cancel();
//		                }
//		            }, end);
//	        }
//	    }
//
//	    public void cancel() {
//	        try {
//	        	System.out.println("Timer cancel");
//	            timer.cancel();
//	            timer.purge();
//	        } catch (IllegalStateException e) {
//	        	e.printStackTrace();
//	        } finally {
//	            timer = new Timer();
//	        }
//	    }
//	}
	
	public static void writeLogFile(String name, String status,String note){
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		dateToday = df.format(date);
		String content="";	
		LogFile logaml =new LogFile( dateToday,name, status, note);
		content = logaml.toString();
		 try {
			 File file = new File("log/log.txt");
					
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true), "UTF8"));
				if (!file.exists()) {
					file.createNewFile();
				}
				out.append(content).append("\n");
				CitadBotJob.displayAndWriteLog("Ghi file log.txt");
				out.flush();
				out.close();
			        
			    } 
			   catch (UnsupportedEncodingException e) 
			   {
				System.out.println(e.getMessage());
			   } 
			   catch (IOException e) 
			   {
				System.out.println(e.getMessage());
			    }
			   catch (Exception e)
			   {
				System.out.println(e.getMessage());
		} 
	}

	public LogFile(DatePicker jdStartDateRs, DatePicker jdStopDateRs) {
		super();
		this.jdStartDateRs = jdStartDateRs;
		this.jdStopDateRs = jdStopDateRs;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector getAllData() throws Exception {

		Vector list = new Vector();
		try {
			ArrayList<String> logs = new ArrayList<String>(FileUtil.loadFileLog());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			int j = 1;
			System.out.println("logs.size() " + logs.size());
			for (int i = 0; i < logs.size(); i++) {
				String line = logs.get(i);
				String[] words = line.split("@");
				String date = words[0].toString().trim();
				
				LocalDate dateRun = LocalDate.parse(date,formatter);
				if (dateRun.isEqual(jdStartDateRs.getDate())
						&& dateRun.isBefore(jdStopDateRs.getDate().plusDays(1))) {
					Vector data = new Vector();
					data.addElement(j);
					data.addElement(words[1]);
					data.addElement(words[0]);
					data.addElement(words[2]);
					data.addElement(words[3]);
					list.add(data);
					j++;
				}
				if (dateRun.isAfter(jdStartDateRs.getDate())
						&& dateRun.isBefore(jdStopDateRs.getDate().plusDays(1))) {
					Vector data = new Vector();
					data.addElement(j);
					data.addElement(words[1]);
					data.addElement(words[0]);
					data.addElement(words[2]);
					data.addElement(words[3]);
					list.add(data);
					j++;
				}

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 

		return list;
	}


	public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();
	}

	public List<LogFile> getAllDataToPrintPDF() throws Exception {
		List<LogFile> list = new ArrayList<>();
		try {
			ArrayList<String> logs = new ArrayList<String>(FileUtil.loadFileLog());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			System.out.println(" logs.size() Read " + logs.size());
			for (int i = 0; i < logs.size(); i++) {
				String line = logs.get(i);
				String[] words = line.split("@");
				String date = words[0].toString().trim();
				System.out.println(" words.length Read " + words.length);

				LocalDate dateRun = LocalDate.parse(date,formatter);
				if (dateRun.isEqual(jdStartDateRs.getDate()) && dateRun.isBefore(jdStopDateRs.getDate().plusDays(1))) {
					LogFile data = new LogFile();
					data.setName(words[1]);
					data.setDateFileDownload(words[0]);
					data.setStatus(words[2]);
					data.setNote(words[3]);
				
					list.add(data);

				}
				if (dateRun.isAfter(jdStartDateRs.getDate()) && dateRun.isBefore(jdStopDateRs.getDate().plusDays(1))) {
					LogFile data = new LogFile();
					data.setName(words[1]);
					data.setDateFileDownload(words[0]);
					data.setStatus(words[2]);
					data.setNote(words[3]);
					list.add(data);
				}

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return list;
	}
	


	public void drawTablePDF(List<LogFile> logList, String nameFile)
			throws Exception {
		String DEST = FileUtil.downloadFolder + "/" + nameFile+ ".pdf";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
//		pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
		pdfDoc.setDefaultPageSize(PageSize.A4);

		PdfFont font = PdfFontFactory.createFont(FONT, BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
		Document doc = new Document(pdfDoc);
		doc.setFont(font);
		Paragraph title = new Paragraph("BÁO CÁO \n Từ ngày "
				+ jdStartDateRs.getDate().format(dtf) + " đến ngày "
				+ jdStopDateRs.getDate().format(dtf) + "\n");
		title.setTextAlignment(TextAlignment.CENTER);
		title.setBold();
		title.setFontSize(fontTitleSize);
		doc.add(title);
		float[] pointColumnWidths = { 30F, 130F, 130F, 120F, 110F};
		Table table = new Table(pointColumnWidths);
		addCell(table, "STT", true);
		addCell(table, "Thời gian thực hiện", true);
		addCell(table, "Số bút toán", true);
		addCell(table, "Trạng thái", true);
		addCell(table, "Ghi chú", true);

		List<LogFile> list = logList;
		int i = 0;
		int j = 1;
		while (i < list.size()) {
			String valueId = "" + j;
			String valueCreatingTime = list.get(i).getDateFileDownload();
			String valueFileName = list.get(i).getName();
			String valueStatus = list.get(i).getStatus();
			String valueNote = list.get(i).getNote();
			i++;
			j++;

			addCell(table, valueId, false);
			addCell(table, valueCreatingTime, false);
			addCell(table, valueFileName, false);
			addCell(table, valueStatus.toString(), false);
			addCell(table, valueNote, false);
		}

		doc.add(table);
		doc.close();
		CitadBotJob.displayAndWriteLog("Data export to pdf file has been successfully. Pdf file has save in: ");
		CitadBotJob.displayAndWriteLog("Pdf file has save in: "+ FileUtil.downloadFolder + "/" + nameFile+ ".pdf");
	}

	private void addCell(Table table, String header, boolean check) {
		Cell cell = new Cell();
		cell.add(header);
		if (check == true) {
			cell.setBold();
		}
		cell.setFontSize(fontTitleTableSize);
		cell.setTextAlignment(TextAlignment.CENTER);
		table.addCell(cell);
	}
}
