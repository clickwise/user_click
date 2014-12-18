package cn.clickwise.liqi.office.excel.app;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelTest {

	public static void main(String[] args)
	{
		HSSFWorkbook wb = new HSSFWorkbook();  
		  
		//添加Worksheet（不添加sheet时生成的xls文件打开时会报错）  
		@SuppressWarnings("unused")  
		Sheet sheet1 = wb.createSheet();  
		@SuppressWarnings("unused")  
		Sheet sheet2 = wb.createSheet();  
		@SuppressWarnings("unused")  
		Sheet sheet3 = wb.createSheet("new sheet");  
		@SuppressWarnings("unused")  
		Sheet sheet4 = wb.createSheet("rensanning"); 
		for (int i = 0; i < 10; i++) {  
		    Row row = wb.getSheet("new sheet").createRow(i);  
		    for (int j = 0; j < 10; j++) {  
		        //添加单元格  
		        Cell cell = row.createCell(j);  
		        cell.setCellValue(i + 1);  
		    }  
		      
		    //删除单元格  
		    row.removeCell(row.getCell(5));  
		}
		  
		//保存为Excel文件  
		FileOutputStream out = null;  
		  
		try {  
		    out = new FileOutputStream("temp/office/excel/text.xls");  
		    wb.write(out);        
		} catch (IOException e) {  
		    System.out.println(e.toString());  
		} finally {  
		    try {  
		        out.close();  
		    } catch (IOException e) {  
		        System.out.println(e.toString());  
		    }  
		}     
	}
	
}
