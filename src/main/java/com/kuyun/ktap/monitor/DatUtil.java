package com.kuyun.ktap.monitor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatUtil {
    private static List<Object> getDatFile(String fileName) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        List<Object> list = new ArrayList<Object>();
        Object obj = null;
        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            while (true) {
                try{
                    obj = ois.readObject();
                    list.add(obj);
                }catch(EOFException e){
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件" + fileName);
            System.exit(-1);
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally{
            if(ois!=null){
                ois.close();
            }
            if(fis!=null){
                fis.close();
            }
        }
        return list;
    }
    public void readMethod1()
    {
        String fileName="c:/kuka1.dat";
        int sum=0;
        try
        {
            DataInputStream in=new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(fileName)));
            sum+=in.readInt();
            sum+=in.readInt();
            sum+=in.readInt();
            System.out.println("The sum is:"+sum);
            in.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void readMethod2()
    {
        try
        {
            FileInputStream stream=new FileInputStream("D:\\工作\\17monipdb.dat");
            int c;
            while((c=stream.read())!=-1)
            {
                System.out.println(c);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
//        List<Object> list = getDatFile("D:\\工作\\17monipdb.dat");
//
//        list.stream().forEach(System.out::println);
        readMethod2();
    }
}
