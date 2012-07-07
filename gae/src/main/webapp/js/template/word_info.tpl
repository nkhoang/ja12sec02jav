*** main template *** (all part outside templates are invisible}
{#template MAIN}
    <div class="cnt-nav-lnk nav-info first">
        <table>
            <tbody>
            <tr>
                {#if $T.soundSource}
                <td class="nav-icon" onclick="{$T.soundSource}" class="sound">
                </td>
                {#else}
                <td class="nav-icon no-sound">
                </td>
                {#/if}
                <td class="nav-lnk-title">
                  {$T.description}
                  {#if $T.pron}
                      {$T.pron}
                  {#/if}

                </td>
                {#if $T.soundSource}

            </tr>
            </tbody>
        </table>
    </div>
    <div class="w-t"></div>
{#/template MAIN}