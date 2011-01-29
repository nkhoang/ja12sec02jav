<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="language" content="en"/>

    <link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/main.css"/>
    <link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/form.css"/>
    <!-- Fancy box -->
    <link rel="stylesheet" type="text/css"
          href="<?php echo Yii::app()->request->baseUrl; ?>/css/jquery.fancybox-1.3.4.css"/>
    <link rel="stylesheet" type="text/css"
          href="<?php echo Yii::app()->request->baseUrl; ?>/css/jquery.autocomplete.css"/>
    <link rel="stylesheet" type="text/css"
          href="<?php echo Yii::app()->request->baseUrl; ?>/css/item/item_picture.css"/>
    <link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/listview/styles.css"/>
    <link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/pager.css"/>
    <link rel="stylesheet" type="text/css"
          href="<?php echo Yii::app()->request->baseUrl; ?>/css/shop/content_layout.css"/>
    <link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/shop/admin_board.css"/>

    <!--[if lt IE 8]>
    <style>
        .wraptocenter span {
            display: inline-block;
            height: 100%;
        }
    </style><![endif]-->

    <script src="<?php echo Yii::app()->request->baseUrl; ?>/js/jquery.js" type="text/javascript"></script>
<?php
        $cs = Yii::app()->clientScript;
$cs->registerScriptFile(Yii::app()->baseUrl . '/js/jquery.autocomplete.js', CClientScript::POS_HEAD);
?>
    <script src="<?php echo Yii::app()->request->baseUrl; ?>/js/item/item.manager.js" type="text/javascript"></script>
    <script src="<?php echo Yii::app()->request->baseUrl; ?>/js/item/item.scripts.js" type="text/javascript"></script>

<?php
        $cs = Yii::app()->clientScript;
$cs->registerScriptFile(Yii::app()->baseUrl . '/js/jquery.fancybox-1.3.4.js', CClientScript::POS_HEAD);
$cs->registerScriptFile(Yii::app()->baseUrl . '/js/jquery.mousewheel-3.0.4.pack.js', CClientScript::POS_HEAD);
?>
    <title><?php echo CHtml::encode($this->pageTitle); ?></title>
</head>
<body>
<h1>Welcome to Miss Chara Admin page</h1>

<div style="color:white">You're logged in as <b><?php echo Yii::app()->user->getName(); ?></b></div>
<div id="main_container">

    <div class="section" id="DashboardContent">
        <div class="sectiontr"></div>
        <div class="sectiontl"></div>
        <div id="titleHolder">
            <table cellspacing="0" cellpadding="0" border="0" class="title">
                <tbody>
                <tr>
                    <td class="titlecontent">
                        <div id="titletl"></div>
                        <div id="titletr"></div>
                        <ul class="options">
                            <li><a class="bfoButton" href="javascript:tw.NewProject()"><span>Add project</span></a>
                            </li>
                        </ul>
                        <h2>Admin Board</h2></td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="sectionContent minHeight">

            <div id="admin_board">
                <?php echo $content; ?>
            </div>
        </div>
        <div class="sectionbl">
            <div class="sectionbr">

            </div>
        </div>
    </div>
</div>
<!-- footer -->

<!-- page -->

</body>
</html>