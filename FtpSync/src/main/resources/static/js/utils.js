//同步与停止切换样式与点击事件移除
function clickAddClass(id){
		$("#"+id).on("click",".syncStart",function(){
			$(this).addClass("layui-btn-disabled");
			$(this).css("pointer-events","none");
			$($(this).next()[0]).removeClass("layui-btn-disabled");
			$($(this).next()[0]).css("pointer-events","auto");
		})
		
		$("#"+id).on("click",".syncStop",function(){
			$(this).addClass("layui-btn-disabled");
			$(this).css("pointer-events","none");
			$($(this).prev()[0]).removeClass("layui-btn-disabled");
			$($(this).prev()[0]).css("pointer-events","auto");
		})
}
