<script type="text/javascript" >
    var category_paging_url = '<?php echo CController::createUrl('/shop/listCategories'); ?>'; // default value.
    $(function(){
        $.ajax({
            'url': '<?php echo CController::createUrl('/shop/listCategories'); ?>',
            'type': 'post',
            'data': {
                'processOutput': true
            },
            'success': function(html) {
                $('#admin_board').html(html);
            }
        });
    })
</script>

<div style="clear:both;"></div>


