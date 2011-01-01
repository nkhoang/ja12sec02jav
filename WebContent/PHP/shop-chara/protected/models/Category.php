<?php

/**
 * This is the model class for table "category".
 *
 * The followings are the available columns in table 'category':
 * @property integer $id
 * @property string $description
 */
class Category extends CActiveRecord {
    const CATEGORY_NUMBER_PART_LENGTH = 5;
    const CATEGORY_CODE_PART_LENGTH = 2;

    /**
     * Returns the static model of the specified AR class.
     * @return category the static model class
     */
    public static function model($className=__CLASS__) {
        return parent::model($className);
    }

    /**
     * @return string the associated database table name
     */
    public function tableName() {
        return 'category';
    }

    /**
     * @return array validation rules for model attributes.
     */
    public function rules() {
        // NOTE: you should only define rules for those attributes that
        // will receive user inputs.
        return array(
            array('title, category_code', 'required'),
            array('description, title, category_code', 'safe'),
            array('title', 'length', 'max' => 256),
            array('category_code', 'length', 'max' => 2),
            array('category_code', 'length', 'min' => 2),
            // The following rule is used by search().
            // Please remove those attributes that should not be searched.
            array('id, description, title, category_code', 'safe', 'on' => 'search'),
        );
    }

    /**
     * @return array relational rules.
     */
    public function relations() {
        // NOTE: you may need to adjust the relation name and the related
        // class name for the relations automatically generated below.
        return array(
        );
    }

    /**
     * @return array customized attribute labels (name=>label)
     */
    public function attributeLabels() {
        return array(
            'id' => 'ID',
            'description' => 'Description',
            'title' => 'Name',
            'category_id' => 'Category ID',
        );
    }

    /**
     * Retrieve the next item number.
     * @param <type> $categoryID category id.
     * @return <type> return output string for next item number.
     */
    public static function getNextItemNumber($categoryID) {
        $result = '';
        $items = Item::model()->lastItem($categoryID)->findAll();
        if ($items !== null && sizeof($items) > 0) {
            $itemCode = $items[0]->item_id; // get the itemCode
            $number_part = substr($itemCode, 3, self::CATEGORY_NUMBER_PART_LENGTH);
            $item_next_number = (int) $number_part + 1;
            $result = str_pad($item_next_number, 5, '0', STR_PAD_LEFT);
        } else {
            $result = str_pad('1', 5, '0', STR_PAD_LEFT);
        }
        return $result;
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
        $criteria->compare('description', $this->description, true);
        $criteria->compare('title', $this->title, true);

        return new CActiveDataProvider(get_class($this), array(
            'criteria' => $criteria,
        ));
    }

}