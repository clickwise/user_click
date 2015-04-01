package cn.clickwise.clickad.office;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import cn.clickwise.liqi.str.basic.FileToArray;

public class CreateExcelFileStd {
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
		if(args.length<2)
		{
			System.err.println("Usage:<input_file>* <excel_file>");
			System.err.println("    input_file : 输入文件，可以有多个");
			System.err.println("    excel_file : 输出文件");
			System.exit(1);
		}
		
		String[] input_files=new String[args.length-1];
		for(int i=0;i<args.length-1;i++)
		{
			input_files[i]=args[i];
		}
		
		String excel_file=args[args.length-1];
		
		txtsToExcelFile(input_files,excel_file);			
	}
	
}
