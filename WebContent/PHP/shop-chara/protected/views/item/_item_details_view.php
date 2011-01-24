<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/item/item.css" />
<script type="text/javascript">
    var item_picture_paging_url = '<?php echo CController::createUrl('/shop/showItemPicture', array('item_id' => $item->id)); ?>'; // default value.;

    function item_picture_list_view_callback() {
        $('#item_pictures .sorter a, #item_pictures li.page a,#item_pictures li.next a,#item_pictures li.previous a').click(function(){ // fix: save the state of list view paging.
            // update paging information
            item_picture_paging_url = $(this).attr('href');
        });

        buildTooltip('div.item_picture_img');
        buildTooltip('div.image_thumb');

        $("a.showItemPictureForm, #showItemPictureCreateForm").fancybox({
            'transitionIn'	:	'fade',
            'transitionOut'	:	'fade',
            'speedIn'		:	600,
            'speedOut'		:	200,
            'overlayShow'	:	true,
            'centerOnScroll': true,
            'type' : 'ajax',
            'ajax' : {
                type: "POST"
            },
            'onClosed': function(){
                $.ajax(
                {
                    'type': 'post',
                    'data':{
                        'item_id':'<?php echo $item->id; ?>'
                    },
                    'url': item_picture_paging_url,
                    'cache':false,
                    'success':function(html){
                        $('#item_pictures').html(html);
                        item_picture_list_view_callback();
                    },
                    'error' : function(x,e) {
                        $('#item_pictures').html(x.responseText);
                    }
                });
            }
        });
    }
    $(function(){
        $.ajax({
            'url': '<?php echo CController::createUrl('/shop/showItemPicture', array('item_id' => $item->id)); ?>',
            'data': {
                'processOutput': true
            },
            'type': 'post',
            'success': function(html) {
                $('#item_pictures').html(html);
                item_picture_list_view_callback();
            }
        });
        $.ajax({
            'url': '<?php echo CController::createUrl('/item/ajaxUpdate', array('item_id' => $item->id)); ?>',
            'type': 'post',
            'success': function(html) {
                $('#item_edit_form').html(html);
            }
        });
    });
</script>
<?php
$this->breadcrumbs = array(
    'Category' => array('/shop/index'),
    $category->title => array('/shop/listItems', 'category_id' => $category->id),
    $item->item_id,
);
?>

<?php
$this->widget('zii.widgets.CBreadcrumbs', array(
    'links' => $this->breadcrumbs,
    'homeLink' => false,
));
?>


<div id="it_c">
    <div class="img_c image_thumb">
        <!-- may check image existence here -->
        <img height="150"  src="<?php echo $itemThumbnailLink ?>"/>
    </div>

    <div id="item_edit_form" class="form_c">

    </div>
    <div style="clear:both;"></div>
    <div id="item_picture_controller">
        <a title="Add Item Picture" id="showItemPictureCreateForm" href="<?php echo CController::createUrl('/itemPicture/ajaxCreateItemPicture', array('id' => $item->id,)); ?>">
            <img src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32" height="32" />
        </a>
    </div>
    <div id="item_pictures">
    </div>
</div>