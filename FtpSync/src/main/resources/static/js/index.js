//JavaScript代码区域
var layer;
$(function() {
	clickAddClass("task-list");
	// 初始化layer
	initLayer();
	// 初始化ftp连接
	initFtpLink();
	// 初始化左侧ftp连接
	initFtpNickList();
	// 退出登录
	logout();
	
})
function initLayer(){
	layui.use(
			['element','layer'],function() {
				var element = layui.element;
				layer = layui.layer;
				// 触发事件
				var active = {
					tabAdd : function() {
						var id = $(this).attr("lay-id");
								// 新增一个Tab项
								element
										.tabAdd(
												'demo',
												{
													title : $(this).text()
															.trim(),// 用于演示
													content : '<iframe id="file-details'
															+ id
															+ '" allowFullscreen="true" scrolling="no" data-frameid="'
															+ id
															+ '" frameborder="0" src="../ftpFileLink?id='
															+ id
															+ '&path=/" style="z-index:2;width:100%;height:100%;"></iframe>',
													id : id
												// 实际使用一般是规定好的id
												}), element.tabChange(
										'demo', id);
						computedIframeH($(this).attr("lay-id"));
					}
				}
				// 点击左侧ftp列表切换到该选项卡
				$('#ftp-nick-list').on('click','.site-demo-active',function() {
							var id = $(this).attr("lay-id");
							if ($(".layui-tab-title li[lay-id='" + id
									+ "']").length > 0) {
								element.tabChange('demo', id);
							} else {
								var othis = $(this), type = othis
										.data('type');
								active[type] ? active[type].call(this,
										othis) : '';
							}
						});
				// Hash地址的定位
				// var layid = location.hash.replace(/^#test=/, '');

				element.on('tab(demo)', function(elem) {
					var layId=$(this).attr('lay-id');
					if(layId=="task-list"){
						//初始化同步任务
						initSyncTask();
					}
					// $("#file-details").load("../ftpFileLink.html")
					// $("#file-details").show();
					// location.hash = 'demo=' + $(this).attr('lay-id');

				});
			});
}
//退出登录
function logout(){
	$("#logout").click(function(){
		$.ajax({
			url:"/ftpLink/logout",
			success:function(){
				
			},
			error:function(){
				alert("退出登录失败")
			}
		})
	});
}
//添加链接
$("#addLink").click(function() {
	$("#myModalLabel").text("新增链接");
	$("#sure-connect").addClass("disabled");
	$("#sure-connect").css("pointer-events","none");
	$("#form-table").clearForm();

})
//更新链接
$("#updateLink").click(function() {
	var data = $("#ftp-link").bootstrapTable('getSelections');
	if (data.length == 0) {
		alert("请选择")
		$('#myModal').modal("hide")
	} else {
		$("#sure-connect").addClass("disabled");
		$("#sure-connect").css("pointer-events","none");
		$("#task-link").val("");
		$("#myModalLabel").text("更新链接");
		fillForm(data[0]);
		$('#myModal').modal("show")
	}

})
// 配置任务
$("#new-task").click(function(){
	var data = $("#ftp-link").bootstrapTable('getSelections');
	if (data.length == 0) {
		alert("请选择")
		$('#task-modal').modal("hide")
	} else {
		$("#remotePath").val("")
		$("#nativePath").val("")
		$("#taskName").val("")
		$("#task-title").text("配置同步任务")
		$('#task-modal').modal("show")
	}
})

//保存配置任务
$("#task-sure").click(function(){
	
	var remotePath=$("#remotePath").val().trim();
	var nativePath=$("#nativePath").val().trim();
	var taskName=$("#taskName").val().trim();
	if(remotePath==null || remotePath=="" || nativePath==null || nativePath=="" || taskName==null || taskName==""){
		alert("所有字段必填")
	}else{
		//新增
		if($("#task-title").text()=="配置同步任务"){
			var data = $("#ftp-link").bootstrapTable('getSelections');
			var ftpId=data[0].id;
			$.ajax({
				url:"/syncTask/addTaskPath",
				type:"post",
				dataType:"json",
				data:{"ftpId":ftpId,"remotePath":remotePath,"nativePath":nativePath,"taskName":taskName},
				success:function(result){
				},
				error:function(){
					alert("配置路径失败")
				}
			})
		}
		//修改
		if($("#task-title").text()=="更新同步任务"){
			var data = $("#task-list").bootstrapTable('getSelections');
			var id=data[0].id;
			var ftpId=data[0].ftp_id;
			$.ajax({
				url:"/syncTask/updateTaskPath",
				type:"post",
				dataType:"json",
				data:{"id":id,"remotePath":remotePath,"nativePath":nativePath,"taskName":taskName,"ftpId":ftpId},
				success:function(result){
					initSyncTask();
				},
				error:function(){
					alert("配置路径失败")
				}
			})
		}
		
	}
})
//删除链接
$("#delLink").click(function() {
	var data = $("#ftp-link").bootstrapTable('getSelections');
	if (data.length == 0) {
		alert("请选择")
	} else {
		layer.msg('是否删除？', {
	        time: 20000, // 20s后自动关闭
	        btn: ['确认', '取消'], yes:function(index, layero){
	        	
	      $.ajax({
			url:"/ftpLink/delFtpLink",
			type:"delete",
			dataType:"json",
			data:{"id":data[0].id},
			success:function(result){
				if(result.value){
					$("#layui-layer"+index).hide()
					clearLi($("#ftp-nick-list"));
					initFtpNickList();
					initFtpLink();
				}else{
					alert(result.info)
				}
			},
			error:function(){
				alert("删除失败")
			}
		})
		  }
	      });
	}
})
// 获取ftp链接列表
function initFtpLink(search) {
	var currUrl = "/ftpLink/getFtpLink" // 当前请求的链接
	var otherColumsList = [ {
		radio : true,
		width : 25,
		align : 'center',
		valign : 'middle'
	}, {
		field : 'task',
		title : '名称',
	}, {
		field : 'remote_ip',
		title : '远程FTP',
	}, {
		field : 'native_ip',
		title : '本地FTP',
	}, {
		field : 'time',
		title : '创建时间',
	}, {
		field : 'creater',
		title : '创建人',
		width : '300',
	}, ]
	$("#ftp-link").bootstrapTable('destroy').bootstrapTable({
		url : currUrl, // 请求后台的URL（*）
		method : 'get', // 请求方式（*）
		toolbar : '#toolbar', // 工具按钮用哪个容器
		striped : false, // 是否显示行间隔色
		cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
		pagination : true, // 是否显示分页（*）
		sortable : false, // 是否启用排序
		 queryParams : function (params) {
		 var temp = {
				 pageSize: params.limit, // 页面大小
				 pageNo: (params.offset / params.limit) + 1, // 页码
				 search:search
		 };
		 return temp;
		 },
		sortOrder : "asc", // 排序方式
		sidePagination : "server", // 分页方式：client客户端分页，server服务端分页（*）
		pageNumber : 1, // 初始化加载第一页，默认第一页
		pageSize : 10, // 每页的记录行数（*）
		search : false, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
		pageList : [ 10, 20 ],
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
		responseHandler : function(datas) {
			var res = {};
			res.total = datas.response.total;
			res.rows = datas.response.data;
			return res;
		}
	})
}

function initFtpNickList() {
	$.ajax({
		url : "/ftpLink/getFtpLink",
		success : function(result) {
			var data = result.response;
			for ( var d in data) {
				createFtpList(data[d]);
			}
		},
		error : function() {
			alert("失败")
		}
	})
}

function createFtpList(data) {
	var str = '<dd>';
	str += '<a href="javascript:;" data-type="tabAdd"';
	str += 'class="site-demo-active" lay-id="' + data.id + '">' + data.task
			+ '</a>';
	str += '</dd>';
	var $str = $(str);
	$("#ftp-nick-list").append($str);
}
// 公共获取表单json对象方法
$.fn.serializeJson = function() {
	var serializeObj = {};
	$(this.serializeArray()).each(function() {
		serializeObj[this.name] = this.value;
	});
	return serializeObj;
};
// 连接测试
$(".test-ftp").click(function() {
	// 0 :测试连接不保存, 1:保存
	var type=$(this)[0].id=="test-connect"?"0":"1";
	var map=$("#form-table").serializeJson();
	map["type"]=type;
	$.ajax({
		url:"/ftpLink/save",
		type:"post",
		dataType:"json",
		data:map,
		success:function(data){
			if(data.value){
				if(type==0){
					$("#sure-connect").removeClass("disabled");
					$("#sure-connect").css("pointer-events","auto");
				}else{
					clearLi($("#ftp-nick-list"));
					initFtpNickList();
					initFtpLink();
				}
				
			}
			$("#task-link").val(data.info);
		}
	})
});


// 公共填充表单方法
function fillForm(data) {
	// console.log(data)
	for ( var k in data) {
		if (k != 0 && k!="remotePassword" && k!="nativePassword") {
			$("#" + k).val(data[k]);
		}
	}
}

// 清空表单方法
$.fn.clearForm = function() {
	$(this.serializeArray()).each(function() {
		$("#" + this.name).val("");
	});
	$("#task-link").val("");
}

// 计算iframe高度
function computedIframeH(id) {
	var headerH = $('.layui-tab-content', window.parent.document)[0].clientHeight
	$('#file-details' + id)[0].style.height = document.documentElement.clientHeight
			- headerH + 'px'
}
// 清空li元素
function clearLi($el){
	$el.empty();
}

//初始化同步任务
function initSyncTask(search){
	var currUrl = "/syncTask/getSyncTask" // 当前请求的链接
		var otherColumsList = [ {
			radio : true,
			width : 25,
			align : 'center',
			valign : 'middle'
		}, {
			field : 'task_name',
			title : '名称',
		}, {
			field : 'remote_path',
			title : '远程路径',
		}, {
			field : 'native_path',
			title : '本地路径',
		}, {
			field : 'time',
			title : '创建时间',
		},
		{
			field : 'sync',
			formatter : function(value, row) {
				var id=row.id
				var ftpId=row.ftp_id;
				var nativePath = row.native_path.replace(/\\/g,"\/");
				var remotePath = row.remote_path.replace(/\\/g,"\/");
				var type=1
				var str = `<a class="layui-btn layui-btn-xs syncStart`+(row.flag==1?" layui-btn-disabled":"")+`"`+(row.flag==1?' style="pointer-events: none"':"")+` lay-event="edit" onclick="syncStart('${id}','${nativePath}','${remotePath}','${type}',${ftpId})">同步</a>`;
				//if (row.type == 1) {
					str += `<a class="layui-btn layui-btn-danger layui-btn-xs syncStop`+(row.flag==0?" layui-btn-disabled":"")+`"`+(row.flag==0?' style="pointer-events: none"':"")+` lay-event="del" onclick="syncStop('${id}','${nativePath}','${remotePath}',${ftpId})">停止</a>`;
				//}
				return str;
			}
		}, ]
		$("#task-list").bootstrapTable('destroy').bootstrapTable({
			url : currUrl, // 请求后台的URL（*）
			method : 'get', // 请求方式（*）
			toolbar : '#toolbar', // 工具按钮用哪个容器
			striped : false, // 是否显示行间隔色
			cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
			pagination : true, // 是否显示分页（*）
			sortable : false, // 是否启用排序
			 queryParams : function (params) {
			 var temp = {
					 pageSize: params.limit, // 页面大小
					 pageNo: (params.offset / params.limit) + 1, // 页码
					 search:search
			 };
			 return temp;
			 },
			sortOrder : "asc", // 排序方式
			sidePagination : "server", // 分页方式：client客户端分页，server服务端分页（*）
			pageNumber : 1, // 初始化加载第一页，默认第一页
			pageSize : 10, // 每页的记录行数（*）
			search : false, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
			pageList : [ 10, 20 ],
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
			responseHandler : function(datas) {
				var res = {};
				res.total = datas.response.total;
				res.rows = datas.response.data;
				return res;
			}
		})
}
/**
 * 配置同步任务的同步启动
 * @param id
 * @param nativePath
 * @param remotePath
 * @param type
 * @returns
 */
function syncStart(id,nativePath,remotePath,type,ftpId){
	
	$.ajax({
		url:"/ftpSync/syncTask",
		type:"post",
		dataType:"json",
		data:{"id":id,"nativePath":nativePath,"remotePath":remotePath,"type":type,"ftpId":ftpId},
		success:function(result){
		},
		error:function(){
			alert("同步出错");
		}
	})
}

/**
 * 配置任务停止
 * @param id
 * @param nativePath
 * @param remotePath
 * @param type
 * @param ftpId
 * @returns
 */
function syncStop(id,nativePath,remotePath,ftpId){
	
	$.ajax({
		url:"/ftpSync/stopSyncTask",
		type:"post",
		dataType:"json",
		data:{"id":id,"nativePath":nativePath,"remotePath":remotePath,"ftpId":ftpId},
		success:function(result){
		},
		error:function(){
			alert("同步出错");
		}
	})
}

//更新同步任务
$("#updateTask").click(function(){
	var data = $("#task-list").bootstrapTable('getSelections');
	if (data.length == 0) {
		alert("请选择")
		$('#task-modal').modal("hide")
	} else {
		$("#task-title").text("更新同步任务")
		$("#remotePath").val(data[0].remote_path)
		$("#nativePath").val(data[0].native_path)
		$("#taskName").val(data[0].task_name)
		$('#task-modal').modal("show")
	}
})

//删除同步任务
$("#delTask").click(function() {
	var data = $("#task-list").bootstrapTable('getSelections');
	if (data.length == 0) {
		alert("请选择")
	} else {
		layer.msg('是否删除？', {
	        time: 20000, // 20s后自动关闭
	        btn: ['确认', '取消'], yes:function(index, layero){
	      $.ajax({
			url:"/syncTask/delFtpSync",
			type:"delete",
			dataType:"json",
			data:{"id":data[0].id},
			success:function(result){
				if(result.value){
					$("#layui-layer"+index).hide()
					initSyncTask();
				}else{
					alert(result.info)
				}
			},
			error:function(){
				alert("删除失败")
			}
		})
		  }
	      });
	}
})

//ftp连接搜索
function onKeyDown(event){
	 var e = event || window.event || arguments.callee.caller.arguments[0];
	 if(e && e.keyCode==13){ // enter 键
		initFtpLink($("#inputEmail3").val().trim())
  }
}
//同步任务搜索
function onSyncDown(event){
	 var e = event || window.event || arguments.callee.caller.arguments[0];
	 if(e && e.keyCode==13){ // enter 键
		 initSyncTask($("#sync-search").val().trim())
  }
}