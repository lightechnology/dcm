{
  "device": "${mac}",
  "kind": "${kind}"<#if 0 lt infolist?size>,</#if>
<#list infolist as l>
  "${l.name}": "${l.value}"
  <#if l_index lt infolist?size-1>,</#if>
</#list>
}