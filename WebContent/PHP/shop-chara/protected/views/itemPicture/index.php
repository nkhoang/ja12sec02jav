<?php
$this->breadcrumbs=array(
	'Item Pictures',
);

$this->menu=array(
	array('label'=>'Create ItemPicture', 'url'=>array('create')),
	array('label'=>'Manage ItemPicture', 'url'=>array('admin')),
);
?>

<h1>Item Pictures</h1>

<?php $this->widget('zii.widgets.CListView', array(
	'dataProvider'=>$dataProvider,
	'itemView'=>'_view',
)); ?>
