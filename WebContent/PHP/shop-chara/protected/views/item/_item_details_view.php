<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/item/item.css" />
<script type="text/javascript">
    $(function(){
        $.ajax({
            'url': '<?php echo CController::createUrl('/shop/showItemPicture', array('item_id' => $itemID)); ?>',
            'type': 'post',
            'success': function(html) {
                $('#item_pictures').html(html);
            }
        });
        $.ajax({
            'url': '<?php echo CController::createUrl('/item/ajaxUpdate', array('item_id' => $itemID)); ?>',
            'type': 'post',
            'success': function(html) {
                $('#item_edit_form').html(html);
            }
        });

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
                        'item_id':'<?php echo $itemID; ?>'
                    },
                    'url': '<?php echo CController::createUrl('/shop/viewItemDetails'); ?>',
                    'cache':false,
                    'success':function(html){
                        jQuery('#item_board').html(html);
                    },
                    'error' : function(x,e) {
                        jQuery('#item_board').html(x.responseText);
                    }
                });
            }
        });

        buildTooltip('div.item_picture_img');
        buildTooltip('div.image_thumb');
    });
</script>
<?php
$this->breadcrumbs = array(
    'Category' => array('/shop/index'),
    $category->title => array('/shop/listItems', 'category_id' => $category->id),
    $itemID,
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
        <a title="Add Item Picture" id="showItemPictureCreateForm" href="<?php echo CController::createUrl('/itemPicture/ajaxCreateItemPicture', array('id' => $itemID,)); ?>">
            <img src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32" height="32" />
        </a>
    </div>
    <div id="item_pictures">
    </div>
</div>