<?php
$this->breadcrumbs = array(
    'Shop',
);
?>
<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/shop/admin_board.css" />
<script type="text/javascript" >
    var category_paging_url = '<?php echo CController::createUrl('/shop/listCategories'); ?>'; // default value.
    var item_paging_url = null;

    $(function(){
       $.ajax({
        'url' : '<?php echo CController::createUrl('/shop/listItems')?>',
        'type': 'post',
        'data' : {
            'category_id': 1
        },
        'success' : function(html) {
            $('#item_board').html(html);
        }
       });
    });
</script>
<!--
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
-->

<div id="item_board"></div>


