package cn.clickwise.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public class HelloWord {  
	
	private class Article{
		
		private int id;
		
		private String title;
		
		private String content;

		private Article()
		{
			
		}
		
		private  Article(int id,String title,String content)
		{
		  this.id=id;
		  this.title=title;
		  this.content=content;
		}
		
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
		
		public String toString()
		{
			String str="";
			str=str+"id:"+id+" title:"+title+" content:"+content;
			return str;
		}
		
	}
    public  void createIndexFile() {  
        IndexWriter indexWriter=null;  
        try {  
            // 需要的分词器  
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);  
            // 创建的是哪个版本的IndexWriterConfig  
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(  
                    Version.LUCENE_36, analyzer);  
            // 创建系统文件----- ./ 当前路径下的  
            Directory directory = new SimpleFSDirectory(new File("./indexDir/"));  
            indexWriter = new IndexWriter(directory,indexWriterConfig);   
            //获取实体对象  
            Article article=new Article(12,"最XX的城市12","XX12");    
            //indexWriter添加索引  
            Document doc=new Document();  
            //文本中添加内容            标题      内容  
            /*doc.add(new Field("title","中国的首都在哪里",Store.YES,Index.ANALYZED));  
            doc.add(new Field("content","中国的首都在北京",Store.YES,Index.ANALYZED));*/      
            doc.add(new Field("id",article.getId()+"",Store.YES,Index.ANALYZED));  
            doc.add(new Field("title",article.getTitle().toString(),Store.YES,Index.ANALYZED));  
            doc.add(new Field("content",article.getContent().toString(),Store.YES,Index.ANALYZED));   
            //添加到索引中去  
            indexWriter.addDocument(doc);     
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }finally{  
            if(indexWriter!=null){  
                try {  
                    indexWriter.close();  
                }  catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
    //如果查询是需要用到解析器，那解析器必须和创建时的解析器相同  
    public void searchIndexFileResult() throws IOException {   
        List<Article> articles=new ArrayList<Article>();      
        //得到索引的目录  
        Directory directory = new SimpleFSDirectory(new File("./indexDir/"));  
        //根据目录打开一个indexReader  
        IndexReader indexReader=IndexReader.open(directory);  
        //System.out.println(indexReader.maxDoc());   
        //获取最小值的document对象  
        //Document doc=indexReader.document(0);  
        //获取最大值的document对象  
        //Document doc=indexReader.document(indexReader.maxDoc()-1);  
        //document对象的get(字段名称)方法获取字段的值  
        /*System.out.println(doc.get("id"));  
        System.out.println(doc.get("title"));  
        System.out.println(doc.get("content"));*/     
        int n=indexReader.maxDoc();  
        for(int i=0;i<n;i++){  
            Document doc=indexReader.document(i);  
            Article article=new Article();  
            if(doc.get("id")==null){  
                System.out.println("id为空");  
            }else{  
                article.setId(Integer.parseInt(doc.get("id")));  
                article.setTitle(doc.get("title"));  
                article.setContent(doc.get("content"));  
                articles.add(article);  
            }  
        }  
        for(Article article:articles){  
            System.out.println(article.toString());  
        }     
    }  
    public static void main(String[] args) throws IOException {  
    	HelloWord hw=new HelloWord();
        // 建立要索引的文件  
        hw.createIndexFile();  
        // 从索引文件中查询数据  
        hw.searchIndexFileResult();  
        // 获得结果，然后交由相关应用程序处理  
    }  
}  
