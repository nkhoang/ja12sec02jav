<?php
$this->breadcrumbs = array(
    'Shop',
);
?>
<h1><?php echo $this->id . '/' . $this->action->id; ?></h1>
<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/shop/admin_board.css" />

<div id="admin_board">
    <div id="category_board" class="content">
    <?php
        Yii::app()->runController('/shop/listCategories');
     ?>
    </div>
    <div id="item_board" class="content">
        <?php
        Yii::app()->runController('/shop/listItems');
     ?>
    </div>
    <div style="clear:both;"></div>
</div>




