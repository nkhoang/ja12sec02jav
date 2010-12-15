<?php
$this->breadcrumbs = array(
    'Shop',
);
?>
<h1><?php echo $this->id . '/' . $this->action->id; ?></h1>

<script type="text/javascript" >

    $(function(){
        $("#showItemForm").fancybox({
            'transitionIn'	:	'fade',
            'transitionOut'	:	'fade',
            'speedIn'		:	600,
            'speedOut'		:	200,
            'overlayShow'	:	true
        });
    });
    
</script>

<p>
    Show item form content by click   <?php
echo CHtml::ajaxLink("here",
        CController::createUrl('/shop/ajaxCreate'),
        array('update' => '#itemForm', 'type' => 'POST'),array('id' => 'load_item_form')
);
?>
</p>

<div id="itemForm">
</div>