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

    $('#item_id').autocomplete(itemArr[album], {
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
            var $this = $('#item_id');
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


// will be used one then will be removed
function loadJS(href){
    var $script = $('<script>').attr('src', href);
    console.debug($script);
    console.debug($('#imageScript'));
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