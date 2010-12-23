<?php
$this->breadcrumbs = array(
    'Admin' => array('/index'),
    'Admin',
);
?>
<h1>Amin Board</h1>
<script type="text/javascript">
    var getCurrentRole = function () {
        return $('#roleList').get(0).value;
    }
</script>
<?php
echo CHtml::dropDownList('roleList', '', $roleList, array(
    'ajax' => array(
        'type' => 'POST',
        'url' => CController::createUrl('admin/listTasks'),
        'update' => '#taskListDiv',
        'data' => array(
            'roleID' => 'js:getCurrentRole()',
        ),
    ),
));
?>

<div id="taskListDiv">
    <?php
    echo CHtml::dropDownList('taskList', '', $taskList);
    ?>
</div>