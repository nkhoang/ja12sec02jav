<script type="text/javascript">
    $(function(){
        tp = <?php echo $totalPage; ?>;
        manager.updatePager(1, tp);
    });
    
</script>

<?php echo $itemsHTML; ?>