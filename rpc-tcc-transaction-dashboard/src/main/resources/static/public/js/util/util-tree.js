//创建菜单按钮
function initMenus(){
	$.ajax({
		type:"post",
		async:false,
		dataType:'json',
		url:"globle/navi.shtml",
		success:function(data){
			creMenuHtml(data);
		},
		error:function(){
			alert("菜单加载异常")
		}
	});
}
function creMenuHtml(data){
	$.each(data,function(i,bm){
		var menuhtml = "";
		var bigid = bm.id;
		menuhtml += '<div id="acc'+bigid+'"style="overflow: hidden;"><ul id="tree'+bigid+'"></ul></div>';
		//折叠1级菜单
		$("#accordion").accordion('add',{
			title:bm.text,
			content:menuhtml,
			iconCls:"icon-save",
			collapsible:true,
			selected:false
		});
		addTree(bigid,bm.children);
	});
	//收起全部折叠菜单
//	colseAccordion();
}

//创建二级菜单
function addTree(bid,trees){
	console.info(trees);
//	debugger;
	$("#tree"+bid).tree({
//		formatter:function(node){
//			return node.text;
//		},
		data:trees,
	    onClick: function(node){
			add_btn(node.text,node.url);
		}
	});
}
//收起所有菜单
function colseAccordion(){
	var panels = [];
	pannels = $("#accordion").accordion("panels");
	$.each(pannels,function(i,pan){
		$("#accordion").accordion("unselect",pan.panel("options").title);
	})
}


//创建tab
function add_btn(title,url){
	if($('#tabs').tabs('exists', title)){
		var tab =  $('#tabs').tabs('getTab',title);
		$("#tabs").tabs("select",title);
		$("#tabs").tabs('update',{
	        tab:tab,
	        options:{
				style:{padding:'1px'},
				content:'<iframe scrolling="yes" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>',  
				closable:true,
				selected:true
	        } 
		});
	}else{
		$('#tabs').tabs('add', {
			title : title,
			closable : true,
			content : '<iframe scrolling="yes" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>',
			closable : true
		});
	}
}