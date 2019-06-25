<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>    
    <div>
        <#list created>
            <div>
                <p><@emailMacros.msg "email.notification.client.added" /></p>
            </div>
            <div>
                <ul>
                    <#items as itemName>
                    <li>${itemName}<li>
                    </#items>
                </ul>                
            </div>
        </#list>
        <#list updated>
            <div>
                <p><@emailMacros.msg "email.notification.client.updated" /></p>
            </div>
            <div>
                <ul>
                    <#items as itemName>
                    <li>${itemName}<li>
                    </#items>
                </ul>                
            </div>
        </#list>
        <#list deleted>
            <div>
                <p><@emailMacros.msg "email.notification.client.deleted" /></p>
            </div>
            <div>
                <ul>
                    <#items as itemName>
                    <li>${itemName}<li>
                    </#items>
                </ul>                
            </div>
        </#list>
        <#list other>
            <div>
                <p><@emailMacros.msg "email.notification.client.other" /></p>
            </div>
            <div>
                <ul>
                    <#items as itemName>
                    <li>${itemName}<li>
                    </#items>
                </ul>                
            </div>
        </#list>
    </div>    
</#escape>
