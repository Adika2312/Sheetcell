package api;



public interface Engine {
    //void loadSystemFile(String filePath);
    SheetData getSheetDTO();
    String getCellValue(int row, int col);
    boolean isCellInBounds(int row, int col);
    void updateCellValue(int row, int column, CellValue value);
    CellData getCellDTO(int row, int col);
    //List<Version> getDocumentVersions(String documentTitle);
}
