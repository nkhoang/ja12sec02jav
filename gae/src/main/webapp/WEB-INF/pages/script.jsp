<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	function processAfterLoad(data){
        // "admin" flag returned by server. Enable captify.
        if (data.admin) {
            $('img.captify').captify({
                speedOver: 'fast',
                speedOut: 'fast',
                hideDelay: 500,
                animation: 'slide',
                opacity: '0.6',
                className: 'caption-top',
                position: 'top',
                spanWidth: '100%'
            });
        }
        // propagate click to show fancybox.
        $('img.item').live('click', function(){
            $(this).parents('div.icc').find('div.subPictures a.itemSubPic').first().click();
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
    }
    
    var manager = new ItemManager('#itemsContainer', processAfterLoad);
	
	$(function(){
		manager.loadItems(); // then load items after finish
		 
	    // handle sub menu clicks.
	    $("#subNav").delegate('.tab', 'click', function(){
	        // handle click
	        $("#subNav .tab").removeClass('active');
	        $(this).addClass('active');
	        // remove .bottom if any
	        $('#subNav .snb').removeClass('hidden');
	        // handle sibling
	        $(this).prev().find('.snb').addClass('hidden');
	    });
	    // handle main menu cliks.
	  var newClass; 
	  $('.menuButton').click(function(){
	    var $this = $(this);
	    var $indicator = $('#indicator-content');
	    
	    var oldClasses = $indicator.attr('class').split(' ');
	    if (oldClasses.length >= 2) {
	      $indicator.removeClass(oldClasses[1]);
	    }
	    
	    // new class
	    var newClasses = $this.attr('class').split(' ');
	    if (newClasses.length >= 2) {
	      newClass = newClasses[1];
	    }
	    var desX = findPosX($(this)[0]);
	    
	    $('#menuIndicator').animate({
	      duration: 'slow',
	      easing: 'linear',
	      left: desX - 50 - $(this).width()
	    }, function(){
	      $indicator.addClass(newClass).fadeIn('slow');
	    }); 
	  });
	  
	  manager.initPager();
	});

	// handle manager login
	$('a.login').live('click',function(){
		jshowURL('<c:url value="/user/login.html" />', 'Login', function(){
			manager.loadItems();
		});
		return false;	
	});
	// handle add items.
	$('a.addItem').live('click', function(){
		jshowTabURL(['<c:url value="/item/register.html" />','<c:url value="/backup/backupPage.html" />'], 'Item Manager');
		return false;
	});
	// handle clear items.
	$('a.clearItems').live('click', function(){
		var $this = $(this);
		if ($this.hasClass('loading')){
			jAlert('You have made a request!!! Please wait a moment', 'Warning');
		}else {
			jConfirm('Delete all items ? Are you sure ?', 'Warning', function(feedback){
				if (feedback){
					$.ajax({
						url: '<c:url value="/item/deleteAll.html" />',
						type: 'POST',
						beforeSend: function(){
							$this.addClass('loading');
							// create a loading indicator
							$this.after($('<img src="/images/simple/loading.gif" width="16" height="16" class="loading">'));
						},
						success: function(data){
							if (data.result){
								$('#itemsContainer').empty();
							}
						},
						complete: function(){
							$this.siblings('img.loading').remove();
							$this.removeClass('loading');
						}
					});
				}
			});
		}
	});
	// handle item delete.
	$('img.itemDelete').live('click',function(){
		var $img = $(this);
		// check class 'process'
		if ($img.hasClass('loading')){

		} else {
			var $container = $(this).parents('div.thumbnail');
			var $idContainer = $container.parent().find('div.id');
			// get the item id.
			var id = $idContainer.html();
			if (id == undefined) return;
			$.ajax({
				url: '<c:url value="/item/deleteItem.html" />',
				data: {
					'itemID': id
				},
				type: 'POST',
				error: function(){
				},
				beforeSend: function(){
					$img.addClass('loading');
				},
				success: function(data){
					if (data.result){
						$img.parents('.itemContainer').remove();
						manager.removeItem(id);
						manager.updatePage();
					}
				},
				complete: function(){
					$img.removeClass('loading');
				}				
			});
		}
	});

</script>