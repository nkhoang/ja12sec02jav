<?php
$this->breadcrumbs=array(
	'Item Pictures'=>array('index'),
	$model->title=>array('view','id'=>$model->id),
	'Update',
);

$this->menu=array(
	array('label'=>'List ItemPicture', 'url'=>array('index')),
	array('label'=>'Create ItemPicture', 'url'=>array('create')),
	array('label'=>'View ItemPicture', 'url'=>array('view', 'id'=>$model->id)),
	array('label'=>'Manage ItemPicture', 'url'=>array('admin')),
);
?>

<h1>Update ItemPicture <?php echo $model->id; ?></h1>

<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>