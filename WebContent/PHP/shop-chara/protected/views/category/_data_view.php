<div class="category_block">
    <div class="controller">
        <a title="Edit Category" id="showCategoryForm" href="<?php echo CController::createUrl('/category/ajaxUpdateCategory', array('id' => $data->id,)); ?>">
            <img src="<?php echo Yii::app()->request->baseUrl . '/images/edit.png'; ?>" width="24" height="24" />
        </a>
    </div>
<a onclick="$.ajax(
    {
        'type': 'post',
        'data':{
            'category_id':'<?php echo $data->id; ?>'},
            'url': '<?php echo CController::createUrl('/shop/listItems'); ?>',
            'cache':false,
            'success':function(html){
                jQuery('#item_board').html(html)
            },
            'error' : function(x,e) {
                jQuery('#item_board').html(x.responseText);
            }
        });"
   href="#"><?php echo CHtml::encode($data->title); ?></a>
    </div>