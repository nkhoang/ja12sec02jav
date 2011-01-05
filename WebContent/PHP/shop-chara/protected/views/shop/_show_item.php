<script type="text/javascript">
    $(function(){
        tp = <?php echo $totalPage; ?>;
        cp = <?php echo $currentPage ?>;
        manager.updatePager(cp, tp);
    });
    
</script>

<?php echo $itemsHTML; ?>