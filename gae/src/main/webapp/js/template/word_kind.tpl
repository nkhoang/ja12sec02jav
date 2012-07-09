*** main template *** (all part outside templates are invisible}
{#template MAIN}
  {#foreach $T.meaningMap as meanings}
   <a href="#">
    <div class="cnt-nav-lnk">
        <table>
            <tbody>
            <tr>
                <td class="nav-icon">
                </td>
                <td class="nav-lnk-title">
                  {$T.meanings$key}
                </td>
                <td class="nav-indicator"></td>
            </tr>
            </tbody>
        </table>
    </div>
   </a>
  {#/for}
{#/template MAIN}