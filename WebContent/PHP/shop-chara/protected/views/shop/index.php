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
            'overlayShow'	:	false
        });
    });
    
</script>

<p>
    Show item form content by click <a id="showItemForm" href="#itemForm">here</a>
</p>

<div id="itemForm">
    <?php $this->renderPartial('/item/_simple_form', array('model' => $item)); ?>
</div>
