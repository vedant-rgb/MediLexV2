package com.medilexV2.medPlus.service;

import com.medilexV2.medPlus.dto.Products;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExcelSheetDownloadService {

    public void exportToExcel(List<Products> products, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // Creating Header Row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"HSN", "Product Name", "Mfg", "Unit","Qty","Sch","Batch","Exp","M.R.P.","Rate","PTR","Gst","Amount"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Writing Data
        int rowNum = 1;
        for (Products product : products) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getHsn());
            row.createCell(1).setCellValue(product.getProductName());
            row.createCell(2).setCellValue(product.getMfg());
            row.createCell(3).setCellValue(product.getUnit());
            row.createCell(4).setCellValue(product.getQty());
            row.createCell(5).setCellValue(product.getSch());
            row.createCell(6).setCellValue(product.getBatch());
            row.createCell(7).setCellValue(product.getExp());
            row.createCell(8).setCellValue(product.getMrp());
            row.createCell(9).setCellValue(product.getRate());
            row.createCell(10).setCellValue(product.getPtr());
            row.createCell(11).setCellValue(product.getGst());
            row.createCell(12).setCellValue(product.getAmount());
        }

        // Set Response Headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=products.xlsx");

        // Write to output stream
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
