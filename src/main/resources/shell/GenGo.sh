#!/bin/sh
APP_JAR=-classpath .;lib\*
configPath=-Dgen.lua.configPath=D:\Code\common_excel_tool\src\main\resources\excel2lua.xml
templateParentPath=-Dgen.lua.templateParentPath=D:\Code\common_excel_tool\src\main\resources
luaOutPutParentPath=-Dgen.lua.luaOutPutParentPath=C:\Users\Admin\Desktop\ss\
excelParentPath=-Dgen.lua.excelParentPath=D:\Code\square_pro_project3x_dev\conf\excel\
unix=-Dgen.lua.unix=false
APP_MAIN=com.ohayoo.common.excel.genlua.GenLuaFile
java $APP_JAR $configPath $templateParentPath $luaOutPutParentPath $excelParentPath $unix $APP_MAIN
pause