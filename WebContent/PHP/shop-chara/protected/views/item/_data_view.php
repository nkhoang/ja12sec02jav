
<a onclick="
    jQuery.ajax({
        'type': 'post',
        'data':{'item_id':'<?php echo $data->item_id; ?>'},
        'url': '<?php CController::createUrl('/shop/listItems'); ?>',
        'cache':false,
        'success':function(html){jQuery('#item_board').html(html)}
    });
   " href="#"><?php CHtml::encode($data->item_id); ?></a>
<br />
