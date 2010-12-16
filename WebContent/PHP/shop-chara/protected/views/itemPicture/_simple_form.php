<div id="itemPictureForm">
<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'item-picture-form',
	'enableAjaxValidation'=>false,
)); ?>
        You're adding item picture for Item <b>id: <?php echo $itemID ?> </b>
        <br />

	<p class="note">Fields with <span class="required">*</span> are required.</p>

	<?php echo $form->errorSummary($model); ?>

	<div class="row">
		<?php echo $form->labelEx($model,'title'); ?>
		<?php echo $form->textField($model,'title',array('maxlength'=>256)); ?>
		<?php echo $form->error($model,'title'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'description'); ?>
		<?php echo $form->textArea($model,'description',array('rows'=>3, 'cols'=>30)); ?>
		<?php echo $form->error($model,'description'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'link'); ?>
		<?php echo $form->textField($model,'link',array('size'=>60,'maxlength'=>256)); ?>
		<?php echo $form->error($model,'link'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'internal_link'); ?>
		<?php echo $form->textField($model,'internal_link',array('size'=>60,'maxlength'=>256)); ?>
		<?php echo $form->error($model,'internal_link'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'is_thumbnail_picture'); ?>
		<?php echo CHtml::activeCheckBox($model,'is_thumbnail_picture'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Create' : 'Save'); ?>
	</div>

<?php $this->endWidget(); ?>
</div>
</div><!-- form -->