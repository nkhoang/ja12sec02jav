<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/item/item.css" />
<script type="text/javascript">
    $(function(){
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
<div id="it_c">
    <div class="img_c image_thumb">
        <?php if ($itemThumbnailLink === ''): ?>
            <img height="150"  src="<?php echo Yii::app()->request->baseUrl . '/images/photo_not_available.jpg' ?>"/>
        <?php else: ?>

                <!-- may check image existence here -->
                <img height="150"  src="<?php echo $itemThumbnailLink ?>"/>
        <?php endif; ?>
            </div>

            <div class="form_c">

        <?php
                echo $editFormHTML;
        ?>
            </div>
            <div id="item_picture_controller">
                <a title="Add Item Picture" id="showItemPictureCreateForm" href="<?php echo CController::createUrl('/itemPicture/ajaxCreateItemPicture', array('id' => $itemID,)); ?>">
                    <img src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32" height="32" />
                </a>
            </div>
            <div id="item_pictures">
        <?php
               echo $itemPictureListView;
        ?>
    </div>
</div>