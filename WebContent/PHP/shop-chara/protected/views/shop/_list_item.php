<!-- Item Controller -->
<div id="item_controller">
    <a title="Add Item" id="showItemForm" href="<?php echo CController::createUrl('/item/ajaxCreateItem', array('category_id' => $categoryID)); ?>"> <img src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32" height="32" /></a>
</div>


<script type="text/javascript" >

    $(function(){
        $("#showItemForm").fancybox({
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
            'onClosed': function() {
                <?php echo $scripts ?>
            }
        });
        buildTooltip('div.ic div.img_c');
    });

</script>
<div id="items_c">
    <?php
    echo $itemListOutput;
    ?>

</div>