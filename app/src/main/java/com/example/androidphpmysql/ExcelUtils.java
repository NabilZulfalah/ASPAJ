package com.example.androidphpmysql;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
    private static final String TAG = "ExcelUtils";

    /** Import assets from Excel file using URI */
    public static List<Asset> importAssetsFromExcel(Context context, Uri uri) {
        Log.d(TAG, "=== STARTING EXCEL IMPORT ===");
        Log.d(TAG, "URI: " + uri.toString());

        List<Asset> assetList = new ArrayList<>();
        InputStream inputStream = null;
        Workbook workbook = null;

        try {
            Log.d(TAG, "Step 1: Opening input stream...");
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e(TAG, "ERROR: Could not open input stream from URI");
                return assetList;
            }

            String fileName = getFileName(context, uri);
            Log.d(TAG, "Step 2: File name: " + fileName);

            // Create workbook
            if (fileName != null && fileName.toLowerCase().endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                Log.e(TAG, "No sheet found in Excel file");
                return assetList;
            }

            int lastRowNum = sheet.getLastRowNum();
            Log.d(TAG, "Sheet total rows: " + lastRowNum);

            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                Asset aset = parseRowToAsset(row, i);
                if (aset != null) {
                    assetList.add(aset);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Excel import failed: " + e.getMessage(), e);
        } finally {
            try {
                if (workbook != null) workbook.close();
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing resources", e);
            }
        }

        Log.d(TAG, "Imported " + assetList.size() + " assets");
        return assetList;
    }

    /** Parse a single row to asset */
    private static Asset parseRowToAsset(Row row, int rowIndex) {
        Asset aset = new Asset();

        int id = getIntCellValue(row.getCell(0));
        aset.setId(id);

        String namaBarang = getStringCellValue(row.getCell(1));
        if (namaBarang.trim().isEmpty()) {
            Log.w(TAG, "Row " + rowIndex + " skipped: Nama Barang kosong");
            return null;
        }
        aset.setNamaBarang(namaBarang);

        aset.setKodeBarang(getStringCellValue(row.getCell(2)));
        aset.setJumlahStok(parseStockCell(row.getCell(3))); // ✅ fix: now returns int
        aset.setLokasiBarang(getStringCellValue(row.getCell(4)));
        aset.setJurusanBarang(getStringCellValue(row.getCell(5)));
        aset.setMerk(getStringCellValue(row.getCell(6)));
        aset.setHargaSatuan(parsePriceCell(row.getCell(7)));
        aset.setSumber(getStringCellValue(row.getCell(8)));
        aset.setTahun(getStringCellValue(row.getCell(9)));
        aset.setDeskripsi(getStringCellValue(row.getCell(10)));

        return aset;
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !getStringCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** ✅ Updated: Now returns int instead of String */
    private static int parseStockCell(Cell cell) {
        if (cell == null) return 0;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else {
                String s = getStringCellValue(cell).trim();
                return s.isEmpty() ? 0 : Integer.parseInt(s);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    private static double parsePriceCell(Cell cell) {
        if (cell == null) return 0.0;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String priceStr = cell.getStringCellValue()
                        .replace(",", "")
                        .replace(".", "")
                        .trim();
                return priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr);
            }
        } catch (Exception ignored) {}
        return 0.0;
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) result = cursor.getString(nameIndex);
                }
            } catch (Exception ignored) {}
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private static String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        double v = cell.getNumericCellValue();
                        return (v == (int) v) ? String.valueOf((int) v) : String.valueOf(v);
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e) {
                        return cell.getCellFormula();
                    }
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static int getIntCellValue(Cell cell) {
        if (cell == null) return 0;
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    String str = cell.getStringCellValue().trim();
                    return str.isEmpty() ? 0 : Integer.parseInt(str);
                case FORMULA:
                    return (int) cell.getNumericCellValue();
                default:
                    return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
}
