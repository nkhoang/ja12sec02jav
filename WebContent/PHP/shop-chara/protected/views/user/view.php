<?php
$this->breadcrumbs = array(
    'Users' => array('index'),
    $model->username,
);

$this->menu = array(
    array('label' => 'List User', 'url' => array('index')),
    array('label' => 'Create User', 'url' => array('create')),
    array('label' => 'Update User', 'url' => array('update', 'id' => $model->id)),
    array('label' => 'Delete User', 'url' => '#', 'linkOptions' => array('submit' => array('delete', 'id' => $model->id), 'confirm' => 'Are you sure you want to delete this item?')),
    array('label' => 'Manage User', 'url' => array('admin')),
);
?>

<h1>User [<?php echo $model->username; ?>]</h1>

<?php
$this->widget('zii.widgets.CDetailView', array(
    'data' => $model,
    'attributes' => array(
        'id',
        'first_name',
        'middle_name',
        'last_name',
        'email',
        'username',
        'password',
        'create_time',
        'update_time',
        'last_login_time',
        'account_locked',
        array(
            'label' => 'Roles',
            'type' => 'raw',
            'value' => $model->getRoles(),
        ),
    ),
)); ?>