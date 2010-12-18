/**
 *Calculate image ratio.
 */
function calculateRatio(targetW, targetH, origW, orgiH){
    return Math.min(Math.min(targetH, orgiH) / Math.max(targetH, orgiH), Math.min(origW, targetW) / Math.max(targetW, origW));
}
/**
 * Render image thumbnail from data received from Picasa.
 */
function renderThumbnail(data){
    var album = data.feed.gphoto$name.$t; // the album name.
    var itemArr = new Array();
    itemArr[album] = new Array(data.feed.entry.length);
    for (var i = 0; i < data.feed.entry.length; i++) { // loop through the list and build up the image with all information neccessary.
        populateImageInformation(itemArr, data.feed.entry[i], i, album);
    }    

    $('#item_picture_link').autocomplete(
        itemArr[album], { // id of the target textbox.
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
                var $this = $('#item_picture_link');
                var html = buildPreviewThumbnail(inputVal, false);
                $this.parents('div.row').find('.placeholder').html(html);
            }
        });
}

/**
 * Build preview thumbnail.
 * Structure:
 * - div.thumbnailContainer
 *  - div.thumbnailContent,.loading
 *   - img
 */
function buildPreviewThumbnail(link, hiddenField){
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
        $thumbContainer.bind('mousemove', function(e){ // bind mouseover for tooltip.
            var $this = $(this);
            var $imgDiv = $('<div></div>'); // build tooltip image container.
            var $img = $('<img />').attr('src', imgSrc); // build image tag.
            $imgDiv.html($img); // append to the container.
            manager.showTooltip($this, e, $imgDiv); // show tooltip using Manager->tooltip.
        });

        $thumbContainer.bind('mouseout', function(e){ // mouseout event.
            manager.hideTooltip($(this)); // hide tooltip.
        });
    });
    return $thumbContainer;
}
var albumData = [{    // albumnData contains key value pair of album name and method processing name.
    album: 'CharaThumbnail',    /* album name */
    renderer: 'renderThumbnail' /* rendering method */
}];
// some preconfiguration params.
var maxres = 1000; // 0 - for all;
var authkey = '';

function updatePicasaAccount(picasaID, albumID) {
    var picasaAccount = $(picasaID).val() != '' ? $(picasaID).val() : 'myhoang0603';
    var picasaAlbum = $(albumID).val() != '' ? $(albumID).val() : 'CharaThumbnail';

    loadAlbum(picasaAccount, picasaAlbum, 'renderThumbnail');
}

function loadAlbum(user, albumName, rendererName){
    var url = 'http://picasaweb.google.com/data/feed/api/user/' + user + '/album/' + albumName + '?kind=photo&alt=json-in-script&callback=' + rendererName + '&access=public&start-index=1';

    if (maxres && maxres != 0) {
        url = url + '&max-results=' + maxres;
    }
    if (authkey && authkey != '') {
        url = url + '&authkey=' + authkey;
    }

    var $script = $('<script>').attr('src', url);
    $('#imageScript').html($script); // pass the script to this div.    }
}
/**
 * Load items data from Picasa.
 */
function loadItemsData(){
    for (var i in albumData) {
        loadAlbum('myhoang0603', albumData[i].album, albumData[i].renderer);
    }
}

/**
 * Populate image information.
 */
function populateImageInformation(itemArr, item, id, album){
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
    //title = "<a href='http://picasaweb.google.com/" + user + "/" + album + "/photo#" + imgId + "'>" + title + "</a>";

    //if (description.length > 0)
    //    title = title + " - " + description;
}

