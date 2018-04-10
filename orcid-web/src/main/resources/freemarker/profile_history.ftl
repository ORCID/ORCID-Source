<@protected>
<div>
    <div class="row">           
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <ul>
                <#list profileHistory as event>
                    <li>${event.dateCreated} : <strong>${event.eventType}</strong><#if event.comment??> | ${event.comment}</#if></li>
                </#list>
            </ul>
        </div>
    </div>
</div>
</@protected>