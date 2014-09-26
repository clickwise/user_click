package cn.clickwise.clickad.feathouse;

public class EasyConfigureFactory extends ConfigureFactory{

	@Override
	public MissesStore getMissesStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MissesTmpStore getMissesTmpStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserRecordTmpStore getUserRecordTmpStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MysqlConfigure getMysqlConfigure() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Dmp[] getDmps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dmp getDmpById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dmp getDmpByArea(String area) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DataStore getDataStore() {
		// TODO Auto-generated method stub
		return new CassandraStore();
	}

	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Record string2Record(String recordString) {
		
		String[] tokens=recordString.split("\001");
		if(tokens.length!=9)
		{
			return null;
		}
		
		String key=tokens[0];
		String value=tokens[3];
		Record record=new Record(key,value);
		// TODO Auto-generated method stub
		return record;
	}

	@Override
	public Table getQueryTable() {
		
		return new Table("QueryReceipts");
	}

	@Override
	public Table getInquiryTable() {

		return new Table("InquiryReceipts");
	}

}
