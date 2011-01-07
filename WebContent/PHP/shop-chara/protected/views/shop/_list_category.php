<script type="text/javascript" >
    $(function(){
        category_list_view_callback();
    });
    function category_list_view_callback() {
        $('#category_board .sorter a, #category_board li.page a,#category_board li.next a,#category_board li.previous a').click(function(){ // fix: save the state of list view paging.
            // update paging information
            category_paging_url = $(this).attr('href');
        });

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
                    'url': category_paging_url,
                    'type': 'post',
                    'success': function(html) {
                        $('#category_board').html(html);
                    }
                });
            }
        });
    }
</script>

<?php echo $category_list; ?>
