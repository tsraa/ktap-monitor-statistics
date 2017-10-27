package com.kuyun.ktap.monitor;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class Main {

    private static final String FILE_DIR = "D:\\gzfile\\";
    public static void main(String[] args) {
	// write your code here
//        process("1010","100001","20171011");
        process();
    }


    public static void process(){
        Map<String,String> monitorMap = new HashMap<>();
//        String[] days = new String[]{"20171009","20171010","20171011","20171012","20171013","20171014","20171015","20171016","20171017","20171018","20171019","20171020","20171021","20171022","20171023"};
        String[] days = new String[]{"20171026"};
        monitorMap.put("1009-100001","风行-精准开机广告-宝马");
        monitorMap.put("1009-100002","风行-开机广告-宝马");
        monitorMap.put("1010-100001","创维-精准开机广告-宝马");
        monitorMap.put("1010-100002","创维-开机广告-宝");
//        monitorMap.put("1012-100002","风行-开机广告-传奇大亨");
        long begin = System.currentTimeMillis();
        for(String key : monitorMap.keySet()){
            String[] arr = key.split("-");
            for(String day : days){
                System.out.println(day + "   " + monitorMap.get(key) + "    " + process(arr[0],arr[1],day));
            }
        }
        System.out.println("uesed "+ ((System.currentTimeMillis() - begin) /1000)+ " s .");

    }

    public static long process(String pid,String aid,String day){
        File dir = new File(FILE_DIR);
        long count = 0;
        if(dir.isDirectory()){
            count =  Arrays.stream(dir.list((File d, String name) -> name.contains(day) && name.endsWith(".gz") )).parallel()
                    .mapToLong(fileName -> count(pid,aid,FILE_DIR+fileName)).sum();
//            System.out.println(count);
        }
        return count;
    }
    public static long count(String pid,String aid,String gzFile){
        long count = 0;
        String filterStr = "p="+pid+"&i="+aid;
        List<String> lines = GzFileUtils.readGzFile(gzFile);
        if(lines != null && lines.size() > 0){
            count = lines.stream().filter(line -> line.contains(filterStr)).collect(Collectors.groupingBy(l -> new MacMatch().getMacByLine(l),Collectors.counting())).size();
//            System.out.println(count);
        }
        return count;
    }


}
