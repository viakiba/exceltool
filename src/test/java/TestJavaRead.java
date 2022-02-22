import org.viakiba.exceltool.ExcelDataService;
import org.viakiba.exceltool.ReadExcelUtil;
import model.DataExcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.viakiba.exceltool.ReadExcelUtil.addSkipRowNumAll;
import static org.viakiba.exceltool.ReadExcelUtil.readReloadSheet;

public class TestJavaRead {
    public static void main(String[] args) throws Exception {
        loadAllExcel();
        ExcelDataService.getByClass(DataExcel.class).clear();
        reloadSingleExcel();
    }

    public static void reloadSingleExcel() throws ClassNotFoundException {
        List<Integer> skipList = new ArrayList<>();
        skipList.add(1);
        skipList.add(2);
        addSkipRowNumAll(skipList);
        List<DataExcel> dataExcels = readReloadSheet(1, 0,
                "Demo_1.xlsm", "/Users/dd/Documents/excelTool/src/test/resources/excel/");
        System.out.println(dataExcels);
    }

    public static void loadAllExcel() throws Exception {
        List<Integer> skipList = new ArrayList<>();
        skipList.add(1);
        skipList.add(2);
        ReadExcelUtil.initExcel(
                "/Users/dd/Documents/excelTool/src/main/resources/config/excel2java.xml",
                "/Users/dd/Documents/excelTool/src/test/resources/excel/",
                1, skipList);
        Map<Object, Object> byClassAndTypeName = ExcelDataService.getByClass(DataExcel.class);
        System.out.println(byClassAndTypeName.keySet());
    }
}
