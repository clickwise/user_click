package cn.clickwise.user_click.field;

public class FIELDSWAP extends DoubleFieldFunc {

	@Override
	public String doubleFieldFunc(String field1, String field2, String params) {
		// TODO Auto-generated method stub
		
		return field2+"\001"+field1;
	}

}
