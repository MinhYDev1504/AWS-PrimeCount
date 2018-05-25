package mapreducewordcount.mapreducewordcount;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCounter {

	public static class TokenizerMapper extends
    Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);

	  @Override
	  public void map(Object key, Text value, Context context) throws IOException,
	      InterruptedException {
		//lấy thông tin trong file input chuyển sang 1 chuổi kiểu string
	      String line = value.toString();
	      //tách từng số bỏ vào mảng string
	      String[] numbers=line.split(",");
	      for(String number: numbers )
	      {
	        //chuyển sang kiểu số
	        int num = Integer.parseInt(number);
	        //khởi tạo key out put
	        Text outputKey = new Text(number.toUpperCase().trim());
	        //khởi tạo biến lưu giá trị của key out put
	        IntWritable outputValue = new IntWritable(1);
	        //kiểm tra xem số trên phải số nguyên tố hay không
	        //nếu đúng thì ghi vào cập giá trị <key,value>
	        if(isPrimeNumber(num)){
	        	context.write(outputKey, outputValue);
	        }        
	      }
	    
	    
	  }
	  //hàm kiểm tra số nguyên tố
	    public static boolean isPrimeNumber(int n) {
	      // so nguyen n < 2 khong phai la so nguyen to
	      if (n < 2) {
	          return false;
	      }
	      // check so nguyen to khi n >= 2
	      int squareRoot = (int) Math.sqrt(n);
	      for (int i = 2; i <= squareRoot; i++) {
	          if (n % i == 0) {
	              return false;
	          }
	      }
	      return true;
	    }
	}
	
	//class Reduce
	  public static class ReduceForWordCount extends Reducer<Text, IntWritable, Text, IntWritable>
	  {
	    public void reduce(Text num, Iterable<IntWritable> values, Context con) throws IOException, InterruptedException
	    {
	    	 int sum = 0;
	         for(IntWritable value : values)
	         {
	         sum += value.get();
	         }
	         con.write(num, new IntWritable(sum));
	    }
	  }
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		 Configuration c=new Configuration();
		    String[] files=new GenericOptionsParser(c,args).getRemainingArgs();
		    if (files.length != 2) {
		        System.err.println("Usage: wordcount-hbase <input> <output>");
		        System.exit(2);
		      }
		    Job j= Job.getInstance(c,"wordcount");
		    j.setJarByClass(WordCounter.class);
		    j.setMapperClass(TokenizerMapper.class);
		    j.setReducerClass(ReduceForWordCount.class);
		    j.setNumReduceTasks(10);		    
		    j.setOutputKeyClass(Text.class);
		    j.setOutputValueClass(IntWritable.class);
		    
		    FileInputFormat.addInputPath(j, new Path(files[0]));
		    FileOutputFormat.setOutputPath(j, new Path(files[1]));
		    
		    System.exit(j.waitForCompletion(true)?0:1);

	}

}
