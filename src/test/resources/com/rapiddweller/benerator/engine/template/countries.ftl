<#list countries as country>
${country.name} has ${country.population?string(",##0")} inhabitants.
It has the following states:
<#list country.states as state>
- ${state.name}
</#list>

</#list>