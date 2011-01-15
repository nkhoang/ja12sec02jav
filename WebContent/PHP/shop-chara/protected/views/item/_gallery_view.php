<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/galleria.css" />
<script src="<?php echo Yii::app()->request->baseUrl; ?>/js/jquery.galleria.js" type="text/javascript"></script>
<script type="text/javascript">
    Galleria.loadTheme('<?php echo Yii::app()->request->baseUrl; ?>/js/galleria.classic.js');
</script>
<style type="text/css">
    /* Fix galleria bug */
    .demo {
        width: 480px;
        height: 420px;
    }
    .gallery_demo {
        display: none;
    }
</style>
<script type="text/javascript">
    function loadGalleria() {
        $('ul.gallery_demo').galleria({
            height:400,
            width: 350,
            autoplay:3000,
            easing: 'linear',
            show_info: true
        });
        $('.gallery_demo').fadeIn();
    }
</script>

<div class="demo">
    <ul class="gallery_demo">
        <?php foreach ($itemPictures as $itemPic): ?>
            <li><img src='<?php echo CHtml::encode($itemPic->link); ?>' alt="<?php echo CHtml::encode($itemPic->description);?>" title="<?php echo CHtml::encode($itemPic->title);?>"></li>
        <?php endforeach; ?>
    </ul>
</div>
