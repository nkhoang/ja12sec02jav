<?php
$this->breadcrumbs=array(
	'Item Pictures'=>array('index'),
	$model->title,
);

$this->menu=array(
	array('label'=>'List ItemPicture', 'url'=>array('index')),
	array('label'=>'Create ItemPicture', 'url'=>array('create')),
	array('label'=>'Update ItemPicture', 'url'=>array('update', 'id'=>$model->id)),
	array('label'=>'Delete ItemPicture', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->id),'confirm'=>'Are you sure you want to delete this item?')),
	array('label'=>'Manage ItemPicture', 'url'=>array('admin')),
);
?>

<h1>View ItemPicture #<?php echo $model->id; ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
		'id',
		'title',
		'description',
		'link',
		'internal_link',
		'item_id',
		'is_thumbnail_picture',
	),
)); ?>
