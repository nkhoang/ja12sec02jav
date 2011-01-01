<?php
$this->breadcrumbs = array(
    'Shop',
);
?>
<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/shop/admin_board.css" />
<script type="text/javascript" >

    $(function(){
        $("#showCategoryForm").fancybox({
            'transitionIn'	:	'fade',
            'transitionOut'	:	'fade',
            'speedIn'		:	600,
            'speedOut'		:	200,
            'overlayShow'	:	true,
            'centerOnScroll': true,
            'type' : 'ajax',
            'ajax' : {
                type: "POST"
            }
        });
    });

</script>

<div id="admin_board">
    <div id="category_board" class="content">
    <?php
        Yii::app()->runController('/shop/listCategories');
     ?>
    </div>
    <div id="item_board" class="content">
        <?php
        Yii::app()->runController('/shop/listItems');
     ?>
    </div>
    <div style="clear:both;"></div>
</div>




