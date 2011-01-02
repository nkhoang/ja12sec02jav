<a href="<?php echo CController::createUrl('/itemPicture/ajaxUpdateItemPicture', array('id' => $data->id)); ?>" class="showItemPictureForm" ><div class="item_picture_block fleft">
        <div class="item_picture_title"><?php echo $data->title ?></div>
        <div class="item_picture_ic">
            <?php if ($data->is_thumbnail_picture === '1'): ?>
                <div class="item_picture_is_thumb">
                    <img width="16" src="<?php echo Yii::app()->request->baseUrl . '/images/thumb.png'; ?>" />
                </div>

            <?php endif; ?>
                <div class="item_picture_img">
                    <img height="80" src="<?php echo $data->link ?>" />
            </div>
        </div>
    </div>
</a>