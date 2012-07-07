*** main template *** (all part outside templates are invisible}
{#template MAIN}
 <div class="w">
    <div class="w-t">{$T.description}</div>
    {#if $T.soundSource}
    <img id="w-d-sound" onclick="{$T.soundSource}" style="cursor: pointer" class="sound" title="Click to hear the US pronunciation of this word" alt="Click to hear the US pronunciation of this word" src="http://dictionary.cambridge.org/external/images/pron-us.png">
    {#/if}
    {#if $T.pron}
    <div class="w-pron">{$T.pron}</div>
    {#/if}
    <div id="w-section" class="w-ks">
      {#foreach $T.meaningMap as meanings}
      <div class="w-k section">
         <a name="{$T.meanings$key}" />
         <div class="w-k-t">{$T.meanings$key}</div>
           {#include SENSE root=$T.meanings}
      </div>
      {#/for}
    </div>
    {#if $T.phraseList}
    <div class="w-phrases">
      {#foreach $T.phraseList as phrase}
         <div class="w-phrase">
            <div class="w-phrase-des">{$T.phrase.description}</div>
             <a name="{$T.phrase.description}" />
            {#if $T.phrase.senseList}
            {#include SENSE root=$T.phrase.senseList}
            {#/if}
         </div>
      {#/for}
    </div>
    {#/if}
 </div>
{#/template MAIN}

{#template SENSE}

<ol>
   {#foreach $T as meaning}
   <li class="w-k-m">
       <div class="w-k-m-container">
          <div class="w-k-m-c">
             {#if $T.meaning.grammarGroup}
             <span class="w-k-m-grammarGroup grammarGroup">{$T.meaning.grammarGroup}</span>
             {#/if}
             {#if $T.meaning.languageGroup}
             <span class="w-k-m-languageGroup languageGroup">[{$T.meaning.languageGroup}]</span>
             {#/if}
             {$T.meaning.definition}
          </div>
          {#if $T.meaning.examples}
          <div class="w-k-m-examples">
             {#foreach $T.meaning.examples as example}
             <div class="w-k-m-example">{$T.example}</div>
             {#/for }
          </div>
          {#/if}
          {#if $T.meaning.subSenses}
          <ul class="w-k-m-subs">
             {#foreach $T.meaning.subSenses as subSense}
                <li class="w-k-m-sub">
                   <div class="w-k-m-sub-c">
                      {#if $T.subSense.grammarGroup}
                      <span class="w-k-m-grammarGroup grammarGroup">{$T.subSense.grammarGroup}</span>
                      {#/if}
                      {#if $T.subSense.languageGroup}
                      <span class="w-k-m-languageGroup languageGroup">[{$T.subSense.languageGroup}]</span>
                      {#/if}
                      {$T.subSense.content}
                   </div>
                   {#if $T.subSense.examples}
                   <ul class="w-k-m-sub-example">
                      {#foreach $T.subSense.examples as example}
                         <li>{$T.example}</li>
                      {#/for}
                   </ul>
                   {#/if}
                </li>
             {#/for}
          </ul>
          {#/if}
      </div>
   </li>
   {#/for}
</ol>
{#/template SENSE}