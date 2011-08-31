<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Fashion Show</title>
<script type="text/javascript"
	src="<c:url value='/js/jquery.js'/>"></script>
<script type="text/javascript">
	$(function(){
	$.ajax({
		url: "<c:url value='/item/viewAll.html' />",
		dataType: "json",
		type: "POST",
		success: function(data){
			var items = data.items;
			// create image
			for (j = 0 ; j < items.length; j++){
				var img = $("<img src='" + items[j].thumbnail + "' />");
				$('#content').append(img);
			}
			}
		});		
		});
</script>
</head>
<body>
<div id="content">
</div>
</body>
</html>