<h1>Welcome to Miss Chara Admin page</h1>

<div>You're logged in as <b><?php echo Yii::app()->user->getName(); ?></b></div>
<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/shop/admin_board.css" />
<script type="text/javascript" >
    var category_paging_url = '<?php echo CController::createUrl('/shop/listCategories'); ?>'; // default value.
    var item_paging_url = null;

    $(function(){
        $.ajax({
            'url': '<?php echo CController::createUrl('/shop/listCategories'); ?>',
            'type': 'post',
            'success': function(html) {
                $('#admin_board').html(html);
            }
        });
    })
</script>
<div id="admin_board">
</div>
<div style="clear:both;"></div>


