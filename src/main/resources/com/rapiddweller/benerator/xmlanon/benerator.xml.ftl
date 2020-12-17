<?xml version="1.0" encoding="UTF-8"?>
<setup>

    <#list setup.files as file>
        <echo>{'Parsing file ' + ${file}}</echo>
        <domtree id="dom_${file}" inputUri="{${file}}"
                 outputUri="{com.rapiddweller.commons.FileUtil.prependFilePrefix('anon_', ${file})}"
                 namespaceAware="false"/>
    </#list>

    <memstore id="memdb"/>

    <#list setup.anonymizations as anon>
        <#if anon.locators?size == 1>
            <echo>Anonymizing ${anon.varname}</echo>
            <iterate name="${anon.locators[0].entity}" source="dom_${anon.locators[0].file}"
                     selector="${anon.locators[0].entityPath}" consumer="dom_${anon.locators[0].file}.updater()">
                <attribute name="${anon.locators[0].attribute}"
                           type="string"
                           condition="this.${anon.locators[0].attribute} != null"
                <#list anon.settings as setting>
                    ${setting.key}="${setting.value}"
                </#list>
                />
            </iterate>

        <#else>
            <echo>Creating anonymization map for ${anon.varname}</echo>
            <generate type="${anon.varname}_map" consumer="memdb">
                <variable name="${anon.varname}" type="string" source="dom_${anon.locators[0].file}"
                          selector="${anon.locators[0].path}"/>
                <id name="source" type="string" script="${anon.varname}"/>
                <attribute name="target" type="string"
                <#list anon.settings as setting>
                    ${setting.key}="${setting.value}"
                </#list>
                />
            </generate>

            <#list anon.locators as locator>
                <echo>Anonymizing ${anon.varname} in ${locator.file}</echo>
                <iterate type="${locator.entity}" source="dom_${locator.file}" selector="${locator.entityPath}"
                         consumer="dom_${locator.file}.updater()">
                    <variable name="${anon.varname}_map" source="memdb" type="${anon.varname}_map"
                              selector="_candidate.source==this.${locator.attribute}"/>
                    <attribute name="${locator.attribute}" script="${anon.varname}_map.target"/>
                </iterate>

            </#list>
        </#if>
    </#list>
    <echo>Generation finished</echo>

</setup>
