package com.kuyun.ktap.monitor;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class GzFileUtils {

    public static final int BUFFER = 1024;
    public static final CharSequence EXT = ".bz2";

    public static List<String> readGzFile(String gzFile){
        List<String> lines=new ArrayList();
        try(InputStream in = new GZIPInputStream(new FileInputStream(gzFile))) {
            Scanner sc=new Scanner(in);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static List<String> readFile(String file){
        List<String> lines=new ArrayList();
        try(InputStream in = new FileInputStream(file)) {
            Scanner sc=new Scanner(in);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static List<String> readZipFile(String file) throws Exception {
        ZipFile zf = new ZipFile(file);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        List<String> lines = new ArrayList<>();
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.isDirectory()) {
            } else {
                System.err.println("file - " + ze.getName() + " : "
                        + ze.getSize() + " bytes");
                long size = ze.getSize();
                if (size > 0) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(zf.getInputStream(ze)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        lines.add(line);
                    }
                    br.close();
                }
                System.out.println();
            }
        }
        zin.closeEntry();

        return lines;
    }
    public static void decompress(String file)
            throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        BZip2CompressorInputStream gis = new BZip2CompressorInputStream(in);

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = gis.read(data, 0, BUFFER)) != -1) {
//            os.write(data, 0, count);
        }

        gis.close();
    }
    public static void main(String[] args) {
        try {
            readZipFile("D:\\gzfile\\track.kuyun.com_show.extra.log-20171024-szq-ops-wn-05.bz2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
