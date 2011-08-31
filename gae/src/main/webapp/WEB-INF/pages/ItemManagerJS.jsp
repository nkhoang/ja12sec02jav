<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    /**
     * Item manager or Manager of the whole page.
     */
    function ItemManager(selector, callback){
        var instance = this; 
        var tooltip_target_id = null; // hold the target id which is current on show.
        var $tooltip; // hold the tooltip instance (singleton)
        var itemService = new ItemService();
        var isAdmin = false;
        var isItemLoading = false;
        var cp = 1, tp = 0, MAX_ITEM_PER_PAGE = 9;
        var numberMapper = {
            '0': 'http://lh4.ggpht.com/_4oj_ltkp9pc/S_XYBs3Q9FI/AAAAAAAAAHQ/obHcKfDDcIA/n_0.gif',
            '1': 'http://lh4.ggpht.com/_4oj_ltkp9pc/S_XYB-f3clI/AAAAAAAAAHU/5l7SpWKUWL0/n_1.gif',
            '3': 'http://lh5.ggpht.com/_4oj_ltkp9pc/S_XYCO6H9DI/AAAAAAAAAHY/USg8kfQLNDM/n_3.gif',
            '4': 'http://lh5.ggpht.com/_4oj_ltkp9pc/S_XYCpAVyFI/AAAAAAAAAHc/zbN1wgDQfD8/n_4.gif',
            '5': 'http://lh5.ggpht.com/_4oj_ltkp9pc/S_XYC4C0bWI/AAAAAAAAAHg/7Bi48FU7vho/n_5.gif',
            '6': 'http://lh6.ggpht.com/_4oj_ltkp9pc/S_XYI1YN1jI/AAAAAAAAAHk/Vz7Q0EWfejk/n_6.gif',
            '7': 'http://lh5.ggpht.com/_4oj_ltkp9pc/S_XYI_m4pEI/AAAAAAAAAHo/OQT1SwFQeWo/n_7.gif',
            '8': 'http://lh6.ggpht.com/_4oj_ltkp9pc/S_XYNgQO2PI/AAAAAAAAAHs/11YJvvtnN6c/n_8.gif',
            '9': 'http://lh4.ggpht.com/_4oj_ltkp9pc/S_XYVWPNcKI/AAAAAAAAAH0/ybYc1yYJnas/n_9.gif',
            '2': 'http://lh5.ggpht.com/_4oj_ltkp9pc/S_ZIeNTClPI/AAAAAAAAAIA/l0u2KVRJ-xY/n_2.gif'
        };
		
        function buildNumberImg(number){ // convert number to image.
            var $imgContainer = $('<div>');
            var numberStr = number + '';
            for (var i = 0; i < numberStr.length; i++) {
                $imgContainer.append($('<img />').attr('src', numberMapper[numberStr.charAt(i)]));
            }
            return $imgContainer.html();
        }
		
        this.updatePager = function(currentPage, totalPage) { // update pager with the new numbers.
            var currentPageStr = currentPage + '';
            var totalPageStr = totalPage + '';
            var widthAdj = (currentPageStr.length + totalPageStr.length) * 16 + 13 - 5;
            var currentW = parseInt($('.mam', '#marker').css('width'));
            var markerW = parseInt($('#marker').css('width'));
            $('#current_page_number').html(buildNumberImg(currentPage));
            $('#total_page_number').html(buildNumberImg(totalPage));
            // change the width
            $('#marker').css('width', widthAdj + 29 + 19);
            $('#marker .mam').css('width', widthAdj);
            var widthDiff = currentW - widthAdj;
        };
        
        this.hideTooltip = function($ele) { // hide tooltip.
            var targetId = $ele.attr('id');
            // call hide tooltip with parameter only if some component on the screen will be removed
            // this will check to make sure that the current tooltip will not be removed incorrectly.
            if (targetId && targetId != tooltip_target_id) {
                // do nothing if targetId and current id the tooltip kept is not the same.
            }
            else {
                // no param
                $tooltip.hide();
            }
        };
        
        this.initPager = function(){ // initialize pager.
            this.updatePager(1, 1); // page 1 - total page 1
            
            $('#pager .par .touchArea').click(function(){ // click on the right arrow.
                var pagerNumber = $('#pager .pagerInput input');
                if (cp != tp) {
                    cp++;
                }
                pagerNumber.val(cp);
                instance.updatePager(cp, tp); // update pager.
                instance.updatePage(); // update current page.
                $('#pager .currentPage').html($('#current_page_number').html()); // update page indicator.
            });
            
            $('#pager .pal .touchArea').click(function(){ // click on the left arrow.
                var pagerNumber = $('#pager .pagerInput input');
                if (cp != 1) {
                    cp--;
                }
                pagerNumber.val(cp);
                instance.updatePager(cp, tp);
                instance.updatePage();
                $('#pager .currentPage').html($('#current_page_number').html());
            });
            
            $('#pager .pagerInput input').bind('keydown', function(e){ // catch enter event and allow number only.
                if (e.which == 13) {
                    var inputVal = $(this).val();
                    $('#pager').hide();
                    if (inputVal > tp) {
                        $(this).val(tp);
                        cp = tp;
                    }
                    else {
                        cp = inputVal;
                    }
                    
                    instance.updatePager(cp, tp);
                    instance.updatePage();
                }
            }).bind('keypress', function(e){
                var code = (e.which) ? e.which : event.keyCode;
                return (code >= 48 && code <= 57 || code == 8);
            });
            		
            $('#current_page_number').click(function() { // handle click on o current page number

                var $this = $(this);
                if ($this.length > 0) {
                    var childrenLength = $this.children().length;
                    var widthDiff = (childrenLength - 1) * 16;

                    $('#pager .pam').css('width', widthDiff + 'px');
                    $('#pager .pagerInput input').css('width', 22 + widthDiff + 'px').val(cp);
                }

                var leftDiff = $this[0].offsetLeft;

                $('#pager .currentPage').html($('#current_page_number').html());
                $('#pager').css({
                    'left': '-10px',
                    'top': '-15px'
                }).show();
                return false;
            });
            		
            $('body').click(function(){ // hide pager if click outside
                $('#pager').hide();
            });
            
            $('#pager').click(function(event){ // not do so if it is pager.
                event.stopPropagation();
            });
        };
        
        var getViewport = function() { // get window viewport.
            return [$(window).width(), $(window).height(), $(document).scrollLeft(), $(document).scrollTop()];
        };
        var getTooltipMargin = function() { // construct default margin for tooltip. The distance between the edges of the tooltip to the viewport.
            return {
                left: 5,
                top: 5
            };
        };
        
        var repositionTooltip = function(obj, ele, e) { // reposition tooltip when mouse move.

            var imageSrc = $('<img />').attr('src', obj.attr('src'));
            // e is mouse event.
            // ele contains position of the element that the mouse is hovering.
            // obj contains the image dimension.
            var view = getViewport();
            var tooltipMargin = getTooltipMargin();
            var object = {
                width: imageSrc[0].width,
                height: imageSrc[0].height
            };
            // obj is image inside the div
            var ratio = Math.min(Math.min(Math.max(e.clientX, view[0] - e.clientX) - tooltipMargin.left * 2, object.width) / object.width, Math.min(Math.max(view[1] - e.clientY, e.clientY) - tooltipMargin.top * 2, object.height) / object.height);
            // resize image
            var imageW = Math.round(ratio * (object.width - tooltipMargin.left * 2));
            var imageH = Math.round(ratio * (object.height - tooltipMargin.top * 2));

            // apply the resize
            obj.attr({
                'width': imageW,
                'height': imageH
            });

            return {
                width: imageW,
                height: imageH
            };
        };
        
        this.showTooltip = function($ele, e, $imgDiv) { // this to make sure that we control only instance of tooltip for showing big imgage thumbnail
            var view = getViewport();
            var targetId = $ele.attr('id');

            var ele = {
                x: findPosX($ele[0]),
                y: findPosY($ele[0]),
                width: $ele.width(),
                height: $ele.height()
            };

            var resizedImg = repositionTooltip($imgDiv.children('img:first'), ele, e);
            var imgPos = {
                left: e.pageX,
                top: e.pageY
            };

            var posAdj = { // now check the position
                left: (view[0] - e.clientX > resizedImg.width) ? (imgPos.left + 5) : (imgPos.left - 5 - resizedImg.width),
                top: (view[1] - e.clientY > resizedImg.height) ? (imgPos.top + 5) : (imgPos.top - 5 - resizedImg.height)
            };

            if (tooltip_target_id) {
                if (tooltip_target_id == targetId) {
                    $tooltip.css({
                        left: posAdj.left,
                        top: posAdj.top
                    });
                    return;
                }
            }
            else
            if (tooltip_target_id == null) {
                buildImgTooltip();
            }
            $tooltip.css({
                left: posAdj.left,
                top: posAdj.top
            });
            // first initialize
            tooltip_target_id = targetId;
            $tooltip.empty();
            $tooltip.html($imgDiv.html()).show();
        };
        
        function buildImgTooltip(){ // build tooltip.
            var $div = $('<div></div>').css({
                position: 'absolute',
                'z-index': 99999,
                'display': 'none',
                'padding': '3px',
                'background-color': '#FFF',
                'border': '1px solid #837C76',
                'id': 'tooltip'
            });
            $tooltip = $div;
            $('body').append($div.hide()); // append to body.
        }
        
        this.updatePage = function() { // to update current page.
            var itemList = itemService.getItemRange(cp, MAX_ITEM_PER_PAGE);
            $(selector).empty(); // clear all.
            for (var i = 0; i < itemList.length; i++) {
                $(selector).append(itemList[i].getHTML());
            }
            $('a.itemSubPic').fancybox({ // enable fancy box
                titleShow: true,
                titlePosition: 'over',
                onComplete: function() {
                    $("#fancybox-wrap").hover(function() {
                        $("#fancybox-title").show();
                    }, function() {
                        $("#fancybox-title").hide();
                    });
                }
            });

            if (isAdmin) { // update captify.
                $('img.captify').captify({
                    speedOver: 'fast',
                    speedOut: 'fast',
                    hideDelay: 500,
                    animation: 'slide',
                    opacity: '0.6',
                    className: 'caption-top',
                    position: 'top',
                    spanWidth: '100%'
                });
            }
        };
        
        this.loadItems = function(){ // load items from server.
            if (isItemLoading) {
                return;
            }
            isItemLoading = true;
            var ajaxUrl = '<c:url value="/item/viewAll.html" />';
            $.ajax({ // call to server.
                url: ajaxUrl,
                dataType: 'json',
                type: 'POST',
                beforeSend: function(){
                    // show loading indicator
                },
                success: function(data){
                    $(selector).empty(); // clear all.
                    if (data.items) {
                        tp = (parseInt(data.items.length / MAX_ITEM_PER_PAGE)) + 1;
                        if (data.items.length % MAX_ITEM_PER_PAGE == 0) { // the number of item equals to MAX_ITEM_PER_PAGE
                            tp -= 1;
                        }
                        instance.updatePager(1, tp);                        
                        itemService.clearAll(); // clean item service
                        for (var i = 0; i < data.items.length; i++) {
                            var item = new Item(data.items[i], data.admin, i); 
                            item.construct();
                            itemService.addItem(item.getId(), item); // add item to itemService.
                        }
                        instance.updatePage(); // update current page.
                        
                        callback(data); // process after items have been added to the page.
                        isAdmin = data.admin;
                    }
                },
                error: function(){
                },
                complete: function(){
                    isItemLoading = false;
                }
            });
        }
    }
	/**
     * Item service.
     */
	function ItemService(){ // item service help to provide item within a range. Pager support.
        var data = new Array();
        var dataIdMapper = new Array();
        this.addItem = function(itemId, item) { // add item to item service
            if (itemId == undefined || itemId == null) {
                itemId = data.length;
            }
            data.push(item);
            // save the position
            dataIdMapper[itemId] = data.length - 1;
        };
        this.removeItem = function(itemId) {
            // get the index of the item
            var index = dataIdMapper[itemId];

            data.splice(index, 1);
        };
        this.getItemRange = function(page, item_per_page) {
            // return a list of item
            return data.slice((page - 1) * item_per_page, page * item_per_page);
        };
        
        this.clearAll = function(){
            data = new Array();
            dataIdMapper = new Array();
        }
    }
</script>
