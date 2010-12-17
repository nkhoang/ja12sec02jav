<?php

/**
 * This is the model class for table "item_picture".
 *
 * The followings are the available columns in table 'item_picture':
 * @property integer $id
 * @property string $title
 * @property string $description
 * @property string $link
 * @property string $internal_link
 * @property integer $item_id
 * @property integer $is_thumbnail_picture
 *
 * The followings are the available model relations:
 * @property Item $item
 */
class ItemPicture extends CActiveRecord {

    /**
     * Returns the static model of the specified AR class.
     * @return ItemPicture the static model class
     */
    public static function model($className=__CLASS__) {
        return parent::model($className);
    }

    /**
     * @return string the associated database table name
     */
    public function tableName() {
        return 'item_picture';
    }

    /**
     * @return array validation rules for model attributes.
     */
    public function rules() {
        // NOTE: you should only define rules for those attributes that
        // will receive user inputs.
        return array(
            array('title, link', 'required'),
            array('title, link, internal_link', 'length', 'max' => 256),
            array('link', 'url'),
            array('description, is_thumbnail_picture', 'safe'),
            // The following rule is used by search().
            // Please remove those attributes that should not be searched.
            array('id, title, item_id', 'safe', 'on' => 'search'),
        );
    }    

    /**
     * @return array relational rules.
     */
    public function relations() {
        // NOTE: you may need to adjust the relation name and the related
        // class name for the relations automatically generated below.
        return array(
            'item' => array(self::BELONGS_TO, 'Item', 'item_id'),
        );
    }

    /**
     * @return array customized attribute labels (name=>label)
     */
    public function attributeLabels() {
        return array(
            'id' => 'ID',
            'title' => 'Title',
            'description' => 'Description',
            'link' => 'Link',
            'internal_link' => 'Internal Link',
            'item_id' => 'Item',
            'is_thumbnail_picture' => 'Is Thumbnail Picture',
        );
    }

    /**
     * Retrieves a list of models based on the current search/filter conditions.
     * @return CActiveDataProvider the data provider that can return the models based on the search/filter conditions.
     */
    public function search() {
        // Warning: Please modify the following code to remove attributes that
        // should not be searched.

        $criteria = new CDbCriteria;

        $criteria->compare('id', $this->id);
        $criteria->compare('title', $this->title, true);
        $criteria->compare('item_id', $this->item_id);

        return new CActiveDataProvider(get_class($this), array(
            'criteria' => $criteria,
        ));
    }

}