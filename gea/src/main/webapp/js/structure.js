// get the template.
var $itemTemplate = $('<div class="itemContainer fleft"></div>').load('/common/item.html');
// prefix name for captify
var captifyPrefix = 'image-caption'; // then + id
// Item mapping hold the mapping between the client item and the server Item.
var itemMapping = {
    'new': undefined,
    'hot': undefined,
    'sale': undefined,
    'id': 'id',
    'codeName': 'code',
    'price': 'price',
    'description': 'description',
    'thumbnail-big': {
        target: 'thumbnailBig',
        process: function($ele, url, adminMode, id){
            var $img = $('<img />').attr({
                'src': url
            });
            $ele.attr('id', 'img-tooltip-' + id);
            $ele.html($img);
        }
    },
    'thumbnail': {
        target: 'thumbnail',
        process: function($ele, url, adminMode){
            var $img = $ele.find('img.item');
            $img.attr({
                'src': url,
                'rel': captifyPrefix,
                'class': 'captify item'
            });
            if (adminMode) {
                $captify = $('<div style="width: 130px;" id="' + captifyPrefix + '"></div>').append($('<img class="itemDelete" src="/images/simple/delete.png" />&nbsp;<img src="images/simple/edit.png" class="itemEdit"/>'));
                $ele.find('img.item').after($captify);
            }
            
            
        }
    },
    'subPictures': {
        target: 'subPictures',
        process: function($ele, items, adminMode, id, data){
            //var id = $ele.siblings('div.id').html();
            for (var i = 0; i < items.length; i++) {
                var $a = $('<a />').attr({
                    'class': 'itemSubPic subPicGroup-' + id,
                    'rel': 'subPicGroup-' + id,
                    'href': items[i],
                    'title': data.description
                });
                
                $ele.append($a);
            }
        }
    },
    'description': 'description'
};

function findPosX(obj){
    var curleft = 0;
    if (obj.offsetParent) 
        while (1) {
            curleft += obj.offsetLeft;
            if (!obj.offsetParent) 
                break;
            obj = obj.offsetParent;
        }
    else 
        if (obj.x) 
            curleft += obj.x;
    return curleft;
}

function findPosY(obj){
    var curtop = 0;
    if (obj.offsetParent) 
        while (1) {
            curtop += obj.offsetTop;
            if (!obj.offsetParent) 
                break;
            obj = obj.offsetParent;
        }
    else 
        if (obj.y) 
            curtop += obj.y;
    return curtop;
}
