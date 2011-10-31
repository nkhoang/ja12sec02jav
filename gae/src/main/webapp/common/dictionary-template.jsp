<script id="dictTemplate" type="text/x-kendo-tmpl">
   <tr>
      <td width="20px">
         <img class="handler"
              src="http://www.defaulticon.com/sites/default/files/styles/icon-front-page-32x32-preview/public/field/image/cancel.png"
              alt="Delete" height="19" onclick="deleteDict(this, '#= id #', '#= name #');"/>
      </td>
      <td width="90px">
         #= name #
      </td>
      <td>
         #= description #
      </td>
   </tr>
</script>