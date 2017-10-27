package com.kuyun.ktap.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MacMatch {

    private static final String FILE_DIR = "D:\\gzfile\\";


//    public static String[] days = new String[]{"20171009","20171010","20171011","20171012","20171013","20171014","20171015","20171016","20171017","20171018","20171019","20171020","20171021","20171022","20171023","20171024","20171025","20171025"};
    public static String[] days = new String[]{"20171026"};
    public static Map<String,String> monitorMap = new HashMap<>();
    static {
        monitorMap.put("1009-100001","风行-精准开机广告-宝马");
        monitorMap.put("1009-100002","风行-开机广告-宝马");
//        monitorMap.put("1012-100001","风行-开机广告-传奇大亨");
//        monitorMap.put("1011-100001","创维-开机广告-传奇大亨");
//        monitorMap.put("1012-100001","风行-开机广告-传奇大亨");
        monitorMap.put("1010-100001","创维-精准开机广告-宝马");
        monitorMap.put("1010-100002","创维-开机广告-宝");
    }
    public List<String> getRealMac(String pid,String aid,String fileName){
        String filterStr = "p="+pid+"&i="+aid;
        List<String> lines = GzFileUtils.readGzFile(fileName);
        List<String> macList = lines.stream().filter(line -> line.contains(filterStr))
                .map(this::getMacByLine).collect(Collectors.toList());

        return macList;
    }

    public Set<String> getMacs(){
        String filePath = "D:\\工作\\macs-funtv";
        try {
            Set<String> macList = Files.lines(Paths.get(filePath))
                    .map(mac -> MD5Utils.md5(mac.toUpperCase().replace(":","")).toLowerCase())
                    .collect(Collectors.toSet());
            return macList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getMacByLine(String line){
        String regex = ".*?mac=(.*?)&.*?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()){
            return matcher.group(1);
        }

        return "";
    }
    public String getMacInfoByLine(String line){

        String regex = ".*?\\[(.*?)\\].*?mac=(.*?)&.*?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()){
            String macInfo = matcher.group(1)+"-"+matcher.group(2);
            return macInfo;
        }

        return "";
    }
    public static MacInfo getIpMacByLine(String line){

        String regex = ".*?&ip=(.*?)&os.*?mac=(.*?)&.*?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()){
            MacInfo macInfo = new MacInfo(matcher.group(2),matcher.group(1));
            return macInfo;
        }

        return new MacInfo("","");
    }
    public Set<String> process(String pid,String aid,String day){
        File dir = new File(FILE_DIR);
        Set<String> countList = null;
        if(dir.isDirectory()){
            countList =  Arrays.stream(dir.list((File d, String name) -> name.contains(day) && name.endsWith(".gz")))
                    .map(fileName -> this.getRealMac(pid,aid,FILE_DIR+fileName))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toSet());
//            System.out.println(count);
        }
        return countList;
    }
    public void process(){
        Set<String> macList = getMacs();
        long begin = System.currentTimeMillis();
        for(String key : monitorMap.keySet()){
            String[] arr = key.split("-");
            for(String day : days){
                Set<String> result = new HashSet<>();
                Set<String> realMacList = process(arr[0],arr[1],day);
                result.addAll(realMacList);
                result.retainAll(macList);
                System.out.println(day + "   " + monitorMap.get(key) + "    " + realMacList.size() + "   "+ result.size());
            }
        }
        System.out.println("uesed "+ ((System.currentTimeMillis() - begin) /1000)+ " s .");

    }

    /**
     * 反作弊
     * @throws IOException
     */
    public void antiCheating() throws IOException {
        long begin = System.currentTimeMillis();
        ExportExcelUtils eeu = new ExportExcelUtils();
        XSSFWorkbook workbook = new XSSFWorkbook();
        FileOutputStream fos = new FileOutputStream(new File("D:\\day.xlsx"));
        String[] headers = new String[]{"mac","数量"};
        for(String key : monitorMap.keySet()){
            String[] arr = key.split("-");

            int i=0;
            for(String day : days){
                List<List<Object>> data = new ArrayList<>();
                Map<String,Long> dayMap = dayCount(arr[0],arr[1],day);
//                Map<String,Long> secondMap = secondCount(arr[0],arr[1],day);
                dayMap.keySet().stream()
                        .filter(k -> dayMap.get(k)>=10)
                        .forEach((k) -> {
                            List<Object> macList = new ArrayList<>();
                            macList.add(k);
                            macList.add(dayMap.get(k));
                            data.add(macList);
                        });
                long secondCount = 0;
//                if(secondMap != null){
//
//                     secondCount = secondMap.keySet().stream().filter(k -> secondMap.get(k)>=2).count();
//                }


                try {
                    eeu.exportExcel(workbook, i, day, headers, data, fos);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;

            }
            workbook.write(fos);
            fos.close();
        }
        System.out.println("uesed "+ ((System.currentTimeMillis() - begin) /1000)+ " s .");
    }

    public void dayCount() throws IOException {
        long begin = System.currentTimeMillis();
        for(String key : monitorMap.keySet()){
            String[] arr = key.split("-");
            for(String day : days){
                Map<String,Long> dayMap = dayCount(arr[0],arr[1],day);
                long count =  dayMap.keySet().stream()
                        .filter(k -> dayMap.get(k)>3)
                        .mapToLong(kk -> dayMap.get(kk)).sum();
                System.out.println(key+ " " + day + " "+ count);
            }
        }
        System.out.println("uesed "+ ((System.currentTimeMillis() - begin) /1000)+ " s .");
    }

    public void ipCount() throws IOException {
        long begin = System.currentTimeMillis();
        for(String key : monitorMap.keySet()){
            String[] arr = key.split("-");

            for(String day : days){
                List<List<Object>> data = new ArrayList<>();
                Map<String,Long> dayMap = secondCount(arr[0],arr[1],day);
                long count = dayMap.keySet().stream()
                        .filter(k -> dayMap.get(k)>=2)
                        .mapToLong(kk -> dayMap.get(kk)).sum();
                System.out.println(key+ " " + day + " "+ count);
            }
        }
        System.out.println("uesed "+ ((System.currentTimeMillis() - begin) /1000)+ " s .");
    }

    public List<String> getLinesFromFile(String pid,String aid,String day){
        List<String> lines = new ArrayList<>();
        String filterStr = "p="+pid+"&i="+aid;
        File dir = new File(FILE_DIR);
        if(dir.isDirectory()) {
            lines = Arrays.stream(dir.list((File d, String name) -> name.contains(day) && name.endsWith(".gz")))
                    .map(fileName -> GzFileUtils.readGzFile(FILE_DIR+fileName))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toList());
        }
        return lines;
    }

    public void ipMatch(){
        for(String key : monitorMap.keySet()){
            String[] arr = key.split("-");
            List<Map<String,Object>> list = new ArrayList<>();
            for(String day : days){

                ipMatch(arr[0],arr[1],day);
            }
        }
    }

    /**
     * ip地域统计
     * @param pid
     * @param aid
     * @param day
     */
    public void ipMatch(String pid,String aid,String day){
        String filterStr = "p="+pid+"&i="+aid;
        File dir = new File(FILE_DIR);
        if(dir.isDirectory()) {
            List<String> lines = Arrays.stream(dir.list((File d, String name) -> name.contains(day) && name.endsWith(".gz")))
                    .map(fileName -> GzFileUtils.readGzFile(FILE_DIR+fileName))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toList());

            if (lines != null && lines.size() > 0) {
                List<String> ipList = lines.parallelStream().filter(line -> line.contains(filterStr)).map(line -> getIpMacByLine(line).getIp()).collect(Collectors.toList());
                IP.load("D:\\工作\\17monipdb.dat");
                long size = ipList.parallelStream()
                        .map(ip -> IP.find(ip))
                        .map(l -> StringUtils.join(l,""))
                        .filter(l -> l.equals("中国北京北京")).count();
                System.out.println(day+"  "+ipList.size()+"  "+size);
            }
        }
    }

    public Map<String,Long> dayCount(String pid,String aid,String day){
        String filterStr = "p="+pid+"&i="+aid;
        File dir = new File(FILE_DIR);
        if(dir.isDirectory()) {
            List<String> lines = getLinesFromFile(pid,aid,day);
            if (lines != null && lines.size() > 0) {
                Map<String, Long> countMap = lines.parallelStream()
                        .filter(line -> line.contains(filterStr))
                        .collect(Collectors
                                .groupingBy(
                                        line -> this.getMacByLine(line),
                                        Collectors.counting()));
                return countMap;
            }
        }
        return new HashMap<>();
    }

    public Map<String,Long> ipCount(String pid,String aid,String day){
        String filterStr = "p="+pid+"&i="+aid;
        File dir = new File(FILE_DIR);
        if(dir.isDirectory()) {
            List<String> lines = Arrays.stream(dir.list((File d, String name) -> name.contains(day) && name.endsWith(".gz")))
                    .map(fileName -> GzFileUtils.readGzFile(FILE_DIR+fileName))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toList());
            if (lines != null && lines.size() > 0) {
                Map<String, Long> countMap = lines.parallelStream()
                        .filter(line -> line.contains(filterStr))
                        .collect(Collectors
                                .groupingBy(
                                        line -> this.getIpMacByLine(line).getIp(),
                                        Collectors.counting()));
                return countMap;
            }
        }
        return new HashMap<>();
    }

    public Map<String,List<String>> ipMacCount(String pid,String aid,String day){
        String filterStr = "p="+pid+"&i="+aid;
        File dir = new File(FILE_DIR);
        if(dir.isDirectory()) {
            List<String> lines = Arrays.stream(dir.list((File d, String name) -> name.contains(day) && name.endsWith(".gz")))
                    .map(fileName -> GzFileUtils.readGzFile(FILE_DIR+fileName))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toList());
            if (lines != null && lines.size() > 0) {
                Map<String, List<String>> countMap = lines.parallelStream()
                        .filter(line -> line.contains(filterStr))
                        .collect(Collectors
                                .groupingBy(line -> this.getIpMacByLine(line).getIp()));
                return countMap;
            }
        }
        return new HashMap<>();
    }

    public void ipMacCount() throws IOException {
        ExportExcelUtils eeu = new ExportExcelUtils();
        XSSFWorkbook workbook = new XSSFWorkbook();
        FileOutputStream fos = new FileOutputStream(new File("D:\\ipMac.xlsx"));
        String[] headers = new String[]{"ip","mac","数量"};
        for(String key : monitorMap.keySet()){
            String[] arr = key.split("-");
            int i=0;
            for(String day : days){
                Map<String,Map<String,Long>> countMap = new LinkedHashMap<>();
                Map<String,List<String>> map = ipMacCount(arr[0],arr[1],day);
                map.keySet().stream()
                        .filter(k -> map.get(k).size() >=5)
                .forEach(kk -> {
                    Map<String,Long> count =  map.get(kk).stream()
                            .collect(Collectors
                            .groupingBy(line -> this.getMacByLine(line),Collectors.counting()));
                    countMap.put(kk,count);
                });
                System.out.println(countMap.size());
                try {
                    eeu.exportExcel(workbook, i, day, headers, countMap, fos);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
        workbook.write(fos);
        fos.close();
    }

    public Map<String,Long> secondCount(String pid,String aid,String day){

        String filterStr = "p="+pid+"&i="+aid;
        File dir = new File(FILE_DIR);
        if(dir.isDirectory()) {
            List<String> lines = Arrays.stream(dir.list((File d, String name) -> name.contains(day) && name.endsWith(".gz")))
                    .map(fileName -> GzFileUtils.readGzFile(FILE_DIR+fileName))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toList());
            if (lines != null && lines.size() > 0) {
                Map<String, Long> countMap = lines.parallelStream()
                        .filter(line -> line.contains(filterStr))
                        .collect(Collectors
                                .groupingBy(line -> this.getMacInfoByLine(line),
                                        Collectors.counting()));
                return countMap;
            }
        }
        return new HashMap<>();
    }

    public static void main(String[] args) throws IOException {
//        String fileName = "D:\\gzfile\\track.kuyun.com_show.extra.log-20171018-szq-ops-wn-05.gz";
//        Map<String,Long> macList = new MacMatch().secondCount("1009","100002",fileName);
////        for(String key : macList.keySet()){
////            System.out.println(key + "="+macList.get(key));
////        }
//        long size = macList.size();
//        long count = macList.keySet().stream().filter(key -> macList.get(key)>=2).peek((k) -> System.out.println(k+"="+macList.get(k))).count();
//
//        System.out.println(size+","+count);
//        macList.stream().forEach(System.out::println);
//        System.out.println(macList.size());
//        new MacMatch().ipMatch("1010","100001","20171023");
//        new MacMatch().ipMacCount();
//        new MacMatch().ipMacCount("1010","100002","20171023");
        MacMatch macMatch = new MacMatch();
        macMatch.dayCount();
        System.out.println("---------------------");
//        macMatch.ipCount();
    }
}
