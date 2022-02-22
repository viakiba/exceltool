package model;

import org.viakiba.exceltool.AfterReadDo;
import org.viakiba.exceltool.convert.*;
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
public class DataExcel implements AfterReadDo{
	/**
	* gid<cs>
	*/
	@ExcelProperty(value = "gid")
	private int gid;

	/**
	* 掉落包的优先级，1优先级最大，不可以掉落优先级比自己大或一样的掉落包
	*/
	@ExcelProperty(value = "droptype")
	private int droptype;

	/**
	* 掉落Id，仅服务器用
	*/
	@ExcelProperty(value = "dropIds",converter = StringListIntegerConverter2.class)
	private List<List<Integer>> dropIds;

	/**
	* 掉落组权重(顺序从分组0开始)
	*/
	@ExcelProperty(value = "dropGroupWeights",converter = StringListIntegerConverter.class)
	private List<Integer> dropGroupWeights;


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
		return gid;
	}
}