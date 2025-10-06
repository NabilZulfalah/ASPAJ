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

    /**
     * Import assets from Excel file using URI with extensive debugging
     */
    public static List<Asset> importAssetsFromExcel(Context context, Uri uri) {
        Log.d(TAG, "=== STARTING EXCEL IMPORT ===");
        Log.d(TAG, "URI: " + uri.toString());

        List<Asset> assetList = new ArrayList<>();
        InputStream inputStream = null;
        Workbook workbook = null;

        try {
            // Step 1: Open input stream
            Log.d(TAG, "Step 1: Opening input stream...");
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e(TAG, "ERROR: Could not open input stream from URI");
                return assetList;
            }
            Log.d(TAG, "✓ Input stream opened successfully");

            // Step 2: Get filename and determine format
            String fileName = getFileName(context, uri);
            Log.d(TAG, "Step 2: File name: " + fileName);

            // Step 3: Create workbook
            Log.d(TAG, "Step 3: Creating workbook...");
            try {
                if (fileName != null && fileName.toLowerCase().endsWith(".xls")) {
                    Log.d(TAG, "Creating HSSFWorkbook for .xls file");
                    workbook = new HSSFWorkbook(inputStream);
                } else {
                    Log.d(TAG, "Creating XSSFWorkbook for .xlsx file");
                    workbook = new XSSFWorkbook(inputStream);
                }
                Log.d(TAG, "✓ Workbook created successfully");
            } catch (Exception e) {
                Log.e(TAG, "ERROR: Failed to create workbook: " + e.getMessage(), e);
                throw new RuntimeException("Invalid Excel file format. Error: " + e.getMessage(), e);
            }

            // Step 4: Get sheet
            Log.d(TAG, "Step 4: Getting sheet...");
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                Log.e(TAG, "ERROR: No sheet found in Excel file");
                return assetList;
            }
            Log.d(TAG, "✓ Sheet found: " + sheet.getSheetName());

            // Step 5: Check rows
            int lastRowNum = sheet.getLastRowNum();
            int firstRowNum = sheet.getFirstRowNum();
            Log.d(TAG, "Step 5: Sheet info - First row: " + firstRowNum + ", Last row: " + lastRowNum);
            Log.d(TAG, "Total rows: " + (lastRowNum - firstRowNum + 1));

            if (lastRowNum < 1) {
                Log.w(TAG, "WARNING: Sheet has no data rows (only header or empty)");
                return assetList;
            }

            // Step 6: Check header row
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                Log.d(TAG, "Step 6: Header row found, checking columns...");
                for (int i = 0; i < 11; i++) {
                    Cell cell = headerRow.getCell(i);
                    String cellValue = getStringCellValue(cell);
                    Log.d(TAG, "Header[" + i + "]: '" + cellValue + "'");
                }
            } else {
                Log.w(TAG, "WARNING: No header row found");
            }

            // Step 7: Process data rows
            Log.d(TAG, "Step 7: Processing data rows...");
            int successCount = 0;
            int skipCount = 0;
            int errorCount = 0;

            for (int i = 1; i <= lastRowNum; i++) {
                Log.d(TAG, "Processing row " + i + "...");

                Row row = sheet.getRow(i);
                if (row == null) {
                    Log.d(TAG, "Row " + i + " is null, skipping");
                    skipCount++;
                    continue;
                }

                // Check if row is empty
                if (isRowEmpty(row)) {
                    Log.d(TAG, "Row " + i + " is empty, skipping");
                    skipCount++;
                    continue;
                }

                try {
                    Asset asset = parseRowToAsset(row, i);
                    if (asset != null) {
                        assetList.add(asset);
                        successCount++;
                        Log.d(TAG, "✓ Row " + i + " parsed successfully: " + asset.getNamaBarang());
                    } else {
                        skipCount++;
                        Log.d(TAG, "Row " + i + " returned null asset, skipping");
                    }
                } catch (Exception e) {
                    errorCount++;
                    Log.w(TAG, "Error parsing row " + i + ": " + e.getMessage(), e);
                }
            }

            Log.d(TAG, "=== IMPORT SUMMARY ===");
            Log.d(TAG, "Total rows processed: " + (lastRowNum));
            Log.d(TAG, "Successful: " + successCount);
            Log.d(TAG, "Skipped: " + skipCount);
            Log.d(TAG, "Errors: " + errorCount);
            Log.d(TAG, "Final asset list size: " + assetList.size());

        } catch (Exception e) {
            Log.e(TAG, "FATAL ERROR during Excel import: " + e.getMessage(), e);
            assetList.clear();
            // Re-throw with more context
            throw new RuntimeException("Excel import failed: " + e.getMessage(), e);
        } finally {
            // Cleanup
            try {
                if (workbook != null) workbook.close();
                if (inputStream != null) inputStream.close();
                Log.d(TAG, "✓ Resources cleaned up");
            } catch (IOException e) {
                Log.e(TAG, "Error cleaning up resources", e);
            }
        }

        Log.d(TAG, "=== EXCEL IMPORT COMPLETED ===");
        return assetList;
    }

    /**
     * Parse a single row to Asset object with detailed logging
     */
    private static Asset parseRowToAsset(Row row, int rowIndex) {
        Log.d(TAG, "Parsing row " + rowIndex + ":");

        Asset asset = new Asset();

        // ID
        int id = getIntCellValue(row.getCell(0));
        asset.setId(id);
        Log.d(TAG, "  ID: " + id);

        // Nama Barang - this is required
        String namaBarang = getStringCellValue(row.getCell(1));
        if (namaBarang.trim().isEmpty()) {
            Log.w(TAG, "  ERROR: Nama Barang is empty, skipping row");
            return null;
        }
        asset.setNamaBarang(namaBarang);
        Log.d(TAG, "  Nama Barang: '" + namaBarang + "'");

        // Kode Barang
        String kodeBarang = getStringCellValue(row.getCell(2));
        asset.setKodeBarang(kodeBarang);
        Log.d(TAG, "  Kode Barang: '" + kodeBarang + "'");

        // Jumlah Stok
        String jumlahStok = parseStockCell(row.getCell(3));
        asset.setJumlahStok(jumlahStok);
        Log.d(TAG, "  Jumlah Stok: '" + jumlahStok + "'");

        // Other fields
        String lokasi = getStringCellValue(row.getCell(4));
        String jurusan = getStringCellValue(row.getCell(5));
        String merk = getStringCellValue(row.getCell(6));
        double harga = parsePriceCell(row.getCell(7));
        String sumber = getStringCellValue(row.getCell(8));
        String tahun = getStringCellValue(row.getCell(9));
        String deskripsi = getStringCellValue(row.getCell(10));

        asset.setLokasiBarang(lokasi);
        asset.setJurusanBarang(jurusan);
        asset.setMerk(merk);
        asset.setHargaSatuan(harga);
        asset.setSumber(sumber);
        asset.setTahun(tahun);
        asset.setDeskripsi(deskripsi);

        Log.d(TAG, "  Complete asset: " + namaBarang + " | " + kodeBarang + " | Stock: " + jumlahStok + " | Price: " + harga);
        return asset;
    }

    /**
     * Check if row is completely empty
     */
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

    /**
     * Parse stock cell with logging
     */
    private static String parseStockCell(Cell cell) {
        if (cell == null) return "0";

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                int stock = (int) cell.getNumericCellValue();
                return String.valueOf(stock);
            } else {
                String stockStr = getStringCellValue(cell).trim();
                return stockStr.isEmpty() ? "0" : stockStr;
            }
        } catch (Exception e) {
            Log.w(TAG, "Error parsing stock cell: " + e.getMessage());
            return "0";
        }
    }

    /**
     * Parse price cell with logging
     */
    private static double parsePriceCell(Cell cell) {
        if (cell == null) return 0.0;

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String priceStr = cell.getStringCellValue().replace(",", "").replace(".", "").trim();
                return priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr);
            }
        } catch (Exception e) {
            Log.w(TAG, "Error parsing price cell: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Get filename from URI
     */
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting filename", e);
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        Log.d(TAG, "Resolved filename: " + result);
        return result;
    }



    /**
     * Safe get string cell value with detailed logging
     */
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
                        double numValue = cell.getNumericCellValue();
                        if (numValue == (int) numValue) {
                            return String.valueOf((int) numValue);
                        } else {
                            return String.valueOf(numValue);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e) {
                        return cell.getCellFormula();
                    }
                case BLANK:
                    return "";
                default:
                    return "";
            }
        } catch (Exception e) {
            Log.w(TAG, "Error getting string cell value: " + e.getMessage());
            return "";
        }
    }

    /**
     * Safe get int cell value
     */
    private static int getIntCellValue(Cell cell) {
        if (cell == null) return 0;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    try {
                        String str = cell.getStringCellValue().trim();
                        return str.isEmpty() ? 0 : Integer.parseInt(str);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                case FORMULA:
                    try {
                        return (int) cell.getNumericCellValue();
                    } catch (Exception e) {
                        return 0;
                    }
                default:
                    return 0;
            }
        } catch (Exception e) {
            Log.w(TAG, "Error getting int cell value: " + e.getMessage());
            return 0;
        }
    }
}