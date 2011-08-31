<%@ include file="/common/taglibs.jsp"%>
<style type="text/css">
    #refreshRevisions{
    	position: absolute;
    	right: 0;
    	top: 0;
    }
    
    #backupForm dt {
    	width: 220px;
    }
   #backupForm dd {
    	width: 320px;
    }
</style>

<script type="text/javascript">

	var refreshRevision = function($caller) {
        // list revision
        $.ajax({
            url: '<c:url value="/backup/listRevisions.html" />',
            type: 'POST',
            beforeSend: function() {
                if ($caller) {
                    $caller.addClass('loading');
                }
            },
            success: function(data) {
                if (data.data && data.data.length > 0) {
                    var revisions = data.data;
                    var $selectRevision = $('#revisionSelection');
                    $selectRevision.empty();
                    for (var i = 0; i < revisions.length; i++) {
                        var $option = $('<option ></option>');
                        $option.attr({
                            'value': revisions[i]
                        }).html(revisions[i]);
                        $selectRevision.append($option);
                    }
                } else {
                    $('#revisionSelection').empty().append($('<option ></option>').html('No revision found'));
                }
            },
            error: function() {
                $('#backupResult').html('Failed!!!').fadeIn('slow').fadeOut('slow');
            },
            complete: function() {
                if ($caller) {
                    $caller.removeClass('loading');
                }

                $('#refreshImg').show();
                $('#refreshLoading').hide();
            }
        });
    };

	$(function(){

		refreshRevision();
		
		$('#refreshRevisions').click(function(){
			// emtpy
			$('#revisionSelection').empty();
			// add Loading ...
			var $option = $('<option ></option>').html('Loading ...');
			$('#revisionSelection').html($option);
			$('#refreshImg').hide();
			$('#refreshLoading').show();
			if ($(this).hasClass('loading')){
			}else {
				refreshRevision($(this));
			}
			return false;
		});

		$('#closeMeBtn').click(function(){
			 $.alerts._hide();
			 $('body').css('overflow','auto');
			
			 manager.loadItems();
    });
		

		$('#restoreBtn').click(function(){
			var $this = $(this);
			var $revisionSelection = $('#revisionSelection');
			// get submit value
			var selectedIndex = $revisionSelection[0].selectedIndex;

			var selectedOption = $('#revisionSelection option').get(selectedIndex);
			var revision = $(selectedOption).attr('value');

			$.ajax({
				url: '<c:url value="/backup/restore.html" />',
				type: 'POST',
				data: {
					'revision': revision
				},
				beforeSend: function(){
					$this.addClass('loading');
					// create a loading indicator
					$('#restoreLoading').show();
				},
				success: function(data){
					if (data.result){
						$('#restoreResult').html('Successfully Restore!!!').show().fadeOut(2000);
					}
				},
				error: function(){
					$('#restoreResult').html('Failed!!!').show().fadeOut(2000);
				},
				complete: function(){
					$('#restoreLoading').hide();				
				}
			});
			});

		$('#backupBtn').click(function(){
			// add loading to make sure it will no be clicked again
			var $this = $(this);
			if ($this.hasClass('loading')){
			}else {
				var backupUrl = '<c:url value="/backup/backupAll.html" />';
				$.ajax({
					url: backupUrl,
					type: 'POST',
					beforeSend: function(){
						$this.addClass('loading');
						// create a loading indicator
						$('#backupLoading').show();
					},
					success: function(data){
						if (data.result){
							$('#backupResult').html('Successfully Backup!!!').fadeIn('slow').fadeOut('slow');
						}
					},
					error: function(){
						$('#backupResult').html('Failed!!!').fadeIn('slow').fadeOut('slow');
					},
					complete: function(){
						$('#backupLoading').hide();				
					}
				});
			}
			return false;
			});
	});
</script>
<div id="container">
	<div id="refreshRevisions">
		<img id="refreshImg" alt="Reload revisions" title="Reload revisions" src="http://lh5.ggpht.com/_4oj_ltkp9pc/S_VoFkL9MXI/AAAAAAAAAGc/mkWFeTSUVFc/arrow_rotate_anticlockwise.png" />
        <img id="refreshLoading" src="<c:url value='/images/simple/loading.gif' />" width='24' height="24" style="display: none; vertical-align: middle;"/>
	</div>
    <form id="backupForm" action="" method="post" class="">
    <fieldset>
	    <dl>
	        <dt>
	        	Backup current data
	        </dt>
	        <dd>
    		    <input type="button" value="Backup" id="backupBtn" name="backupBtn" />
    		    <img id="backupLoading" src="<c:url value='/images/simple/loading.gif' />" width='24' height="24" style="display: none; vertical-align: middle;"/>
    		    <span id="backupResult"></span>
	        </dd>
	    </dl>
	    <dl>
	        <dt>
	        	Restore to one of those revisions
	        </dt>
	        <dd>
	        	<select name="revisionSelection" size="1" id="revisionSelection">
	        		<option value="Loading">Loading ...</option>
	        	</select>
                <label>
                    <input type="button" value="Restore" id="restoreBtn" name="restoreBtn"/>
                </label>
                <img alt="Restore Image" id="restoreLoading" src="<c:url value='/images/simple/loading.gif' />" width='24' height="24" style="display: none; vertical-align: middle;"/>
    		    <span id="restoreResult"></span>
	        </dd> 
	    </dl>
	    <dl>
	        <dt>
	        </dt>
	        <dd>
                <label>
                    <input type="button" value="Close me" id="closeMeBtn" name="closeMe"/>
                </label>
            </dd>
	    </dl>
    </fieldset>
</form>
</div>
