package ${packageName};

import ${afterReadDoPackage};
import ${convertPackage};
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.alibaba.excel.annotation.*;
import java.util.*;

/**
* 实体类
*/
@Data
@ToString
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class ${supplier.getClassName()} implements AfterReadDo{
<#list supplier.getFiled() as lc>
	/**
	* ${lc.getFiledDesc()}
	*/
	${lc.getExcelProperty()}
	private ${lc.getFiledType()} ${lc.getFiledName()};

</#list>

	/**
	* 每读完成一个sheet之后会调用此方法 此时遍历所有对象做check
	*/
	@Override
	public void afterAllSheetReadDo() {

	}

	/**
	* 会以此方法返回的id作为key（不可能是字符串吧）
	*/
	@Override
	public Object getIdKey() {
		return null;
	}
}