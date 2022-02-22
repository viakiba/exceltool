# 游戏策划Excel数值表解析工具

    常用于游戏研发中，简单方便的进行Excel数值表的解析导出。

* JSON 格式文件生成
* Lua 代码与数据的生成
* Go结构体生成
* Java代码生成与解析

## 构建 可执行的Jar 文件

    需要具备 jdk1.8 以及 maven 环境,采用 EaasyExcel 以及 Freemarker 进行实现。
    
    注意 ( org.viakiba.exceltool.ReadExcelUtil )
        一维分割符 使用 , 分割
        二维分割符 使用#分割成一维一维使用分割成具体单元
        忽略字段 以 # 开头 
    可以修改上述符合，进行构建使用。
    Go 与 Java 进行类的类型定义
        go:     org.viakiba.exceltool.gengolang.HeadReadListener.getType
        java:   org.viakiba.exceltool.genjava.HeadReadListener.getType

在代码根目录执行如下脚本

```shell
# 在 target 目录下生成 exceltool-1.0-SNAPSHOT.jar 文件，即为可执行的文件。
mvn clean package -Dmaven.test.skip=true -f pom-jar.xml
```

## JSON 格式文件生成

    新建一个 json  文件夹，根目录包含构建生成的 exceltool-1.0-SNAPSHOT.jar 文件 。 
    config 文件夹也存放在此根目录下，不过可以随意放置，取名叫做 config 文件夹,文件夹内放入 excel2json.xml（resources 源代码）以及 Json.ftl 配置。
    excel 文件夹也存放在此根目录的上一级，不过可以随意放置，取名叫做 excelresource。

```text
❯ pwd
/Users/dd/Desktop/exceltool
❯ tree
.
├── excelresource
│   └── Demo_1.xlsm
├── json
│   ├── config
│   │   ├── Json.ftl
│   │   └── excel2json.xml
│   ├── exceltool-1.0-SNAPSHOT.jar
└── jsonexport
    └── luaDataExcel.json
```

    扩展几个工具
        https://www.bejson.com/explore/index_new/  json  格式化
        https://www.bejson.com/convert/json2csharp/  json  转 c# bean 在线
        https://www.bejson.com/json2javapojo/new/  json  转 java bean 在线

### excel2json.xml 配置

    excel2json.xml 在 源代码 resource 下的 config 里面有例子与注释

### Java VM 启动参数

    以下下路径均为系统的绝对路径，根据项目的实际情况进行合理制定即可，工具不对以下路径进行硬编码，保证工具属性，不侵入项目。
    这样可以根据这些参数使用不用的配置以及不同的excel。

```text
设置  excel2json.xml配置路径   
    -Dgen.json.configPath=/Users/dd/Desktop/exceltool/json/config/excel2json.xml
设置  模板所在父路径 （templateName）
    -Dgen.json.templateParentPath=/Users/dd/Desktop/exceltool/json/config/
设置  json文件输出父路径
    -Dgen.json.jsonOutPutParentPath=/Users/dd/Desktop/exceltool/jsonexport/
设置项  excel所在父路径    
    -Dgen.json.excelParentPath=/Users/dd/Desktop/exceltool/excelresource/
可选设置项  指定系统环境是否为 unix ，用于 路径处理 windows 为 false ，其余 为 true。
    -Dgen.json.unix=false
```

### 举例

文件及路径如下
```text
❯ pwd
/Users/dd/Desktop/exceltool
❯ tree
.
├── excelresource                              excel资源
│   └── Demo_1.xlsm
└── json                                        json导出工具及配置
│   ├── config
│   │  └── excel2json.xml
│   │  └── Json.ftl                             excel2json.xml 里指定的 templateName 
│   └── exceltool-1.0-SNAPSHOT.jar              可执行的jar
└── jsonexport                                   json导出位置
```

### 启动

```shell
java -cp exceltool-1.0-SNAPSHOT.jar \
 -Dgen.json.configPath=/Users/dd/Desktop/exceltool/json/config/excel2json.xml \
 -Dgen.json.templateParentPath=/Users/dd/Desktop/exceltool/json/config/ \
 -Dgen.json.jsonOutPutParentPath=/Users/dd/Desktop/exceltool/jsonexport/ \
 -Dgen.json.excelParentPath=/Users/dd/Desktop/exceltool/excelresource/ \
 org.viakiba.exceltool.genjson.GenJsonFile
```

结果

```json lines
{"gid":1100001,"droptype":10,"dropIds":[[1080001]],"dropGroupWeights":[100]}
{"gid":1100002,"droptype":10,"dropIds":[[1080002]],"dropGroupWeights":[100]}
{"gid":1100003,"droptype":10,"dropIds":[[1120101],[1120201],[1120301],[1120401],[1120102],[1120102],[1120302],[1120402]],"dropGroupWeights":[125,125,125,125,125,125,125,125]}
```

## Go结构体生成

    新建一个 go  文件夹，根目录包含构建生成的 exceltool-1.0-SNAPSHOT.jar 文件 。 
    config 文件夹也存放在此根目录下，不过可以随意放置，取名叫做 config 文件夹,文件夹内放入 excel2json.xml（resources 源代码）以及 Go.ftl 配置。
    excel 文件夹也存放在此根目录的上一级，不过可以随意放置，取名叫做 excelresource。

```text
❯ pwd
/Users/dd/Desktop/exceltool
❯ tree
.
├── excelresource
│   └── Demo_1.xlsm
├── go
│   ├── config
│   │   ├── Go.ftl
│   │   └── excel2go.xml
│   └── exceltool-1.0-SNAPSHOT.jar
├── goexport
```

### excel2go.xml 配置

     excel2go.xml 在 源代码 resource 下的 config 里面有例子与注释

### Java VM 启动参数

```text
设置  excel2go.xml配置路径
    -Dgen.config.configPath=/Users/dd/Desktop/exceltool/go/config/excel2go.xml
```

### 举例

```shell
java -cp exceltool-1.0-SNAPSHOT.jar \
 -Dgen.config.configPath=/Users/dd/Desktop/exceltool/go/config/excel2go.xml \
 org.viakiba.exceltool.gengolang.GenGoFile
```

结果

```golang
packge excel_struct

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
```

## Lua 代码与数据的生成
    
    lua的代码与数据在一个文件里面。
    新建一个 lua  文件夹，根目录包含构建生成的 exceltool-1.0-SNAPSHOT.jar 文件 。 
    config 文件夹也存放在此根目录下，不过可以随意放置，取名叫做 config 文件夹,文件夹内放入 excel2lua.xml（resources 源代码）以及 Lua.ftl 配置。
    excel 文件夹也存放在此根目录的上一级，不过可以随意放置，取名叫做 excelresource。

```text
❯ pwd
/Users/dd/Desktop/exceltool
❯ tree
.
├── excelresource
│   └── Demo_1.xlsm
├── lua
│   ├── config
│   │   ├── Lua.ftl
│   │   └── excel2lua.xml
│   └── exceltool-1.0-SNAPSHOT.jar
└── luaexport
```

### excel2lua.xml 配置

     excel2lua.xml 在 源代码 resource 下的 config 里面有例子与注释

### Java VM 启动参数

```text
设置excel2lua.xml配置路径
    -Dgen.lua.configPath=/Users/dd/Desktop/exceltool/lua/config/excel2lua.xml
设置模板所在父路径
    -Dgen.lua.templateParentPath=/Users/dd/Desktop/exceltool/lua/config/
设置lua文件输出父路径
    -Dgen.lua.luaOutPutParentPath=/Users/dd/Desktop/exceltool/luaexport/
设置项excel所在父路径
    -Dgen.lua.excelParentPath=/Users/dd/Desktop/exceltool/excelresource/
可选设置项  指定系统环境是否为 unix ，用于 路径处理 windows 为 false ，其余 为 true。
    -Dgen.lua.unix=false
```

### 举例

```shell
java -cp exceltool-1.0-SNAPSHOT.jar \
 -Dgen.lua.configPath=/Users/dd/Desktop/exceltool/lua/config/excel2lua.xml \
 -Dgen.lua.templateParentPath=/Users/dd/Desktop/exceltool/lua/config/ \
 -Dgen.lua.luaOutPutParentPath=/Users/dd/Desktop/exceltool/luaexport/ \
 -Dgen.lua.excelParentPath=/Users/dd/Desktop/exceltool/excelresource/ \
 org.viakiba.exceltool.genlua.GenLuaFile
```

结果

```lua
local key_map = {
	--[[ gid<cs> ]]--
	-- int
	gid = 1,
	--[[ 掉落包的优先级，1优先级最大，不可以掉落优先级比自己大或一样的掉落包 ]]--
	-- int
	droptype = 2,
	--[[ 掉落Id，仅服务器用 ]]--
	-- int[][]
	dropIds = 3,
	--[[ 掉落组权重(顺序从分组0开始) ]]--
	-- int[]
	dropGroupWeights = 4
}

local map = {
	[1100001] =	{1100001,	10,	{{1080001}},	{100}},
	[1100002] =	{1100002,	10,	{{1080002}},	{100}},
	[1100003] =	{1100003,	10,	{{1120101},{1120201},{1120301},{1120401},{1120102},{1120102},{1120302},{1120402}},	{125,125,125,125,125,125,125,125}},
do
	local item_metatable = {
		__index = function (t, k)
			local pos = key_map[k]
			if pos then
				return rawget(t, pos)
			else
				return nil
			end
		end,
		__newindex = function (_,k,v)
			errorf("can not change the config key [%s] to [%s]", k,v)
		end
	}

	local setmetatable = setmetatable
	for _, date_item in pairs(map) do
		setmetatable(date_item, item_metatable)
	end
end

return map
```

## Java代码生成与解析

    java是本库支持最为完善的语言，不仅包含代码生成，也包含了excel直接解析。
    使用如下命令生成可以依赖的jar文件。使用 mvn 导入本地仓库或者私有仓库。
    mvn clean package -Dmaven.test.skip=true -f pom.xml

    在已有的项目中引入此 jar 的依赖。
    见源代码 test下 的测试代码。

### 生成 Java Model

```text
见 test 下的 TestJavaGen.java
在项目里面新建类似的可执行方法，执行 vm 参数 增加：
    -Dgen.config.configPath=/Users/dd/Documents/excelTool/src/main/resources/config/excel2java.xml
    生成的 JavaBean 注意实现 getIdKey 方法。
excel2java.xml
    里面有详细的注释
UnionAnnotate
    可以指定大 key 双层map。

校验实现
    基础校验：
        apache-bval https://www.baeldung.com/apache-bval
    逻辑校验
        实现 afterAllSheetReadDo 接口即可。    
```

### 解析java到内存

```text
见 test 下的 TestJavaRead.java

几个查询方法
    ExcelDataService
        getByClassAndId  （根据 class 与 id 查询对应的数据）
        getByClass       （根据 class查询所有数据）  
        getByClassAndTypeName （根据 class 与 大 key 查询所有数据） 配合 UnionAnnotate 注解使用，UnionAnnotate 的 type 指定成 大key 的字段名称即可。
```

### 后记

    本来想实现 protobuf 版本，但是序列化后的数据 需要和代码解析顺序保持一致，每次生成的时候很难固定顺利，或者说 需要额外的文件进行指定顺序。所以就不搞了。不差这点空间，客户端稍微优化一个图片就把空间省下来了。