var form="";
$(function() {
	var layer="";
	layui.use(['form','layer'], function() {
		form = layui.form;
		layer = layui.layer;
	});
	
	$("#login").click(function(){
		var username=$("#count").val().trim();
		var pass=$("#password").val().trim();
		if(username==""||pass==""){
			layer.msg("用户名或密码不能为空");
		}else{
			$.ajax({
				url:"ftpLink/login",
				type:"post",
				dataType:"json",
				data:{"username":username,"pass":pass},
				success:function(result){
					if(result.value){
						location.href="index";
					}else{
						layer.msg(result.info);
					}
				},
				error:function(){
					alert("登陆失败");
				}
			});
		}
		return false;
	});

});
