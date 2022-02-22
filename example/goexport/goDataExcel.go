package excel_struct

type goDataExcel struct{
	/**
	* gid<cs>
	*/
	gid int32
	/**
	* 掉落包的优先级，1优先级最大，不可以掉落优先级比自己大或一样的掉落包
	*/
	droptype int32
	/**
	* 掉落Id，仅服务器用
	*/
	dropIds [][]int32
	/**
	* 掉落组权重(顺序从分组0开始)
	*/
	dropGroupWeights []int32
}