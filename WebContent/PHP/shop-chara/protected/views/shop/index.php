<?php
$this->breadcrumbs = array(
    'Shop',
);
?>
<h1><?php echo $this->id . '/' . $this->action->id; ?></h1>
<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/shop/admin_board.css" />
<script type="text/javascript" >

    $(function(){
        $("#showItemForm").fancybox({
            'transitionIn'	:	'fade',
            'transitionOut'	:	'fade',
            'speedIn'		:	600,
            'speedOut'		:	200,
            'overlayShow'	:	false,
            'centerOnScroll': true,            
            'type' : 'ajax',
            'ajax' : {
                type: "POST"
            }
        });
   
    });
    
</script>

<div id="admin_board">
    <div id="category_board">
    <?php
        Yii::app()->runController('/shop/listCategories');
     ?>
    </div>
</div>
