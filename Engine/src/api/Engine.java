package api;


import generated.STLSheet;
import jakarta.xml.bind.JAXBException;

import java.io.IOException;

public interface Engine {
    void loadFile(String filePath) throws IOException, JAXBException;
    DTO getSheetDTO();
    boolean isCellInBounds(int row, int col);
    void updateCellValue(String cellIdentity, CellValue value, String originalValue);
    DTO getCellDTO(String cellIdentity);
    STLSheet buildSTLSheetFromXML(String filePath)throws IOException, JAXBException;
    void buildSheetFromSTLSheet(STLSheet currentSTLSheet);
    boolean isSheetLoaded();
    //List<Version> getDocumentVersions(String documentTitle);
}
