package cn.clickwise.user_click.field;

public class FIELDMERGE extends DoubleFieldFunc{
	@Override
	public String doubleFieldFunc(String field1, String field2, String params) {
		// TODO Auto-generated method stub
		String[] seg_arr=params.split("\\s+");
		if(seg_arr.length<1)
		{
			return "";
		}
		return field1+seg_arr[0]+field2;
	}
}
