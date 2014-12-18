package cn.clickwise.liqi.office.excel.create;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import cn.clickwise.liqi.str.basic.FileToArray;

/**
 * 创建excel 文件，并导入数据
 * @author zkyz
 *
 */
public class CreateExcelFile {

	/**
	 * 从txt文件创建excel file
	 */
	public static void txtsToExcelFile(String[] txtfiles,String excel_file) throws Exception
	{
		HSSFWorkbook wb = new HSSFWorkbook(); 
		Sheet[] sheets=new Sheet[txtfiles.length];
		for(int i=0;i<sheets.length;i++)
		{
			sheets[i]=wb.createSheet();
		}
		
		String[][] data_arr=null;
		
		
		for(int i=0;i<txtfiles.length;i++)
		{
			data_arr=FileToArray.fileToDoubleDimArr(txtfiles[i],"\001");
			for (int j = 0; j < data_arr.length; j++) { 
				 Row row=null;
				try{
			    row =sheets[i].createRow(j);  
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					continue;
					
				}
			    for (int k = 0; k <data_arr[0].length ; k++) {  
			        //添加单元格  
			        Cell cell = row.createCell(k);  
			        cell.setCellValue(data_arr[j][k]);  
			    }   
			}	
		}
			
		//保存为Excel文件  
				FileOutputStream out = null;  
				  
				try {  
				    out = new FileOutputStream(excel_file);  
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
	
	public static void main(String[] args) throws Exception
	{
		String[] input_files={"D:/projects/user_click_win_workplace/user_click/temp/profile_001.txt"};
		String excel_file="D:/projects/user_click_win_workplace/user_click/temp/profile_001.xls";
		txtsToExcelFile(input_files,excel_file);			
	}
	
}
