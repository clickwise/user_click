package cn.clickwise.user_click.field;

import cn.clickwise.liqi.str.basic.SSO;

public class STRMAP extends FieldFunc{

	@Override
	public String fieldFunc(String field, String params) {
		// TODO Auto-generated method stub
		if(SSO.tioe(params))
		{
			return "";
		}
		
		String[] seg_arr=params.split("\\s+");
		if(seg_arr.length!=1)
		{
			return "";
		}
		
		
		return seg_arr[0];
	}

}
