package com.kuyun.ktap.monitor;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

public class ExportExcelUtils {

    /**
     * @Title: exportExcel
     * @Description: 导出Excel的方法
     * @author: evan @ 2014-01-09
     * @param workbook
     * @param sheetNum (sheet的位置，0表示第一个表格中的第一个sheet)
     * @param sheetTitle  （sheet的名称）
     * @param headers    （表格的标题）
     * @param result   （表格的数据）
     * @param out  （输出流）
     * @throws Exception
     */
    public void exportExcel(XSSFWorkbook workbook, int sheetNum,
                            String sheetTitle, String[] headers, List<List<Object>> result,
                            OutputStream out) throws Exception {
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(sheetNum, sheetTitle);
        // 设置表格默认列宽度为20个字节
        sheet.setDefaultColumnWidth((short) 20);
        // 生成一个样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        // 把字体应用到当前的样式
        style.setFont(font);

        // 指定当单元格内容显示不下时自动换行
        style.setWrapText(true);

        // 产生表格标题行
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = row.createCell((short) i);

            cell.setCellStyle(style);
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text.toString());
        }
        // 遍历集合数据，产生数据行
        if (result != null) {
            int index = 1;
            for (List<Object> m : result) {
                row = sheet.createRow(index);
                int cellIndex = 0;
                for (Object obj : m) {
                    XSSFCell cell = row.createCell(cellIndex);
                    if(obj instanceof Integer){
                        cell.setCellValue((Integer)obj);
                        XSSFCellStyle cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(0);
                        cell.setCellStyle(cellStyle);
                    }else{
                        cell.setCellValue(obj.toString());
                    }
                    cellIndex++;
                }
                index++;
            }
        }
    }

    public void exportExcel(XSSFWorkbook workbook, int sheetNum,
                            String sheetTitle, String[] headers, Map<String,Map<String,Long>> result,
                            OutputStream out) throws Exception {
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(sheetNum, sheetTitle);
        // 设置表格默认列宽度为20个字节
        sheet.setDefaultColumnWidth((short) 20);
        // 生成一个样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        // 把字体应用到当前的样式
        style.setFont(font);

        // 指定当单元格内容显示不下时自动换行
        style.setWrapText(true);
        // 产生表格标题行
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = row.createCell(i);

            cell.setCellStyle(style);
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text.toString());

        }
        // 遍历集合数据，产生数据行
        if (result != null) {
            int j = 1;
            int count=0;
            for(String key : result.keySet()){
                row = sheet.createRow(j);
                XSSFCell cell0 = row.createCell(0);
                cell0.setCellValue(key);
                Map<String,Long> map = result.get(key);
                int i = 0;
                for(String k : map.keySet()){
                    if(i==0){
                        XSSFCell cell1 = row.createCell(1);
                        XSSFCell cell2 = row.createCell(2);
                        cell1.setCellValue(k);
                        cell2.setCellValue(map.get(k));
                    }else{
                        XSSFRow row1 = sheet.createRow(j);
                        XSSFCell cell1 = row1.createCell(1);
                        XSSFCell cell2 = row1.createCell(2);
                        cell1.setCellValue(k);
                        cell2.setCellValue(map.get(k));
                    }
                    if(j==1){
                        sheet.autoSizeColumn(0, true);
                        sheet.autoSizeColumn(1, true);
                    }
                    count+=map.get(k);
                    i++;
                    j++;
                }
                // 合并单元格
                if(j-1 != j-i){

                    CellRangeAddress cra =new CellRangeAddress(j-i, j-1, 0, 0); // 起始行, 终止行, 起始列, 终止列
                    sheet.addMergedRegion(cra);
                }
            }

            row = sheet.createRow(j);
            XSSFCell cell0 = row.createCell(0);
            cell0.setCellValue("总数");
            XSSFCell cell2 = row.createCell(2);
            System.out.println(sheetTitle+" : "+count);
            cell2.setCellValue(count);
        }
    }

    public static void main(String[] args) {
        try {
            OutputStream out = new FileOutputStream("D:\\test.xlsx");
            List<List<Object>> data = new ArrayList<>();
            for (int i = 1; i < 5; i++) {
                List rowData = new ArrayList();
                rowData.add(String.valueOf(i));
                rowData.add("东霖柏鸿");
                data.add(rowData);
            }
            String[] headers = { "ID", "用户名" };
            ExportExcelUtils eeu = new ExportExcelUtils();
            XSSFWorkbook workbook = new XSSFWorkbook();
            eeu.exportExcel(workbook, 0, "上海", headers, data, out);
            eeu.exportExcel(workbook, 1, "深圳", headers, data, out);
            eeu.exportExcel(workbook, 2, "广州", headers, data, out);
            //原理就是将所有的数据一起写入，然后再关闭输入流。
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}