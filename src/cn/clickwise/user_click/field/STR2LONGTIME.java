package cn.clickwise.user_click.field;

import cn.clickwise.liqi.time.utils.TimeOpera;

public class STR2LONGTIME extends FieldFunc {


	public String fieldFunc(String field,String params) {
		// TODO Auto-generated method stub
		return (TimeOpera.str2long(field)+"");
	}

}
