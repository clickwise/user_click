package cn.clickwise.clickad.sample;

public class MetricsFactory {
	
	 public static Metrics getMetrics()
	 {
		 if(MetricsConfig.metricsType==1)
		 {
		    return new MIMetrics();
		 }
		 else if(MetricsConfig.metricsType==0)
		 {
			return new DFMetrics();
		 }
		 return null;
	 }

}
