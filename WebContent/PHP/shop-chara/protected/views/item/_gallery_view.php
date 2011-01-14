<script src="<?php echo Yii::app()->request->baseUrl; ?>/js/jquery.galleria.js" type="text/javascript"></script>
<script type="text/javascript">
    Galleria.loadTheme('<?php echo Yii::app()->request->baseUrl; ?>/js/galleria.classic.js');</script>
<style type="text/css">
    /* BEGIN DEMO STYLE */
    *{margin:0;padding:0}
    body{padding:20px;background:white;text-align:center;background:black;color:#bba;font:80%/140% georgia,serif;}
    h1,h2{font:bold 80% 'helvetica neue',sans-serif;letter-spacing:3px;text-transform:uppercase;}
    a{color:#348;text-decoration:none;outline:none;}
    a:hover{color:#67a;}
    .caption{font-style:italic;color:#887;}
    .demo{position:relative;margin-top:2em;}
    .gallery_demo{width:502px;margin:0 auto;}
    .gallery_demo li{width:68px;height:50px;border:3px double #111;margin: 0 2px;background:#000;}
    .gallery_demo li div{left:240px}
    .gallery_demo li div .caption{font:italic 0.7em/1.4 georgia,serif;}

    #main_image{margin:0 auto 60px auto;height:438px;width:500px;background:black;}
    #main_image img{margin-bottom:10px;}

    .nav{padding-top:15px;clear:both;font:80% 'helvetica neue',sans-serif;letter-spacing:3px;text-transform:uppercase;}

    .info{text-align:left;width:500px;margin:30px auto;border-top:1px dotted #221;padding-top:30px;}
    .info p{margin-top:1.6em;}
</style>
<script type="text/javascript">
    $(document).ready(function(){
        $('ul.gallery_demo').galleria({
            height:400,
            autoplay:3000,
            easing: 'linear'
        });
    });
</script>

<div class="demo">
    <ul class="gallery_demo">
        <?php foreach ($itemPictures as $itemPic): ?>
            <li><img src='<?php echo CHtml::encode($itemPic->link); ?>' alt="" title=""></li>
        <?php endforeach; ?>
    </ul>
</div>
