local key_map = {
<#assign count = 1>
<#list supplier.getHeader() as key,lc>
	--[[ ${supplier.getDescIndex(key)} ]]--
	-- ${supplier.getTypeIndex(key)}
	${lc} = ${count}<#sep>,
	<#assign count = count + 1>
</#list>

}

local map = {
<#list data as mapValue>
	[${mapValue[supplier.getIdIndex()]}] =<#rt>
	{<#list supplier.getStrs() as key><#rt>
	<#lt>${mapValue[key]}<#sep>,<#rt>
	</#list>}<#sep>,
</#list>
}

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