<%@ include file="/common/taglibs.jsp"%>
<style type="text/css">
    img.btn {
        cursor: pointer;
        vertical-align: middle;
    }
    
    #refreshItems{
    	position: absolute;
    	right: 0;
    	top: 0;
    }
    
    #itemForm dt {
    	width: 120px;
    }
    #itemForm dd {
    	width: 390px;
    }
    /* Thumbnail container */
    .thumbnailContainer {
        height: 40px;
        width: 40px;
        padding: 2px;
        border: 1px solid #9F9F9F;
        float: left;
        margin: 3px;
        cursor: pointer;
    }

	.placeholder .thumbnailContainer {
		float: none;
	}
         
    .thumbnailContent {
        height: 100%;
        width: 100%;
        background-color: #FFF;
        text-align: center;
    }
    
    .thumbnailContent img {
        vertical-align: middle;
    }
    
    .thumbnailContent.loading {
        background: #FFF url(/images/simple/loading.gif) center center no-repeat;
    }
    
    #thumbnailArea {
        width: 200px;
    }
</style>
<script type='text/javascript'>
var MAX_ITEM_ALLOW = 10;
var subPictureIdArr = new Array();
var itemArr = new Array();
var itemCounter = 0, idNumber = 0;

function renderThumbnail(data){
    var album = data.feed.gphoto$name.$t;
    itemArr[album] = new Array(data.feed.entry.length);
    for (var i = 0; i < data.feed.entry.length; i++) {
        addDiv(data.feed.entry[i], i, album);
    }
    
    $('#itemThumbnail').autocomplete(itemArr[album], {
        width: 310,
        minChars: 0,
        max: 1000,
        scrollHeight: 300,
        matchContains: true,
        formatItem: function(data, i, n, value){
            return "<table><tr><td>" + data.picSmall + "</td><td>" + data.description + "-" + data.title + "</td></tr></table>";
        },
        formatMatch: function(row, i, max){
            return row.description + ' ' + row.title;
        },
        formatResult: function(row){
            return row.org;
        },
		onEnter: function(inputVal){
			var $this = $('#itemThumbnail');
			var html = buildThumbnail(inputVal, false);
			$this.parents('dd').find('.placeholder').html(html);
        }
    });
}

function renderThumbnailBig(data){
    var album = data.feed.gphoto$name.$t;
    itemArr[album] = new Array(data.feed.entry.length);
    for (var i = 0; i < data.feed.entry.length; i++) {
        addDiv(data.feed.entry[i], i, album);
    }
    $('#itemThumbnailBig').autocomplete(itemArr[album], {
        autocomplete: true,
        width: 310,
        minChars: 0,
        max: 1000,
        scrollHeight: 300,
        matchContains: true,
        formatItem: function(data, i, n, value){
            return "<table><tr><td>" + data.picSmall + "</td><td>" + data.description + "-" + data.title + "</td></tr></table>";
        },
        formatMatch: function(row, i, max){
            return row.description + ' ' + row.title;
        },
        formatResult: function(row){
            return row.org;
        },
        onEnter: function(inputVal){
			var $this = $('#itemThumbnailBig');
			var html = buildThumbnail(inputVal, false);
			$this.parents('dd').find('.placeholder').html(html);
        }
    });
}

function renderThumbnailPreview(data){
    var album = data.feed.gphoto$name.$t;
    itemArr[album] = new Array(data.feed.entry.length);
    for (var i = 0; i < data.feed.entry.length; i++) {
        addDiv(data.feed.entry[i], i, album);
    }
    $('.itemSubPic').autocomplete(itemArr[album], {
        width: 310,
        minChars: 0,
        max: 1000,
        scrollHeight: 300,
        matchContains: true,
        formatItem: function(data, i, n, value){
            return "<table><tr><td>" + data.picSmall + "</td><td>" + data.description + "-" + data.title + "</td></tr></table>";
        },
        formatMatch: function(row, i, max){
            return row.description + ' ' + row.title;
        },
        formatResult: function(row){
            return row.org;
        },
        onEnter: function(inputVal, $ele){
            if (itemCounter == MAX_ITEM_ALLOW) {
                return;
            }
			$ele.val('');
            var html = buildThumbnail(inputVal);
            $('#thumbnailArea').prepend(html);
        }
    });
}

function buildThumbnail(link, hiddenField){
    var $thumbContainer = $('<div class="thumbnailContainer"><div class="thumbnailContent loading"></div></div>');
    var $img = $('<img />').attr({
        'src': link
    }).load(function(){
		var height = $thumbContainer.find('.thumbnailContent').height();
        var ratio = calculateRatio($img[0].width, $img[0].height, height, height);
        
        $img.attr({
            'width': Math.round(ratio * $img[0].width),
            'height': Math.round(ratio * $img[0].height)
        });
        $thumbContainer.find('.thumbnailContent').removeClass('loading').html($img);
        
        var imgSrc = $thumbContainer.find('img').attr('src');
        
        // build tooltip
        $thumbContainer.bind('mousemove', function(e){
            var $this = $(this);
            
            var $imgDiv = $('<div></div>');
            
            var $img = $('<img />').attr('src', imgSrc);
            $imgDiv.html($img);
            
            manager.showTooltip($this, e, $imgDiv);
        });
        
        $thumbContainer.bind('mouseout', function(e){
            manager.hideTooltip($(this));
        });
        if (hiddenField == undefined || hiddenField) {
			$thumbContainer.click(function(){
				var id = $(this).find('input').data('id');
				subPictureIdArr.push(id);
				
				$thumbContainer.remove();
				manager.hideTooltip($(this));
				itemCounter--;
			});
			
			// create hidden field
			var name = 'subPictures';
			var idNumber = subPictureIdArr.splice(subPictureIdArr.length - 1, 1);
			
			var id = name + '[' + idNumber + ']';
			
			$('<input />').attr({
				'name': id,
				'type': 'hidden',
				'class': 'itemSubPic'
			}).val(imgSrc).data('id', idNumber).appendTo($thumbContainer);
			
			itemCounter++;
		}
    });
    return $thumbContainer;
}

function calculateRatio(targetW, targetH, origW, orgiH){
    return Math.min(Math.min(targetH, orgiH) / Math.max(targetH, orgiH), Math.min(origW, targetW) / Math.max(targetW, origW));       
}

function addDiv(item, id, album){
    itemArr[album][id] = {};
    var title = item.title.$t;// the filename
    var imgId = item.gphoto$id.$t;// the file id
    var targetURL = item.content.src + '?imgmax=0';//useful for downloading image
    var pictureURL = item.content.src + '?imgmax=320'; //288
    var pictureURL2 = item.content.src + '?imgmax=90';
    var commentCount = item.gphoto$commentCount.$t;
    
    itemArr[album][id].title = title; // id
    itemArr[album][id].org = targetURL;
    itemArr[album][id].picSmall = "<img src='" + pictureURL2 + "' class='thumbnail' />";
    itemArr[album][id].linkPicSmall = pictureURL2;
    itemArr[album][id].picMedium = "<img src='" + pictureURL + "' class='thumbnail' />";
    itemArr[album][id].linkPicMedium = pictureURL;
    
    var description = item.media$group.media$description.$t;// Picasa Web photo caption
    if (description == null || description.length == 0) {
        description = "Item " + id;
    }
    itemArr[album][id].description = description;
    //var keywords = item.media$group.media$keywords.$t;// Picasa Web photo caption
    var description2 = "";
    
    try {
        var camera = item.exif$tags.exif$make.$t + " " + item.exif$tags.exif$model.$t;
    } 
    catch (err) {
        var camera = "";
    }
    
    // comment this is line for remove tags data
    //if (keywords.length > 0) 
    // description2 = description2 + "Tags: <a href='http://picasaweb.google.com/lh/searchbrowse?q=" + keywords + "'>" + keywords + "</a>;  ";
    
    // comment this is line for remove comments counter
    description2 = description2 + "Comments: " + commentCount + ";  ";
    
    // comment this is line for remove information about camera
    if (camera.length > 0) 
        description2 = description2 + "Taken with: " + camera + "; ";
    
    // title
    title = "<a href='http://picasaweb.google.com/" + user + "/" + album + "/photo#" + imgId + "'>" + title + "</a>";
    
    if (description.length > 0) 
        title = title + " - " + description;
}

// will be used one then will be removed
function loadJS(href){
    var $script = $('<script>').attr('src', href);
    $('#imageScript').html($script);
}

var user = 'myhoang0603';
var albumData = [{
    album: 'CharaBigThumbnail',
    renderer: 'renderThumbnailBig'
}, {
    album: 'CharaPreview',
    renderer: 'renderThumbnailPreview'
}, {
    album: 'CharaThumbnail',
    renderer: 'renderThumbnail'
}];
var maxres = 1000; // 0 - for all;
var authkey = '';

function loadItemsData(){
    for (var i in albumData) {
        var url = 'http://picasaweb.google.com/data/feed/api/user/' + user + '/album/' + albumData[i].album + '?kind=photo&alt=json-in-script&callback=' + albumData[i].renderer + '&access=public&start-index=1';
        
        if (maxres && maxres != 0) {
            url = url + '&max-results=' + maxres;
        }
        if (authkey && authkey != '') {
            url = url + '&authkey=' + authkey;
        }
        loadJS(url);
    }
}

function initSubPictureIdArray(){
    for (var i = 0; i < MAX_ITEM_ALLOW; i++) {
        subPictureIdArr.push(i);
    }
}

$(function(){
    $('img.itemPreview').live('mousemove', function(e){
        var $this = $(this);
        
        var $imgDiv = $('<div></div>');
        var imgSrc = $this.parents('dd').find('input').val();
        var $img = $('<img />').attr('src', imgSrc);
        $imgDiv.html($img);
        
        manager.showTooltip($this, e, $imgDiv);
    });
    
    $('img.itemPreview').live('mouseout', function(e){
        manager.hideTooltip($(this));
    });
    
    $('#popup_ok').click(function(){
    
    
        $.alerts._hide();
        $('body').css('overflow', 'auto');
        
        manager.loadItems();
    });
    
    $('#refreshItems').click(function(){
        $('#refreshImg').hide();
        $('#refreshLoading').show();
        loadItemsData();
        $('#refreshImg').show();
        $('#refreshLoading').hide();
    });
    loadItemsData();
    
    // client-side validation
    $('#itemForm').validate({
        rules: {
            code: {
                required: true,
                minlength: 2
            },
            description: {
                required: true,
                minlength: 5
            },
            price: {
                digits: true,
                minlength: 2,
                maxlength: 3
            },
            quantity: {
                digits: true,
                maxlength: 3
            },
            thumbnail: {
                url: true,
                required: true
            },
            thumbnailBig: {
                url: true,
                required: true
            }
        },
        messages: {
            code: {
                required: "Item code is required",
                minlength: "Code length must greater than 2 chars"
            },
            description: {
                required: "Please input some descriptions for the item. ",
                minlength: "Description length must greater then 5 chars"
            },
            price: {
                digits: "Number for price please!.",
                minlength: "Not resonable price. Too cheap!! >.<",
                maxlength: "Your price is not reasonable. Too expensive! ).("
            },
            thumbnail: {
                imageValid: "Broken Image!!!"
            },
            thumbnailBig: {
                imageValid: "Broken Image!!!"
            }
        },
        errorPlacement: function(error, element){
            //create error section
            $errorDiv = $('<div class="error"></div>');
            $errorDiv.html(error);
            
            // remove first
            
            element.parents('dd').find('div.error').remove();
            element.parents("dd").append($errorDiv);
        },
        debug: true
    });
    
	$('#itemThumbnailBig').focusout(function(){
		var $this = $(this);
		if ($this.val() == ''){
			$this.parent().find('.thumbnailContainer').remove();	
		}
	});
	
	$('#itemThumbnail').focusout(function(){
		var $this = $(this);
		if ($this.val() == ''){
			$this.parent().find('.thumbnailContainer').remove();	
		}
	});
	
    // init subPicture id array
    initSubPictureIdArray();
});
</script>
<div id="container">
	<div id="refreshItems">
		<img id="refreshImg" alt="Reload item images from Picasa" title="Reload item images from Picasa" src="http://lh5.ggpht.com/_4oj_ltkp9pc/S_VoFkL9MXI/AAAAAAAAAGc/mkWFeTSUVFc/arrow_rotate_anticlockwise.png" />
	    <img id="refreshLoading" src="<c:url value='/images/simple/loading.gif' />" width='24' height="24" style="display: none; vertical-align: middle;"/>
		
	</div>
	<div id="imageScript"></div>
    <form id="itemForm" action="" method="post" class="">
    <fieldset>
	    <dl>
	        <dt>
	            <label for="itemCode">
	                <fmt:message key="item.itemForm.code" />
	            </label>
	        </dt>
	        <dd>
	            <input type="text" name="code" id="itemCode" size="10" maxlength="128" />
	        </dd>
	    </dl>
	    <dl>
	        <dt>
	            <label for="itemDescription">
	                <fmt:message key='item.itemForm.description' />
	            </label>
	        </dt>
	        <dd>
	            <textarea name="description" id="itemDescription" rows="3" cols="30"></textarea>
	        </dd>
	    </dl>
	    <dl>
	        <dt>
	            <label for="itemPrice">
	                <fmt:message key='item.itemForm.price' />
	            </label>
	        </dt>
	        <dd>
	            <input type="text" name="price" id="itemPrice" size="10" maxlength="10" />
	        </dd>
	    </dl>
	    <dl>
	        <dt>
	            <label for="itemQuantity">
	                <fmt:message key='item.itemForm.quantity' />
	            </label>
	        </dt>
	        <dd>
	            <input type="text" name="quantity" id="itemQuantity" size="3" maxlength="4" />
	        </dd>
	    </dl>
	    <dl>
	        <dt>
	            <label for="itemThumbnailBig">
	                <fmt:message key='item.itemForm.thumbnail.big' />
	            </label>
	        </dt>
	        <dd>
	            <input type="text" name="thumbnailBig" autocomplete="off" id="itemThumbnailBig" size="30" maxlength="100" />
	            <div class='placeholder'></div>
	        </dd>
	    </dl>
	    <dl>
	        <dt>
	            <label for="itemThumbnail">
	                <fmt:message key='item.itemForm.thumbnail' />
	            </label>
	        </dt>
	        <dd>
	            <input type="text" name="thumbnail" autocomplete="off" id="itemThumbnail" size="30" maxlength="100" /><img id="addNewPic" title="Add preview pictures" src="<c:url value='/images/simple/Add.png' />" title="Add a new sub picture" height="24" width="24" class="btn"/>
	            <div class='placeholder'></div>
	        </dd>
	    </dl>
	    <dl>
            <dt>
                <label for="itemThumbnailPreview">
                    <fmt:message key='item.itemForm.thumbnail' />
                </label>
            </dt>
            <dd>
                <input type="text" name="thumbnailPreview" autocomplete="off" id="itemThumbnailPreview" size="30" maxlength="100" class="itemSubPic"/>
                <div id="thumbnailArea">
                    <div style="clear:both;">
                    </div>
                </div>
            </dd>
        </dl>
	    <dl>
	        <dt>
	        </dt>
	        <dd>
	        	<input type="button" value="Close me" id="popup_ok" name="closeMe" />
	        	&nbsp;
	            <input type="submit" value="Add A New Item" id="addItemBtn" />
	            &nbsp; 
	            <img id="itemLoading" src="<c:url value='/images/simple/loading.gif' />" width='24' height="24" style="display: none; vertical-align: middle;"/>
	        </dd> 
	    </dl>
    </fieldset>
</form>
</div>
<!-- Handle fail on server -->
<spring:hasBindErrors name="item">  
</spring:hasBindErrors>  
