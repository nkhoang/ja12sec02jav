
<div class="ic">
    <div class="img_c it_b">
        <!-- may check image existence here -->
        <img height="40"  src="<?php echo $data->itemPictures[0]->link ?>"/>
    </div>

    <div class="it_b">
        <div class="it_title"><?php echo CHtml::encode($data->getAttributeLabel('item_id')); ?></div>
        <div class="seperator"></div>
        <div class="it_content"><?php echo CHtml::encode($data->item_id); ?></div>
    </div>
         
</div>
