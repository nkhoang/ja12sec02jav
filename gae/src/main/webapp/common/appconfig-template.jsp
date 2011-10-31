<script id="appconfigTemplate" type="text/x-kendo-tmpl">
   <tr>
      <td width="20px">
         <img class="handler"
              src="http://www.defaulticon.com/sites/default/files/styles/icon-front-page-32x32-preview/public/field/image/cancel.png"
              alt="Delete" height="19" onclick="deleteAppConfig(this, '#= id #', '#= label #');"/>
      </td>
      <td width="90px">
         #= label #
      </td>
      <td>
         # for (var i = 0; i < values.length - 1; i++) { #
         #= values[i]  # ,
         # } #
         #= values[values.length - 1] #
      </td>
   </tr>
</script>