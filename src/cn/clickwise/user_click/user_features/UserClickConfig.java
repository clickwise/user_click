package cn.clickwise.user_click.user_features;

public interface UserClickConfig {
  public String redis_host="42.62.29.25";
  public int redis_port=16379;
  public int redis_db=14;
  public void init_interface();

}
