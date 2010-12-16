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
            'overlayShow'	:	false,
            'ajax' : {
                type: "POST"
            }
        });
        $('#showItemPictureForm').fancybox({
            'transitionIn'	:	'fade',
            'transitionOut'	:	'fade',
            'speedIn'		:	600,
            'speedOut'		:	200,
            'overlayShow'	:	false,
            'ajax' : {
                type: "POST"
            }
        });
    });
    
</script>

<p>
    Show item form content by click <a href="<?php echo CController::createUrl('/shop/ajaxCreate'); ?>" id="showItemForm"> here </a>
    <br />
    Click <a id="showItemPictureForm" href="<?php echo CController::createUrl('/shop/ajaxCreateItemPicture'); ?>">here</a> to add Item
</p>