<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
    <title>Galleria Demo 1</title>
	<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/simple/galleria.css'/>" />
	<script type="text/javascript" src="<c:url value='/js/jquery-1.4.2.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.galleria.js'/>"></script>	
	<style media="screen,projection" type="text/css">
	
	/* BEGIN DEMO STYLE */
	*{margin:0;padding:0}
	body{padding:20px;background:white;text-align:center;background:black;color:#bba;font:80%/140% georgia,serif;}
	h1,h2{font:bold 80% 'helvetica neue',sans-serif;letter-spacing:3px;text-transform:uppercase;}
	a{color:#348;text-decoration:none;outline:none;}
	a:hover{color:#67a;}
	.caption{font-style:italic;color:#887;}
	.demo{position:relative;margin-top:2em;}
	.gallery_demo{width:502px;margin:0 auto;}
	.gallery_demo li{width:68px;height:50px;border:3px double #111;margin: 0 2px;background:#000;}
	.gallery_demo li div{left:240px}
	.gallery_demo li div .caption{font:italic 0.7em/1.4 georgia,serif;}
	
	#main_image{margin:0 auto 60px auto;height:438px;width:500px;background:black;}
	#main_image img{margin-bottom:10px;}
	
	.nav{padding-top:15px;clear:both;font:80% 'helvetica neue',sans-serif;letter-spacing:3px;text-transform:uppercase;}
	
	.info{text-align:left;width:500px;margin:30px auto;border-top:1px dotted #221;padding-top:30px;}
	.info p{margin-top:1.6em;}
	
    </style>
	<script type="text/javascript">
	
	$(document).ready(function(){
		
		$('.gallery_demo_unstyled').addClass('gallery_demo'); // adds new class name to maintain degradability
		
		$('ul.gallery_demo').galleria({
			history   : false, // activates the history object for bookmarking, back-button etc.
			clickNext : true, // helper for making the image clickable
			insert    : '#main_image', // the containing selector for our main image
			onImage   : function(image,caption,thumb) { // let's add some image effects for demonstration purposes
				
				// fade in the image & caption
				image.css('display','none').fadeIn(1000);
				caption.css('display','none').fadeIn(1000);
				
				// fetch the thumbnail container
				var _li = thumb.parents('li');
				
				// fade out inactive thumbnail
				_li.siblings().children('img.selected').fadeTo(500,0.3);
				
				// fade in active thumbnail
				thumb.fadeTo('fast',1).addClass('selected');
				
				// add a title for the clickable image
				image.attr('title','Next image >>');
			},
			onThumb : function(thumb) { // thumbnail effects goes here
				
				// fetch the thumbnail container
				var _li = thumb.parents('li');
				
				// if thumbnail is active, fade all the way.
				var _fadeTo = _li.is('.active') ? '1' : '0.3';
				
				// fade in the thumbnail when finnished loading
				thumb.css({display:'none',opacity:_fadeTo}).fadeIn(1500);
				
				// hover effects
				thumb.hover(
					function() { thumb.fadeTo('fast',1); },
					function() { _li.not('.active').children('img').fadeTo('fast',0.3); } // don't fade out if the parent is active
				)
			}

		});
	});
	
	</script>
	
</head>
<body>
	<div class="demo">
	 <c:set var="itemPictures" value="${pictures}"></c:set>
		<div id="main_image"></div>
		  <ul class="gallery_demo_unstyled">
		    <li><img class="active" src='<c:out value="${itemPictures[0] }" />' alt="" title=""></li>
				<c:forEach begin="1" var="item" items="${itemPictures }">
				  <li><img src='<c:out value="${item }" />' alt="" title=""></li>
		   </c:forEach>
		</ul>
		<p class="nav"><a href="#" onclick="$.galleria.prev(); return false;">&laquo; previous</a> | <a href="#" onclick="$.galleria.next(); return false;">next &raquo;</a></p>
	</div>
</body>
</html>
