package cn.clickwise.clickad.profile;

import java.util.HashMap;

import love.cq.util.MapCount;

/**
 * 预测用户的各种属性
 * @author zkyz
 */
public abstract class ProfilePredict {
	
	HashMap<String,MapCount<String>> variousMapDict=new HashMap<String,MapCount<String>>();

	public abstract void loadKnowledge();
	
	public abstract Profile predict(User user);
	
	
}
