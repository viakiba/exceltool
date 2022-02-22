package ${packageName}

type ${supplier.getClassName()} struct{
<#list supplier.getFiled() as lc>
	/**
	* ${lc.getFiledDesc()}
	*/
	${lc.getFiledName()} ${lc.getFiledType()}
</#list>
}