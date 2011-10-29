<script type="text/javascript" src="<c:url value='/js/jquery.notify.js' />"></script>
<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/ui.notify.css' />"/>
<script type="text/javascript">
    function showMessage(vars, opts) {
        return $container.notify("create", vars, opts);
    }

    $(function() {
        $container = $("#notify-container").notify();
    });
</script>

<div id="notify-container">

    <div id="basic-template">
        <a class="ui-notify-cross ui-notify-close" href="#">x</a>

        <h1>X{title}</h1>

        <p>X{text}</p>
    </div>
</div>