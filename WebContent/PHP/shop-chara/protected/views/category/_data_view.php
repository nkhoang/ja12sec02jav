<a onclick="$.ajax(
    {
        'type': 'post',
        'data':{
            'category_id':'<?php echo $data->id; ?>'},
            'url': '<?php echo CController::createUrl('/shop/listItems'); ?>',
            'cache':false,
            'success':function(html){
                jQuery('#item_board').html(html)
            }
        });"
   href="#"><?php echo CHtml::encode($data->title); ?></a>
<br />