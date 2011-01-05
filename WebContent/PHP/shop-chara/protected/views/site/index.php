<?php $this->pageTitle = Yii::app()->name; ?>

<h1>Welcome to <i><?php echo CHtml::encode(Yii::app()->name); ?></i></h1>

<p>
    To enter shop-chara please visit <?php echo CHtml::link('Shop now!!!!', Yii::app()->urlManager->createUrl('/shop')); ?>
</p>
<script type="text/javascript">
    function preloadAllImage() {
        $('.thumbnail img').each(function(){
            $(this).load(function() {
                var $this= $(this);
                var ratio = calculateRatio($this[0].width, $this[0].height, 138, 158);
                $this.attr({
                    'width': Math.round(ratio * $this[0].width),
                    'height': Math.round(ratio * $this[0].height)
                });

                $this.parents('div.wraptocenter').removeClass('loading');
            });
        });
    }

    $(function(){
        $.ajax({
            'type': 'post',
            'url': '<?php echo CController::createUrl('/shop/showItems', array('category_id' => 1)); ?>',
            'success': function(html) {
                $('#itemsContainer').html(html);
                preloadAllImage();
            }
        });

        // propagate click to show fancybox.
        $('img.item').live('click', function(){
            $(this).parents('div.icc').find('a.itemSubPic').first().click();
        });

        // Enable tooltip.
        $('img.item').live('mousemove', function(e){
            var $this = $(this);
            var $imgDiv = $('div.thumbnail-big', $this.parents('div.itemCover'));
            manager.showTooltip($this, e, $imgDiv);
        });

        
        $('img.item').live('mouseout', function(e){
            var $this = $(this);
            manager.hideTooltip($this);
        });


        manager.initPager('<?php echo CController::createUrl('/shop/showItems', array('category_id' => 1)); ?>', preloadAllImage);

    });
</script>
<div id="body-wrapper">
    <div id="header"></div>
    <div id="menuContainer">
        <div id="menuButtons">
            <div class="menuButton home fleft">
            </div>
            <div class="menuSeperator fleft"></div>
            <div class="menuButton about fleft">
            </div>
        </div>
        <div id="menuIndicator">
            <div class="mit fleft">
                <div class="mil fleft"></div>
                <div class="mic fleft">
                </div>
                <div class="mir fleft"></div>
            </div>
            <div class="mib fleft">
                <div id="indicator-content" class="indicatorTitle home"></div>
            </div>
        </div>
        <div class="mnt fleft">
            <div class='mnl fleft'>
            </div>
            <div class="mnc fleft">
            </div>
            <div class="mnr fleft">
            </div>
        </div>
        <div class="mnm fleft">
            <div class='mnl fleft'>
            </div>
            <div class="mnc fleft">
            </div>
            <div class="mnr fleft">
            </div>
        </div>
        <div class="mnb fleft">
            <div class='mnl fleft'>
            </div>
            <div class="mnc fleft">
            </div>
            <div class="mnr fleft">
            </div>
        </div>
        <div id="login">
            <div id="loginPanel">
                <?php if (Yii::app()->user->isGuest): ?>
                    <a class="login" href="#"><img alt="Login to Chara" title="Login to Chara" src='http://docs.google.com/File?id=d5brrvd_1053rsk33gdr_b' /></a>
                <?php endif; ?>
            </div>
        </div>
    </div>
    <div id="loginContent"></div>
    <div id="leftContent">
        <div id="subNav">
            <!--
            <div class="tab first short active">
                <div class="snt">
                </div>
                <div class="snm">
                    <div class="snl">
                    </div>
                    <div class="snc">
                    </div>
                    <div class="snr">
                    </div>
                </div>
                <div class="snb">
                </div>
            </div>
            -->
            <div class="tab shirt first last active">
                <div class="snt">
                </div>
                <div class="snm">
                    <div class="snl">
                    </div>
                    <div class="snc">
                    </div>
                    <div class="snr">
                    </div>
                </div>
                <div class="snb">
                </div>
            </div>
        </div>
        <div id="contentContainer">
            <div class="ctt">
                <div class="ctl"></div>
                <div class="ctc"></div>
                <div class="ctr"></div>
            </div>
            <div class="ctm">
                <div class="ctl"></div>
                <div class="ctc"><div id="itemsContainer"></div></div>
                <div class="ctr"></div>
            </div>
            <div class="ctb">
                <div class="ctl"></div>
                <div class="ctc"></div>
                <div class="ctr"></div>
            </div>
            <div id="marker">
                <div id="markerContent">
                    <div class="mal fleft"></div>
                    <div class="mam fleft"></div>
                    <div class="mar fleft"></div></div>
                <div id="page_number">
                    <div id="number_container">
                        <div id="current_page_number"></div>
                        <img src="http://lh3.ggpht.com/_4oj_ltkp9pc/S_XYVBipyXI/AAAAAAAAAHw/bzEvOwr9tMs/n_slash.gif" style="vertical-align: middle; margin-top: -7px;"/>
                        <div id="total_page_number"></div>
                    </div>
                </div>
                <div id="pager">
                    <div class="pal fleft"><div class="touchArea"></div></div>
                    <div class="pam fleft"></div>
                    <div class="par fleft"><div class="touchArea"></div>
                        <div class="pagerInput"><input type="text" maxlength="2" size="1"/></div>
                        <div class="currentPage"><img src="http://lh5.ggpht.com/_4oj_ltkp9pc/S_ZIeNTClPI/AAAAAAAAAIA/l0u2KVRJ-xY/n_2.gif"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div style="clear: both;">&nbsp;</div>

