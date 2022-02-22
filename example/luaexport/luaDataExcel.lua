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
	[1100004] =	{1100004,	10,	{{1080001}},	{100}},
	[1100005] =	{1100005,	10,	{{1120301}},	{100}},
	[1100006] =	{1100006,	10,	{{1120101}},	{100}},
	[1100007] =	{1100007,	10,	{{1120202}},	{100}},
	[1100008] =	{1100008,	10,	{{1120201}},	{100}},
	[1100009] =	{1100009,	10,	{{1120401}},	{100}},
	[1100010] =	{1100010,	10,	{{1120101}},	{100}},
	[1100011] =	{1100011,	10,	{{1120202}},	{100}},
	[1100012] =	{1100012,	10,	{{1100014},{1100015,1100016}},	{60,40}},
	[1100013] =	{1100013,	10,	{{1100001,1100002},{1100003}},	{100,100}},
	[1100014] =	{1100014,	30,	{{1120101},{1120201},{1120301},{1120401}},	{100,100,100,100}},
	[1100015] =	{1100015,	30,	{{1120101},{1120201},{1120301},{1120401}},	{100,100,100,100}},
	[1100016] =	{1100016,	20,	{{1120202}},	{100}},
	[1100017] =	{1100017,	10,	{{1100018},{1100019,1100016}},	{60,40}},
	[1100018] =	{1100018,	20,	{{1100014,1100014}},	{100}},
	[1100019] =	{1100019,	20,	{{1100014,1100015}},	{100}},
	[1100020] =	{1100020,	10,	{{1080001}},	{100}},
	[1100021] =	{1100021,	10,	{{1100022,1100023,1100024,1100025,1100064}},	{100}},
	[1100022] =	{1100022,	20,	{{1120301}},	{100}},
	[1100023] =	{1100023,	20,	{{1120101}},	{100}},
	[1100024] =	{1100024,	20,	{{1120401}},	{100}},
	[1100025] =	{1100025,	20,	{{1120202}},	{100}},
	[1100026] =	{1100026,	10,	{{1080001}},	{100}},
	[1100027] =	{1100027,	10,	{{1100028,1100029,1100030,1100032},{1100028,1100029,1100031}},	{50,50}},
	[1100028] =	{1100028,	20,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100029] =	{1100029,	20,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100030] =	{1100030,	20,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100031] =	{1100031,	15,	{{1100033,1100032,1100034},{1100033,1100032,1100035}},	{90,10}},
	[1100032] =	{1100032,	20,	{{1120202},{1120303},{1120403}},	{100,100,100}},
	[1100033] =	{1100033,	30,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100034] =	{1100034,	30,	{{1120102},{1120203},{1120404}},	{100,100,100}},
	[1100035] =	{1100035,	30,	{{1120103},{1120304}},	{100,100}},
	[1100036] =	{1100036,	10,	{{1100037,1100038}},	{100}},
	[1100037] =	{1100037,	20,	{{1120101}},	{100}},
	[1100038] =	{1100038,	20,	{{1120201}},	{100}},
	[1100039] =	{1100039,	10,	{{1120202}},	{100}},
	[1100040] =	{1100040,	10,	{{1100041,1100042,1100043,1100044}},	{100}},
	[1100041] =	{1100041,	20,	{{1120101}},	{100}},
	[1100042] =	{1100042,	20,	{{1120201}},	{100}},
	[1100043] =	{1100043,	20,	{{1120302}},	{100}},
	[1100044] =	{1100044,	20,	{{1120403}},	{100}},
	[1100045] =	{1100045,	10,	{{1100046,1100047,1100048,1100049,1100050}},	{100}},
	[1100046] =	{1100046,	20,	{{1120301}},	{100}},
	[1100047] =	{1100047,	20,	{{1120101}},	{100}},
	[1100048] =	{1100048,	20,	{{1120201}},	{100}},
	[1100049] =	{1100049,	20,	{{1120202}},	{100}},
	[1100050] =	{1100050,	20,	{{1120203}},	{100}},
	[1100051] =	{1100051,	10,	{{1080001}},	{100}},
	[1100052] =	{1100052,	10,	{{1100053},{1100054,1100055}},	{60,40}},
	[1100053] =	{1100053,	20,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100054] =	{1100054,	20,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100055] =	{1100055,	20,	{{1120202},{1120303},{1120403}},	{100,100,100}},
	[1100056] =	{1100056,	10,	{{1100057},{1100058}},	{60,40}},
	[1100057] =	{1100057,	11,	{{1100053,1100053}},	{100}},
	[1100058] =	{1100058,	11,	{{1100053,1100054}},	{100}},
	[1100059] =	{1100059,	20,	{{1080001}},	{100}},
	[1100060] =	{1100060,	20,	{{1080001}},	{100}},
	[1100061] =	{1100061,	20,	{{1080001}},	{100}},
	[1100062] =	{1100062,	20,	{{1080001}},	{100}},
	[1100063] =	{1100063,	20,	{{1080001}},	{100}},
	[1100064] =	{1100064,	20,	{{1120102}},	{100}},
	[1100065] =	{1100065,	10,	{{1120302}},	{100}},
	[1100066] =	{1100066,	10,	{{1120101},{1120201},{1120301},{1120401}},	{100,100,100,100}},
	[1100067] =	{1100067,	10,	{{1120403}},	{100}},
	[1100068] =	{1100068,	10,	{{1100069,1100070,1100071,1100072,1100073}},	{100}},
	[1100069] =	{1100069,	20,	{{1120302}},	{100}},
	[1100070] =	{1100070,	20,	{{1120101}},	{100}},
	[1100071] =	{1100071,	20,	{{1120401}},	{100}},
	[1100072] =	{1100072,	20,	{{1120403}},	{100}},
	[1100073] =	{1100073,	20,	{{1120304}},	{100}},
	[1100074] =	{1100074,	10,	{{1100075,1100076},{1100077,1100078}},	{20,80}},
	[1100075] =	{1100075,	20,	{{1120202},{1120303},{1120403}},	{100,100,100}},
	[1100076] =	{1100076,	20,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100077] =	{1100077,	20,	{{1120202},{1120303},{1120403}},	{100,100,100}},
	[1100078] =	{1100078,	20,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100079] =	{1100079,	9,	{{1100074,1100074}},	{100}},
	[1100080] =	{1100080,	10,	{{1100081,1100082},{1100083,1100084}},	{50,50}},
	[1100081] =	{1100081,	20,	{{1120202},{1120303},{1120403}},	{100,100,100}},
	[1100082] =	{1100082,	20,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100083] =	{1100083,	20,	{{1120202},{1120303},{1120403}},	{100,100,100}},
	[1100084] =	{1100084,	20,	{{1120101},{1120201},{1120301},{1120401},{1120302},{1120402}},	{100,100,100,100,100,100}},
	[1100085] =	{1100085,	9,	{{1100081,1100082,1100083,1100084}},	{100}}}

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