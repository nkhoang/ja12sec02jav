<div class="category_block">
    <div class="controller">
        <a title="Edit <?php echo CHtml::encode($data->title); ?>" class="showCategoryForm" href="<?php echo CController::createUrl('/category/ajaxUpdateCategory', array('id' => $data->id,)); ?>">
            <img src="<?php echo Yii::app()->request->baseUrl . '/images/edit.png'; ?>" width="24" height="24" />
        </a>
    </div>

    <a class="category-<?php echo CHtml::encode($data->id); ?>" href="<?php echo CController::createUrl('/shop/listItems', array('category_id' => $data->id));?>"
     title="Show <?php echo CHtml::encode($data->title); ?> items"><?php echo CHtml::encode($data->title); ?></a>
</div>