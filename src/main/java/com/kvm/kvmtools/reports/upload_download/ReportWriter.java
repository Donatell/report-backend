package com.kvm.kvmtools.reports.upload_download;

import com.kvm.kvmtools.reports.dao.*;
import com.kvm.kvmtools.reports.entity.ColumnTitle;
import com.kvm.kvmtools.reports.entity.Patient;
import com.kvm.kvmtools.reports.entity.PatientList;
import com.kvm.kvmtools.reports.entity.TransneftPriceCategory;
import com.kvm.kvmtools.reports.specify.PriceData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ReportWriter {
	
	private final PatientListRepository patientListRepository;
	
	private final PatientRepository patientRepository;
	
	private final ServiceRepository serviceRepository;
	
	private final ColumnTitleRepository columnTitleRepository;
	
	private final TransneftPriceCategoryRepository transneftPriceCategoryRepository;
	
	@Autowired
	public ReportWriter(PatientListRepository patientListRepository, PatientRepository patientRepository, ServiceRepository serviceRepository, ColumnTitleRepository columnTitleRepository, TransneftPriceCategoryRepository transneftPriceCategoryRepository) {
		this.patientListRepository = patientListRepository;
		this.patientRepository = patientRepository;
		this.serviceRepository = serviceRepository;
		this.columnTitleRepository = columnTitleRepository;
		this.transneftPriceCategoryRepository = transneftPriceCategoryRepository;
	}
	
	public void writeReports(long patientListId, int moduleId) throws IOException {
		PatientList patientList = patientListRepository.getById(patientListId);
		List<Patient> patients = patientRepository.findAllByPatientListId(patientListId);
		List<PriceData> priceDataList = patientList.getPricesAsListWithTitles(serviceRepository);
		List<ColumnTitle> columnTitleList = columnTitleRepository.findAll();
		
		String reportsDirectory = System.getProperty("user.dir") + "/reports/" + patientListId;
		boolean b = new File(reportsDirectory).mkdirs();
		
		switch (moduleId) {
			case 1:
				// filter out excessive column titles not needed by the common module
				columnTitleList = columnTitleList.stream()
						.filter(columnTitle -> !columnTitle.getPatientColumn().equals("transneftBase"))
						.collect(Collectors.toList());
				
				// write common reports
				writeDetailedReport(patients, priceDataList, columnTitleList, reportsDirectory);
				writeGeneralReport(priceDataList, reportsDirectory);
				break;
			case 2:
				writeTransneftReport(patients, priceDataList, columnTitleList, reportsDirectory);
				break;
			
		}
	}
	
	private void writeDetailedReport(List<Patient> patients, List<PriceData> priceDataList, List<ColumnTitle> columnTitleList, String reportsDirectory) throws IOException {
		Workbook wb = new XSSFWorkbook();
		Sheet sh = wb.createSheet("Поимённый расчёт");
		
		// bold Calibri font
		Font bold = wb.createFont();
		bold.setBold(true);
		
		// Common cell style
		CellStyle commonStyle = wb.createCellStyle();
		commonStyle.setBorderBottom(BorderStyle.THIN);
		commonStyle.setBorderLeft(BorderStyle.THIN);
		commonStyle.setBorderTop(BorderStyle.THIN);
		commonStyle.setBorderRight(BorderStyle.THIN);
		commonStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		commonStyle.setAlignment(HorizontalAlignment.LEFT);
		
		// Date cell style
		CreationHelper createHelper = wb.getCreationHelper();
		CellStyle dateStyle = wb.createCellStyle();
		dateStyle.setBorderBottom(BorderStyle.THIN);
		dateStyle.setBorderLeft(BorderStyle.THIN);
		dateStyle.setBorderTop(BorderStyle.THIN);
		dateStyle.setBorderRight(BorderStyle.THIN);
		dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dateStyle.setAlignment(HorizontalAlignment.LEFT);
		dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
		
		
		// Price cell style
		CellStyle priceStyle = wb.createCellStyle();
		priceStyle.setBorderBottom(BorderStyle.THIN);
		priceStyle.setBorderLeft(BorderStyle.THIN);
		priceStyle.setBorderTop(BorderStyle.THIN);
		priceStyle.setBorderRight(BorderStyle.THIN);
		priceStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		priceStyle.setAlignment(HorizontalAlignment.CENTER);
		
		// Number cell styles
		CellStyle numberStyle = wb.createCellStyle();
		numberStyle.setBorderBottom(BorderStyle.THIN);
		numberStyle.setBorderLeft(BorderStyle.THIN);
		numberStyle.setBorderTop(BorderStyle.THIN);
		numberStyle.setBorderRight(BorderStyle.THIN);
		numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		numberStyle.setAlignment(HorizontalAlignment.LEFT);
		
		// Header cell styles
		CellStyle headerStyle = wb.createCellStyle();
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
		headerStyle.setAlignment(HorizontalAlignment.LEFT);
		headerStyle.setFont(bold);
		
		// Merged cell styles
		CellStyle mergedStyle = wb.createCellStyle();
		mergedStyle.setBorderBottom(BorderStyle.THIN);
		mergedStyle.setBorderLeft(BorderStyle.THIN);
		mergedStyle.setBorderTop(BorderStyle.THIN);
		mergedStyle.setBorderRight(BorderStyle.THIN);
		mergedStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		mergedStyle.setAlignment(HorizontalAlignment.RIGHT);
		
		// Service header style
		CellStyle serviceHeaderStyle = wb.createCellStyle();
		serviceHeaderStyle.setBorderBottom(BorderStyle.THIN);
		serviceHeaderStyle.setBorderLeft(BorderStyle.THIN);
		serviceHeaderStyle.setBorderTop(BorderStyle.THIN);
		serviceHeaderStyle.setBorderRight(BorderStyle.THIN);
		serviceHeaderStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
		serviceHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
		serviceHeaderStyle.setRotation((short) 90);
		serviceHeaderStyle.setWrapText(true);
		headerStyle.setFont(bold);
		
		// Headers
		List<String> infoHeaders = columnTitleList.stream()
				.map(ColumnTitle::getTitle)
				.collect(Collectors.toCollection(ArrayList::new));
		
		ArrayList<String> serviceHeaders = priceDataList.stream()
				.map(PriceData::getServiceTitle).collect(Collectors.toCollection(ArrayList::new));
		
		int currentRowIndex = 0;
		int currentColumnIndex = 0;
		
		Row headerRow = sh.createRow(currentRowIndex);
		
		// HEADER ROW START
		
		// Patient index header cell
		Cell patientIndexCell = headerRow.createCell(currentColumnIndex);
		patientIndexCell.setCellStyle(headerStyle);
		patientIndexCell.setCellValue("№");
		currentColumnIndex++;
		
		// Patient gender header cell
		Cell genderHeaderCell = headerRow.createCell(currentColumnIndex);
		genderHeaderCell.setCellStyle(headerStyle);
		genderHeaderCell.setCellValue("Пол");
		currentColumnIndex++;
		
		// Common info header cells
		for (String infoHeader : infoHeaders) {
			Cell cell = headerRow.createCell(currentColumnIndex);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(infoHeader);
			currentColumnIndex++;
		}
		
		// service header cells
		for (String serviceHeader : serviceHeaders) {
			Cell cell = headerRow.createCell(currentColumnIndex);
			cell.setCellStyle(serviceHeaderStyle);
			cell.setCellValue(serviceHeader);
			currentColumnIndex++;
		}
		
		// header sum cell at the last column
		Cell sumHeaderCell = headerRow.createCell(currentColumnIndex);
		sumHeaderCell.setCellStyle(headerStyle);
		sumHeaderCell.setCellValue("Сумма");
		
		// HEADER ROW END
		currentRowIndex++;
		currentColumnIndex = 0;
		
		// PATIENT ROWS START
		
		// for each patient
		for (Patient patient : patients) {
			Row patientRow = sh.createRow(currentRowIndex);
			
			// patient index
			Cell indexCell = patientRow.createCell(currentColumnIndex);
			indexCell.setCellStyle(commonStyle);
			indexCell.setCellValue(currentRowIndex);
			currentColumnIndex++;
			
			// gender cell
			Cell genderCell = patientRow.createCell(currentColumnIndex);
			genderCell.setCellStyle(numberStyle);
			genderCell.setCellValue(patient.getGender().getTitle());
			currentColumnIndex++;
			
			// create a cell for each column title entry from the database
			for (ColumnTitle columnTitle : columnTitleList) {
				Cell cell = patientRow.createCell(currentColumnIndex);
				cell.setCellStyle(commonStyle);
				
				if (columnTitle.getPatientColumn().equals("fullName")) cell.setCellValue(patient.getFullName());
				if (columnTitle.getPatientColumn().equals("birthDate")) {
					cell.setCellStyle(dateStyle);
					cell.setCellValue(patient.getBirthDate());
				}
				if (columnTitle.getPatientColumn().equals("factorCodes")) cell.setCellValue(patient.getFactorCodes());
				if (columnTitle.getPatientColumn().equals("profession")) cell.setCellValue(patient.getProfession());
				if (columnTitle.getPatientColumn().equals("department")) cell.setCellValue(patient.getDepartment());
				
				currentColumnIndex++;
			}
			
			// for each price data entry if the patient has got the service, put the price of it, else put a dash
			for (PriceData priceData : priceDataList) {
				Cell cell = patientRow.createCell(currentColumnIndex);
				cell.setCellStyle(priceStyle);
				
				boolean hasService = false;
				for (int patientServiceId : patient.getServiceIdAsList()) {
					if (priceData.getServiceId() == patientServiceId) {
						hasService = true;
						break;
					}
				}
				if (hasService) cell.setCellValue(priceData.getPrice());
				if (!hasService) cell.setCellValue("-");
				
				currentColumnIndex++;
			}
			
			String firstServiceAddress = patientRow.getCell(currentColumnIndex - priceDataList.size())
					.getAddress()
					.formatAsString();
			String lastServiceAddress = patientRow.getCell(currentColumnIndex - 1).getAddress().formatAsString();
			
			Cell patientSumCell = patientRow.createCell(currentColumnIndex);
			patientSumCell.setCellStyle(priceStyle);
			patientSumCell.setCellFormula(String.format("SUM(%s:%s)", firstServiceAddress, lastServiceAddress));
			
			// autosize row's height
			patientRow.setHeight((short) -1);
			
			// END OF CURRENT PATIENT ROW
			currentRowIndex++;
			currentColumnIndex = 0;
		}
		
		// END OF PATIENT ROWS
		
		// TOTAL ROW START
		int lastColumnIndex = headerRow.getLastCellNum() - 1;
		
		Row totalRow = sh.createRow(currentRowIndex);
		for (int i = 0; i < headerRow.getLastCellNum(); i++) {
			Cell cell = totalRow.createCell(i);
			cell.setCellStyle(commonStyle);
		}
		CellRangeAddress mergedRegion = CellRangeAddress.valueOf(String.format("%s:%s", totalRow.createCell(0)
				.getAddress().formatAsString(), totalRow.createCell(lastColumnIndex - 1)
				.getAddress()
				.formatAsString()));
		sh.addMergedRegion(mergedRegion);
		totalRow.getCell(0).setCellValue("Итого:");
		totalRow.getCell(0).setCellStyle(mergedStyle);
		
		String totalRangeFirstCellAddress = sh.getRow(headerRow.getRowNum() + 1)
				.getCell(lastColumnIndex)
				.getAddress()
				.formatAsString();
		
		String totalRangeLastCellAddress = sh.getRow(totalRow.getRowNum() - 1)
				.getCell(lastColumnIndex)
				.getAddress()
				.formatAsString();
		
		Cell totalCell = totalRow.createCell(lastColumnIndex);
		totalCell.setCellStyle(numberStyle);
		totalCell.setCellFormula(String.format("SUM(%s:%s)", totalRangeFirstCellAddress, totalRangeLastCellAddress));
		
		// TOTAL ROW END
		
		// autosize
		for (int i = 0; i < headerRow.getLastCellNum(); i++) {
			sh.autoSizeColumn(i);
		}
		
		// fix rotated text autosize
		int charactersInLine = 40;
		int lineHeight = 3;
		for (int i = 2 + infoHeaders.size(); i < headerRow.getLastCellNum() - 1; i++) {
			int recommendedWidth = (int) (Math.ceil((double) headerRow.getCell(i)
					.getStringCellValue()
					.length() / charactersInLine) * lineHeight * 256);
			if (recommendedWidth > sh.getColumnWidth(i)) {
				sh.setColumnWidth(i, recommendedWidth);
			}
		}
		
		// fix total cell width
		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
		CellValue totalCellValue = evaluator.evaluate(totalCell);
		if (totalCellValue.formatAsString().length() > sumHeaderCell.getStringCellValue().length()) {
			sh.setColumnWidth(totalCell.getColumnIndex(), totalCellValue.formatAsString()
					.length() * 256);
		}
		
		// shrink header
		headerRow.setHeight((short) (20 * 256));
		
		try (OutputStream fileOut = new FileOutputStream(String.format("%s/2.xlsx", reportsDirectory))) {
			wb.write(fileOut);
		}
	}
	
	private void writeGeneralReport(List<PriceData> priceDataList, String reportsDirectory) throws IOException {
		Workbook wb = new XSSFWorkbook();
		Sheet sh = wb.createSheet("Общая спецификация");
		
		// bold Calibri font
		Font bold = wb.createFont();
		bold.setBold(true);
		
		// Service title cell style
		CellStyle titleStyle = wb.createCellStyle();
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderLeft(BorderStyle.THIN);
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setBorderRight(BorderStyle.THIN);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleStyle.setAlignment(HorizontalAlignment.LEFT);
		titleStyle.setWrapText(true);
		
		// Number cell styles
		CellStyle numberStyle = wb.createCellStyle();
		numberStyle.setBorderBottom(BorderStyle.THIN);
		numberStyle.setBorderLeft(BorderStyle.THIN);
		numberStyle.setBorderTop(BorderStyle.THIN);
		numberStyle.setBorderRight(BorderStyle.THIN);
		numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		numberStyle.setAlignment(HorizontalAlignment.LEFT);
		
		// Header cell styles
		CellStyle headerStyle = wb.createCellStyle();
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setAlignment(HorizontalAlignment.LEFT);
		headerStyle.setFont(bold);
		
		// Merged cell styles
		CellStyle mergedStyle = wb.createCellStyle();
		mergedStyle.setBorderBottom(BorderStyle.THIN);
		mergedStyle.setBorderLeft(BorderStyle.THIN);
		mergedStyle.setBorderTop(BorderStyle.THIN);
		mergedStyle.setBorderRight(BorderStyle.THIN);
		mergedStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		mergedStyle.setAlignment(HorizontalAlignment.RIGHT);
		
		// Headers
		ArrayList<String> headers = new ArrayList<>(Arrays.asList("Услуга", "Цена", "Кол-во", "Сумма"));
		Row header = sh.createRow(0);
		for (int i = 0; i < headers.size(); i++) {
			Cell cell = header.createCell(i);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(headers.get(i));
		}
		
		// Services and prices
		int headerOffset = 1;
		Cell firstSumCell = null;
		Cell lastSumCell = null;
		for (int i = 0; i < priceDataList.size(); i++) {
			Row row = sh.createRow(i + headerOffset);
			Cell serviceTitleCell = row.createCell(0);
			serviceTitleCell.setCellStyle(titleStyle);
			serviceTitleCell.setCellValue(priceDataList.get(i).getServiceTitle());
			
			Cell priceCell = row.createCell(1);
			priceCell.setCellStyle(numberStyle);
			priceCell.setCellValue(priceDataList.get(i).getPrice());
			
			Cell quantityCell = row.createCell(2);
			quantityCell.setCellStyle(numberStyle);
			quantityCell.setCellValue(priceDataList.get(i).getQuantity());
			
			Cell sumCell = row.createCell(3);
			sumCell.setCellStyle(numberStyle);
			sumCell.setCellFormula(String.format("%s*%s", priceCell.getAddress()
					.formatAsString(), quantityCell.getAddress()
					.formatAsString()));
			
			// if first cell
			if (i == 0) {
				firstSumCell = row.getCell(3);
			}
			
			// if last cell
			if (i == priceDataList.size() - 1) {
				lastSumCell = row.getCell(3);
			}
		}
		
		// footer with total
		Row totalRow = sh.createRow(priceDataList.size() + headerOffset);
		
		// merge first three cells
		CellRangeAddress mergedRegion = CellRangeAddress.valueOf(String.format("%s:%s", totalRow.createCell(0)
				.getAddress().formatAsString(), totalRow.createCell(2).getAddress().formatAsString()));
		sh.addMergedRegion(mergedRegion);
		totalRow.getCell(0).setCellValue("Итого:");
		totalRow.getCell(0).setCellStyle(mergedStyle);
		
		Cell totalCell = totalRow.createCell(3);
		totalCell.setCellStyle(numberStyle);
		if (firstSumCell != null && lastSumCell != null) {
			totalCell.setCellFormula(String.format("SUM(%s:%s)", firstSumCell.getAddress()
					.formatAsString(), lastSumCell.getAddress().formatAsString()));
			
		}
		
		// Shrink service titles
		sh.setColumnWidth(0, 50 * 256);
		
		// Autosize number columns
		sh.autoSizeColumn(1);
		sh.autoSizeColumn(2);
		sh.autoSizeColumn(3);
		
		// fix total cell width
		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
		CellValue totalCellValue = evaluator.evaluate(totalCell);
		if (totalCellValue.formatAsString().length() > 5) {
			sh.setColumnWidth(totalCell.getColumnIndex(), totalCellValue.formatAsString()
					.length() * 256);
		}
		
		try (OutputStream fileOut = new FileOutputStream(String.format("%s/1.xlsx", reportsDirectory))) {
			wb.write(fileOut);
		}
	}
	
	private void writeTransneftReport(List<Patient> patients, List<PriceData> priceDataList, List<ColumnTitle> columnTitleList, String reportsDirectory) throws IOException {
		Workbook wb = new XSSFWorkbook();
		Sheet sh = wb.createSheet("Поимённый расчёт Транснефть");
		
		// bold Calibri font
		Font bold = wb.createFont();
		bold.setBold(true);
		
		// Common cell style
		CellStyle commonStyle = wb.createCellStyle();
		commonStyle.setBorderBottom(BorderStyle.THIN);
		commonStyle.setBorderLeft(BorderStyle.THIN);
		commonStyle.setBorderTop(BorderStyle.THIN);
		commonStyle.setBorderRight(BorderStyle.THIN);
		commonStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		commonStyle.setAlignment(HorizontalAlignment.LEFT);
		
		// Date cell style
		CreationHelper createHelper = wb.getCreationHelper();
		CellStyle dateStyle = wb.createCellStyle();
		dateStyle.setBorderBottom(BorderStyle.THIN);
		dateStyle.setBorderLeft(BorderStyle.THIN);
		dateStyle.setBorderTop(BorderStyle.THIN);
		dateStyle.setBorderRight(BorderStyle.THIN);
		dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dateStyle.setAlignment(HorizontalAlignment.LEFT);
		dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
		
		
		// Price cell style
		CellStyle priceStyle = wb.createCellStyle();
		priceStyle.setBorderBottom(BorderStyle.THIN);
		priceStyle.setBorderLeft(BorderStyle.THIN);
		priceStyle.setBorderTop(BorderStyle.THIN);
		priceStyle.setBorderRight(BorderStyle.THIN);
		priceStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		priceStyle.setAlignment(HorizontalAlignment.CENTER);
		
		// Number cell styles
		CellStyle numberStyle = wb.createCellStyle();
		numberStyle.setBorderBottom(BorderStyle.THIN);
		numberStyle.setBorderLeft(BorderStyle.THIN);
		numberStyle.setBorderTop(BorderStyle.THIN);
		numberStyle.setBorderRight(BorderStyle.THIN);
		numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		numberStyle.setAlignment(HorizontalAlignment.LEFT);
		
		// Header cell styles
		CellStyle headerStyle = wb.createCellStyle();
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
		headerStyle.setAlignment(HorizontalAlignment.LEFT);
		headerStyle.setFont(bold);
		
		// Merged cell styles
		CellStyle mergedStyle = wb.createCellStyle();
		mergedStyle.setBorderBottom(BorderStyle.THIN);
		mergedStyle.setBorderLeft(BorderStyle.THIN);
		mergedStyle.setBorderTop(BorderStyle.THIN);
		mergedStyle.setBorderRight(BorderStyle.THIN);
		mergedStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		mergedStyle.setAlignment(HorizontalAlignment.RIGHT);
		
		// Service header style
		CellStyle serviceHeaderStyle = wb.createCellStyle();
		serviceHeaderStyle.setBorderBottom(BorderStyle.THIN);
		serviceHeaderStyle.setBorderLeft(BorderStyle.THIN);
		serviceHeaderStyle.setBorderTop(BorderStyle.THIN);
		serviceHeaderStyle.setBorderRight(BorderStyle.THIN);
		serviceHeaderStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
		serviceHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
		serviceHeaderStyle.setRotation((short) 90);
		serviceHeaderStyle.setWrapText(true);
		headerStyle.setFont(bold);
		
		// Headers
		List<String> infoHeaders = columnTitleList.stream()
				.map(ColumnTitle::getTitle)
				.collect(Collectors.toCollection(ArrayList::new));
		List<String> serviceHeaders = priceDataList.stream()
				.map(PriceData::getServiceTitle).collect(Collectors.toCollection(ArrayList::new));
		
		int currentRowIndex = 0;
		int currentColumnIndex = 0;
		
		Row headerRow = sh.createRow(currentRowIndex);
		
		// HEADER ROW START
		
		// Patient index header cell
		Cell patientIndexCell = headerRow.createCell(currentColumnIndex);
		patientIndexCell.setCellStyle(headerStyle);
		patientIndexCell.setCellValue("№");
		currentColumnIndex++;
		
		// Patient gender header cell
		Cell genderHeaderCell = headerRow.createCell(currentColumnIndex);
		genderHeaderCell.setCellStyle(headerStyle);
		genderHeaderCell.setCellValue("Пол");
		currentColumnIndex++;
		
		// Common info header cells
		for (String infoHeader : infoHeaders) {
			Cell cell = headerRow.createCell(currentColumnIndex);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(infoHeader);
			currentColumnIndex++;
		}
		
		// service header cells
		for (String serviceHeader : serviceHeaders) {
			Cell cell = headerRow.createCell(currentColumnIndex);
			cell.setCellStyle(serviceHeaderStyle);
			cell.setCellValue(serviceHeader);
			currentColumnIndex++;
		}
		
		// header sum cell at the last column
		Cell sumHeaderCell = headerRow.createCell(currentColumnIndex);
		sumHeaderCell.setCellStyle(headerStyle);
		sumHeaderCell.setCellValue("Сумма");
		
		// HEADER ROW END
		currentRowIndex++;
		currentColumnIndex = 0;
		
		// PATIENT ROWS START
		
		// prices for transneft used in patients loop
		List<TransneftPriceCategory> transneftPriceCategories = transneftPriceCategoryRepository.findAll();
		
		// for each patient
		for (Patient patient : patients) {
			Row patientRow = sh.createRow(currentRowIndex);
			
			// patient index
			Cell indexCell = patientRow.createCell(currentColumnIndex);
			indexCell.setCellStyle(commonStyle);
			indexCell.setCellValue(currentRowIndex);
			currentColumnIndex++;
			
			// gender cell
			Cell genderCell = patientRow.createCell(currentColumnIndex);
			genderCell.setCellStyle(numberStyle);
			genderCell.setCellValue(patient.getGender().getTitle());
			currentColumnIndex++;
			
			// create a cell for each column title entry from the database
			for (ColumnTitle columnTitle : columnTitleList) {
				Cell cell = patientRow.createCell(currentColumnIndex);
				cell.setCellStyle(commonStyle);
				
				if (columnTitle.getPatientColumn().equals("fullName")) cell.setCellValue(patient.getFullName());
				if (columnTitle.getPatientColumn().equals("birthDate")) {
					cell.setCellStyle(dateStyle);
					cell.setCellValue(patient.getBirthDate());
				}
				if (columnTitle.getPatientColumn().equals("factorCodes"))
					cell.setCellValue(patient.getFactorCodes());
				if (columnTitle.getPatientColumn().equals("profession")) cell.setCellValue(patient.getProfession());
				if (columnTitle.getPatientColumn().equals("department")) cell.setCellValue(patient.getDepartment());
				if (columnTitle.getPatientColumn().equals("transneftBase"))
					cell.setCellValue(patient.getTransneftBase().getTitle());
				
				currentColumnIndex++;
			}
			
			// for each price data entry if the patient has got the service, put the price of it, else put a dash
			for (PriceData priceData : priceDataList) {
				Cell cell = patientRow.createCell(currentColumnIndex);
				cell.setCellStyle(priceStyle);
				
				boolean hasService = false;
				for (int patientServiceId : patient.getServiceIdAsList()) {
					if (priceData.getServiceId() == patientServiceId) {
						hasService = true;
						break;
					}
				}
				if (hasService) cell.setCellValue(getTransneftPrice(patient, priceData));
				if (!hasService) cell.setCellValue("-");
				
				currentColumnIndex++;
			}
			
			String firstServiceAddress = patientRow.getCell(currentColumnIndex - priceDataList.size())
					.getAddress()
					.formatAsString();
			String lastServiceAddress = patientRow.getCell(currentColumnIndex - 1).getAddress().formatAsString();
			
			Cell patientSumCell = patientRow.createCell(currentColumnIndex);
			patientSumCell.setCellStyle(priceStyle);
			patientSumCell.setCellFormula(String.format("SUM(%s:%s)", firstServiceAddress, lastServiceAddress));
			
			// autosize row's height
			patientRow.setHeight((short) -1);
			
			// END OF CURRENT PATIENT ROW
			currentRowIndex++;
			currentColumnIndex = 0;
		}
		
		// END OF PATIENT ROWS
		
		// TOTAL ROW START
		int lastColumnIndex = headerRow.getLastCellNum() - 1;
		
		Row totalRow = sh.createRow(currentRowIndex);
		for (int i = 0; i < headerRow.getLastCellNum(); i++) {
			Cell cell = totalRow.createCell(i);
			cell.setCellStyle(commonStyle);
		}
		CellRangeAddress mergedRegion = CellRangeAddress.valueOf(String.format("%s:%s", totalRow.createCell(0)
				.getAddress().formatAsString(), totalRow.createCell(lastColumnIndex - 1)
				.getAddress()
				.formatAsString()));
		sh.addMergedRegion(mergedRegion);
		totalRow.getCell(0).setCellValue("Итого:");
		totalRow.getCell(0).setCellStyle(mergedStyle);
		
		String totalRangeFirstCellAddress = sh.getRow(headerRow.getRowNum() + 1)
				.getCell(lastColumnIndex)
				.getAddress()
				.formatAsString();
		
		String totalRangeLastCellAddress = sh.getRow(totalRow.getRowNum() - 1)
				.getCell(lastColumnIndex)
				.getAddress()
				.formatAsString();
		
		Cell totalCell = totalRow.createCell(lastColumnIndex);
		totalCell.setCellStyle(numberStyle);
		totalCell.setCellFormula(String.format("SUM(%s:%s)", totalRangeFirstCellAddress, totalRangeLastCellAddress));
		
		// TOTAL ROW END
		
		// autosize
		for (int i = 0; i < headerRow.getLastCellNum(); i++) {
			sh.autoSizeColumn(i);
		}
		
		// fix rotated text autosize
		int charactersInLine = 40;
		int lineHeight = 3;
		for (int i = 2 + infoHeaders.size(); i < headerRow.getLastCellNum() - 1; i++) {
			int recommendedWidth = (int) (Math.ceil((double) headerRow.getCell(i)
					.getStringCellValue()
					.length() / charactersInLine) * lineHeight * 256);
			if (recommendedWidth > sh.getColumnWidth(i)) {
				sh.setColumnWidth(i, recommendedWidth);
			}
		}
		
		// fix total cell width
		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
		CellValue totalCellValue = evaluator.evaluate(totalCell);
		if (totalCellValue.formatAsString().length() > sumHeaderCell.getStringCellValue().length()) {
			sh.setColumnWidth(totalCell.getColumnIndex(), totalCellValue.formatAsString()
					.length() * 256);
		}
		
		// shrink header
		headerRow.setHeight((short) (20 * 256));
		
		// apply conditional formatting, highlight all cells equal to 0
		SheetConditionalFormatting shCF = sh.getSheetConditionalFormatting();
		ConditionalFormattingRule rule = shCF.createConditionalFormattingRule(ComparisonOperator.EQUAL, "0");
		PatternFormatting fill = rule.createPatternFormatting();
		fill.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
		fill.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
		FontFormatting font = rule.createFontFormatting();
		font.setFontColorIndex(IndexedColors.DARK_YELLOW.index);
		
		CellRangeAddress[] regions = {
				CellRangeAddress.valueOf(String.format("%s:%s", sh.getRow(1)
						.getCell(1 + infoHeaders.size())
						.getAddress()
						.formatAsString(), sh.getRow(sh.getLastRowNum() - 1)
						.getCell(infoHeaders.size() + serviceHeaders.size()).getAddress().formatAsString()))
		};
		shCF.addConditionalFormatting(regions, rule);
		
		try (OutputStream fileOut = new FileOutputStream(String.format("%s/2.xlsx", reportsDirectory))) {
			wb.write(fileOut);
		}
	}
	
	private double getTransneftPrice(Patient patient, PriceData priceData) {
		for (PriceData categoryPriceData : patient.getTransneftPriceCategory()
				.getPricesAsList()) {
			if (categoryPriceData.getServiceId() == priceData.getServiceId()) {
				return categoryPriceData.getPrice();
			}
		}
		return 0;
	}
	
	
}
