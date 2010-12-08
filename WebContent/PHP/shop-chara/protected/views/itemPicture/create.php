<?php
$this->breadcrumbs=array(
	'Item Pictures'=>array('index'),
	'Create',
);

$this->menu=array(
	array('label'=>'List ItemPicture', 'url'=>array('index')),
	array('label'=>'Manage ItemPicture', 'url'=>array('admin')),
);
?>

<h1>Create ItemPicture</h1>

<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>