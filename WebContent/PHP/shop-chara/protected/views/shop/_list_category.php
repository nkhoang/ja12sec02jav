<script type="text/javascript" >

    $(function(){
        $("a.showCategoryForm").fancybox({
            'transitionIn'	:	'fade',
            'transitionOut'	:	'fade',
            'speedIn'		:	600,
            'speedOut'		:	200,
            'overlayShow'	:	true,
            'centerOnScroll': true,
            'type' : 'ajax',
            'ajax' : {
                type: "POST"
            },
            'onClosed': function(){
                $.ajax({
                    'url': '<?php echo CController::createUrl('/shop/listCategories'); ?>',
                    'type': 'post',
                    'success': function(html) {
                        $('#category_board').html(html);
                    }
                });
            }
        });

    });

</script>

<?php echo $category_list; ?>
