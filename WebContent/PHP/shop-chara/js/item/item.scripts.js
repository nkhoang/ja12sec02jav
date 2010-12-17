var itemCounter = 0, idNumber = 0;

function calculateRatio(targetW, targetH, origW, orgiH){
    return Math.min(Math.min(targetH, orgiH) / Math.max(targetH, orgiH), Math.min(origW, targetW) / Math.max(targetW, origW));
}

function renderThumbnail(data){
    var album = data.feed.gphoto$name.$t; // get the albumn name.
    var itemArr = new Array();
    itemArr[album] = new Array(data.feed.entry.length);
    for (var i = 0; i < data.feed.entry.length; i++) {
        updateAlbumnInfo(itemArr, data.feed.entry[i], i, album);
    }    

    $('#Item_item_id').autocomplete(itemArr[album], { // id of the target textbox.
        width: 310,
        minChars: 0,
        max: 1000,
        scrollHeight: 300,
        matchContains: true,
        formatItem: function(data, i, n, value){ // how item to be displayed.
            return "<table><tr><td>" + data.picSmall + "</td><td>" + data.description + "-" + data.title + "</td></tr></table>";
        },
        formatMatch: function(row, i, max){ // match when typing.
            return row.description + ' ' + row.title;
        },
        formatResult: function(row){ // returned result when hit enter.
            return row.org;
        },
        onEnter: function(inputVal){
            var $this = $('#Item_item_id');
            var html = buildThumbnail(inputVal, false);
            $this.parents('div.row').find('.placeholder').html(html);
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


var user = 'myhoang0603'; /* username used to look up */

var albumData = [{    // albumnData contains key value pair of album name and method processing name.
    album: 'CharaThumbnail',    /* album name */
    renderer: 'renderThumbnail' /* rendering method */
}];
// some preconfiguration params.
var maxres = 1000; // 0 - for all;
var authkey = '';
/**
 * Load items data from Picasa.
 */
function loadItemsData(){
    for (var i in albumData) {
        var url = 'http://picasaweb.google.com/data/feed/api/user/' + user + '/album/' + albumData[i].album + '?kind=photo&alt=json-in-script&callback=' + albumData[i].renderer + '&access=public&start-index=1';

        if (maxres && maxres != 0) {
            url = url + '&max-results=' + maxres;
        }
        if (authkey && authkey != '') {
            url = url + '&authkey=' + authkey;
        }

        var $script = $('<script>').attr('src', url);
        $('#imageScript').html($script); // pass the script to this div.
    }
}

function updateAlbumnInfo(itemArr, item, id, album){
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

