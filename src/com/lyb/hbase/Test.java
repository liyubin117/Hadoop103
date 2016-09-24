package com.lyb.hbase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
  






import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;
  
public class Test
{  
	

    public static Configuration configuration;
    static {  
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "172.168.2.9");
        configuration.set("hbase.master", "172.168.2.9:60000");
    }  
  
    public static void main(String[] args) {  
    	Logger log=Logger.getLogger(ZooKeeper.class);
    	log.setLevel(Level.ERROR);

    	
    	System.out.println("-----createTable-----");
         createTable("liyubin");
        System.out.println("-----createTable-----");
         insertData("liyubin");
         QueryAll("liyubin");
         QueryByCondition1("liyubin");
         QueryByCondition2("liyubin");
         QueryByCondition3("liyubin");
         deleteRow("liyubin","r2");
        //deleteByCondition("liyubin","abcdef");
    }  
  
    /** 
     * 创建表 
     * @param tableName 
     */  
    public static void createTable(String tableName) {  
        System.out.println("start create table ......");
        try {  
            HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
            if (hBaseAdmin.tableExists(tableName)) {// 如果存在要创建的表，那么先删除，再创建  
            	dropTable(tableName);
                //hBaseAdmin.disableTable(tableName);
                //hBaseAdmin.deleteTable(tableName);
                System.out.println(tableName + " is exist,detele....");
            }  
            HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
            tableDescriptor.addFamily(new HColumnDescriptor("column1"));
            tableDescriptor.addFamily(new HColumnDescriptor("column2"));
            tableDescriptor.addFamily(new HColumnDescriptor("column3"));
            hBaseAdmin.createTable(tableDescriptor);
        } catch (MasterNotRunningException e) {  
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {  
            e.printStackTrace();
        } catch (IOException e) {  
            e.printStackTrace();
        }  
        System.out.println("end create table ......");
    }  
  
    /** 
     * 插入数据 
     * @param tableName 
     */  
    public static void insertData(String tableName) {  
        System.out.println("start insert data ......");
        @SuppressWarnings("resource")
		HTablePool pool = new HTablePool(configuration, 1000);
        //HTable table = (HTable) pool.getTable(tableName);
        Put put = new Put("112233bbbcccc".getBytes());// 一个PUT代表一行数据，再NEW一个PUT表示第二行数据,每行一个唯一的ROWKEY，此处rowkey为put构造方法中传入的值  
        put.add("column1".getBytes(), null, "111".getBytes());// 本行数据的第一列  
        put.add("column2".getBytes(), null, "bbb".getBytes());// 本行数据的第三列  
        put.add("column3".getBytes(), null, "ccc".getBytes());// 本行数据的第三列  
        try {  
        	pool.getTable(tableName).put(put);
        } catch (IOException e) {  
            e.printStackTrace();
        }  
        System.out.println("end insert data ......");
        
        put = new Put("r2".getBytes());// 一个PUT代表一行数据，再NEW一个PUT表示第二行数据,每行一个唯一的ROWKEY，此处rowkey为put构造方法中传入的值  
        put.add("column1".getBytes(), null, "aaa".getBytes());// 本行数据的第一列  
        put.add("column2".getBytes(), null, "bbb".getBytes());// 本行数据的第三列  
        put.add("column3".getBytes(), null, "ccc".getBytes());// 本行数据的第三列  
        try {  
        	pool.getTable(tableName).put(put);
        } catch (IOException e) {  
            e.printStackTrace();
        }  
        System.out.println("end insert data ......");
    }  
  
    /** 
     * 删除一张表 
     * @param tableName 
     */  
    public static void dropTable(String tableName) {  
        try {  
            HBaseAdmin admin = new HBaseAdmin(configuration);
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        } catch (MasterNotRunningException e) {  
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {  
            e.printStackTrace();
        } catch (IOException e) {  
            e.printStackTrace();
        }  
  
    }  
    /** 
     * 根据 rowkey删除一条记录 
     * @param tablename 
     * @param rowkey 
     */  
     public static void deleteRow(String tablename, String rowkey)  {  
        try {  
            HTable table = new HTable(configuration, tablename);
            List list = new ArrayList();
            Delete d1 = new Delete(rowkey.getBytes());
            list.add(d1);
              
            table.delete(list);
            System.out.println("删除行成功!");
              
        } catch (IOException e) {  
            e.printStackTrace();
        }  
          
  
    }  
  
     /** 
      * 组合条件删除 
      * @param tablename 
      * @param rowkey 
      */  
     public static void deleteByCondition(String tablename, String rowkey)  {  
            //目前还没有发现有效的API能够实现 根据非rowkey的条件删除 这个功能能，还有清空表全部数据的API操作  
  
    }  
  
  
    /** 
     * 查询所有数据 
     * @param tableName 
     */  
    public static void QueryAll(String tableName) {  
        HTablePool pool = new HTablePool(configuration, 1000);
//        HTable table = (HTable) pool.getTable(tableName);
        try {  
            ResultScanner rs = pool.getTable(tableName).getScanner(new Scan());
            for (Result r : rs) {  
                System.out.println("获得到rowkey:" + new String(r.getRow()));
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("列：" + new String(keyValue.getFamily())  
                            + "====值:" + new String(keyValue.getValue()));
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();
        }  
    }  
  
    /** 
     * 单条件查询,根据rowkey查询唯一一条记录 
     * @param tableName 
     */  
    public static void QueryByCondition1(String tableName) {  
  
        @SuppressWarnings("resource")
		HTablePool pool = new HTablePool(configuration, 1000);
        //HTable table = (HTable) pool.getTable(tableName);
        try {  
            Get scan = new Get("r2".getBytes());// 根据rowkey查询  
            Result r = pool.getTable(tableName).get(scan);
            System.out.println("获得到rowkey:" + new String(r.getRow()));
            for (KeyValue keyValue : r.raw()) {  
                System.out.println("列：" + new String(keyValue.getFamily())  
                        + "====值:" + new String(keyValue.getValue()));
            }  
        } catch (IOException e) {  
            e.printStackTrace();
        }  
    }  
  
    /** 
     * 单条件按查询，查询多条记录 
     * @param tableName 
     */  
    public static void QueryByCondition2(String tableName) {  
  
        try {  
            HTablePool pool = new HTablePool(configuration, 1000);
            //HTable table = (HTable) pool.getTable(tableName);
            Filter filter = new SingleColumnValueFilter(Bytes  
                    .toBytes("column1"), null, CompareOp.EQUAL, Bytes  
                    .toBytes("aaa")); // 当列column1的值为aaa时进行查询  
            Scan s = new Scan();
            s.setFilter(filter);
            ResultScanner rs = pool.getTable(tableName).getScanner(s);
            for (Result r : rs) {  
                System.out.println("获得到rowkey:" + new String(r.getRow()));
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("列：" + new String(keyValue.getFamily())  
                            + "====值:" + new String(keyValue.getValue()));
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();
        }  
  
    }  
  
    /** 
     * 组合条件查询 
     * @param tableName 
     */  
    public static void QueryByCondition3(String tableName) {  
  
        try {  
            HTablePool pool = new HTablePool(configuration, 1000);
            //HTable table = (HTable) pool.getTable(tableName);
  
            List<Filter> filters = new ArrayList<Filter>();
  
            Filter filter1 = new SingleColumnValueFilter(Bytes  
                    .toBytes("column1"), null, CompareOp.EQUAL, Bytes  
                    .toBytes("aaa"));
            filters.add(filter1);
  
            Filter filter2 = new SingleColumnValueFilter(Bytes  
                    .toBytes("column2"), null, CompareOp.EQUAL, Bytes  
                    .toBytes("bbb"));
            filters.add(filter2);
  
            Filter filter3 = new SingleColumnValueFilter(Bytes  
                    .toBytes("column3"), null, CompareOp.EQUAL, Bytes  
                    .toBytes("ccc"));
            filters.add(filter3);
  
            FilterList filterList1 = new FilterList(filters);
  
            Scan scan = new Scan();
            scan.setFilter(filterList1);
            ResultScanner rs = pool.getTable(tableName).getScanner(scan);
            for (Result r : rs) {  
                System.out.println("获得到rowkey:" + new String(r.getRow()));
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("列：" + new String(keyValue.getFamily())  
                            + "====值:" + new String(keyValue.getValue()));
                }  
            }  
            rs.close();
  
        } catch (Exception e) {  
            e.printStackTrace();
        }  
  
    }  
  
}  