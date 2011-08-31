<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    // the reason I create this class inside a jsp page is to resort the JSP parser to check for security for some function of this webpage.
    function Item(itemData, adminMode, counter){
        var data = itemData;
        var id;
        // take a template to be filled with data
        var htmlInstance = $itemTemplate.clone();
        this.getHTML = function() {
            return htmlInstance;
        };
        this.getId = function() {
            return id;
        };
        this.construct = function(){
            for (var property in itemMapping) {
                if (property) {
                    if (itemMapping[property] instanceof Object) {
                        var selector = '.' + property;
                        var arr = $(selector, htmlInstance);
                        if (arr.length > 0) {
                            var ele = arr[0];
                            // counter base on number of item, this counter is used for image galleries group name.
                            itemMapping[property].process($(ele), data[itemMapping[property].target], adminMode, counter, data);
                        }
                    }
                    else {
                        // property != undefined
                        var selector = '.' + property;
                        var arr = $(selector, htmlInstance);
                        if (arr.length > 0) {
                            var ele = arr[0];
                            // check admin role
                            if ($(ele).hasClass('admin') && !adminMode) {
                                $(ele).remove();
                            }
                            else {
                                var itemID = data[itemMapping[property]];
                                // set id to this item.
                                id = itemID;
                                $(ele).html(itemID);
                                if ($(ele).hasClass('admin')) {
                                    $(ele).hide();
                                    // check if it is id then process the other tag
                                    if ($(ele).hasClass('id')) {
                                        var $thumbnail = $('.thumbnail', htmlInstance);
                                        var $img = $thumbnail.find('img');
                                        var $caption = $thumbnail.find('div');
                                        $img.attr('rel', $img.attr('rel') + '-' + itemID);
                                        $caption.attr('id', $caption.attr('id') + '-' + itemID);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
    };
</script>
