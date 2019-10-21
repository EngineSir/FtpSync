var layer;
$(function() {
	initLayui();
	$("#json-renderer").hide();
	var path = getQueryString("path");
	initFtpFolder(path);
	clickAddClass("fileList");
	//返回上一级目录
	goBack();
	
})
//初始化layui参数
function initLayui(){
	layui.use(['form', 'layedit', 'laydate'], function(){
		   layer = layui.layer

	})
}
//初始化ftp文件夹目录
function initFtpFolder(path) {
	var index;
	var id = getQueryString("id");
	var currUrl = "/ftpSync/getRemoteFtpFileDir" // 当前请求的链接
	var otherColumsList = [
			{
				field : 'name',
				title : '名称',
				cellStyle : function(value, row, index) {
					return {
						css : {
							"cursor" : "pointer"
						}
					}
				}
			},
			{
				field : 'updateTime',
				title : '修改日期',
			},
			{
				field : 'type',
				title : '类型',
				formatter : function(value, row) {
					var str = "";
					if (value == 1) {
						str = "文件夹";
					} else {
						str = row.name.substring(row.name.indexOf(".") + 1)
								+ "文件";
					}
					return str;
				}
			},
			{
				field : 'size',
				title : '大小',
			},
			{
				field : 'sync',
				formatter : function(value, row) {
					var name = row.name;
					var type = row.type;
					var str = `<a class="layui-btn layui-btn-xs syncStart`+(row.syncFlag==1?" layui-btn-disabled":"")+`"`+(row.syncFlag==1?' style="pointer-events: none"':"")+` lay-event="edit" onclick="syncStart('${id}','${path}','${name}','${type}')">同步</a>`;
					if (row.type == 1) {
						str += `<a class="layui-btn layui-btn-danger layui-btn-xs syncStop`+(row.syncFlag==0?" layui-btn-disabled":"")+`"`+(row.syncFlag==0?' style="pointer-events: none"':"")+` lay-event="del" onclick="syncStop('${id}','${path}','${name}')">停止</a>`;
					}
					return str;
				}
			}, ]
	$("#fileList").bootstrapTable('destroy').bootstrapTable({
		url : currUrl, // 请求后台的URL（*）
		method : 'get', // 请求方式（*）
		toolbar : '#toolbar', // 工具按钮用哪个容器
		striped : false, // 是否显示行间隔色
		cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
		pagination : true, // 是否显示分页（*）
		sortable : false, // 是否启用排序
		queryParams : function(params) {
			var temp = {
				key : id, // 页面大小
				path : path,
				pageSize: params.limit, //页面大小
				pageNo: (params.offset / params.limit) + 1, //页码
				search:params.search
			};
			console.log(params)
			return temp;
		},
		sortOrder : "asc", // 排序方式
		sidePagination : "server", // 分页方式：client客户端分页，server服务端分页（*）
		pageNumber : 1, // 初始化加载第一页，默认第一页
		pageSize : 10, // 每页的记录行数（*）
		search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
		pageList : [ 10, 15 ],
		strictSearch : true,
		showColumns : false, // 是否显示所有的列
		showRefresh : false, // 是否显示刷新按钮
		minimumCountColumns : 2, // 最少允许的列数
		clickToSelect : true, // 是否启用点击选中行
		// height: 700, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
		uniqueId : "no", // 每一行的唯一标识，一般为主键列
		showToggle : false, // 是否显示详细视图和列表视图的切换按钮
		cardView : false, // 是否显示详细视图
		detailView : false, // 是否显示父子表
		columns : otherColumsList,
		onClickCell : function(field, value, row, $element) {
			// 切换ftp下级目录
			if (row.type == 1 && field == 'name') {
				
				path += row.name + "/";
				//创建返回li
				createGoBackLi(path,row.name);
				
				initFtpFolder(path)
			}
			//如果是文件则返回文件内容
			if(row.type==0  && field == 'name'){
				//获取文件后缀
				var suff=row.name.substring(row.name.lastIndexOf('.')+1);
				if(suff=="json"){
					getFileContent(id,path,row.name);
					path += row.name + "/";
					createGoBackLi(path,row.name);
				}else if(suff=="jpg"){
					//图片预览
					
				}else{
					alert("无法查看该文件内容")
				}
				
				
			}
			
		},
		responseHandler : function(datas) {
			if(layer!=null && layer!=""){
				index = layer.load(0, {shade:  [0.3,'#000']}); //0代表加载的风格，支持0-2
			}
			var res = {};
			res.total = 0;
			res.rows = 0;
			if(datas.response!=null){
				res.total = datas.response.total;
				res.rows = datas.response.data;
			}
			return res;
		},
		//加载成功关闭加载层
		onLoadSuccess:function(){
			if(layer!=null && layer!=""){
				layer.close(index);//关闭指定加载层
			}
		}
	})
}

/**
 * 获取文件内容
 * @param id
 * @param path
 * @param name
 * @returns
 */
function getFileContent(id,path,name){
	var index;
	$.ajax({
		url:"/ftpSync/getFileContent",
		type:"get",
		dataType:"json",
		data:{"id":id,"path":path,"name":name},
		beforeSend:function(){
			index = layer.load(0, {shade:  [0.3,'#000']}); //0代表加载的风格，支持0-2
		},
		success:function(result){
			if(result.value){
				$(".bootstrap-table").hide();
				$("#json-renderer").show();
				var options = {
					      collapsed: $('#collapsed').is(':checked'),
					      rootCollapsable: $('#root-collapsable').is(':checked'),
					      withQuotes: $('#with-quotes').is(':checked'),
					      withLinks: $('#with-links').is(':checked')
					    };
				$('#json-renderer').jsonViewer(result.response, options);
			}
			
		},
		complete:function(){
			 layer.close(index);//关闭指定加载层
		},
		error:function(){
			alert("获取文件内容失败")
		}
	})
}


//返回上一级目录
function goBack(){
	$(".go-back-btn").click(function(){
		$("#json-renderer").hide();
		$("#fileList").show();
		var $lis=$(".go-back").find("li");
		var path=$($lis[$lis.length-1]).data("path");
		path=path.substring(0,path.length-1);
		path=path.substring(0,path.lastIndexOf("/")+1)
		//删除最后一级目录显示
		$lis.eq(-1).remove();
		initFtpFolder(path);
		if(path=="/"){
			$(".go-back").hide();
		}
		
	})
}

//创建返回li
function createGoBackLi(path,name){
	var index=path.split('/').length-1;
	var li="";
	if(path!="/"){
		$(".go-back").show();
		if(index==2){
			li="<li>"+name+"</li>";
		}else{
			li='<li><span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>&nbsp;&nbsp;&nbsp;'+name+'</li>';
		}
		var $li=$(li);
		$li.data("path",path);
		$(".go-back").append($li);
	}
}


// 获取url参数name值
function getQueryString(name) {
	var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
	var r = window.location.search.substr(1).match(reg);
	if (r != null) {
		return unescape(r[2]);
	}
	return null;
}

// 同步启动
function syncStart(id, path, name, type) {
	$.ajax({
		url:"/ftpSync/syncFiles",
		type:"post",
		dataType:"json",
		data:{"id":id,"path":path,"name":name,"type":type},
		success:function(result){
			console.log(result)
		},
		error:function(){
			alert("同步出错");
		}
	})
}



//同步停止
function syncStop(id, path, name){
	path=path+name;
	$.ajax({
		url:"/ftpSync/syncFoldeStop",
		type:"post",
		dataType:"json",
		data:{"id":id,"path":path},
		success:function(result){
			console.log(result)
		},
		error:function(){
			alert("同步出错");
		}
	})
}