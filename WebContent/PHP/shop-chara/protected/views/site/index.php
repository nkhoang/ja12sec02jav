<?php $this->pageTitle=Yii::app()->name; ?>

<h1>Welcome to <i><?php echo CHtml::encode(Yii::app()->name); ?></i></h1>

<p>
    To enter shop-chara please visit <?php echo CHtml::link('Shop now!!!!', Yii::app()->urlManager->createUrl('/shop')); ?>

    Test Widget
    <?php echo phpinfo(); ?>
</p>

